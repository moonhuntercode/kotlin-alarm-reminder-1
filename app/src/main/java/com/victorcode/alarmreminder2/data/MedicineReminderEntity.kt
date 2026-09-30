// inicio file: MedicineReminderEntity.kt
package com.victorcode.alarmreminder2.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

// 📦 Versión 0.0.1
// 📄 Archivo: data/MedicineReminderEntity.kt

/**
 * 💊 Tabla SQLite para registrar cualquier medicina configurable.
 *
 * @property id Identificador único autogenerado.
 * @property medicineName Nombre del medicamento (ej: "Paracetamol", "Ibuprofeno").
 * @property intervalMinutes Intervalo de tiempo elegido para el recordatorio (ej: 15, 60 min).
 * @property targetDoses Meta de tomas en el día (ej: 3 o 4 veces).
 * @property completedDoses Cantidad de veces que ya tomó el medicamento hoy.
 * @property dateString Fecha del registro (ej: "2026-09-29") para reiniciar al día siguiente.
 * @property isGoalAchieved Indica si ya cumplió todas las tomas programadas del día.
 */
@Entity(tableName = "medicine_reminders")
data class MedicineReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val medicineName: String,
    val intervalMinutes: Int,
    val targetDoses: Int,
    val completedDoses: Int = 0,
    val durationDays: Int = 1,
    val endDateMillis: Long,
    val isGoalAchieved: Boolean = false,
    val ringtoneUri: String? = null,
    val isRunning: Boolean = false,
    val targetAlarmTimeMillis: Long = 0L
)
// fin file: MedicineReminderEntity.kt