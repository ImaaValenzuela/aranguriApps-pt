package com.mitimiti.app.presentation.cierre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitimiti.app.domain.model.SplitType
import com.mitimiti.app.domain.repository.TableRepository
import com.mitimiti.app.domain.usecase.CalculateSplitExpensesUseCase
import com.mitimiti.app.domain.usecase.TableBillSummary
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SummaryUiState(
    val tableId: String = "",
    val splitType: SplitType = SplitType.BY_CONSUMPTION,
    val billSummary: TableBillSummary? = null,
    val formattedShareText: String = "",
    val isCopied: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class SummaryViewModel(
    private val tableRepository: TableRepository,
    private val calculateSplitExpensesUseCase: CalculateSplitExpensesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    fun calculateSplit(tableId: String) {
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
                        val summary = calculateSplitExpensesUseCase(table)
                        val shareText = buildShareText(table.name, summary)
                        _uiState.update {
                            it.copy(
                                tableId = tableId,
                                splitType = table.splitType,
                                billSummary = summary,
                                formattedShareText = shareText,
                                isLoading = false,
                            )
                        }
                    }
                }
            }
    }

    fun updateSplitType(splitType: SplitType) {
        val tableId = _uiState.value.tableId
        if (tableId.isEmpty()) return

        viewModelScope.launch {
            val table = tableRepository.getTable(tableId)
            if (table != null) {
                tableRepository.saveTable(table.copy(splitType = splitType))
            }
        }
    }

    fun onClipboardCopied() {
        _uiState.update { it.copy(isCopied = true) }
    }

    private fun buildShareText(
        tableName: String,
        summary: TableBillSummary,
    ): String {
        return buildString {
            appendLine("🍽️ MitiMiti - Resumen de Cuenta: $tableName 🍽️")
            appendLine("---------------------------------")
            summary.friendBills.forEach { bill ->
                val balanceStr =
                    if (bill.balance >= 0) {
                        "recibe $${bill.balance.format(2)}"
                    } else {
                        "debe $${(-bill.balance).format(2)}"
                    }
                appendLine("👤 ${bill.friendName}:")
                appendLine("   Pagó: $${bill.amountPaid.format(2)}")
                appendLine("   Consume: $${bill.total.format(2)}")
                appendLine("   Saldo: $balanceStr")
                appendLine()
            }
            appendLine("---------------------------------")
            if (summary.transactions.isNotEmpty()) {
                appendLine("💸 Transferencias para saldar:")
                summary.transactions.forEach { tx ->
                    appendLine("👉 ${tx.fromFriendName} le paga $${tx.amount.format(2)} a ${tx.toFriendName}")
                }
            } else {
                appendLine("✅ ¡Todos saldados! Sin transferencias.")
            }
            appendLine("---------------------------------")
            appendLine("🧾 Subtotal: $${summary.subtotal.format(2)}")
            appendLine("💰 Propina Total: $${summary.totalTip.format(2)}")
            appendLine("⚡ Extras/Cubiertos: $${summary.totalExtra.format(2)}")
            appendLine("💵 Total a Pagar: $${summary.total.format(2)}")
        }
    }

    override fun onCleared() {
        observeJob?.cancel()
        super.onCleared()
    }
}

internal fun Double.format(digits: Int): String {
    val raw = this.toString()
    val parts = raw.split(".")
    if (parts.size < 2) return "$raw.00"
    val decimals = parts[1]
    return if (decimals.length >= digits) {
        parts[0] + "." + decimals.substring(0, digits)
    } else {
        parts[0] + "." + decimals + "0".repeat(digits - decimals.length)
    }
}
