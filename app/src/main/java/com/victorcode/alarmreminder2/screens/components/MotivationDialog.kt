// inicio file: MotivationDialog.kt
package com.victorcode.alarmreminder2.screens.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

// 📦 Versión 0.0.1
// 📄 Archivo: screens/components/MotivationDialog.kt

/**
 * 💬 Diálogo emergente que anima al usuario tras cumplir y pausar la alarma.
 */
@Composable
fun MotivationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "👏 ¡Dosis / Intervalo Registrado!") },
        text = { Text(text = "¡Excelente disciplina! ¿Comenzamos con la siguiente cuenta regresiva?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = "🔥 ¡Empezamos!")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(text = "🛑 Detener por hoy")
            }
        }
    )
}
// fin file: MotivationDialog.kt