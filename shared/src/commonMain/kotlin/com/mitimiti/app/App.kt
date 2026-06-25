package com.mitimiti.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mitimiti.app.presentation.navigation.AppNavigation
import com.mitimiti.app.presentation.theme.MitiMitiTheme

@Composable
@Suppress("FunctionName")
fun App() {
    MitiMitiTheme {
        Box(
            modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .safeContentPadding()
                    .fillMaxSize(),
        ) {
            AppNavigation()
        }
    }
}
