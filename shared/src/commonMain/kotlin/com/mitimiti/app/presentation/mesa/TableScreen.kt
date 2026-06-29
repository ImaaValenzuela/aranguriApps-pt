package com.mitimiti.app.presentation.mesa

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.presentation.components.QRCodeView
import com.mitimiti.app.presentation.perfil.AppSettings
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.claymorphic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongMethod", "FunctionNaming")
fun TableScreen(
    tableId: String,
    viewModel: TableViewModel,
    onNavigateToExpenses: (String) -> Unit,
    onNavigateToSummary: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val isDark = isSystemInDarkTheme()

    val frequentFriends by AppSettings.frequentFriends.collectAsState()
    var friendNameInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var copyMessageSuccess by remember { mutableStateOf(false) }
    var showQrBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(tableId) {
        viewModel.startObservingTable(tableId)
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                // Claymorphic Juntada Detail Card
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .claymorphic(
                                backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                                cornerRadius = 24.dp,
                                elevation = 4.dp,
                                isDark = isDark,
                            ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Juntada: ${state.tableName}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector =
                                    if (state.type == TableType.RESTAURANT) {
                                        Icons.Default.ShoppingCart
                                    } else {
                                        Icons.Default.Home
                                    },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (state.type == TableType.RESTAURANT) "Restaurante" else "Asado / Casa",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            )
                        }

                        if (state.isClosed) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier =
                                    Modifier
                                        .claymorphic(
                                            backgroundColor = MaterialTheme.colorScheme.errorContainer,
                                            cornerRadius = 12.dp,
                                            elevation = 2.dp,
                                            isDark = isDark,
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LA JUNTADA SE CERRÓ",
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Compact Access Code Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    text = "CÓDIGO DE ACCESO",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                )
                                Text(
                                    text = state.tableId,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }

                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(state.tableId))
                                    copyMessageSuccess = true
                                },
                            ) {
                                Icon(
                                    imageVector = if (copyMessageSuccess) Icons.Default.Check else Icons.Default.Share,
                                    contentDescription = "Copiar Código",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Invitation/QR trigger button
                        ClayButton(
                            onClick = { showQrBottomSheet = true },
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 16.dp,
                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Invitar amigos / Mostrar QR",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Amigos en la juntada:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    if (state.friends.isNotEmpty()) {
                        val isHighlighted = state.friends.size >= 2
                        Box(
                            modifier =
                                Modifier
                                    .claymorphic(
                                        backgroundColor =
                                            if (isHighlighted) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant
                                            },
                                        cornerRadius = 12.dp,
                                        elevation = 1.dp,
                                        isDark = isDark,
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = "${state.friends.size} amigos",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color =
                                    if (isHighlighted) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                            )
                        }
                    }
                }
            }

            items(state.friends) { friend ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .claymorphic(
                                backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                                cornerRadius = 20.dp,
                                elevation = 3.dp,
                                isDark = isDark,
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = friend.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                            if (!friend.alias.isNullOrBlank() || !friend.cbu.isNullOrBlank()) {
                                Text(
                                    text = "Alias: ${friend.alias ?: "-"} • CBU: ${friend.cbu ?: "-"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                )
                            }
                        }
                        if (!state.isClosed) {
                            IconButton(onClick = { viewModel.removeFriend(friend.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar amigo",
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                }
            }

            if (!state.isClosed) {
                item {
                    // Claymorphic Add Friend Box
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
                                .padding(top = 8.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Sumar amigos a la juntada",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                OutlinedTextField(
                                    value = friendNameInput,
                                    onValueChange = {
                                        friendNameInput = it
                                        errorMessage = null
                                    },
                                    label = { Text("Apodo o @usuario") },
                                    placeholder = { Text("Ej: @santi o Juan") },
                                    modifier = Modifier.weight(1.2f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                ClayButton(
                                    onClick = {
                                        val input = friendNameInput.trim()
                                        if (input.startsWith("@") || !input.contains(" ")) {
                                            val targetUsername = input.removePrefix("@")
                                            viewModel.addFriendToTableByUsername(
                                                username = targetUsername,
                                                onSuccess = {
                                                    friendNameInput = ""
                                                    errorMessage = null
                                                },
                                                onError = { error ->
                                                    if (!input.startsWith("@")) {
                                                        viewModel.addFriend(input)
                                                        friendNameInput = ""
                                                        errorMessage = null
                                                    } else {
                                                        errorMessage = error
                                                    }
                                                },
                                            )
                                        } else {
                                            viewModel.addFriend(input)
                                            friendNameInput = ""
                                            errorMessage = null
                                        }
                                    },
                                    modifier = Modifier.weight(0.8f),
                                    enabled = friendNameInput.isNotEmpty(),
                                    cornerRadius = 16.dp,
                                    horizontalPadding = 8.dp,
                                ) {
                                    Text(
                                        text = "Agregar",
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }

                            errorMessage?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }

                            if (frequentFriends.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Tus amigos frecuentes:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    items(frequentFriends) { friend ->
                                        val alreadyInTable =
                                            state.friends.any {
                                                it.name.equals(
                                                    friend.username,
                                                    ignoreCase = true,
                                                )
                                            }
                                        Box(
                                            modifier =
                                                Modifier
                                                    .claymorphic(
                                                        backgroundColor =
                                                            if (alreadyInTable) {
                                                                MaterialTheme.colorScheme.primaryContainer
                                                            } else if (isDark) {
                                                                MaterialTheme.colorScheme.surfaceVariant
                                                            } else {
                                                                Color(0xFFF5F5F5)
                                                            },
                                                        cornerRadius = 12.dp,
                                                        elevation = 1.dp,
                                                        isDark = isDark,
                                                    )
                                                    .clickable(enabled = !alreadyInTable) {
                                                        viewModel.addFrequentFriendToTable(friend)
                                                    }
                                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint =
                                                        if (alreadyInTable) {
                                                            MaterialTheme.colorScheme.primary
                                                        } else {
                                                            MaterialTheme.colorScheme.onSurfaceVariant
                                                        },
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "@${friend.username}",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color =
                                                        if (alreadyInTable) {
                                                            MaterialTheme.colorScheme.onPrimaryContainer
                                                        } else {
                                                            MaterialTheme.colorScheme.onSurface
                                                        },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // Add a spacer at the end for comfortable scrolling above the fixed bottom button
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ClayButton(
            onClick = { onNavigateToExpenses(state.tableId) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.friends.isNotEmpty(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = if (state.isClosed) "Ver Gastos" else "Ir a Cargar Gastos",
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }

    if (showQrBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showQrBottomSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Código de Acceso",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = state.tableId,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    TextButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(state.tableId))
                            copyMessageSuccess = true
                        },
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (copyMessageSuccess) Icons.Default.Check else Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (copyMessageSuccess) "¡Copiado!" else "Copiar Código")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier =
                        Modifier
                            .size(200.dp)
                            .claymorphic(
                                backgroundColor = Color.White,
                                cornerRadius = 20.dp,
                                elevation = 2.dp,
                                isDark = false,
                            )
                            .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    QRCodeView(
                        text = state.tableId,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Pedile a tus amigos que escaneen el QR para unirse a la juntada.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
