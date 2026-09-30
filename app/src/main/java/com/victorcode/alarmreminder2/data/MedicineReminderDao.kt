// inicio file: MedicineReminderDao.kt
package com.victorcode.alarmreminder2.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

// 📦 Versión 0.0.1
// 📄 Archivo: data/MedicineReminderDao.kt

/**
 * ✍️ Objeto de Acceso a Datos (DAO) para operaciones CRUD con Coroutines y Flow.
 */
@Dao
interface MedicineReminderDao {

    // ➕ CREATE: Inserta un nuevo recordatorio personalizado
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: MedicineReminderEntity): Long

    // 🔄 UPDATE: Actualiza el conteo de dosis tomadas o si cumplió la meta
    @Update
    suspend fun updateReminder(reminder: MedicineReminderEntity)

    // ❌ DELETE: Elimina un recordatorio si el usuario ya no lo necesita
    @Delete
    suspend fun deleteReminder(reminder: MedicineReminderEntity)

    // 🔍 READ: Obtiene todos los recordatorios activos ordenados por id
    @Query("SELECT * FROM medicine_reminders ORDER BY id DESC")
    fun getAllReminders(): Flow<List<MedicineReminderEntity>>

    // 🔍 READ: Obtiene los recordatorios de tratamientos que aún no han expirado y no se han cumplido
    @Query("SELECT * FROM medicine_reminders WHERE endDateMillis >= :currentTimeMillis AND isGoalAchieved = 0 ORDER BY id DESC")
    fun getActiveReminders(currentTimeMillis: Long): Flow<List<MedicineReminderEntity>>
}
// fin file: MedicineReminderDao.kt