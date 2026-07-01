package com.mitimiti.app.presentation.consumo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.claymorphic
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
@Suppress("FunctionNaming", "LongMethod")
fun ExpenseScreen(
    tableId: String,
    viewModel: ExpenseViewModel,
    onNavigateToLobby: (tableId: String) -> Unit,
    onNavigateToSummary: (tableId: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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

    // Sync tip and extra inputs with state once loaded
    LaunchedEffect(state.tipPercentage, state.fixedExtraCost) {
        tipInput = state.tipPercentage.toString()
        extraInput = state.fixedExtraCost.toString()
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refreshTable(tableId) },
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (state.isClosed) {
                    item {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .claymorphic(
                                        backgroundColor = MaterialTheme.colorScheme.errorContainer,
                                        cornerRadius = 16.dp,
                                        elevation = 2.dp,
                                        isDark = isDark,
                                    )
                                    .padding(12.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text =
                                        "JUNTADA CERRADA - Modo de Solo Lectura. " +
                                            "No se pueden modificar ni eliminar gastos.",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                } else {
                    // Locked OCR Receipt Scanner Option
                    item {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .claymorphic(
                                        backgroundColor =
                                            if (isDark) {
                                                MaterialTheme.colorScheme.surface
                                            } else {
                                                Color.White
                                            },
                                        cornerRadius = 20.dp,
                                        elevation = 2.dp,
                                        isDark = isDark,
                                    )
                                    .padding(12.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Bloqueado",
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Escanear Ticket",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                        Text(
                                            text = "Carga automática con foto (Próximamente)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        )
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Bloqueado",
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.outline,
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Bloqueado",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.outline,
                                    )
                                }
                            }
                        }
                    }

                    // Claymorphic Add/Edit Expense Box
                    item {
                        AddExpenseForm(
                            friends = state.friends,
                            isDark = isDark,
                            itemNameInput = itemNameInput,
                            onItemNameInputChange = { itemNameInput = it },
                            itemCostInput = itemCostInput,
                            onItemCostInputChange = { itemCostInput = it },
                            selectedPayerId = selectedPayerId,
                            onSelectedPayerIdChange = { selectedPayerId = it },
                            selectedFriendIds = selectedFriendIds,
                            onToggleFriendSharer = { friendId ->
                                if (selectedFriendIds.contains(friendId)) {
                                    selectedFriendIds.remove(friendId)
                                } else {
                                    selectedFriendIds.add(friendId)
                                }
                            },
                            onSelectAllFriends = {
                                selectedFriendIds.clear()
                                selectedFriendIds.addAll(state.friends.map { it.id })
                            },
                            onClearFriendsSharers = {
                                selectedFriendIds.clear()
                            },
                            editingExpenseId = editingExpenseId,
                            onCancelEdit = {
                                editingExpenseId = null
                                itemNameInput = ""
                                itemCostInput = ""
                                selectedFriendIds.clear()
                                selectedFriendIds.addAll(state.friends.map { it.id })
                                if (state.friends.isNotEmpty()) {
                                    selectedPayerId = state.friends.first().id
                                }
                            },
                            onSubmit = {
                                val cost = itemCostInput.toDoubleOrNull() ?: 0.0
                                val currentEditingId = editingExpenseId
                                val isEditing = currentEditingId != null
                                if (isEditing) {
                                    viewModel.updateExpenseItem(
                                        id = currentEditingId!!,
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
                                val msg = if (isEditing) "✓ Gasto actualizado" else "✓ Gasto agregado"
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            },
                        )
                    }
                }

                item {
                    ExpenseList(
                        expenses = state.expenses,
                        friends = state.friends,
                        isClosed = state.isClosed,
                        isDark = isDark,
                        editingExpenseId = editingExpenseId,
                        onEditExpense = { item ->
                            editingExpenseId = item.id
                            itemNameInput = item.name
                            itemCostInput = item.cost.toString()
                            selectedPayerId = item.paidByFriendId
                            selectedFriendIds.clear()
                            selectedFriendIds.addAll(item.sharedByFriendIds)
                        },
                        onDeleteExpense = { item ->
                            if (editingExpenseId == item.id) {
                                editingExpenseId = null
                                itemNameInput = ""
                                itemCostInput = ""
                                selectedFriendIds.clear()
                                selectedFriendIds.addAll(state.friends.map { it.id })
                            }
                            viewModel.deleteExpenseItem(item.id)
                        },
                    )
                }

                if (state.type == TableType.RESTAURANT && !state.isClosed) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        TipAdjustCard(
                            tipInput = tipInput,
                            onTipInputChange = {
                                tipInput = it
                                val tipVal = it.toDoubleOrNull() ?: 0.0
                                val extraVal = extraInput.toDoubleOrNull() ?: 0.0
                                viewModel.updateTipAndExtra(tipVal, extraVal)
                            },
                            extraInput = extraInput,
                            onExtraInputChange = {
                                extraInput = it
                                val tipVal = tipInput.toDoubleOrNull() ?: 0.0
                                val extraVal = it.toDoubleOrNull() ?: 0.0
                                viewModel.updateTipAndExtra(tipVal, extraVal)
                            },
                            isDark = isDark,
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    ClayButton(
                        onClick = { onNavigateToSummary(state.tableId) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.expenses.isNotEmpty(),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = if (state.isClosed) "Ver Cuenta Final" else "Ir a Cerrar la Cuenta",
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
            }
        }
    } // end Scaffold
}
