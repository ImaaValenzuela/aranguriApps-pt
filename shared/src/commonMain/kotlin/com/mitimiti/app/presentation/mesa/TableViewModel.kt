package com.mitimiti.app.presentation.mesa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitimiti.app.domain.model.Friend
import com.mitimiti.app.domain.model.Table
import com.mitimiti.app.domain.repository.RealtimeSyncRepository
import com.mitimiti.app.domain.repository.TableRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TableUiState(
    val tableId: String = "",
    val tableName: String = "",
    val friends: List<Friend> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class TableViewModel(
    private val tableRepository: TableRepository,
    private val syncRepository: RealtimeSyncRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TableUiState())
    val uiState: StateFlow<TableUiState> = _uiState.asStateFlow()

    fun createTable(name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val generatedId = "table_${ClockUtils.currentTimeMillis()}"
            val newTable = Table(id = generatedId, name = name)

            tableRepository.saveTable(newTable)
            syncRepository.startSync(generatedId)

            _uiState.update {
                it.copy(
                    tableId = generatedId,
                    tableName = name,
                    friends = emptyList(),
                    isLoading = false,
                )
            }
        }
    }

    fun addFriend(name: String) {
        val currentTableId = _uiState.value.tableId
        if (currentTableId.isEmpty() || name.trim().isEmpty()) return

        viewModelScope.launch {
            val newFriend = Friend(id = "friend_${ClockUtils.currentTimeMillis()}", name = name)
            val updatedFriends = _uiState.value.friends + newFriend

            _uiState.update { it.copy(friends = updatedFriends) }

            val currentTable = tableRepository.getTable(currentTableId)
            if (currentTable != null) {
                tableRepository.saveTable(currentTable.copy(friends = updatedFriends))
            }
        }
    }
}

internal object ClockUtils {
    fun currentTimeMillis(): Long {
        return (1..1000000).random().toLong()
    }
}
