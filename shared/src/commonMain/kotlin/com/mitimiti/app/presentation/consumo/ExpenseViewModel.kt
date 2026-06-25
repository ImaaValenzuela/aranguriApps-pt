package com.mitimiti.app.presentation.consumo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitimiti.app.domain.model.ExpenseItem
import com.mitimiti.app.domain.model.Friend
import com.mitimiti.app.domain.repository.TableRepository
import com.mitimiti.app.presentation.mesa.ClockUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExpenseUiState(
    val tableId: String = "",
    val friends: List<Friend> = emptyList(),
    val expenses: List<ExpenseItem> = emptyList(),
    val tipPercentage: Double = 10.0,
    val fixedExtraCost: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class ExpenseViewModel(
    private val tableRepository: TableRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    fun loadTable(tableId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val table = tableRepository.getTable(tableId)
            if (table != null) {
                _uiState.update {
                    it.copy(
                        tableId = tableId,
                        friends = table.friends,
                        expenses = table.expenses,
                        tipPercentage = table.tipPercentage,
                        fixedExtraCost = table.fixedExtraCost,
                        isLoading = false,
                    )
                }
            } else {
                _uiState.update { it.copy(error = "Mesa no encontrada", isLoading = false) }
            }
        }
    }

    fun addExpenseItem(
        name: String,
        cost: Double,
        sharedByFriendIds: List<String>,
    ) {
        val tableId = _uiState.value.tableId
        if (tableId.isEmpty() || name.trim().isEmpty() || cost <= 0) return

        viewModelScope.launch {
            val newItem =
                ExpenseItem(
                    id = "item_${ClockUtils.currentTimeMillis()}",
                    name = name,
                    cost = cost,
                    sharedByFriendIds = sharedByFriendIds,
                )
            val updatedExpenses = _uiState.value.expenses + newItem

            _uiState.update { it.copy(expenses = updatedExpenses) }

            updateTableInRepository()
        }
    }

    fun updateTipAndExtra(
        tipPercentage: Double,
        fixedExtraCost: Double,
    ) {
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
}
