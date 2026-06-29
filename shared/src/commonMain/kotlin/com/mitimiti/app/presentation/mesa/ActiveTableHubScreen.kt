package com.mitimiti.app.presentation.mesa

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
import androidx.compose.runtime.getValue
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

        // Wizard Progress Tracker (Clickable tabs)
        WizardProgressBar(
            currentStep = selectedStep,
            onStepClick = { step ->
                selectedStep = step
            },
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Content
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (selectedStep) {
                0 ->
                    TableScreen(
                        tableId = tableId,
                        viewModel = tableViewModel,
                        onNavigateToExpenses = { selectedStep = 1 },
                        onNavigateToSummary = { selectedStep = 2 },
                        onBack = onBack,
                        modifier = Modifier.fillMaxSize(),
                    )
                1 ->
                    ExpenseScreen(
                        tableId = tableId,
                        viewModel = expenseViewModel,
                        onNavigateToLobby = { selectedStep = 0 },
                        onNavigateToSummary = { selectedStep = 2 },
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
