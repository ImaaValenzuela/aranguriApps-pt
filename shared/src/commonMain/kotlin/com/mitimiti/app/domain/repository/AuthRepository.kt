package com.mitimiti.app.domain.repository

import com.mitimiti.app.domain.model.AuthResult
import com.mitimiti.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: User?
    val isAuthenticated: Boolean

    suspend fun signInWithEmail(
        email: String,
        password: String,
    ): AuthResult

    suspend fun signUpWithEmail(
        email: String,
        password: String,
    ): AuthResult

    suspend fun signInWithGoogle(idToken: String): AuthResult

    suspend fun signOut()

    fun observeAuthState(): Flow<User?>
}
