package com.mitimiti.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mitimiti.app.presentation.navigation.AppNavigation
import com.mitimiti.app.presentation.theme.MitiMitiTheme

@Composable
@Suppress("FunctionName")
fun App(
    onGoogleSignInClick: () -> Unit = {},
    googleIdToken: String? = null,
    onGoogleTokenConsumed: () -> Unit = {},
    deepLinkUrl: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
) {
    MitiMitiTheme {
        Box(
            modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize(),
        ) {
            AppNavigation(
                onGoogleSignInClick = onGoogleSignInClick,
                googleIdToken = googleIdToken,
                onGoogleTokenConsumed = onGoogleTokenConsumed,
                deepLinkUrl = deepLinkUrl,
                onDeepLinkConsumed = onDeepLinkConsumed,
            )
        }
    }
}
