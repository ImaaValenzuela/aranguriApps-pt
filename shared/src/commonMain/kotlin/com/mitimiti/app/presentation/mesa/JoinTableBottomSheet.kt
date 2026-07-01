package com.mitimiti.app.presentation.mesa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.claymorphic
import com.mitimiti.app.rememberQRScanner

@Composable
@Suppress("LongMethod", "FunctionNaming")
fun JoinTableBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    onSwitchToCreate: () -> Unit,
    viewModel: TableViewModel,
    onNavigateToLobby: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    val formState by viewModel.joinFormState.collectAsState()

    val qrScanner =
        rememberQRScanner { result ->
            val parsedCode = viewModel.parseQRCodeResult(result)
            viewModel.updateJoinForm(joinCodeInput = parsedCode)
        }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onDismissRequest() },
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 12.dp)
                        .claymorphic(
                            backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                            cornerRadius = 28.dp,
                            elevation = 10.dp,
                            isDark = isDark,
                        )
                        .clickable(enabled = false) {},
            ) {
                Column(
                    modifier =
                        Modifier
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState())
                            .imePadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Sumarse a Juntada",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        IconButtonWrapper(
                            onClick = onDismissRequest,
                            isDark = isDark,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }

                    // Toggle mode tabs in BottomSheet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .clickable { onSwitchToCreate() }
                                    .claymorphic(
                                        backgroundColor =
                                            if (isDark) {
                                                MaterialTheme.colorScheme.surface
                                            } else {
                                                Color.White
                                            },
                                        cornerRadius = 14.dp,
                                        elevation = 1.dp,
                                        isDark = isDark,
                                    )
                                    .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Nueva Juntada",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }

                        Box(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .claymorphic(
                                        backgroundColor = MaterialTheme.colorScheme.primary,
                                        cornerRadius = 14.dp,
                                        elevation = 4.dp,
                                        isDark = isDark,
                                    )
                                    .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Unirse a Código",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                            )
                        }
                    }

                    Divider()

                    // Join Table Form
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            OutlinedTextField(
                                value = formState.joinCodeInput,
                                onValueChange = { viewModel.updateJoinForm(joinCodeInput = it) },
                                label = { Text("Código de la Juntada") },
                                placeholder = { Text("Ej: 492041") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                            )

                            ClayButton(
                                onClick = {
                                    qrScanner()
                                },
                                modifier = Modifier.height(56.dp),
                                cornerRadius = 16.dp,
                                backgroundColor = MaterialTheme.colorScheme.secondary,
                                contentColor = Color.Black,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Escanear código QR",
                                    tint = Color.Black,
                                )
                            }
                        }

                        OutlinedTextField(
                            value = formState.joinNicknameInput,
                            onValueChange = { viewModel.updateJoinForm(joinNicknameInput = it) },
                            label = { Text("Tu apodo / nombre") },
                            placeholder = { Text("Ej: Maria") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        ClayButton(
                            onClick = {
                                viewModel.joinTable(
                                    code = formState.joinCodeInput,
                                    nickname = formState.joinNicknameInput,
                                    onSuccess = {
                                        viewModel.clearJoinForm()
                                        onNavigateToLobby(it)
                                    },
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = formState.joinCodeInput.length >= 5 && formState.joinNicknameInput.isNotEmpty(),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text("Unirme a la Juntada", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
