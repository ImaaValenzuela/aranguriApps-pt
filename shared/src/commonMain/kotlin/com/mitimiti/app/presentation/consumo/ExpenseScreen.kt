package com.mitimiti.app.presentation.consumo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mitimiti.app.domain.model.TableType

@Composable
@Suppress("FunctionNaming")
fun ExpenseScreen(
    tableId: String,
    viewModel: ExpenseViewModel,
    onNavigateToSummary: (tableId: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    var itemNameInput by remember { mutableStateOf("") }
    var itemCostInput by remember { mutableStateOf("") }
    val selectedFriendIds = remember { mutableStateListOf<String>() }
    var selectedPayerId by remember { mutableStateOf("") }
    var editingExpenseId by remember { mutableStateOf<String?>(null) }

    var tipInput by remember { mutableStateOf("10") }
    var extraInput by remember { mutableStateOf("0") }

    LaunchedEffect(tableId) {
        viewModel.loadTable(tableId)
    }

    // Set default payer and select all friends as sharers by default when loaded
    LaunchedEffect(state.friends) {
        if (selectedPayerId.isEmpty() && state.friends.isNotEmpty()) {
            selectedPayerId = state.friends.first().id
        }
        if (selectedFriendIds.isEmpty() && state.friends.isNotEmpty()) {
            selectedFriendIds.addAll(state.friends.map { it.id })
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = onBack,
            ) {
                Text("← Volver a la Mesa")
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Mesa: ${state.tableName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = if (state.type == TableType.RESTAURANT) "🍽️ Restaurante" else "🏠 Comida en Casa",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (state.isClosed) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "🔒 MESA CERRADA - Modo de Solo Lectura. No se pueden modificar ni eliminar consumos.",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp),
                )
            }
        } else {
            // Locked OCR Receipt Scanner Option
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    ),
            ) {
                Row(
                    modifier =
                        Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("📷", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Escanear Ticket",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = "Carga automática con IA y foto",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            )
                        }
                    }
                    Text(
                        text = "🔒 Próximamente",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (editingExpenseId != null) "Editar Producto" else "Añadir Producto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = itemNameInput,
                            onValueChange = { itemNameInput = it },
                            label = { Text("Ítem (Ej: Pizza, Asado)") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = itemCostInput,
                            onValueChange = { itemCostInput = it },
                            label = { Text("Costo ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pagado por:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(state.friends) { friend ->
                            val isPayer = selectedPayerId == friend.id
                            Card(
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            if (isPayer) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant
                                            },
                                    ),
                                modifier =
                                    Modifier.clickable {
                                        selectedPayerId = friend.id
                                    },
                            ) {
                                Text(
                                    text = friend.name,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isPayer) FontWeight.Bold else FontWeight.Normal,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Compartido por:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
                        TextButton(
                            onClick = {
                                if (selectedFriendIds.size == state.friends.size) {
                                    selectedFriendIds.clear()
                                } else {
                                    selectedFriendIds.clear()
                                    selectedFriendIds.addAll(state.friends.map { it.id })
                                }
                            },
                        ) {
                            Text(
                                text = if (selectedFriendIds.size == state.friends.size) "Ninguno" else "Todos",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        items(state.friends) { friend ->
                            val isChecked = selectedFriendIds.contains(friend.id)
                            Card(
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            if (isChecked) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant
                                            },
                                    ),
                                modifier =
                                    Modifier
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                        .clickable {
                                            if (isChecked) {
                                                selectedFriendIds.remove(friend.id)
                                            } else {
                                                selectedFriendIds.add(friend.id)
                                            }
                                        },
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { checked ->
                                            if (checked == true) {
                                                selectedFriendIds.add(friend.id)
                                            } else {
                                                selectedFriendIds.remove(friend.id)
                                            }
                                        },
                                    )
                                    Text(text = friend.name, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val isCostValid = itemCostInput.toDoubleOrNull() != null
                    val isValidForm =
                        itemNameInput.isNotEmpty() &&
                            isCostValid &&
                            selectedFriendIds.isNotEmpty() &&
                            selectedPayerId.isNotEmpty()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (editingExpenseId != null) {
                            TextButton(
                                onClick = {
                                    editingExpenseId = null
                                    itemNameInput = ""
                                    itemCostInput = ""
                                    selectedFriendIds.clear()
                                    selectedFriendIds.addAll(state.friends.map { it.id })
                                    if (state.friends.isNotEmpty()) {
                                        selectedPayerId = state.friends.first().id
                                    }
                                },
                                modifier = Modifier.padding(end = 8.dp),
                            ) {
                                Text("Cancelar")
                            }
                        }

                        Button(
                            onClick = {
                                val cost = itemCostInput.toDoubleOrNull() ?: 0.0
                                if (isValidForm) {
                                    val currentEditingId = editingExpenseId
                                    if (currentEditingId != null) {
                                        viewModel.updateExpenseItem(
                                            id = currentEditingId,
                                            name = itemNameInput,
                                            cost = cost,
                                            sharedByFriendIds = selectedFriendIds.toList(),
                                            paidByFriendId = selectedPayerId,
                                        )
                                        editingExpenseId = null
                                    } else {
                                        viewModel.addExpenseItem(
                                            name = itemNameInput,
                                            cost = cost,
                                            sharedByFriendIds = selectedFriendIds.toList(),
                                            paidByFriendId = selectedPayerId,
                                        )
                                    }
                                    itemNameInput = ""
                                    itemCostInput = ""
                                    selectedFriendIds.clear()
                                    selectedFriendIds.addAll(state.friends.map { it.id })
                                }
                            },
                            enabled = isValidForm,
                        ) {
                            Text(if (editingExpenseId != null) "Guardar Cambios" else "Añadir Gasto")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Ítems Registrados:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) {
            items(state.expenses) { item ->
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            val payerName =
                                state.friends
                                    .find { it.id == item.paidByFriendId }?.name ?: "Desconocido"
                            Text(
                                text =
                                    "Pagado por: $payerName | " +
                                        "Compartido por ${item.sharedByFriendIds.size} pers.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }
                        Text(
                            text = "$${item.cost}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        if (!state.isClosed) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    editingExpenseId = item.id
                                    itemNameInput = item.name
                                    itemCostInput = item.cost.toString()
                                    selectedPayerId = item.paidByFriendId
                                    selectedFriendIds.clear()
                                    selectedFriendIds.addAll(item.sharedByFriendIds)
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar gasto",
                                    tint = MaterialTheme.colorScheme.secondary,
                                )
                            }
                            IconButton(
                                onClick = {
                                    if (editingExpenseId == item.id) {
                                        editingExpenseId = null
                                        itemNameInput = ""
                                        itemCostInput = ""
                                        selectedFriendIds.clear()
                                        selectedFriendIds.addAll(state.friends.map { it.id })
                                    }
                                    viewModel.deleteExpenseItem(item.id)
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar gasto",
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (state.type == TableType.RESTAURANT) {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier =
                        Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value = tipInput,
                        onValueChange = {
                            tipInput = it
                            val tipVal = it.toDoubleOrNull() ?: 0.0
                            val extraVal = extraInput.toDoubleOrNull() ?: 0.0
                            viewModel.updateTipAndExtra(tipVal, extraVal)
                        },
                        label = { Text("Propina %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        enabled = !state.isClosed,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = extraInput,
                        onValueChange = {
                            extraInput = it
                            val tipVal = tipInput.toDoubleOrNull() ?: 0.0
                            val extraVal = it.toDoubleOrNull() ?: 0.0
                            viewModel.updateTipAndExtra(tipVal, extraVal)
                        },
                        label = { Text("Extras Fijos ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        enabled = !state.isClosed,
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = { onNavigateToSummary(state.tableId) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.expenses.isNotEmpty(),
        ) {
            Text(if (state.isClosed) "Ver Cuenta Final" else "Ir a Cierre y Cuenta")
        }
    }
}
