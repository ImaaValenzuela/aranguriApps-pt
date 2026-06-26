package com.mitimiti.app.data.repository

import com.mitimiti.app.domain.model.AuthResult
import com.mitimiti.app.domain.model.User
import com.mitimiti.app.domain.repository.AuthRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseAuthRepository : AuthRepository {

    private val auth = Firebase.auth

    override val currentUser: User?
        get() = auth.currentUser?.toUser()

    override val isAuthenticated: Boolean
        get() = auth.currentUser != null

    override suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password)
            val user = result.user
            if (user != null) {
                AuthResult.Success(user.toUser())
            } else {
                AuthResult.Error("Usuario no encontrado tras iniciar sesión")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al iniciar sesión")
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password)
            val user = result.user
            if (user != null) {
                AuthResult.Success(user.toUser())
            } else {
                AuthResult.Error("No se pudo crear el usuario")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al registrarse")
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult {
        return try {
            val credential = GoogleAuthProvider.credential(idToken, null)
            val result = auth.signInWithCredential(credential)
            val user = result.user
            if (user != null) {
                AuthResult.Success(user.toUser())
            } else {
                AuthResult.Error("No se pudo autenticar con Google")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al autenticar con Google")
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override fun observeAuthState(): Flow<User?> {
        return auth.authStateChanged.map { firebaseUser ->
            firebaseUser?.toUser()
        }
    }

    private fun FirebaseUser.toUser(): User {
        return User(
            uid = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoURL,
        )
    }
}
