// inicio file: TimerDisplay.kt
package com.victorcode.alarmreminder2.screens.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

// 📦 Versión 0.0.1
// 📄 Archivo: screens/components/TimerDisplay.kt

/**
 * ⏱️ Componente atómico encargado únicamente de formatear y mostrar el tiempo.
 */
@Composable
fun TimerDisplay(secondsLeft: Int) {
    val hours = secondsLeft / 3600
    val minutes = (secondsLeft % 3600) / 60
    val seconds = secondsLeft % 60

    val displayTime = if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }

    Text(
        text = displayTime,
        style = MaterialTheme.typography.displayLarge
    )
}
// fin file: TimerDisplay.kt