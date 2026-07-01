package com.mitimiti.app.presentation.consumo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.claymorphic
import com.mitimiti.app.rememberImagePicker
import com.mitimiti.app.rememberTextRecognizer
import com.mitimiti.app.rememberTicketScanner
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

    var isProcessingImage by remember { mutableStateOf(false) }
    var showTicketDialog by remember { mutableStateOf(false) }
    var parsedItems by remember { mutableStateOf<List<ParsedExpenseItem>>(emptyList()) }
    val parsedItemsTextMap = remember { mutableStateMapOf<String, String>() }

    val ticketScanner =
        rememberTicketScanner { recognizedText ->
            isProcessingImage = false
            if (recognizedText != null) {
                val parsed = TicketParser.parse(recognizedText)
                if (parsed.isNotEmpty()) {
                    parsedItemsTextMap.clear()
                    parsed.forEach { item ->
                        parsedItemsTextMap[item.id] = item.cost.toString()
                    }
                    parsedItems = parsed
                    showTicketDialog = true
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "No se pudieron detectar productos en el ticket. Intentá enfocar mejor.",
                        )
                    }
                }
            }
        }

    val textRecognizer =
        rememberTextRecognizer { recognizedText ->
            isProcessingImage = false
            if (recognizedText != null) {
                val parsed = TicketParser.parse(recognizedText)
                if (parsed.isNotEmpty()) {
                    parsedItemsTextMap.clear()
                    parsed.forEach { item ->
                        parsedItemsTextMap[item.id] = item.cost.toString()
                    }
                    parsedItems = parsed
                    showTicketDialog = true
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "No se pudieron detectar productos en el ticket. Intentá con otra foto.",
                        )
                    }
                }
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar("Error al procesar la imagen.")
                }
            }
        }

    val imagePicker =
        rememberImagePicker { bytes ->
            isProcessingImage = true
            textRecognizer(bytes)
        }

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
                    // Unlocked OCR Receipt Scanner Option
                    item {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isProcessingImage) {
                                        ticketScanner()
                                    }
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
                                    if (isProcessingImage) {
                                        androidx.compose.material3.CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp,
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Escanear",
                                            modifier = Modifier.size(24.dp),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text =
                                                if (isProcessingImage) {
                                                    "Procesando Ticket..."
                                                } else {
                                                    "Escanear Ticket"
                                                },
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                        Text(
                                            text =
                                                if (isProcessingImage) {
                                                    "Analizando texto con ML Kit..."
                                                } else {
                                                    "Carga automática leyendo una foto"
                                                },
                                            style = MaterialTheme.typography.bodySmall,
                                            color =
                                                MaterialTheme.colorScheme
                                                    .onSurfaceVariant.copy(alpha = 0.7f),
                                        )
                                    }
                                }
                                if (!isProcessingImage) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Claymorphic Add/Edit Expense Box
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
                                        cornerRadius = 24.dp,
                                        elevation = 4.dp,
                                        isDark = isDark,
                                    ),
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector =
                                            if (editingExpenseId != null) {
                                                Icons.Default.Edit
                                            } else {
                                                Icons.Default.Add
                                            },
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text =
                                            if (editingExpenseId != null) {
                                                "Editar Gasto"
                                            } else {
                                                "Cargar un Gasto"
                                            },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = itemNameInput,
                                        onValueChange = { itemNameInput = it },
                                        label = { Text("Gasto (Ej: Vacío, Fernet...)") },
                                        modifier = Modifier.weight(1.4f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(16.dp),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    OutlinedTextField(
                                        value = itemCostInput,
                                        onValueChange = { itemCostInput = it },
                                        label = { Text("Precio ($)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(16.dp),
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Quién garpó:",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))

                                // FlowRow: los chips hacen wrap automático si no entran en una fila
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    state.friends.forEach { friend ->
                                        val isPayer = selectedPayerId == friend.id
                                        Box(
                                            modifier =
                                                Modifier
                                                    .clickable { selectedPayerId = friend.id }
                                                    .claymorphic(
                                                        backgroundColor =
                                                            if (isPayer) {
                                                                MaterialTheme.colorScheme.primaryContainer
                                                            } else if (isDark) {
                                                                MaterialTheme.colorScheme.surface
                                                            } else {
                                                                Color.White
                                                            },
                                                        cornerRadius = 14.dp,
                                                        elevation = if (isPayer) 4.dp else 1.dp,
                                                        isDark = isDark,
                                                    )
                                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                        ) {
                                            Text(
                                                text = friend.name,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = if (isPayer) FontWeight.Bold else FontWeight.Normal,
                                                color =
                                                    if (isPayer) {
                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface
                                                    },
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Quiénes consumieron:",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
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
                                            text =
                                                if (selectedFriendIds.size == state.friends.size) {
                                                    "Ninguno"
                                                } else {
                                                    "Todos"
                                                },
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))

                                // FlowRow: chips con checkbox en wrap automático
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    state.friends.forEach { friend ->
                                        val isChecked = selectedFriendIds.contains(friend.id)
                                        Box(
                                            modifier =
                                                Modifier
                                                    .clickable {
                                                        if (isChecked) {
                                                            selectedFriendIds.remove(friend.id)
                                                        } else {
                                                            selectedFriendIds.add(friend.id)
                                                        }
                                                    }
                                                    .claymorphic(
                                                        backgroundColor =
                                                            if (isChecked) {
                                                                MaterialTheme.colorScheme.primaryContainer
                                                            } else if (isDark) {
                                                                MaterialTheme.colorScheme.surface
                                                            } else {
                                                                Color.White
                                                            },
                                                        cornerRadius = 14.dp,
                                                        elevation = if (isChecked) 4.dp else 1.dp,
                                                        isDark = isDark,
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            ) {
                                                Checkbox(
                                                    checked = isChecked,
                                                    onCheckedChange = { checked ->
                                                        if (checked) {
                                                            selectedFriendIds.add(friend.id)
                                                        } else {
                                                            selectedFriendIds.remove(friend.id)
                                                        }
                                                    },
                                                )
                                                Text(
                                                    text = friend.name,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color =
                                                        if (isChecked) {
                                                            MaterialTheme.colorScheme.onPrimaryContainer
                                                        } else {
                                                            MaterialTheme.colorScheme.onSurface
                                                        },
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

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
                                            Text("Cancelar", color = MaterialTheme.colorScheme.error)
                                        }
                                    }

                                    ClayButton(
                                        onClick = {
                                            val cost = itemCostInput.toDoubleOrNull() ?: 0.0
                                            if (isValidForm) {
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
                                                // Snackbar de feedback
                                                val msg = if (isEditing) "✓ Gasto actualizado" else "✓ Gasto agregado"
                                                scope.launch { snackbarHostState.showSnackbar(msg) }
                                            }
                                        },
                                        enabled = isValidForm,
                                        cornerRadius = 16.dp,
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector =
                                                    if (editingExpenseId != null) {
                                                        Icons.Default.Check
                                                    } else {
                                                        Icons.Default.Add
                                                    },
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (editingExpenseId != null) "Guardar Gasto" else "Sumar Gasto",
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Detalle de los Gastos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                if (state.expenses.isEmpty()) {
                    item {
                        Text(
                            text = "Aún no se cargaron gastos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }
                } else {
                    items(state.expenses) { item ->
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
                                        elevation = 3.dp,
                                        isDark = isDark,
                                    )
                                    .padding(12.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                                            "Garpó: $payerName | " +
                                                "Se divide entre ${item.sharedByFriendIds.size} amigos",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Text(
                                    text = "$${item.cost}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Black,
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
                                            tint = MaterialTheme.colorScheme.primary,
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

                if (state.type == TableType.RESTAURANT && !state.isClosed) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
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
                                    ),
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
                                    shape = RoundedCornerShape(16.dp),
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
                                    shape = RoundedCornerShape(16.dp),
                                )
                            }
                        }
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

    if (showTicketDialog) {
        Dialog(
            onDismissRequest = { showTicketDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .claymorphic(
                                backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                                cornerRadius = 24.dp,
                                elevation = 8.dp,
                                isDark = isDark,
                            )
                            .padding(16.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Title
                        Text(
                            text = "Ticket Escaneado",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Revisá los nombres y precios, y seleccioná los que quieras cargar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Dialog Payer & Sharers selectors
                        var dialogPayerId by remember { mutableStateOf(selectedPayerId) }
                        val dialogFriendIds =
                            remember { mutableStateListOf<String>().apply { addAll(selectedFriendIds) } }

                        // Who paid
                        Text(
                            text = "Quién pagó el ticket:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            state.friends.forEach { friend ->
                                val isPayer = dialogPayerId == friend.id
                                Box(
                                    modifier =
                                        Modifier
                                            .clickable { dialogPayerId = friend.id }
                                            .claymorphic(
                                                backgroundColor =
                                                    if (isPayer) {
                                                        MaterialTheme.colorScheme.primaryContainer
                                                    } else if (isDark) {
                                                        MaterialTheme.colorScheme.surface
                                                    } else {
                                                        Color.White
                                                    },
                                                cornerRadius = 12.dp,
                                                elevation = if (isPayer) 3.dp else 1.dp,
                                                isDark = isDark,
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                ) {
                                    Text(
                                        text = friend.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color =
                                            if (isPayer) {
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            },
                                    )
                                }
                            }
                        }

                        // Who shared
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Quiénes consumieron:",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                            )
                            TextButton(
                                onClick = {
                                    if (dialogFriendIds.size == state.friends.size) {
                                        dialogFriendIds.clear()
                                    } else {
                                        dialogFriendIds.clear()
                                        dialogFriendIds.addAll(state.friends.map { it.id })
                                    }
                                },
                            ) {
                                Text(
                                    text = if (dialogFriendIds.size == state.friends.size) "Ninguno" else "Todos",
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            state.friends.forEach { friend ->
                                val isChecked = dialogFriendIds.contains(friend.id)
                                Box(
                                    modifier =
                                        Modifier
                                            .clickable {
                                                if (isChecked) {
                                                    dialogFriendIds.remove(
                                                        friend.id,
                                                    )
                                                } else {
                                                    dialogFriendIds.add(friend.id)
                                                }
                                            }
                                            .claymorphic(
                                                backgroundColor =
                                                    if (isChecked) {
                                                        MaterialTheme.colorScheme.primaryContainer
                                                    } else if (isDark) {
                                                        MaterialTheme.colorScheme.surface
                                                    } else {
                                                        Color.White
                                                    },
                                                cornerRadius = 12.dp,
                                                elevation = if (isChecked) 3.dp else 1.dp,
                                                isDark = isDark,
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                ) {
                                    Text(
                                        text = friend.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color =
                                            if (isChecked) {
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            },
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Items list
                        Box(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(parsedItems) { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Checkbox(
                                            checked = item.selected,
                                            onCheckedChange = { checked ->
                                                parsedItems =
                                                    parsedItems.map {
                                                        if (it.id == item.id) it.copy(selected = checked) else it
                                                    }
                                            },
                                        )

                                        // Description input
                                        OutlinedTextField(
                                            value = item.name,
                                            onValueChange = { newName ->
                                                parsedItems =
                                                    parsedItems.map {
                                                        if (it.id == item.id) it.copy(name = newName) else it
                                                    }
                                            },
                                            modifier = Modifier.weight(1.5f),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                        )

                                        // Cost input
                                        val costStr = parsedItemsTextMap[item.id] ?: item.cost.toString()
                                        OutlinedTextField(
                                            value = costStr,
                                            onValueChange = { newCostStr ->
                                                parsedItemsTextMap[item.id] = newCostStr
                                                val parsedCost = newCostStr.toDoubleOrNull()
                                                if (parsedCost != null) {
                                                    parsedItems =
                                                        parsedItems.map {
                                                            if (it.id == item.id) it.copy(cost = parsedCost) else it
                                                        }
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            shape = RoundedCornerShape(12.dp),
                                        )

                                        IconButton(
                                            onClick = {
                                                parsedItems = parsedItems.filter { it.id != item.id }
                                                parsedItemsTextMap.remove(item.id)
                                            },
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar",
                                                tint = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Summary & Actions
                        val selectedCount = parsedItems.count { it.selected }
                        val totalCost = parsedItems.filter { it.selected }.sumOf { it.cost }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "$selectedCount seleccionados",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Total: $$totalCost",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TextButton(
                                onClick = { showTicketDialog = false },
                            ) {
                                Text("Cancelar", color = MaterialTheme.colorScheme.error)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            ClayButton(
                                onClick = {
                                    val itemsToAdd = parsedItems.filter { it.selected }
                                    if (itemsToAdd.isNotEmpty()) {
                                        itemsToAdd.forEach { item ->
                                            viewModel.addExpenseItem(
                                                name = item.name,
                                                cost = item.cost,
                                                sharedByFriendIds = dialogFriendIds.toList(),
                                                paidByFriendId = dialogPayerId,
                                            )
                                        }
                                        val count = itemsToAdd.size
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                "✓ Se cargaron $count gastos del ticket",
                                            )
                                        }
                                    }
                                    showTicketDialog = false
                                },
                                enabled =
                                    selectedCount > 0 &&
                                        dialogPayerId.isNotEmpty() &&
                                        dialogFriendIds.isNotEmpty(),
                                cornerRadius = 16.dp,
                            ) {
                                Text("Cargar Gastos", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
