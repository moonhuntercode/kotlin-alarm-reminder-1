// inicio file: MainActivity.kt
package com.victorcode.alarmreminder2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.victorcode.alarmreminder2.data.UserEntity
import com.victorcode.alarmreminder2.screens.AdminDashboardScreen
import com.victorcode.alarmreminder2.screens.LoginScreen
import com.victorcode.alarmreminder2.screens.LoopTimerScreen
import com.victorcode.alarmreminder2.ui.theme.AlarmReminder2Theme

// 📦 Versión 0.0.1
// 📄 Archivo: MainActivity.kt

enum class AppScreen {
    LOGIN,
    TIMER,
    ADMIN_PANEL
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlarmReminder2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }
                    var currentUser by remember { mutableStateOf<UserEntity?>(null) }

                    // 🚪 Función reutilizable para cerrar sesión limpiamente
                    val logoutAction = {
                        currentUser = null
                        currentScreen = AppScreen.LOGIN
                    }

                    // ⏰ Verificación activa de sesión
                    LaunchedEffect(currentUser, currentScreen) {
                        currentUser?.let { user ->
                            val currentTime = System.currentTimeMillis()
                            val expirationTime = user.lastLoginTimestamp + user.sessionDurationMillis
                            if (currentTime > expirationTime && user.lastLoginTimestamp != 0L) {
                                logoutAction()
                            }
                        }
                    }

                    when (currentScreen) {
                        AppScreen.LOGIN -> {
                            LoginScreen(
                                onLoginSuccess = { user ->
                                    currentUser = user
                                    currentScreen = AppScreen.TIMER
                                }
                            )
                        }

                        AppScreen.TIMER -> {
                            LoopTimerScreen(
                                currentUser = currentUser,
                                onNavigateToAdmin = {
                                    currentScreen = AppScreen.ADMIN_PANEL
                                },
                                onLogout = logoutAction // 🚪 Pasa el logout aquí
                            )
                        }

                        AppScreen.ADMIN_PANEL -> {
                            AdminDashboardScreen(
                                onNavigateBack = {
                                    currentScreen = AppScreen.TIMER
                                },
                                onLogout = logoutAction // 🚪 Pasa el logout aquí también
                            )
                        }
                    }
                }
            }
        }
    }
}
// fin file: MainActivity.kt