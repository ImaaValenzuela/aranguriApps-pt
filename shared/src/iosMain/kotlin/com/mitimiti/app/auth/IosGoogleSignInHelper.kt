package com.mitimiti.app.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object IosGoogleSignInHelper {
    private val _pendingToken = MutableStateFlow<String?>(null)
    val pendingToken: StateFlow<String?> = _pendingToken.asStateFlow()

    fun setGoogleToken(token: String) {
        _pendingToken.value = token
    }

    fun clearToken() {
        _pendingToken.value = null
    }
}
