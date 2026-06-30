package com.mitimiti.app.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@Suppress("FunctionNaming", "LongMethod")
fun AnimatedLogo(
    modifier: Modifier = Modifier,
    logoSize: Dp = 200.dp,
    onAnimationFinished: () -> Unit = {},
) {
    // Animation states
    val splitOffset = remember { Animatable(180f) } // starts far apart
    val logoScale = remember { Animatable(0.3f) }
    val detailsAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    val celesteColor = Color(0xFF74ACDF)
    val rightHalfBg = Color.White

    LaunchedEffect(Unit) {
        // Step 1: Scale up the logo while sliding the halves together
        launch {
            logoScale.animateTo(
                targetValue = 1.0f,
                animationSpec =
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow,
                    ),
            )
        }
        launch {
            // Animates splitOffset to 4f (close together but with a micro gap)
            splitOffset.animateTo(
                targetValue = 4f,
                animationSpec =
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow,
                    ),
            )
        }

        delay(600)

        // Step 2: Fade in the inner details (dots/bars)
        launch {
            detailsAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 400, easing = LinearEasing),
            )
        }

        // Step 3: Fade in the brand text
        launch {
            textAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 500, easing = LinearEasing),
            )
        }

        delay(1200) // Keep visible for a moment
        onAnimationFinished()
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(
            modifier =
                Modifier
                    .size(logoSize),
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val baseRadius = canvasWidth * 0.42f
            val cx = canvasWidth / 2
            val cy = canvasHeight / 2

            val currentOffset = splitOffset.value
            val currentScale = logoScale.value
            val currentAlpha = detailsAlpha.value

            val cxLeft = cx - currentOffset
            val cxRight = cx + currentOffset

            // Apply scale animation
            val scaledRadius = baseRadius * currentScale

            // Draw Left Semicircle (Celeste)
            drawArc(
                color = celesteColor,
                startAngle = 90f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cxLeft - scaledRadius, cy - scaledRadius),
                size = Size(scaledRadius * 2, scaledRadius * 2),
            )

            // Draw Left Semicircle interior details (White)
            if (currentAlpha > 0f) {
                val detailColor = Color.White.copy(alpha = currentAlpha)

                // Top 3 Dots
                val dotRadius = scaledRadius * 0.055f
                val topY = cy - scaledRadius * 0.28f
                drawCircle(
                    color = detailColor,
                    radius = dotRadius,
                    center = Offset(cxLeft - scaledRadius * 0.45f, topY),
                )
                drawCircle(
                    color = detailColor,
                    radius = dotRadius,
                    center = Offset(cxLeft - scaledRadius * 0.65f, topY),
                )
                drawCircle(
                    color = detailColor,
                    radius = dotRadius,
                    center = Offset(cxLeft - scaledRadius * 0.25f, topY),
                )

                // Middle Bar
                val barThickness = scaledRadius * 0.09f
                val barWidth = scaledRadius * 0.6f
                drawRoundRect(
                    color = detailColor,
                    topLeft = Offset(cxLeft - scaledRadius * 0.75f, cy - barThickness / 2),
                    size = Size(barWidth, barThickness),
                    cornerRadius = CornerRadius(barThickness / 2, barThickness / 2),
                )

                // Bottom 3 Dots
                val bottomY = cy + scaledRadius * 0.28f
                drawCircle(
                    color = detailColor,
                    radius = dotRadius,
                    center = Offset(cxLeft - scaledRadius * 0.45f, bottomY),
                )
                drawCircle(
                    color = detailColor,
                    radius = dotRadius,
                    center = Offset(cxLeft - scaledRadius * 0.65f, bottomY),
                )
                drawCircle(
                    color = detailColor,
                    radius = dotRadius,
                    center = Offset(cxLeft - scaledRadius * 0.25f, bottomY),
                )
            }

            // Draw Right Semicircle (White fill)
            drawArc(
                color = rightHalfBg,
                startAngle = 270f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cxRight - scaledRadius, cy - scaledRadius),
                size = Size(scaledRadius * 2, scaledRadius * 2),
            )

            // Draw Right Semicircle border outline (Celeste)
            drawArc(
                color = celesteColor,
                startAngle = 270f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cxRight - scaledRadius, cy - scaledRadius),
                size = Size(scaledRadius * 2, scaledRadius * 2),
                style = Stroke(width = scaledRadius * 0.045f, cap = StrokeCap.Round),
            )

            // Draw Right Semicircle interior details (Celeste)
            if (currentAlpha > 0f) {
                val detailColor = celesteColor.copy(alpha = currentAlpha)

                // Top 3 Dots
                val dotRadius = scaledRadius * 0.055f
                val topY = cy - scaledRadius * 0.28f
                drawCircle(
                    color = detailColor,
                    radius = dotRadius,
                    center = Offset(cxRight + scaledRadius * 0.45f, topY),
                )
                drawCircle(
                    color = detailColor,
                    radius = dotRadius,
                    center = Offset(cxRight + scaledRadius * 0.65f, topY),
                )
                drawCircle(
                    color = detailColor,
                    radius = dotRadius,
                    center = Offset(cxRight + scaledRadius * 0.25f, topY),
                )

                // Middle Bar
                val barThickness = scaledRadius * 0.09f
                val barWidth = scaledRadius * 0.6f
                drawRoundRect(
                    color = detailColor,
                    topLeft = Offset(cxRight + scaledRadius * 0.15f, cy - barThickness / 2),
                    size = Size(barWidth, barThickness),
                    cornerRadius = CornerRadius(barThickness / 2, barThickness / 2),
                )

                // Bottom 3 Dots
                val bottomY = cy + scaledRadius * 0.28f
                drawCircle(
                    color = detailColor,
                    radius = dotRadius,
                    center = Offset(cxRight + scaledRadius * 0.45f, bottomY),
                )
                drawCircle(
                    color = detailColor,
                    radius = dotRadius,
                    center = Offset(cxRight + scaledRadius * 0.65f, bottomY),
                )
                drawCircle(
                    color = detailColor,
                    radius = dotRadius,
                    center = Offset(cxRight + scaledRadius * 0.25f, bottomY),
                )
            }
        }

        if (textAlpha.value > 0f) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Miti y Miti",
                style =
                    MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = celesteColor.copy(alpha = textAlpha.value),
                    ),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Dividí sin vueltas 🇦🇷",
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = textAlpha.value * 0.7f),
                    ),
            )
        }
    }
}
