package com.mitimiti.app.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mitimiti.app.presentation.components.AnimatedLogo

@Composable
@Suppress("FunctionNaming")
fun SplashScreen(
    onAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedLogo(
            logoSize = 220.dp,
            onAnimationFinished = onAnimationFinished,
        )
    }
}
