package com.mitimiti.app.presentation.mesa

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.presentation.components.QRCodeView
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.claymorphic

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

    var friendNameInput by remember { mutableStateOf("") }
    var copyMessageSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(tableId) {
        viewModel.startObservingTable(tableId)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "CÓDIGO DE ACCESO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
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
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier =
                        Modifier
                            .size(160.dp)
                            .claymorphic(
                                backgroundColor = Color.White,
                                cornerRadius = 16.dp,
                                elevation = 2.dp,
                                isDark = false,
                            )
                            .padding(12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    QRCodeView(
                        text = state.tableId,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Escaneá para unirte al instante",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!state.isClosed) {
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
                        ),
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
                            text = "Sumar amigo a mano",
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
                            onValueChange = { friendNameInput = it },
                            label = { Text("Apodo del amigo") },
                            modifier = Modifier.weight(1.3f),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ClayButton(
                            onClick = {
                                viewModel.addFriend(friendNameInput)
                                friendNameInput = ""
                            },
                            modifier = Modifier.weight(0.7f),
                            enabled = friendNameInput.isNotEmpty(),
                            cornerRadius = 16.dp,
                        ) {
                            Text("Agregar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "Amigos en la juntada:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Claymorphic Friends List
        LazyColumn(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
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
                        Text(
                            text = friend.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                        )
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
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        if (state.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
