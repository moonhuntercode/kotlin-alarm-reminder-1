// inicio file: LoginScreen.kt
package com.victorcode.alarmreminder2.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.victorcode.alarmreminder2.data.AppDatabase
import com.victorcode.alarmreminder2.data.UserEntity
import kotlinx.coroutines.launch

// 📦 Versión 0.0.1
// 📄 Archivo: screens/LoginScreen.kt

/**
 * 🔐 Pantalla de autenticación por PIN con consulta a Room.
 *
 * @param onLoginSuccess Callback que envía el usuario autenticado a la actividad principal.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: (UserEntity) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userDao = remember { AppDatabase.getDatabase(context).userDao() }

    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔐 Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ingresa tu PIN de 4 dígitos",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔢 Campo numérico oculto tipo contraseña
        OutlinedTextField(
            value = enteredPin,
            onValueChange = { if (it.length <= 4) enteredPin = it },
            label = { Text("PIN") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ⚠️ Mensaje de error condicional si está bloqueado o PIN incorrecto
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 🚀 Botón de ingreso
        Button(
            onClick = {
                errorMessage = null
                // fragmento en LoginScreen.kt (dentro del onClick del botón Entrar):
                coroutineScope.launch {
                    val user = userDao.getUserByPin(enteredPin)
                    if (user == null) {
                        errorMessage = "❌ PIN incorrecto"
                    } else if (!user.isEnabled) {
                        errorMessage = "🔒 Acceso suspendido por Superadmin"
                    } else {
                        val now = System.currentTimeMillis()
                        userDao.updateLastLogin(user.username, now) // ⏱️ Guardamos el momento exacto
                        onLoginSuccess(user.copy(lastLoginTimestamp = now))
                    }
                }
            },
            enabled = enteredPin.length == 4,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(text = "Entrar")
        }
    }
}
// fin file: LoginScreen.kt