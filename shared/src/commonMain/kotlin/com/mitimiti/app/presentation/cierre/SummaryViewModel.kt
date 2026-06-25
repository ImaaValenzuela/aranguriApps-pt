package com.mitimiti.app.presentation.cierre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitimiti.app.domain.repository.TableRepository
import com.mitimiti.app.domain.usecase.CalculateSplitExpensesUseCase
import com.mitimiti.app.domain.usecase.TableBillSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SummaryUiState(
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

    fun calculateSplit(tableId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val table = tableRepository.getTable(tableId)
            if (table != null) {
                val summary = calculateSplitExpensesUseCase(table)
                val shareText = buildShareText(summary)
                _uiState.update {
                    it.copy(
                        billSummary = summary,
                        formattedShareText = shareText,
                        isLoading = false,
                    )
                }
            } else {
                _uiState.update { it.copy(error = "Mesa no encontrada", isLoading = false) }
            }
        }
    }

    fun onClipboardCopied() {
        _uiState.update { it.copy(isCopied = true) }
    }

    private fun buildShareText(summary: TableBillSummary): String {
        return buildString {
            appendLine("🍽️ MitiMiti - Resumen de Cuenta 🍽️")
            appendLine("---------------------------------")
            summary.friendBills.forEach { bill ->
                appendLine("👤 ${bill.friendName}: $${bill.total.format(2)}")
                appendLine(
                    "   (Consumo: $${bill.subtotal.format(
                        2,
                    )} + Propina: $${bill.tipShare.format(2)} + Extra: $${bill.extraShare.format(2)})",
                )
            }
            appendLine("---------------------------------")
            appendLine("🧾 Subtotal: $${summary.subtotal.format(2)}")
            appendLine("💰 Propina Total: $${summary.totalTip.format(2)}")
            appendLine("⚡ Extras/Cubiertos: $${summary.totalExtra.format(2)}")
            appendLine("💵 Total a Pagar: $${summary.total.format(2)}")
        }
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
