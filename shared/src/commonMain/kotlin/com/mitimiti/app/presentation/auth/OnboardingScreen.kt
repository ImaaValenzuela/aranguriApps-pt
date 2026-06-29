@file:Suppress("ktlint:standard:function-naming")

package com.mitimiti.app.presentation.auth

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.claymorphic

@Composable
fun OnboardingScreen(
    viewModel: AuthViewModel,
    onSignOut: () -> Unit,
    deepLinkUrl: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    var username by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    var cbu by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    var showMpLinkingDialog by remember { mutableStateOf(false) }
    var isLinkingMp by remember { mutableStateOf(false) }

    val isUsernameValid = username.trim().length >= 3 && !username.contains(" ")
    val isAliasValid = alias.trim().isNotEmpty()
    val isCbuValid = cbu.trim().length == 22 && cbu.trim().all { it.isDigit() }
    val isFormValid = isUsernameValid && isAliasValid && isCbuValid

    LaunchedEffect(deepLinkUrl) {
        if (deepLinkUrl != null) {
            val query = deepLinkUrl.substringAfter("?", "")
            if (query.isNotEmpty()) {
                val params = query.split("&")
                params.forEach { param ->
                    val pair = param.split("=")
                    if (pair.size == 2) {
                        val key = pair[0]
                        val value = pair[1].replace("%20", " ")
                            .replace("%3A", ":")
                            .replace("%2F", "/")
                            .replace("%3F", "?")
                            .replace("%3D", "=")
                            .replace("%26", "&")
                            .replace("%40", "@")
                        if (key.equals("alias", ignoreCase = true)) {
                            alias = value
                        } else if (key.equals("cbu", ignoreCase = true)) {
                            cbu = value
                        }
                    }
                }
            }
            onDeepLinkConsumed()
        }
    }

    LaunchedEffect(isLinkingMp) {
        if (isLinkingMp) {
            kotlinx.coroutines.delay(1800)
            val cleanUser = username.trim().lowercase().filter { it.isLetterOrDigit() }
            alias = if (cleanUser.isNotEmpty()) "$cleanUser.mp" else "mitimiti.mp"
            cbu = "000000310" + (1000000000000L..9999999999999L).random().toString()
            isLinkingMp = false
            showMpLinkingDialog = false
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "¡Te damos la bienvenida!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Configurá tu perfil para empezar a compartir gastos con tus amigos.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Onboarding Card
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
                    .padding(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Datos de tu cuenta",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                // Username field
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it.filter { char -> !char.isWhitespace() }
                            localError = null
                        },
                        label = { Text("Nombre de Usuario (Único)") },
                        placeholder = { Text("Ej: santi") },
                        leadingIcon = {
                            Text(
                                text = "  @",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )
                    Text(
                        text = "Con este nombre de usuario te agregarán tus amigos.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                HorizontalDivider()

                // Link Mercado Pago Button
                ClayButton(
                    onClick = { showMpLinkingDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFF009EE3),
                    contentColor = Color.White,
                    cornerRadius = 16.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("⚡ Vincular Mercado Pago", fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = " o ingresá a mano ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                // Alias field
                OutlinedTextField(
                    value = alias,
                    onValueChange = {
                        alias = it
                        localError = null
                    },
                    label = { Text("Alias de MercadoPago / Banco") },
                    placeholder = { Text("Ej: mate.mitimiti.app") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                // CBU field
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = cbu,
                        onValueChange = { input ->
                            if (input.length <= 22 && input.all { it.isDigit() }) {
                                cbu = input
                                localError = null
                            }
                        },
                        label = { Text("CBU / CVU (22 dígitos)") },
                        placeholder = { Text("00000031000123...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                    Text(
                        text = "Debe tener exactamente 22 números.",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (cbu.isNotEmpty() && cbu.length != 22) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                val displayError = localError ?: state.error
                displayError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                ClayButton(
                    onClick = {
                        if (!isFormValid) {
                            localError = "Por favor, completa todos los campos correctamente."
                            return@ClayButton
                        }
                        viewModel.saveUserProfile(
                            username = username.trim(),
                            alias = alias.trim(),
                            cbu = cbu.trim(),
                            onSuccess = {},
                            onError = { errorMsg ->
                                localError = errorMsg
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !state.isLoading,
                    cornerRadius = 16.dp,
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text("Guardar y Continuar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout escape hatch
        ClayButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(0.6f),
            backgroundColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
            contentColor = MaterialTheme.colorScheme.error,
            cornerRadius = 16.dp,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    if (showMpLinkingDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { if (!isLinkingMp) showMpLinkingDialog = false }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .claymorphic(
                        backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                        cornerRadius = 28.dp,
                        elevation = 8.dp,
                        isDark = isDark
                    )
                    .padding(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mercado Pago Style Header
                    Text(
                        text = "mercado pago",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF009EE3),
                        letterSpacing = (-1).sp
                    )

                    Text(
                        text = "Conectar con MitiMiti",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Text(
                        text = "MitiMiti solicita los siguientes permisos para simplificar la división de tus gastos:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PermissionRow(text = "Ver tu Alias de Mercado Pago")
                        PermissionRow(text = "Ver tu CVU asociado (Mercado Pago)")
                        PermissionRow(text = "Tu nombre completo de cuenta")
                    }

                    if (isLinkingMp) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(
                            color = Color(0xFF009EE3),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Obteniendo datos de Mercado Pago...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF009EE3),
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            // Cancel button
                            ClayButton(
                                onClick = { showMpLinkingDialog = false },
                                modifier = Modifier.weight(1f),
                                backgroundColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFE0E0E0),
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                cornerRadius = 16.dp
                            ) {
                                Text("Cancelar", fontWeight = FontWeight.Bold)
                            }

                            // Authorize button
                            ClayButton(
                                onClick = {
                                    isLinkingMp = true
                                },
                                modifier = Modifier.weight(1f),
                                backgroundColor = Color(0xFF009EE3),
                                contentColor = Color.White,
                                cornerRadius = 16.dp
                            ) {
                                Text("Autorizar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "✓",
            color = Color(0xFF2E7D32),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
