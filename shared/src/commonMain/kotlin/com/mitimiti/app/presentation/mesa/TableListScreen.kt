package com.mitimiti.app.presentation.mesa

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mitimiti.app.domain.model.Table
import com.mitimiti.app.domain.model.TableType

@Composable
@Suppress("LongMethod", "FunctionNaming")
fun TableListScreen(
    viewModel: TableViewModel,
    onNavigateToLobby: (String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val tables by viewModel.tables.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Mis Mesas, 1: Crear, 2: Unirse

    // Form states for Create Table
    var tableNameInput by remember { mutableStateOf("") }
    var creatorNickname by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TableType.RESTAURANT) }
    var tipPercentageInput by remember { mutableStateOf("10") }
    var fixedCostInput by remember { mutableStateOf("0") }
    var cubiertoInput by remember { mutableStateOf("0") }

    // Form states for Join Table
    var joinCodeInput by remember { mutableStateOf("") }
    var joinNicknameInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.observeUserTables()
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .safeDrawingPadding()
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

        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Mis Mesas", fontWeight = FontWeight.Bold) },
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Crear", fontWeight = FontWeight.Bold) },
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Unirse", fontWeight = FontWeight.Bold) },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                if (tables.isEmpty()) {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "📭",
                                style = MaterialTheme.typography.displayMedium,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No tienes mesas registradas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Crea o únete a una mesa para comenzar.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(tables) { table ->
                            TableHistoryCard(
                                table = table,
                                onClick = { onNavigateToLobby(table.id) },
                            )
                        }
                    }
                }
            }
            1 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Configuración de la Mesa",
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
                                    modifier = Modifier.padding(12.dp),
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
                                    modifier = Modifier.padding(12.dp),
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
                            placeholder = { Text("Ej: Cena Viernes, Asado, etc.") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )

                        OutlinedTextField(
                            value = creatorNickname,
                            onValueChange = { creatorNickname = it },
                            label = { Text("Tu apodo (Anfitrión)") },
                            placeholder = { Text("Ej: Juan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )

                        if (selectedType == TableType.RESTAURANT) {
                            OutlinedTextField(
                                value = tipPercentageInput,
                                onValueChange = { tipPercentageInput = it },
                                label = { Text("Propina sugerida (%)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = cubiertoInput,
                                onValueChange = { cubiertoInput = it },
                                label = { Text("Cubierto por persona ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                            )
                        } else {
                            OutlinedTextField(
                                value = fixedCostInput,
                                onValueChange = { fixedCostInput = it },
                                label = { Text("Costo extra fijo común ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                            )
                        }

                        state.error?.let { err ->
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.createTable(
                                    name = tableNameInput,
                                    type = selectedType,
                                    tipPercentage = tipPercentageInput.toDoubleOrNull() ?: 10.0,
                                    fixedExtraCost = fixedCostInput.toDoubleOrNull() ?: 0.0,
                                    cubiertoPerPerson = cubiertoInput.toDoubleOrNull() ?: 0.0,
                                    hostName = creatorNickname,
                                    onSuccess = { onNavigateToLobby(it) },
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = tableNameInput.isNotEmpty() && creatorNickname.isNotEmpty(),
                        ) {
                            Text("Crear Mesa")
                        }
                    }
                }
            }
            2 -> {
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
                                    onSuccess = { onNavigateToLobby(it) },
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
        }

        if (state.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TableHistoryCard(
    table: Table,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = if (table.type == TableType.RESTAURANT) "🍽️" else "🏠",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = table.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Código: ${table.id} • 👥 ${table.friends.size} personas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Box(modifier = Modifier.padding(start = 8.dp)) {
                if (table.isClosed) {
                    Card(
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                            ),
                    ) {
                        Text(
                            text = "Cerrada",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                } else {
                    Card(
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                    ) {
                        Text(
                            text = "Activa",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }
        }
    }
}
