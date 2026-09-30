// inicio file: AppDatabase.kt
package com.victorcode.alarmreminder2.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// 📦 Versión 0.0.1
// 📄 Archivo: data/AppDatabase.kt

@Database(
    entities = [MedicineReminderEntity::class, UserEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicineReminderDao(): MedicineReminderDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alarm_reminder_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()

                INSTANCE = instance

                // 🚀 Inserción segura con CoroutineScope: se asegura de que existan siempre
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = instance.userDao()

                    // 👑 Superadmin: PIN "0666"
                    dao.insertUser(
                        UserEntity(
                            username = "superadmin",
                            pin = "0666",
                            role = UserRole.SUPERADMIN,
                            isEnabled = true,
                            sessionDurationMillis = 86_400_000L, // 1 día
                            lastLoginTimestamp = 0L
                        )
                    )

                    // 👩‍💼 Noelia: PIN "0000"
                    dao.insertUser(
                        UserEntity(
                            username = "noelia",
                            pin = "0000",
                            role = UserRole.USER,
                            isEnabled = true,
                            sessionDurationMillis = 86_400_000L, // 1 día
                            lastLoginTimestamp = 0L
                        )
                    )
                }

                instance
            }
        }
    }
}
// fin file: AppDatabase.kt