package com.victorcode.alarmreminder2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.victorcode.alarmreminder2.data.AppDatabase
import com.victorcode.alarmreminder2.data.UserEntity
import com.victorcode.alarmreminder2.data.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 📦 Versión 1.1.0
// 📄 Archivo: screens/AdminDashboardScreen.kt

@Composable
fun AdminDashboardScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).userDao() }
    val scope = rememberCoroutineScope()

    var users by remember { mutableStateOf<List<UserEntity>>(emptyList()) }
    var showCreateDialog by remember { mutableStateOf(false) }

    // Cargar usuarios
    fun loadUsers() {
        scope.launch(Dispatchers.IO) {
            val list = dao.getAllUsers()
            withContext(Dispatchers.Main) { users = list }
        }
    }

    LaunchedEffect(Unit) { loadUsers() }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onNavigateBack) { Text("⬅️ Volver") }
            TextButton(onClick = onLogout) { Text("🚪 Salir", color = Color.Red) }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("⚙️ Panel de Usuarios", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showCreateDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("➕ Crear Nuevo Usuario")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(users) { user ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "👤 ${user.username}", style = MaterialTheme.typography.titleMedium)
                            Text(text = "Rol: ${user.role} | ${if(user.isEnabled) "Activo" else "Suspendido"}", style = MaterialTheme.typography.bodyMedium)
                        }
                        
                        if (user.username != "superadmin") {
                            TextButton(onClick = {
                                scope.launch(Dispatchers.IO) {
                                    dao.deleteUser(user)
                                    loadUsers()
                                }
                            }) {
                                Text("🗑️", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        var newUsername by remember { mutableStateOf("") }
        var newPin by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Nuevo Usuario") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newUsername, 
                        onValueChange = { newUsername = it.lowercase().trim() }, 
                        label = { Text("Nombre de Usuario") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPin, 
                        onValueChange = { if(it.length <= 4) newPin = it }, 
                        label = { Text("PIN (4 dígitos)") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newUsername.isNotBlank() && newPin.length == 4) {
                        scope.launch(Dispatchers.IO) {
                            dao.insertUser(UserEntity(username = newUsername, pin = newPin, role = UserRole.USER))
                            loadUsers()
                            showCreateDialog = false
                        }
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancelar") }
            }
        )
    }
}