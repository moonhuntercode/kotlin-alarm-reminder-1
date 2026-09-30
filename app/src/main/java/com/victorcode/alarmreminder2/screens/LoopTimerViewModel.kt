package com.victorcode.alarmreminder2.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.victorcode.alarmreminder2.data.MedicineReminderDao
import com.victorcode.alarmreminder2.data.MedicineReminderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.victorcode.alarmreminder2.receiver.AlarmReceiver
import com.victorcode.alarmreminder2.utils.AlarmPlayerManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

// 📦 Versión 1.0.0
// 📄 Archivo: screens/LoopTimerViewModel.kt

class LoopTimerViewModel(private val dao: MedicineReminderDao) : ViewModel() {
    private val _allReminders = MutableStateFlow<List<MedicineReminderEntity>>(emptyList())
    val allReminders: StateFlow<List<MedicineReminderEntity>> = _allReminders.asStateFlow()

    private val _activeReminder = MutableStateFlow<MedicineReminderEntity?>(null)
    val activeReminder: StateFlow<MedicineReminderEntity?> = _activeReminder.asStateFlow()

    init {
        loadActiveReminders()
    }

    val currentTimeMillis = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1000L.milliseconds)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), System.currentTimeMillis())

    private fun loadActiveReminders() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.getActiveReminders(System.currentTimeMillis()).collectLatest { reminders ->
                _allReminders.value = reminders
            }
        }
    }

    fun saveNewMedicine(name: String, interval: Int, targetDoses: Int, durationDays: Int) {
        val endDateMillis = System.currentTimeMillis() + (durationDays * 24L * 60L * 60L * 1000L)
        val newReminder = MedicineReminderEntity(
            medicineName = name,
            intervalMinutes = interval,
            targetDoses = targetDoses,
            completedDoses = 0,
            durationDays = durationDays,
            endDateMillis = endDateMillis,
            isGoalAchieved = false,
            isRunning = false,
            targetAlarmTimeMillis = 0L
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertReminder(newReminder)
        }
    }

    fun deleteMedicine(reminder: MedicineReminderEntity, context: Context) {
        cancelAlarm(context, reminder)
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteReminder(reminder)
        }
    }

    fun setRingtone(reminder: MedicineReminderEntity, uri: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateReminder(reminder.copy(ringtoneUri = uri))
        }
    }

    fun toggleTimer(reminder: MedicineReminderEntity, context: Context) {
        if (reminder.isRunning) {
            // Detener
            cancelAlarm(context, reminder)
            viewModelScope.launch(Dispatchers.IO) {
                dao.updateReminder(reminder.copy(isRunning = false, targetAlarmTimeMillis = 0L))
            }
        } else {
            // Iniciar
            val targetTime = System.currentTimeMillis() + (reminder.intervalMinutes * 60_000L)
            scheduleAlarm(context, reminder, targetTime)
            viewModelScope.launch(Dispatchers.IO) {
                dao.updateReminder(reminder.copy(isRunning = true, targetAlarmTimeMillis = targetTime))
            }
        }
    }

    private fun scheduleAlarm(context: Context, reminder: MedicineReminderEntity, targetTimeMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("MEDICINE_NAME", reminder.medicineName)
            putExtra("RINGTONE_URI", reminder.ringtoneUri)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetTimeMillis, pendingIntent)
        } catch (e: SecurityException) {
            e.printStackTrace() // Manejar si el permiso de alarma exacta es denegado en Android 14+
        }
    }

    private fun cancelAlarm(context: Context, reminder: MedicineReminderEntity) {
        AlarmPlayerManager.stopAlarm(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun takeMedicine(reminder: MedicineReminderEntity, context: Context) {
        cancelAlarm(context, reminder)
        // Multiplicamos la meta diaria por el total de días de tratamiento.
        val totalTargetDoses = reminder.targetDoses * reminder.durationDays
        if (reminder.completedDoses < totalTargetDoses) {
            val newCompleted = reminder.completedDoses + 1
            val goalAchieved = newCompleted >= totalTargetDoses
            
            val updatedReminder = reminder.copy(
                completedDoses = newCompleted,
                isGoalAchieved = goalAchieved,
                isRunning = false,
                targetAlarmTimeMillis = 0L
            )
            
            viewModelScope.launch(Dispatchers.IO) {
                dao.updateReminder(updatedReminder)
            }
        }
    }
}

class LoopTimerViewModelFactory(private val dao: MedicineReminderDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoopTimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoopTimerViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
