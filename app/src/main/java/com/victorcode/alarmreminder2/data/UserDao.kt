// inicio file: UserDao.kt
package com.victorcode.alarmreminder2.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

// 📦 Versión 0.0.1
// 📄 Archivo: data/UserDao.kt

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE pin = :enteredPin LIMIT 1")
    suspend fun getUserByPin(enteredPin: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = 'noelia' LIMIT 1")
    fun getNoeliaUser(): Flow<UserEntity?>

    @Query("UPDATE users SET isEnabled = :enabled WHERE username = :username")
    suspend fun setUserStatus(username: String, enabled: Boolean)

    @Query("UPDATE users SET pin = :newPin WHERE username = :username")
    suspend fun updateUserPin(username: String, newPin: String)

    // ⏱️ Actualiza la marca de tiempo al iniciar sesión con éxito
    @Query("UPDATE users SET lastLoginTimestamp = :timestamp WHERE username = :username")
    suspend fun updateLastLogin(username: String, timestamp: Long)

    // ⚙️ Cambia la duración de la sesión permitida (1 día, 1 semana, etc.)
    @Query("UPDATE users SET sessionDurationMillis = :durationMillis WHERE username = :username")
    suspend fun updateSessionDuration(username: String, durationMillis: Long)
}
// fin file: UserDao.kt