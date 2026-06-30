package com.mitimiti.app.presentation.consumo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitimiti.app.domain.model.ExpenseItem
import com.mitimiti.app.domain.model.Friend
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.domain.repository.TableRepository
import com.mitimiti.app.presentation.mesa.ClockUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExpenseUiState(
    val tableId: String = "",
    val tableName: String = "",
    val type: TableType = TableType.RESTAURANT,
    val friends: List<Friend> = emptyList(),
    val expenses: List<ExpenseItem> = emptyList(),
    val tipPercentage: Double = 10.0,
    val fixedExtraCost: Double = 0.0,
    val cubiertoPerPerson: Double = 0.0,
    val isClosed: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false,
)

class ExpenseViewModel(
    private val tableRepository: TableRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    fun refreshTable(tableId: String) {
        if (tableId.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                tableRepository.getTable(tableId)
                loadTable(tableId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private var observeJob: Job? = null

    fun loadTable(tableId: String) {
        observeJob?.cancel()
        observeJob =
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                val initialTable = tableRepository.getTable(tableId)
                if (initialTable == null) {
                    _uiState.update { it.copy(error = "Mesa no encontrada", isLoading = false) }
                    return@launch
                }

                tableRepository.observeTable(tableId).collect { table ->
                    if (table != null) {
                        _uiState.update {
                            it.copy(
                                tableId = tableId,
                                tableName = table.name,
                                type = table.type,
                                friends = table.friends,
                                expenses = table.expenses,
                                tipPercentage = table.tipPercentage,
                                fixedExtraCost = table.fixedExtraCost,
                                cubiertoPerPerson = table.cubiertoPerPerson,
                                isClosed = table.isClosed,
                                isLoading = false,
                            )
                        }
                    }
                }
            }
    }

    fun addExpenseItem(
        name: String,
        cost: Double,
        sharedByFriendIds: List<String>,
        paidByFriendId: String,
    ) {
        val state = _uiState.value
        if (state.isClosed) return
        val tableId = state.tableId
        if (tableId.isEmpty() || name.trim().isEmpty() || cost <= 0 || paidByFriendId.isEmpty()) return

        viewModelScope.launch {
            val newItem =
                ExpenseItem(
                    id = "item_${ClockUtils.currentTimeMillis()}",
                    name = name,
                    cost = cost,
                    sharedByFriendIds = sharedByFriendIds,
                    paidByFriendId = paidByFriendId,
                )
            val updatedExpenses = state.expenses + newItem

            _uiState.update { it.copy(expenses = updatedExpenses) }

            updateTableInRepository()
        }
    }

    fun deleteExpenseItem(itemId: String) {
        val state = _uiState.value
        if (state.isClosed) return
        val tableId = state.tableId
        if (tableId.isEmpty()) return

        viewModelScope.launch {
            val updatedExpenses = state.expenses.filter { it.id != itemId }
            _uiState.update { it.copy(expenses = updatedExpenses) }
            updateTableInRepository()
        }
    }

    fun updateExpenseItem(
        id: String,
        name: String,
        cost: Double,
        sharedByFriendIds: List<String>,
        paidByFriendId: String,
    ) {
        val state = _uiState.value
        if (state.isClosed) return
        val tableId = state.tableId
        if (tableId.isEmpty() || name.trim().isEmpty() || cost <= 0 || paidByFriendId.isEmpty()) return

        viewModelScope.launch {
            val updatedExpenses =
                state.expenses.map {
                    if (it.id == id) {
                        it.copy(
                            name = name,
                            cost = cost,
                            sharedByFriendIds = sharedByFriendIds,
                            paidByFriendId = paidByFriendId,
                        )
                    } else {
                        it
                    }
                }
            _uiState.update { it.copy(expenses = updatedExpenses) }
            updateTableInRepository()
        }
    }

    fun updateTipAndExtra(
        tipPercentage: Double,
        fixedExtraCost: Double,
    ) {
        val state = _uiState.value
        if (state.isClosed) return
        _uiState.update {
            it.copy(
                tipPercentage = tipPercentage,
                fixedExtraCost = fixedExtraCost,
            )
        }
        viewModelScope.launch {
            updateTableInRepository()
        }
    }

    private suspend fun updateTableInRepository() {
        val state = _uiState.value
        val table = tableRepository.getTable(state.tableId)
        if (table != null) {
            tableRepository.saveTable(
                table.copy(
                    expenses = state.expenses,
                    tipPercentage = state.tipPercentage,
                    fixedExtraCost = state.fixedExtraCost,
                ),
            )
        }
    }

    override fun onCleared() {
        observeJob?.cancel()
        super.onCleared()
    }
}
