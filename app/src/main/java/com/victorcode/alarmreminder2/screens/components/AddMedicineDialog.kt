package com.victorcode.alarmreminder2.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

// 📦 Versión 0.0.2
// 📄 Archivo: screens/components/AddMedicineDialog.kt

@Suppress("FunctionName")
@Composable
fun AddMedicineDialog(
    onSave: (name: String, intervalMinutes: Int, targetDoses: Int, durationDays: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var medicineName by remember { mutableStateOf("") }
    var intervalMinutes by remember { mutableFloatStateOf(15f) }
    var targetDoses by remember { mutableFloatStateOf(4f) }
    var durationDays by remember { mutableFloatStateOf(3f) } // Default 3 días (ej. Embarazadas o tratamientos cortos)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📝 Nueva Medicina",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = medicineName,
                    onValueChange = { medicineName = it },
                    label = { Text("Nombre del Medicamento") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Intervalo: ${intervalMinutes.toInt()} min")
                Slider(
                    value = intervalMinutes,
                    onValueChange = { intervalMinutes = it },
                    valueRange = 1f..120f,
                    steps = 119
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Meta diaria: ${targetDoses.toInt()} tomas")
                Slider(
                    value = targetDoses,
                    onValueChange = { targetDoses = it },
                    valueRange = 1f..10f,
                    steps = 9
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Duración del Tratamiento: ${durationDays.toInt()} días")
                Slider(
                    value = durationDays,
                    onValueChange = { durationDays = it },
                    valueRange = 1f..30f,
                    steps = 29
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (medicineName.isNotBlank()) {
                            onSave(medicineName, intervalMinutes.toInt(), targetDoses.toInt(), durationDays.toInt())
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = medicineName.isNotBlank()
                ) {
                    Text("💾 Guardar y Empezar")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        }
    }
}
