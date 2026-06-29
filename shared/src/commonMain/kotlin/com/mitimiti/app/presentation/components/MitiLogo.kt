package com.mitimiti.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
@Suppress("FunctionNaming")
fun MitiLogo(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
) {
    val celesteColor = Color(0xFF74ACDF)
    val rightHalfBg = Color.White

    Canvas(
        modifier = modifier.size(size),
    ) {
        val canvasWidth = this.size.width
        val canvasHeight = this.size.height
        val baseRadius = canvasWidth * 0.42f
        val cx = canvasWidth / 2
        val cy = canvasHeight / 2

        // A small visual separation gap (4f) between halves
        val gap = 4f
        val cxLeft = cx - gap
        val cxRight = cx + gap

        // Draw Left Semicircle (Celeste)
        drawArc(
            color = celesteColor,
            startAngle = 90f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(cxLeft - baseRadius, cy - baseRadius),
            size = Size(baseRadius * 2, baseRadius * 2),
        )

        // Draw Left Semicircle interior details (White)
        val leftDetailColor = Color.White
        val dotRadius = baseRadius * 0.055f
        val topY = cy - baseRadius * 0.28f

        // Top 3 Dots
        drawCircle(
            color = leftDetailColor,
            radius = dotRadius,
            center = Offset(cxLeft - baseRadius * 0.45f, topY),
        )
        drawCircle(
            color = leftDetailColor,
            radius = dotRadius,
            center = Offset(cxLeft - baseRadius * 0.65f, topY),
        )
        drawCircle(
            color = leftDetailColor,
            radius = dotRadius,
            center = Offset(cxLeft - baseRadius * 0.25f, topY),
        )

        // Middle Bar
        val barThickness = baseRadius * 0.09f
        val barWidth = baseRadius * 0.6f
        drawRoundRect(
            color = leftDetailColor,
            topLeft = Offset(cxLeft - baseRadius * 0.75f, cy - barThickness / 2),
            size = Size(barWidth, barThickness),
            cornerRadius = CornerRadius(barThickness / 2, barThickness / 2),
        )

        // Bottom 3 Dots
        val bottomY = cy + baseRadius * 0.28f
        drawCircle(
            color = leftDetailColor,
            radius = dotRadius,
            center = Offset(cxLeft - baseRadius * 0.45f, bottomY),
        )
        drawCircle(
            color = leftDetailColor,
            radius = dotRadius,
            center = Offset(cxLeft - baseRadius * 0.65f, bottomY),
        )
        drawCircle(
            color = leftDetailColor,
            radius = dotRadius,
            center = Offset(cxLeft - baseRadius * 0.25f, bottomY),
        )

        // Draw Right Semicircle (White fill)
        drawArc(
            color = rightHalfBg,
            startAngle = 270f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(cxRight - baseRadius, cy - baseRadius),
            size = Size(baseRadius * 2, baseRadius * 2),
        )

        // Draw Right Semicircle border outline (Celeste)
        drawArc(
            color = celesteColor,
            startAngle = 270f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cxRight - baseRadius, cy - baseRadius),
            size = Size(baseRadius * 2, baseRadius * 2),
            style = Stroke(width = baseRadius * 0.045f, cap = StrokeCap.Round),
        )

        // Draw Right Semicircle interior details (Celeste)
        val rightDetailColor = celesteColor

        // Top 3 Dots
        drawCircle(
            color = rightDetailColor,
            radius = dotRadius,
            center = Offset(cxRight + baseRadius * 0.45f, topY),
        )
        drawCircle(
            color = rightDetailColor,
            radius = dotRadius,
            center = Offset(cxRight + baseRadius * 0.65f, topY),
        )
        drawCircle(
            color = rightDetailColor,
            radius = dotRadius,
            center = Offset(cxRight + baseRadius * 0.25f, topY),
        )

        // Middle Bar
        drawRoundRect(
            color = rightDetailColor,
            topLeft = Offset(cxRight + baseRadius * 0.15f, cy - barThickness / 2),
            size = Size(barWidth, barThickness),
            cornerRadius = CornerRadius(barThickness / 2, barThickness / 2),
        )

        // Bottom 3 Dots
        drawCircle(
            color = rightDetailColor,
            radius = dotRadius,
            center = Offset(cxRight + baseRadius * 0.45f, bottomY),
        )
        drawCircle(
            color = rightDetailColor,
            radius = dotRadius,
            center = Offset(cxRight + baseRadius * 0.65f, bottomY),
        )
        drawCircle(
            color = rightDetailColor,
            radius = dotRadius,
            center = Offset(cxRight + baseRadius * 0.25f, bottomY),
        )
    }
}
