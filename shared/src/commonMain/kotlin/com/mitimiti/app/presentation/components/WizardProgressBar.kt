package com.mitimiti.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitimiti.app.presentation.theme.claymorphic

@Composable
@Suppress("FunctionNaming")
fun WizardProgressBar(
    currentStep: Int,
    onStepClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    // El paso máximo al que se puede navegar (inclusive).
    // Por defecto = currentStep, lo que permite ir hacia atrás pero no adelante sin completar.
    maxAllowedStep: Int = currentStep,
) {
    val isDark = isSystemInDarkTheme()
    val steps = listOf("Juntada", "Gastos", "Cuenta")
    val icons =
        listOf(
            Icons.Default.Person,
            Icons.Default.ShoppingCart,
            Icons.Default.List,
        )

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        steps.forEachIndexed { index, title ->
            val isActive = index == currentStep
            val isCompleted = index < currentStep
            val isLocked = index > maxAllowedStep

            val bubbleBgColor =
                when {
                    isActive -> MaterialTheme.colorScheme.primary
                    isCompleted -> MaterialTheme.colorScheme.primaryContainer
                    else ->
                        if (isDark) {
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        }
                }

            val textColor =
                when {
                    isActive -> MaterialTheme.colorScheme.onPrimary
                    isCompleted -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                }

            // Pasos bloqueados se muestran al 40% de opacidad
            val stepAlpha = if (isLocked) 0.4f else 1f

            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .alpha(stepAlpha)
                        .claymorphic(
                            backgroundColor = bubbleBgColor,
                            cornerRadius = 16.dp,
                            elevation = if (isActive) 6.dp else 2.dp,
                            isDark = isDark,
                        )
                        .then(
                            // Solo clickable si el paso no está bloqueado
                            if (!isLocked) Modifier.clickable { onStepClick(index) } else Modifier,
                        )
                        .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = icons[index],
                        contentDescription = title,
                        tint = textColor,
                        modifier = Modifier.size(16.dp).padding(end = 4.dp),
                    )
                    Text(
                        text = title,
                        color = textColor,
                        fontSize = 11.sp,
                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Bold,
                    )
                }
            }
        }
    }
}
