package com.mitimiti.app.presentation.mesa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitimiti.app.domain.model.Friend
import com.mitimiti.app.domain.model.SplitType
import com.mitimiti.app.domain.model.Table
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.domain.repository.RealtimeSyncRepository
import com.mitimiti.app.domain.repository.TableRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TableUiState(
    val tableId: String = "",
    val tableName: String = "",
    val type: TableType = TableType.RESTAURANT,
    val splitType: SplitType = SplitType.BY_CONSUMPTION,
    val friends: List<Friend> = emptyList(),
    val tipPercentage: Double = 10.0,
    val fixedExtraCost: Double = 0.0,
    val cubiertoPerPerson: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class TableViewModel(
    private val tableRepository: TableRepository,
    private val syncRepository: RealtimeSyncRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TableUiState())
    val uiState: StateFlow<TableUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    fun createTable(
        name: String,
        type: TableType,
        tipPercentage: Double = 10.0,
        fixedExtraCost: Double = 0.0,
        cubiertoPerPerson: Double = 0.0,
        hostName: String,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val generatedCode = (100000..999999).random().toString()
            val host = Friend(id = "friend_${ClockUtils.currentTimeMillis()}", name = hostName)
            val newTable =
                Table(
                    id = generatedCode,
                    name = name,
                    type = type,
                    splitType = SplitType.BY_CONSUMPTION,
                    friends = listOf(host),
                    tipPercentage = tipPercentage,
                    fixedExtraCost = fixedExtraCost,
                    cubiertoPerPerson = cubiertoPerPerson,
                )

            tableRepository.saveTable(newTable)
            syncRepository.startSync(generatedCode)

            _uiState.update {
                it.copy(
                    tableId = generatedCode,
                    tableName = name,
                    type = type,
                    friends = listOf(host),
                    tipPercentage = tipPercentage,
                    fixedExtraCost = fixedExtraCost,
                    cubiertoPerPerson = cubiertoPerPerson,
                    isLoading = false,
                )
            }
            startObservingTable(generatedCode)
        }
    }

    fun joinTable(
        code: String,
        nickname: String,
        onSuccess: (String) -> Unit,
    ) {
        val trimmedCode = code.trim()
        val trimmedNickname = nickname.trim()
        if (trimmedCode.isEmpty() || trimmedNickname.isEmpty()) {
            _uiState.update { it.copy(error = "Código y nombre son obligatorios") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val table = tableRepository.getTable(trimmedCode)
            if (table != null) {
                val exists = table.friends.any { it.name.equals(trimmedNickname, ignoreCase = true) }
                val updatedTable =
                    if (!exists) {
                        val newFriend = Friend(id = "friend_${ClockUtils.currentTimeMillis()}", name = trimmedNickname)
                        table.copy(friends = table.friends + newFriend)
                    } else {
                        table
                    }

                tableRepository.saveTable(updatedTable)
                syncRepository.startSync(trimmedCode)

                _uiState.update {
                    it.copy(
                        tableId = updatedTable.id,
                        tableName = updatedTable.name,
                        type = updatedTable.type,
                        splitType = updatedTable.splitType,
                        friends = updatedTable.friends,
                        tipPercentage = updatedTable.tipPercentage,
                        fixedExtraCost = updatedTable.fixedExtraCost,
                        cubiertoPerPerson = updatedTable.cubiertoPerPerson,
                        isLoading = false,
                    )
                }
                startObservingTable(trimmedCode)
                onSuccess(trimmedCode)
            } else {
                _uiState.update { it.copy(error = "Mesa no encontrada. Verifica el código.", isLoading = false) }
            }
        }
    }

    fun startObservingTable(tableId: String) {
        observeJob?.cancel()
        observeJob =
            viewModelScope.launch {
                tableRepository.observeTable(tableId).collect { table ->
                    if (table != null) {
                        _uiState.update {
                            it.copy(
                                tableId = table.id,
                                tableName = table.name,
                                type = table.type,
                                splitType = table.splitType,
                                friends = table.friends,
                                tipPercentage = table.tipPercentage,
                                fixedExtraCost = table.fixedExtraCost,
                                cubiertoPerPerson = table.cubiertoPerPerson,
                            )
                        }
                    }
                }
            }
    }

    fun addFriend(name: String) {
        val currentTableId = _uiState.value.tableId
        val trimmedName = name.trim()
        if (currentTableId.isEmpty() || trimmedName.isEmpty()) return

        viewModelScope.launch {
            val table = tableRepository.getTable(currentTableId)
            if (table != null) {
                val exists = table.friends.any { it.name.equals(trimmedName, ignoreCase = true) }
                if (!exists) {
                    val newFriend = Friend(id = "friend_${ClockUtils.currentTimeMillis()}", name = trimmedName)
                    val updatedFriends = table.friends + newFriend
                    tableRepository.saveTable(table.copy(friends = updatedFriends))
                }
            }
        }
    }

    override fun onCleared() {
        observeJob?.cancel()
        super.onCleared()
    }
}

internal object ClockUtils {
    fun currentTimeMillis(): Long {
        return (1..1000000).random().toLong()
    }
}
