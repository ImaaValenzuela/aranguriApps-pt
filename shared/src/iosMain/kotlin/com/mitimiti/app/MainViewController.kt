package com.mitimiti.app

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.ComposeUIViewController
import com.mitimiti.app.auth.IosGoogleSignInHelper

@Suppress("FunctionName")
fun MainViewController() = ComposeUIViewController {
    val token by IosGoogleSignInHelper.pendingToken.collectAsState()

    App(
        onGoogleSignInClick = {
            // iOS: el botón de Google en LoginScreen llama a este callback.
            // Desde acé tenés que invocar GIDSignIn.shared.signIn()
            // y en el handler llamar a:
            //   IosGoogleSignInHelper.setGoogleToken(idToken)
        },
        googleIdToken = token,
        onGoogleTokenConsumed = { IosGoogleSignInHelper.clearToken() },
    )
}
