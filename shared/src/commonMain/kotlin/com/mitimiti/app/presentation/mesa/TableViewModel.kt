package com.mitimiti.app.presentation.mesa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitimiti.app.compressImage
import com.mitimiti.app.domain.model.Friend
import com.mitimiti.app.domain.model.SplitType
import com.mitimiti.app.domain.model.Table
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.domain.model.UserProfile
import com.mitimiti.app.domain.repository.AuthRepository
import com.mitimiti.app.domain.repository.RealtimeSyncRepository
import com.mitimiti.app.domain.repository.TableRepository
import com.mitimiti.app.downloadBytes
import com.mitimiti.app.presentation.perfil.AppSettings
import com.mitimiti.app.toFirebaseData
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.storage
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
    val isClosed: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false,
)

data class CreateTableFormState(
    val tableNameInput: String = "",
    val creatorNickname: String = "",
    val selectedType: TableType = TableType.RESTAURANT,
    val tipPercentageInput: String = "10",
    val fixedCostInput: String = "0",
    val cubiertoInput: String = "0",
)

data class JoinTableFormState(
    val joinCodeInput: String = "",
    val joinNicknameInput: String = "",
)

class TableViewModel(
    private val tableRepository: TableRepository,
    private val authRepository: AuthRepository,
    private val syncRepository: RealtimeSyncRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TableUiState())
    val uiState: StateFlow<TableUiState> = _uiState.asStateFlow()

    private val _createFormState = MutableStateFlow(CreateTableFormState())
    val createFormState: StateFlow<CreateTableFormState> = _createFormState.asStateFlow()

    private val _joinFormState = MutableStateFlow(JoinTableFormState())
    val joinFormState: StateFlow<JoinTableFormState> = _joinFormState.asStateFlow()

    init {
        viewModelScope.launch {
            AppSettings.username.collect { username ->
                if (_createFormState.value.creatorNickname.isEmpty()) {
                    _createFormState.update { it.copy(creatorNickname = username) }
                }
                if (_joinFormState.value.joinNicknameInput.isEmpty()) {
                    _joinFormState.update { it.copy(joinNicknameInput = username) }
                }
            }
        }
    }

    fun updateCreateForm(
        tableNameInput: String = _createFormState.value.tableNameInput,
        creatorNickname: String = _createFormState.value.creatorNickname,
        selectedType: TableType = _createFormState.value.selectedType,
        tipPercentageInput: String = _createFormState.value.tipPercentageInput,
        fixedCostInput: String = _createFormState.value.fixedCostInput,
        cubiertoInput: String = _createFormState.value.cubiertoInput,
    ) {
        _createFormState.update {
            it.copy(
                tableNameInput = tableNameInput,
                creatorNickname = creatorNickname,
                selectedType = selectedType,
                tipPercentageInput = tipPercentageInput,
                fixedCostInput = fixedCostInput,
                cubiertoInput = cubiertoInput,
            )
        }
    }

    fun updateJoinForm(
        joinCodeInput: String = _joinFormState.value.joinCodeInput,
        joinNicknameInput: String = _joinFormState.value.joinNicknameInput,
    ) {
        _joinFormState.update {
            it.copy(
                joinCodeInput = joinCodeInput,
                joinNicknameInput = joinNicknameInput,
            )
        }
    }

    fun clearCreateForm() {
        _createFormState.value =
            CreateTableFormState(
                creatorNickname = AppSettings.username.value,
            )
    }

    fun clearJoinForm() {
        _joinFormState.value =
            JoinTableFormState(
                joinNicknameInput = AppSettings.username.value,
            )
    }

    fun refreshTable(tableId: String) {
        if (tableId.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                tableRepository.getTable(tableId)
                startObservingTable(tableId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private val _tables = MutableStateFlow<List<Table>>(emptyList())
    val tables: StateFlow<List<Table>> = _tables.asStateFlow()

    private val _avatarBytes = MutableStateFlow<ByteArray?>(null)
    val avatarBytes: StateFlow<ByteArray?> = _avatarBytes.asStateFlow()

    private var observeJob: Job? = null
    private var userTablesJob: Job? = null
    private var settingsSyncJob: Job? = null

    fun observeUserTables() {
        val userId = authRepository.currentUser?.uid ?: return
        userTablesJob?.cancel()
        userTablesJob =
            viewModelScope.launch {
                tableRepository.observeUserTables(userId).collect { list ->
                    _tables.value = list
                }
            }
        observeSettingsAndFriends()
    }

    fun observeSettingsAndFriends() {
        val userId = authRepository.currentUser?.uid ?: return
        settingsSyncJob?.cancel()
        settingsSyncJob =
            viewModelScope.launch {
                launch {
                    tableRepository.observeUserProfile(userId).collect { profile ->
                        if (profile != null) {
                            AppSettings.updateUsername(profile.username)
                            AppSettings.updateAlias(profile.alias)
                            AppSettings.updateCbu(profile.cbu)
                            AppSettings.updateAvatarUrl(profile.avatarUrl)
                            if (profile.avatarUrl != null) {
                                try {
                                    val storageRef = Firebase.storage.reference.child("avatars/$userId.jpg")
                                    val bytes = storageRef.downloadBytes(1024 * 1024 * 5)
                                    _avatarBytes.value = bytes
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    _avatarBytes.value = null
                                }
                            } else {
                                _avatarBytes.value = null
                            }
                        }
                    }
                }
                launch {
                    tableRepository.observeFrequentFriends(userId).collect { list ->
                        AppSettings.setFrequentFriends(list)
                    }
                }
            }
    }

    fun saveUserProfile(
        alias: String,
        cbu: String,
    ) {
        val userId = authRepository.currentUser?.uid ?: return
        val currentUsername = AppSettings.username.value
        val currentAvatarUrl = AppSettings.avatarUrl.value
        AppSettings.updateAlias(alias)
        AppSettings.updateCbu(cbu)
        viewModelScope.launch {
            tableRepository.saveUserProfile(userId, UserProfile(currentUsername, alias, cbu, currentAvatarUrl))
        }
    }

    fun uploadAvatar(bytes: ByteArray) {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val compressedBytes = bytes.compressImage()
                val storageRef = Firebase.storage.reference.child("avatars/$userId.jpg")
                val firebaseData = compressedBytes.toFirebaseData()
                storageRef.putData(firebaseData)
                val url = storageRef.getDownloadUrl()
                val currentUsername = AppSettings.username.value
                val currentAlias = AppSettings.alias.value
                val currentCbu = AppSettings.cbu.value
                AppSettings.updateAvatarUrl(url)
                tableRepository.saveUserProfile(userId, UserProfile(currentUsername, currentAlias, currentCbu, url))
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun searchAndAddFrequentFriend(
        username: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val trimmedUsername = username.trim().removePrefix("@")
        if (trimmedUsername.isEmpty()) {
            onError("El nombre de usuario no puede estar vacío")
            return
        }

        val currentUserId = authRepository.currentUser?.uid ?: return
        val currentUsername = AppSettings.username.value
        if (trimmedUsername.equals(currentUsername, ignoreCase = true)) {
            onError("No podés agregarte a vos mismo")
            return
        }

        viewModelScope.launch {
            val targetUserId = tableRepository.getUserIdByUsername(trimmedUsername)
            if (targetUserId == null) {
                onError("El usuario @$trimmedUsername no está registrado")
                return@launch
            }

            val targetProfile = tableRepository.getUserProfile(targetUserId)
            if (targetProfile == null) {
                onError("No se pudo obtener el perfil del usuario")
                return@launch
            }

            AppSettings.addFriend(targetProfile)
            tableRepository.saveFrequentFriends(currentUserId, AppSettings.frequentFriends.value)
            onSuccess()
        }
    }

    fun removeFrequentFriend(username: String) {
        val trimmed = username.trim().removePrefix("@")
        AppSettings.removeFriend(trimmed)
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            tableRepository.saveFrequentFriends(userId, AppSettings.frequentFriends.value)
        }
    }

    fun resetTableState() {
        observeJob?.cancel()
        _uiState.value = TableUiState()
    }

    fun createTable(
        name: String,
        type: TableType,
        tipPercentage: Double = 10.0,
        fixedExtraCost: Double = 0.0,
        cubiertoPerPerson: Double = 0.0,
        hostName: String,
        onSuccess: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid ?: return@launch
            _uiState.update { it.copy(isLoading = true, error = null) }
            val generatedCode = (100000..999999).random().toString()
            val host =
                Friend(
                    id = "friend_${ClockUtils.currentTimeMillis()}",
                    name = AppSettings.username.value.ifBlank { hostName },
                    alias = AppSettings.alias.value,
                    cbu = AppSettings.cbu.value,
                )
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
                    isClosed = false,
                )

            tableRepository.saveTable(newTable)
            tableRepository.saveUserTableRelation(userId, generatedCode)
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
                    isClosed = false,
                    isLoading = false,
                )
            }
            startObservingTable(generatedCode)
            onSuccess(generatedCode)
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
            val userId = authRepository.currentUser?.uid ?: return@launch
            _uiState.update { it.copy(isLoading = true, error = null) }
            val table = tableRepository.getTable(trimmedCode)
            if (table != null) {
                val exists = table.friends.any { it.name.equals(trimmedNickname, ignoreCase = true) }
                val updatedTable =
                    if (!exists) {
                        val newFriend =
                            Friend(
                                id = "friend_${ClockUtils.currentTimeMillis()}",
                                name = AppSettings.username.value.ifBlank { trimmedNickname },
                                alias = AppSettings.alias.value,
                                cbu = AppSettings.cbu.value,
                            )
                        table.copy(friends = table.friends + newFriend)
                    } else {
                        table
                    }

                tableRepository.saveTable(updatedTable)
                tableRepository.saveUserTableRelation(userId, trimmedCode)
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
                        isClosed = updatedTable.isClosed,
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

    fun addFrequentFriendToTable(friendProfile: UserProfile) {
        val currentTableId = _uiState.value.tableId
        if (currentTableId.isEmpty()) return

        viewModelScope.launch {
            val table = tableRepository.getTable(currentTableId)
            if (table != null) {
                val exists = table.friends.any { it.name.equals(friendProfile.username, ignoreCase = true) }
                if (!exists) {
                    val newFriend =
                        Friend(
                            id = "friend_${ClockUtils.currentTimeMillis()}",
                            name = friendProfile.username,
                            alias = friendProfile.alias,
                            cbu = friendProfile.cbu,
                        )
                    val updatedFriends = table.friends + newFriend
                    tableRepository.saveTable(table.copy(friends = updatedFriends))
                }
            }
        }
    }

    fun addFriendToTableByUsername(
        username: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val currentTableId = _uiState.value.tableId
        val trimmedUsername = username.trim().removePrefix("@")
        if (currentTableId.isEmpty() || trimmedUsername.isEmpty()) return

        viewModelScope.launch {
            val targetUserId = tableRepository.getUserIdByUsername(trimmedUsername)
            if (targetUserId == null) {
                onError("El usuario @$trimmedUsername no está registrado")
                return@launch
            }

            val targetProfile = tableRepository.getUserProfile(targetUserId)
            if (targetProfile == null) {
                onError("No se pudo obtener el perfil del usuario")
                return@launch
            }

            val table = tableRepository.getTable(currentTableId)
            if (table != null) {
                val exists = table.friends.any { it.name.equals(targetProfile.username, ignoreCase = true) }
                if (!exists) {
                    val newFriend =
                        Friend(
                            id = "friend_${ClockUtils.currentTimeMillis()}",
                            name = targetProfile.username,
                            alias = targetProfile.alias,
                            cbu = targetProfile.cbu,
                        )
                    val updatedFriends = table.friends + newFriend
                    tableRepository.saveTable(table.copy(friends = updatedFriends))
                    onSuccess()
                } else {
                    onError("El usuario ya está en la mesa")
                }
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
                                isClosed = table.isClosed,
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

    fun removeFriend(friendId: String) {
        val currentTableId = _uiState.value.tableId
        if (currentTableId.isEmpty()) return

        viewModelScope.launch {
            val table = tableRepository.getTable(currentTableId)
            if (table != null && !table.isClosed) {
                val updatedFriends = table.friends.filter { it.id != friendId }
                val updatedExpenses =
                    table.expenses.mapNotNull { expense ->
                        if (expense.paidByFriendId == friendId) {
                            null
                        } else {
                            val updatedSharers = expense.sharedByFriendIds.filter { it != friendId }
                            if (updatedSharers.isEmpty()) {
                                null
                            } else {
                                expense.copy(sharedByFriendIds = updatedSharers)
                            }
                        }
                    }
                tableRepository.saveTable(table.copy(friends = updatedFriends, expenses = updatedExpenses))
            }
        }
    }

    override fun onCleared() {
        observeJob?.cancel()
        userTablesJob?.cancel()
        settingsSyncJob?.cancel()
        super.onCleared()
    }
}

internal object ClockUtils {
    fun currentTimeMillis(): Long {
        return (1..1000000).random().toLong()
    }
}
