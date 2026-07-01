package com.mitimiti.app.presentation.cierre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitimiti.app.domain.model.SplitType
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.domain.repository.TableRepository
import com.mitimiti.app.domain.usecase.CalculateSplitExpensesUseCase
import com.mitimiti.app.domain.usecase.TableBillSummary
import com.mitimiti.app.utils.format
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SummaryUiState(
    val tableId: String = "",
    val tableName: String = "",
    val splitType: SplitType = SplitType.BY_CONSUMPTION,
    val billSummary: TableBillSummary? = null,
    val formattedShareText: String = "",
    val isClosed: Boolean = false,
    val isCopied: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val tipPercentage: Double = 10.0,
    val fixedExtraCost: Double = 0.0,
    val cubiertoPerPerson: Double = 0.0,
    val tableType: TableType = TableType.RESTAURANT,
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
                        val shareText = buildShareText(table.name, summary, table.friends)
                        _uiState.update {
                            it.copy(
                                tableId = tableId,
                                tableName = table.name,
                                splitType = table.splitType,
                                billSummary = summary,
                                formattedShareText = shareText,
                                isClosed = table.isClosed,
                                isLoading = false,
                                tipPercentage = table.tipPercentage,
                                fixedExtraCost = table.fixedExtraCost,
                                cubiertoPerPerson = table.cubiertoPerPerson,
                                tableType = table.type,
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
            if (table != null && !table.isClosed) {
                tableRepository.saveTable(table.copy(splitType = splitType))
            }
        }
    }

    fun updateTipAndExtra(
        tipPercentage: Double,
        fixedExtraCost: Double,
        cubiertoPerPerson: Double,
    ) {
        val tableId = _uiState.value.tableId
        if (tableId.isEmpty()) return

        viewModelScope.launch {
            val table = tableRepository.getTable(tableId)
            if (table != null && !table.isClosed) {
                tableRepository.saveTable(
                    table.copy(
                        tipPercentage = tipPercentage,
                        fixedExtraCost = fixedExtraCost,
                        cubiertoPerPerson = cubiertoPerPerson,
                    ),
                )
            }
        }
    }

    fun closeTable() {
        val tableId = _uiState.value.tableId
        if (tableId.isEmpty()) return

        viewModelScope.launch {
            val table = tableRepository.getTable(tableId)
            if (table != null) {
                tableRepository.saveTable(table.copy(isClosed = true))
            }
        }
    }

    fun onClipboardCopied() {
        _uiState.update { it.copy(isCopied = true) }
    }

    private fun buildShareText(
        tableName: String,
        summary: TableBillSummary,
        friends: List<com.mitimiti.app.domain.model.Friend>,
    ): String {
        return buildString {
            appendLine("Miti y Miti - Resumen de la Vaquita: $tableName")
            appendLine("---------------------------------")
            summary.friendBills.forEach { bill ->
                val balanceStr =
                    if (bill.balance >= 0) {
                        "recibe $${bill.balance.format(2)}"
                    } else {
                        "debe $${(-bill.balance).format(2)}"
                    }
                appendLine("- ${bill.friendName}:")
                appendLine("   Garpó: $${bill.amountPaid.format(2)}")
                appendLine("   Consume: $${bill.total.format(2)}")
                appendLine("   Saldo: $balanceStr")
                appendLine()
            }
            appendLine("---------------------------------")
            if (summary.transactions.isNotEmpty()) {
                appendLine("Transferencias para saldar:")
                summary.transactions.forEach { tx ->
                    appendLine("  * ${tx.fromFriendName} le transfiere $${tx.amount.format(2)} a ${tx.toFriendName}")
                    val recipient = friends.find { it.name.equals(tx.toFriendName, ignoreCase = true) }
                    if (recipient != null) {
                        val rAlias = recipient.alias
                        val rCbu = recipient.cbu
                        if (!rAlias.isNullOrBlank() || !rCbu.isNullOrBlank()) {
                            append("    (Datos de ${tx.toFriendName}: ")
                            val parts = mutableListOf<String>()
                            if (!rAlias.isNullOrBlank()) parts.add("Alias: $rAlias")
                            if (!rCbu.isNullOrBlank()) parts.add("CBU/CVU: $rCbu")
                            append(parts.joinToString(" | "))
                            appendLine(")")
                        }
                    }
                }
            } else {
                appendLine("¡Quedaron a mano! No hay deudas.")
            }
            appendLine("---------------------------------")
            appendLine("Subtotal: $${summary.subtotal.format(2)}")
            appendLine("Propina del Mozo: $${summary.totalTip.format(2)}")
            appendLine("Extras/Cubiertos: $${summary.totalExtra.format(2)}")
            appendLine("Total de la Vaquita: $${summary.total.format(2)}")

            val alias = com.mitimiti.app.presentation.perfil.AppSettings.alias.value
            val cbu = com.mitimiti.app.presentation.perfil.AppSettings.cbu.value
            if (alias.isNotBlank() || cbu.isNotBlank()) {
                appendLine("---------------------------------")
                appendLine("Datos para Transferencia:")
                if (alias.isNotBlank()) appendLine("  Alias: $alias")
                if (cbu.isNotBlank()) appendLine("  CBU/CVU: $cbu")
            }
        }
    }

    override fun onCleared() {
        observeJob?.cancel()
        super.onCleared()
    }
}
