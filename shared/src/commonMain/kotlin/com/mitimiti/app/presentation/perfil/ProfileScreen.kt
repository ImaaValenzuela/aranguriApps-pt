package com.mitimiti.app.presentation.perfil

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.claymorphic
import com.mitimiti.app.rememberImagePicker
import com.mitimiti.app.toImageBitmap

@Composable
@Suppress("FunctionNaming", "LongMethod")
fun ProfileScreen(
    userEmail: String?,
    avatarBytes: ByteArray?,
    onSaveProfile: (alias: String, cbu: String) -> Unit,
    onUploadAvatar: (ByteArray) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    val alias by AppSettings.alias.collectAsState()
    val cbu by AppSettings.cbu.collectAsState()

    var aliasInput by remember(alias) { mutableStateOf(alias) }
    var cbuInput by remember(cbu) { mutableStateOf(cbu) }
    var showSavedMessage by remember { mutableStateOf(false) }

    val isCbuValid =
        cbuInput.trim().isEmpty() ||
            (cbuInput.trim().length == 22 && cbuInput.trim().all { it.isDigit() })
    val cbuError =
        if (cbuInput.isNotEmpty() && cbuInput.length != 22) {
            "El CBU debe tener exactamente 22 números"
        } else {
            null
        }

    val imagePicker =
        rememberImagePicker { bytes ->
            onUploadAvatar(bytes)
        }

    val avatarBitmap =
        remember(avatarBytes) {
            avatarBytes?.let {
                try {
                    it.toImageBitmap()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

    val scrollState = rememberScrollState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(scrollState)
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
                    modifier = Modifier.size(72.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(72.dp)
                                .claymorphic(
                                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                    cornerRadius = 36.dp,
                                    elevation = 2.dp,
                                    isDark = isDark,
                                )
                                .clickable { imagePicker() },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (avatarBitmap != null) {
                            androidx.compose.foundation.Image(
                                bitmap = avatarBitmap,
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(36.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(36.dp),
                            )
                        }
                    }
                    // Edit pencil icon overlay
                    Box(
                        modifier =
                            Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                                .claymorphic(
                                    backgroundColor = MaterialTheme.colorScheme.primary,
                                    cornerRadius = 12.dp,
                                    elevation = 3.dp,
                                    isDark = isDark,
                                )
                                .clickable { imagePicker() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    val username by AppSettings.username.collectAsState()
                    if (username.isNotEmpty()) {
                        Text(
                            text = "@$username",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        Text(
                            text = "¡Hola!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                        )
                    }
                    Text(
                        text = userEmail ?: "Usuario Anónimo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val showCbuBanner = cbu.isEmpty()
        if (showCbuBanner) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .claymorphic(
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                            cornerRadius = 16.dp,
                            elevation = 2.dp,
                            isDark = isDark,
                        )
                        .padding(16.dp),
            ) {
                Text(
                    text = "💡 ¡Completá tu CBU/CVU para recibir transferencias de tus amigos más fácilmente!",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

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

                HorizontalDivider()

                val username by AppSettings.username.collectAsState()
                OutlinedTextField(
                    value = "@$username",
                    onValueChange = {},
                    label = { Text("Nombre de Usuario (Único)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                )

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
                    onValueChange = { input ->
                        if (input.length <= 22 && input.all { it.isDigit() }) {
                            cbuInput = input
                            showSavedMessage = false
                        }
                    },
                    label = { Text("CBU / CVU (22 dígitos)") },
                    placeholder = { Text("22 dígitos de tu cuenta") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = cbuError != null,
                    supportingText = cbuError?.let { { Text(it) } },
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
                    enabled = (aliasInput != alias || cbuInput != cbu) && isCbuValid,
                ) {
                    Text("Guardar Datos", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
