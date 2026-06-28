package com.mitimiti.app.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Argentine Palette Colors
private val CelesteArgentina = Color(0xFF74ACDF)
private val CelesteArgentinaOscuro = Color(0xFF4A8BBF)
private val SolDeMayo = Color(0xFFF1C40F)
private val CelesteSoft = Color(0xFFEBF3FA)
private val CelesteBackgroundDark = Color(0xFF0F172A)
private val SurfaceDark = Color(0xFF1E293B)

private val LightColorScheme =
    lightColorScheme(
        primary = CelesteArgentina,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFD4E6F1),
        onPrimaryContainer = Color(0xFF1B4F72),
        secondary = SolDeMayo,
        onSecondary = Color.Black,
        background = CelesteSoft,
        onBackground = Color(0xFF1C1E21),
        surface = Color.White,
        onSurface = Color(0xFF1C1E21),
        surfaceVariant = Color(0xFFE2EAF4),
        onSurfaceVariant = Color(0xFF4A5568),
        error = Color(0xFFE74C3C),
        onError = Color.White,
        errorContainer = Color(0xFFFADBD8),
        onErrorContainer = Color(0xFF78281F),
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = CelesteArgentina,
        onPrimary = Color(0xFF0B2545),
        primaryContainer = Color(0xFF1F4E79),
        onPrimaryContainer = Color(0xFFD4E6F1),
        secondary = SolDeMayo,
        onSecondary = Color.Black,
        background = CelesteBackgroundDark,
        onBackground = Color(0xFFE2E8F0),
        surface = SurfaceDark,
        onSurface = Color(0xFFE2E8F0),
        surfaceVariant = Color(0xFF2D3748),
        onSurfaceVariant = Color(0xFFCBD5E0),
        error = Color(0xFFE74C3C),
        onError = Color.White,
        errorContainer = Color(0xFF78281F),
        onErrorContainer = Color(0xFFFADBD8),
    )

@Composable
@Suppress("FunctionNaming")
fun MitiMitiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

/**
 * A beautiful claymorphic modifier for a pillowy, soft 3D look with custom highlights and shadows.
 */
fun Modifier.claymorphic(
    backgroundColor: Color,
    cornerRadius: Dp = 24.dp,
    elevation: Dp = 6.dp,
    isDark: Boolean = false,
): Modifier {
    val highlightColor =
        if (isDark) {
            Color.White.copy(alpha = 0.12f)
        } else {
            Color.White.copy(alpha = 0.7f)
        }
    val shadowColor =
        if (isDark) {
            Color.Black.copy(alpha = 0.45f)
        } else {
            Color(0xFFBCCCDC).copy(alpha = 0.6f) // soft blue-gray shadow for Argentine palette
        }
    val bottomShadowColor =
        if (isDark) {
            Color.Black.copy(alpha = 0.35f)
        } else {
            Color(0xFF8FA9C4).copy(alpha = 0.3f)
        }

    return this.then(
        Modifier
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius),
                clip = false,
                ambientColor = shadowColor,
                spotColor = shadowColor,
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(cornerRadius),
            )
            .border(
                width = 2.dp,
                brush =
                    Brush.linearGradient(
                        colors = listOf(highlightColor, Color.Transparent, bottomShadowColor),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset.Infinite,
                    ),
                shape = RoundedCornerShape(cornerRadius),
            ),
    )
}

/**
 * A beautiful 3D claymorphic button that follows the Argentine theme.
 */
@Composable
fun ClayButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    cornerRadius: Dp = 24.dp,
    content: @Composable RowScope.() -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    val finalBg = if (enabled) backgroundColor else (if (isDark) Color(0xFF2D3748) else Color(0xFFE2E8F0))
    val finalContentColor = if (enabled) contentColor else (if (isDark) Color(0xFF718096) else Color(0xFFA0AEC0))

    Box(
        modifier =
            modifier
                .then(
                    if (enabled) {
                        Modifier.claymorphic(
                            backgroundColor = finalBg,
                            cornerRadius = cornerRadius,
                            elevation = 6.dp,
                            isDark = isDark,
                        )
                    } else {
                        Modifier.background(
                            color = finalBg,
                            shape = RoundedCornerShape(cornerRadius),
                        )
                    },
                )
                .clickable(enabled = enabled, onClick = onClick)
                .padding(vertical = 12.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}
