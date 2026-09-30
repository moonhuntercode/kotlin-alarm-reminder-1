package com.victorcode.alarmreminder2.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.victorcode.alarmreminder2.MainActivity
import com.victorcode.alarmreminder2.utils.AlarmPlayerManager

// 📦 Versión 1.0.0
// 📄 Archivo: receiver/AlarmReceiver.kt

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra("MEDICINE_NAME") ?: "Medicina"
        val ringtoneUri = intent.getStringExtra("RINGTONE_URI")
        
        // 1. Iniciar el sonido de la alarma (Servicio local)
        AlarmPlayerManager.playAlarm(context, ringtoneUri)
        
        // 2. Disparar Notificación de Alta Prioridad en el Sistema (Foreground/Background)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "ALARM_CHANNEL_ID"

        // Crear canal de notificación (Requerido desde Android 8.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarmas de Medicamentos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones críticas cuando es hora de tomar medicina"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la app al tocar la notificación
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir la notificación
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder) // Ícono por defecto
            .setContentTitle("🚨 ¡Hora de tu Medicina!")
            .setContentText("Toca aquí para detener la alarma de: $medicineName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(true) // No se puede borrar deslizando, obliga al usuario a entrar
            .build()

        notificationManager.notify(medicineName.hashCode(), notification)
    }
}
