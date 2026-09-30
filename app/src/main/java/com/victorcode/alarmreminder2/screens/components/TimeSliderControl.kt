// inicio file: TimeSliderControl.kt
package com.victorcode.alarmreminder2.screens.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// 📦 Versión 0.0.1
// 📄 Archivo: screens/components/TimeSliderControl.kt

/**
 * 🎚️ Control deslizante para configurar la duración del ciclo (1 a 60 min).
 */
@Composable
fun TimeSliderControl(
    selectedMinutes: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = "🎯 Duración objetivo: ${selectedMinutes.toInt()} min",
        style = MaterialTheme.typography.bodyLarge
    )

    Slider(
        value = selectedMinutes,
        onValueChange = onValueChange,
        valueRange = 1f..60f,
        steps = 58,
        modifier = modifier.fillMaxWidth(0.85f)
    )
}
// fin file: TimeSliderControl.kt