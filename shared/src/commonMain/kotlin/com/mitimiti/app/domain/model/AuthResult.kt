package com.mitimiti.app.domain.model

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()

    data class Error(val message: String) : AuthResult()
}
