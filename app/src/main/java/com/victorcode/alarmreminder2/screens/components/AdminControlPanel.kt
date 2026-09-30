// inicio file: AdminControlPanel.kt
package com.victorcode.alarmreminder2.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// 📦 Versión 0.0.1
// 📄 Archivo: screens/components/AdminControlPanel.kt

/**
 * 🛡️ Panel visible solo para Superadmin para habilitar/deshabilitar a Noelia.
 */
@Composable
fun AdminControlPanel(
    isNoeliaEnabled: Boolean,
    onToggleAccess: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isNoeliaEnabled) "🔓 Acceso a Noelia: PERMITIDO" else "🔒 Acceso a Noelia: BLOQUEADO",
                style = MaterialTheme.typography.bodyLarge
            )

            Switch(
                checked = isNoeliaEnabled,
                onCheckedChange = onToggleAccess
            )
        }
    }
}
// fin file: AdminControlPanel.kt