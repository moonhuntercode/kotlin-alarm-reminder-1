package com.victorcode.alarmreminder2.screens

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.content.Context
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.victorcode.alarmreminder2.data.AppDatabase
import com.victorcode.alarmreminder2.data.MedicineReminderEntity
import com.victorcode.alarmreminder2.data.UserEntity
import com.victorcode.alarmreminder2.data.UserRole
import com.victorcode.alarmreminder2.screens.components.AddMedicineDialog
import com.victorcode.alarmreminder2.screens.components.TimerDisplay

// 📦 Versión 1.0.0
// 📄 Archivo: screens/LoopTimerScreen.kt

@Suppress("FunctionName", "DEPRECATION")
@Composable
fun LoopTimerScreen(
    currentUser: UserEntity? = null,
    onNavigateToAdmin: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).medicineReminderDao() }
    val factory = remember { LoopTimerViewModelFactory(dao) }
    val viewModel: LoopTimerViewModel = remember {
        ViewModelProvider(context as ViewModelStoreOwner, factory)[LoopTimerViewModel::class.java]
    }
    
    val allReminders by viewModel.allReminders.collectAsState()
    val currentTime by viewModel.currentTimeMillis.collectAsState()

    var showAddMedicineDialog by remember { mutableStateOf(false) }
    var reminderToUpdateRingtone by remember { mutableStateOf<MedicineReminderEntity?>(null) }

    val ringtonePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if (uri != null && reminderToUpdateRingtone != null) {
                viewModel.setRingtone(reminderToUpdateRingtone!!, uri.toString())
            }
        }
        reminderToUpdateRingtone = null
    }

    val alarmingReminders = allReminders.filter { 
        it.isRunning && (it.targetAlarmTimeMillis - currentTime) <= 0 
    }

    if (alarmingReminders.isNotEmpty()) {
        val ringingReminder = alarmingReminders.first()
        AlertDialog(
            onDismissRequest = { /* Require explicit action */ },
            title = { Text("🚨 ¡Alarma Sonando!") },
            text = { Text("Es hora de tomar tu medicamento:\n\n💊 ${ringingReminder.medicineName}\n\nSelecciona una acción para detener la alarma.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.takeMedicine(ringingReminder, context) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Tomar Dosis")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.toggleTimer(ringingReminder, context) }
                ) {
                    Text("Posponer / Detener")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔝 BARRA SUPERIOR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "👤 Hola, ${currentUser?.username ?: "Invitado"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Row {
                if (currentUser?.role == UserRole.SUPERADMIN) {
                    TextButton(onClick = onNavigateToAdmin) {
                        Text("⚙️ Panel", textDecoration = TextDecoration.Underline, color = MaterialTheme.colorScheme.primary)
                    }
                }
                TextButton(onClick = onLogout) {
                    Text(text = "🚪 Salir", color = Color(0xFFD32F2F))
                }
            }
        }

        Text(text = "⏰ Control de Medicamentos", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showAddMedicineDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("📝 Añadir Nueva Medicina")
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (allReminders.isEmpty()) {
            Text("No hay medicinas programadas para hoy.", color = Color.Gray)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allReminders) { reminder ->
                    MedicineCard(
                        reminder = reminder,
                        currentTimeMillis = currentTime,
                        onToggle = { viewModel.toggleTimer(reminder, context) },
                        onTakeDose = { viewModel.takeMedicine(reminder, context) },
                        onDelete = { viewModel.deleteMedicine(reminder, context) },
                        onChangeRingtone = {
                            reminderToUpdateRingtone = reminder
                            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM or RingtoneManager.TYPE_NOTIFICATION)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                            }
                            ringtonePickerLauncher.launch(intent)
                        }
                    )
                }
            }
        }
    }

    if (showAddMedicineDialog) {
        AddMedicineDialog(
            onSave = { name, interval, targetDoses, durationDays ->
                viewModel.saveNewMedicine(name, interval, targetDoses, durationDays)
                showAddMedicineDialog = false
            },
            onDismiss = { showAddMedicineDialog = false }
        )
    }
}

@Suppress("FunctionName")
@Composable
fun MedicineCard(
    reminder: MedicineReminderEntity,
    currentTimeMillis: Long,
    onToggle: () -> Unit,
    onTakeDose: () -> Unit,
    onDelete: () -> Unit,
    onChangeRingtone: () -> Unit
) {
    val goalReached = reminder.completedDoses >= reminder.targetDoses
    var secondsLeft = 0
    var isAlarming = false

    if (reminder.isRunning) {
        val diff = (reminder.targetAlarmTimeMillis - currentTimeMillis) / 1000
        if (diff > 0) {
            secondsLeft = diff.toInt()
        } else {
            secondsLeft = 0
            isAlarming = true
        }
    } else {
        secondsLeft = reminder.intervalMinutes * 60
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isAlarming) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "💊 ${reminder.medicineName}", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = if (goalReached) "🎉 Objetivo del día cumplido" else "Progreso: ${reminder.completedDoses}/${reminder.targetDoses} tomas",
                        color = if (goalReached) Color(0xFF2E7D32) else Color.DarkGray
                    )
                }
                
                // Ringtone / Delete
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        TextButton(onClick = onChangeRingtone) { Text("🎵 Cambiar Tono") }
                        Text(
                            text = getRingtoneName(LocalContext.current, reminder.ringtoneUri),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                    TextButton(onClick = onDelete) { Text("🗑️") }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!goalReached) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isAlarming) {
                        Text("🚨 ¡HORA DE TOMAR!", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium)
                        Button(onClick = onTakeDose, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                            Text("Tomar Dosis")
                        }
                    } else {
                        TimerDisplay(secondsLeft = secondsLeft)
                        Button(
                            onClick = onToggle,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (reminder.isRunning) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(if (reminder.isRunning) "⏹ Detener" else "▶️ Iniciar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getRingtoneName(context: Context, uriString: String?): String {
    return remember(uriString) {
        if (uriString == null) "Tono por defecto"
        else {
            try {
                val uri = Uri.parse(uriString)
                val ringtone = RingtoneManager.getRingtone(context, uri)
                ringtone?.getTitle(context) ?: "Tono personalizado"
            } catch (e: Exception) {
                "Tono personalizado"
            }
        }
    }
}