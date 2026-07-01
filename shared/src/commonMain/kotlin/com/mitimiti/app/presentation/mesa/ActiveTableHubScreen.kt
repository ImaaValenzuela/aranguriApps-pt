package com.mitimiti.app.presentation.mesa

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mitimiti.app.presentation.cierre.SummaryScreen
import com.mitimiti.app.presentation.cierre.SummaryViewModel
import com.mitimiti.app.presentation.components.WizardProgressBar
import com.mitimiti.app.presentation.consumo.ExpenseScreen
import com.mitimiti.app.presentation.consumo.ExpenseViewModel

@Composable
@Suppress("LongMethod", "FunctionNaming")
fun ActiveTableHubScreen(
    tableId: String,
    tableViewModel: TableViewModel,
    expenseViewModel: ExpenseViewModel,
    summaryViewModel: SummaryViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedStep by remember { mutableStateOf(0) } // 0: Lobby, 1: Expenses, 2: Summary

    // Observamos el estado de ambos ViewModels para saber cuánto se puede avanzar
    val tableState by tableViewModel.uiState.collectAsState()
    val expenseState by expenseViewModel.uiState.collectAsState()

    // Reglas de validación:
    //   Paso 0 (Lobby):    siempre accesible
    //   Paso 1 (Gastos):   al menos 2 amigos en la juntada (1 amigo sumado + host)
    //   Paso 2 (Cuenta):   al menos 1 gasto cargado
    val hasFriends = tableState.friends.size >= 1
    val hasExpenses = expenseState.expenses.isNotEmpty()

    val stepsEnabled =
        listOf(
            true,
            hasFriends,
            hasFriends && hasExpenses,
        )

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onBack) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mis Juntadas")
                }
            }
            Text(
                text =
                    when (selectedStep) {
                        0 -> "Lobby"
                        1 -> "Consumos"
                        else -> "Resumen"
                    },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        // Wizard Progress Tracker con validación de pasos
        WizardProgressBar(
            currentStep = selectedStep,
            stepsEnabled = stepsEnabled,
            onStepClick = { step ->
                // Solo permite navegar si el paso está habilitado
                if (step < stepsEnabled.size && stepsEnabled[step]) {
                    selectedStep = step
                }
            },
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Content con AnimatedContent para transición suave entre pasos
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            AnimatedContent(
                targetState = selectedStep,
                transitionSpec = {
                    // Slide desde la derecha al avanzar, desde la izquierda al retroceder
                    if (targetState > initialState) {
                        (slideInHorizontally { it } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it } + fadeOut())
                    } else {
                        (slideInHorizontally { -it } + fadeIn()) togetherWith
                            (slideOutHorizontally { it } + fadeOut())
                    }
                },
                label = "WizardStepTransition",
            ) { step ->
                key(step) {
                    when (step) {
                        0 ->
                            TableScreen(
                                tableId = tableId,
                                viewModel = tableViewModel,
                                onNavigateToExpenses = {
                                    if (stepsEnabled[1]) selectedStep = 1
                                },
                                onNavigateToSummary = {
                                    if (stepsEnabled[2]) selectedStep = 2
                                },
                                onBack = onBack,
                                modifier = Modifier.fillMaxSize(),
                            )
                        1 ->
                            ExpenseScreen(
                                tableId = tableId,
                                viewModel = expenseViewModel,
                                onNavigateToLobby = { selectedStep = 0 },
                                onNavigateToSummary = {
                                    if (stepsEnabled[2]) selectedStep = 2
                                },
                                onBack = onBack,
                                modifier = Modifier.fillMaxSize(),
                            )
                        2 ->
                            SummaryScreen(
                                tableId = tableId,
                                viewModel = summaryViewModel,
                                onNavigateToLobby = { selectedStep = 0 },
                                onNavigateToExpenses = { selectedStep = 1 },
                                onRestart = onBack,
                                onBack = onBack,
                                modifier = Modifier.fillMaxSize(),
                            )
                    }
                }
            }
        }
    }
}
