package com.mitimiti.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitimiti.app.domain.model.AuthResult
import com.mitimiti.app.domain.model.User
import com.mitimiti.app.domain.repository.AuthRepository
import com.mitimiti.app.domain.repository.TableRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val isOnboarded: Boolean? = null,
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val tableRepository: TableRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                isAuthenticated = authRepository.isAuthenticated,
                user = authRepository.currentUser,
            )
        }
        var profileJob: kotlinx.coroutines.Job? = null
        viewModelScope.launch {
            authRepository.observeAuthState().collect { user ->
                _uiState.update {
                    it.copy(
                        isAuthenticated = user != null,
                        user = user,
                    )
                }

                profileJob?.cancel()
                if (user != null) {
                    profileJob = launch {
                        tableRepository.observeUserProfile(user.uid).collect { profile ->
                            val onboarded = profile != null &&
                                            profile.username.isNotBlank() &&
                                            profile.alias.isNotBlank() &&
                                            profile.cbu.isNotBlank()
                            if (onboarded && profile != null) {
                                com.mitimiti.app.presentation.perfil.AppSettings.updateUsername(profile.username)
                                com.mitimiti.app.presentation.perfil.AppSettings.updateAlias(profile.alias)
                                com.mitimiti.app.presentation.perfil.AppSettings.updateCbu(profile.cbu)
                            }
                            _uiState.update {
                                it.copy(isOnboarded = onboarded)
                            }
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(isOnboarded = null)
                    }
                }
            }
        }
    }

    fun signInWithEmail(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.signInWithEmail(email, password)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true, user = result.user) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun signUpWithEmail(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.signUpWithEmail(email, password)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true, user = result.user) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.signInWithGoogle(idToken)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true, user = result.user) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.update { AuthUiState() }
        }
    }

    fun saveUserProfile(
        username: String,
        alias: String,
        cbu: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val profile = com.mitimiti.app.domain.model.UserProfile(
                username = username.trim(),
                alias = alias.trim(),
                cbu = cbu.trim(),
            )
            val success = tableRepository.claimUsernameAndSaveProfile(userId, profile)
            if (success) {
                _uiState.update { it.copy(isLoading = false, isOnboarded = true) }
                onSuccess()
            } else {
                val errorMsg = "El nombre de usuario ya está en uso"
                _uiState.update { it.copy(isLoading = false, error = errorMsg) }
                onError(errorMsg)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
