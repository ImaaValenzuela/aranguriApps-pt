package com.mitimiti.app.presentation.consumo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp

@Composable
@Suppress("FunctionNaming")
fun ExpenseScreen(
    tableId: String,
    viewModel: ExpenseViewModel,
    onNavigateToSummary: (tableId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    var itemNameInput by remember { mutableStateOf("") }
    var itemCostInput by remember { mutableStateOf("") }
    val selectedFriendIds = remember { mutableStateListOf<String>() }

    var tipInput by remember { mutableStateOf("10") }
    var extraInput by remember { mutableStateOf("0") }

    LaunchedEffect(tableId) {
        viewModel.loadTable(tableId)
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Text(
            text = "MitiMiti - El Gasto",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Añadir Ítem del Ticket", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = itemNameInput,
                        onValueChange = { itemNameInput = it },
                        label = { Text("Ítem (Ej: Pizza)") },
                        modifier = Modifier.weight(1.5f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = itemCostInput,
                        onValueChange = { itemCostInput = it },
                        label = { Text("Costo ($)") },
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Compartido por:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
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
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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

                Spacer(modifier = Modifier.height(12.dp))

                val isCostValid = itemCostInput.toDoubleOrNull() != null
                Button(
                    onClick = {
                        val cost = itemCostInput.toDoubleOrNull() ?: 0.0
                        if (itemNameInput.isNotEmpty() && cost > 0 && selectedFriendIds.isNotEmpty()) {
                            viewModel.addExpenseItem(
                                name = itemNameInput,
                                cost = cost,
                                sharedByFriendIds = selectedFriendIds.toList(),
                            )
                            itemNameInput = ""
                            itemCostInput = ""
                            selectedFriendIds.clear()
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled =
                        itemNameInput.isNotEmpty() &&
                            isCostValid &&
                            selectedFriendIds.isNotEmpty(),
                ) {
                    Text("Añadir Gasto")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Ítems Registrados:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

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
                            Text(
                                text = "Compartido por ${item.sharedByFriendIds.size} pers.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }
                        Text(
                            text = "$${item.cost}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    modifier = Modifier.weight(1f),
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
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onNavigateToSummary(state.tableId) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.expenses.isNotEmpty(),
        ) {
            Text("Ir a Cierre y Cuenta")
        }
    }
}
