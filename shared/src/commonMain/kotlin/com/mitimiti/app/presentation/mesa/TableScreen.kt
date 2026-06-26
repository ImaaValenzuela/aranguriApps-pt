@file:Suppress("ktlint:standard:function-naming")

package com.mitimiti.app.presentation.mesa

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mitimiti.app.domain.model.TableType

@Composable
fun TableScreen(
    viewModel: TableViewModel,
    onNavigateToExpenses: (tableId: String) -> Unit,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    var isCreateTab by remember { mutableStateOf(true) }

    // Create table form state
    var tableNameInput by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TableType.RESTAURANT) }
    var tipInput by remember { mutableStateOf("10") }
    var cubiertoInput by remember { mutableStateOf("0") }
    var creatorNickname by remember { mutableStateOf("") }

    // Join table form state
    var joinCodeInput by remember { mutableStateOf("") }
    var joinNicknameInput by remember { mutableStateOf("") }

    // Lobby manual friend input
    var friendNameInput by remember { mutableStateOf("") }

    var copyMessageSuccess by remember { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "MitiMiti",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
            )
            TextButton(onClick = onSignOut) {
                Text("Cerrar sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.tableId.isEmpty()) {
            TabRow(
                selectedTabIndex = if (isCreateTab) 0 else 1,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Tab(
                    selected = isCreateTab,
                    onClick = { isCreateTab = true },
                    text = { Text("Crear Mesa", fontWeight = FontWeight.Bold) },
                )
                Tab(
                    selected = !isCreateTab,
                    onClick = { isCreateTab = false },
                    text = { Text("Unirse a Mesa", fontWeight = FontWeight.Bold) },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isCreateTab) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Tipo de Evento",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Card(
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .clickable { selectedType = TableType.RESTAURANT },
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            if (selectedType == TableType.RESTAURANT) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant
                                            },
                                    ),
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text("🍽️", style = MaterialTheme.typography.headlineMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Restaurante",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }

                            Card(
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .clickable { selectedType = TableType.HOME_MADE },
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            if (selectedType == TableType.HOME_MADE) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant
                                            },
                                    ),
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text("🏠", style = MaterialTheme.typography.headlineMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Comida en Casa",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = tableNameInput,
                            onValueChange = { tableNameInput = it },
                            label = { Text("Nombre de la mesa") },
                            placeholder = { Text("Ej: Cena de Viernes, Asado, etc.") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )

                        OutlinedTextField(
                            value = creatorNickname,
                            onValueChange = { creatorNickname = it },
                            label = { Text("Tu apodo/nombre") },
                            placeholder = { Text("Ej: Juan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )

                        if (selectedType == TableType.RESTAURANT) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                OutlinedTextField(
                                    value = tipInput,
                                    onValueChange = { tipInput = it },
                                    label = { Text("Propina %") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                )
                                OutlinedTextField(
                                    value = cubiertoInput,
                                    onValueChange = { cubiertoInput = it },
                                    label = { Text("Cubierto $ / pers") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val tip = tipInput.toDoubleOrNull() ?: 10.0
                                val cubierto = cubiertoInput.toDoubleOrNull() ?: 0.0
                                viewModel.createTable(
                                    name = tableNameInput,
                                    type = selectedType,
                                    tipPercentage = tip,
                                    cubiertoPerPerson = cubierto,
                                    hostName = creatorNickname,
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = tableNameInput.isNotEmpty() && creatorNickname.isNotEmpty(),
                        ) {
                            Text("Crear Mesa")
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Unirse a una Mesa existente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )

                        OutlinedTextField(
                            value = joinCodeInput,
                            onValueChange = { joinCodeInput = it },
                            label = { Text("Código de la mesa (6 dígitos)") },
                            placeholder = { Text("Ej: 492041") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )

                        OutlinedTextField(
                            value = joinNicknameInput,
                            onValueChange = { joinNicknameInput = it },
                            label = { Text("Tu apodo/nombre") },
                            placeholder = { Text("Ej: Maria") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )

                        state.error?.let { err ->
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.joinTable(
                                    code = joinCodeInput,
                                    nickname = joinNicknameInput,
                                    onSuccess = { onNavigateToExpenses(it) },
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = joinCodeInput.length >= 5 && joinNicknameInput.isNotEmpty(),
                        ) {
                            Text("Unirse a la Mesa")
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Mesa: ${state.tableName}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (state.type == TableType.RESTAURANT) "🍽️ Restaurante" else "🏠 Comida en Casa",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "CÓDIGO PARA UNIRSE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
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
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(state.tableId))
                                copyMessageSuccess = true
                            },
                        ) {
                            Text(if (copyMessageSuccess) "¡Copiado!" else "Copiar")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Añadir Comensal Extra (Manual)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = friendNameInput,
                            onValueChange = { friendNameInput = it },
                            label = { Text("Nombre del comensal") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.addFriend(friendNameInput)
                                friendNameInput = ""
                            },
                            modifier = Modifier.align(Alignment.CenterVertically),
                            enabled = friendNameInput.isNotEmpty(),
                        ) {
                            Text("Añadir")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Comensales en la mesa:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start),
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(state.friends) { friend ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "👤",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = friend.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onNavigateToExpenses(state.tableId) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.friends.isNotEmpty(),
            ) {
                Text("Continuar a Gastos")
            }
        }

        if (state.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
