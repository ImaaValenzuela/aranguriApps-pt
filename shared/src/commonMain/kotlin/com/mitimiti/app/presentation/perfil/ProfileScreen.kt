package com.mitimiti.app.presentation.perfil

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.claymorphic

@Composable
@Suppress("FunctionNaming", "LongMethod")
fun ProfileScreen(
    userEmail: String?,
    onSaveProfile: (alias: String, cbu: String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    val alias by AppSettings.alias.collectAsState()
    val cbu by AppSettings.cbu.collectAsState()

    var aliasInput by remember(alias) { mutableStateOf(alias) }
    var cbuInput by remember(cbu) { mutableStateOf(cbu) }
    var showSavedMessage by remember { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Mi Perfil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // Avatar Card
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .claymorphic(
                        backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                        cornerRadius = 24.dp,
                        elevation = 4.dp,
                        isDark = isDark,
                    )
                    .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(60.dp)
                            .claymorphic(
                                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                cornerRadius = 30.dp,
                                elevation = 2.dp,
                                isDark = isDark,
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp),
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "¡Hola!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        text = userEmail ?: "Usuario Anónimo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Payment Info Card
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .claymorphic(
                        backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                        cornerRadius = 24.dp,
                        elevation = 4.dp,
                        isDark = isDark,
                    )
                    .padding(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Datos de Transferencia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Text(
                    text =
                        "Configurá tus datos predeterminados. Al cerrar una vaquita, " +
                            "se incluirán automáticamente en el resumen copiado.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                )

                Divider()

                OutlinedTextField(
                    value = aliasInput,
                    onValueChange = {
                        aliasInput = it
                        showSavedMessage = false
                    },
                    label = { Text("Alias de MercadoPago / Banco") },
                    placeholder = { Text("Ej: mate.mitimiti.app") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                )

                OutlinedTextField(
                    value = cbuInput,
                    onValueChange = {
                        cbuInput = it
                        showSavedMessage = false
                    },
                    label = { Text("CBU / CVU") },
                    placeholder = { Text("22 dígitos de tu cuenta") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                )

                if (showSavedMessage) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Datos guardados correctamente",
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                ClayButton(
                    onClick = {
                        onSaveProfile(aliasInput, cbuInput)
                        showSavedMessage = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = aliasInput.isNotBlank() || cbuInput.isNotBlank(),
                ) {
                    Text("Guardar Datos", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        ClayButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
