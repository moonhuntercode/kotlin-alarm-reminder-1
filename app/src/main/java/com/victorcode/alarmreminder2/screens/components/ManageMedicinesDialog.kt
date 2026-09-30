package com.victorcode.alarmreminder2.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.victorcode.alarmreminder2.data.MedicineReminderEntity

// 📦 Versión 0.0.4
// 📄 Archivo: screens/components/ManageMedicinesDialog.kt

@Suppress("FunctionName")
@Composable
fun ManageMedicinesDialog(
    reminders: List<MedicineReminderEntity>,
    activeReminderId: Int?,
    onSelectActive: (Int) -> Unit,
    onDelete: (MedicineReminderEntity) -> Unit,
    onDismiss: () -> Unit
) {
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
                    text = "📋 Mis Medicinas (Hoy)",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (reminders.isEmpty()) {
                    Text("No hay medicinas registradas.", color = Color.Gray)
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(reminders) { reminder ->
                            val isActive = reminder.id == activeReminderId
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = reminder.medicineName,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else Color.Unspecified
                                        )
                                        Text(
                                            text = "${reminder.intervalMinutes} min | Meta: ${reminder.targetDoses}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    Row {
                                        if (!isActive) {
                                            TextButton(onClick = { onSelectActive(reminder.id) }) {
                                                Text("✅ Activar", color = Color(0xFF2E7D32))
                                            }
                                        }
                                        TextButton(onClick = { onDelete(reminder) }) {
                                            Text("🗑️ Borrar", color = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }
        }
    }
}
