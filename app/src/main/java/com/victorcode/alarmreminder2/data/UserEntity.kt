// inicio file: UserEntity.kt
package com.victorcode.alarmreminder2.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

// 📦 Versión 0.0.1
// 📄 Archivo: data/UserEntity.kt

enum class UserRole {
    SUPERADMIN,
    USER
}

/**
 * 👤 Entidad de usuario con control de sesión por tiempo de dispositivo.
 *
 * @property sessionDurationMillis Duración máxima de la sesión en ms (por defecto: 1 día = 86_400_000L).
 * @property lastLoginTimestamp Momento exacto del último inicio de sesión (0L si nunca inició o cerró sesión).
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val username: String,
    val pin: String,
    val role: UserRole,
    val isEnabled: Boolean = true,
    val sessionDurationMillis: Long = 86_400_000L, // ⏳ 1 día por defecto
    val lastLoginTimestamp: Long = 0L              // ⏱️ Timestamp del móvil al loguearse
)
// fin file: UserEntity.kt