package com.mitimiti.app.presentation.cierre

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mitimiti.app.domain.model.SplitType
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.presentation.components.WizardProgressBar
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.claymorphic

@Composable
@Suppress("FunctionNaming", "LongMethod")
fun SummaryScreen(
    tableId: String,
    viewModel: SummaryViewModel,
    onNavigateToLobby: (tableId: String) -> Unit,
    onNavigateToExpenses: (tableId: String) -> Unit,
    onRestart: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val isDark = isSystemInDarkTheme()

    val emeraldGreen = Color(0xFF2E7D32)
    val coralRed = Color(0xFFD32F2F)

    LaunchedEffect(tableId) {
        viewModel.calculateSplit(tableId)
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = onBack,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Volver a Cargar Gastos")
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Resumen de la Vaquita",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        // Wizard Progress Tracker (Step 2) - Clickable tabs
        WizardProgressBar(
            currentStep = 2,
            onStepClick = { step ->
                when (step) {
                    0 -> onNavigateToLobby(tableId)
                    1 -> onNavigateToExpenses(tableId)
                }
            },
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Split Strategy Toggle Selector
        if (state.isClosed) {
            val strategyText = if (state.splitType == SplitType.EQUAL) "Partes Iguales" else "Consumo"
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .claymorphic(
                            backgroundColor = MaterialTheme.colorScheme.errorContainer,
                            cornerRadius = 16.dp,
                            elevation = 2.dp,
                            isDark = isDark,
                        )
                        .padding(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LA JUNTADA SE CERRÓ - Dividido por $strategyText",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val isEqual = state.splitType == SplitType.EQUAL
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .clickable { viewModel.updateSplitType(SplitType.EQUAL) }
                            .claymorphic(
                                backgroundColor =
                                    if (isEqual) {
                                        MaterialTheme.colorScheme.primary
                                    } else if (isDark) {
                                        MaterialTheme.colorScheme.surface
                                    } else {
                                        Color.White
                                    },
                                cornerRadius = 16.dp,
                                elevation = if (isEqual) 6.dp else 2.dp,
                                isDark = isDark,
                            )
                            .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Miti y Miti",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color =
                            if (isEqual) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                    )
                }

                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .clickable { viewModel.updateSplitType(SplitType.BY_CONSUMPTION) }
                            .claymorphic(
                                backgroundColor =
                                    if (!isEqual) {
                                        MaterialTheme.colorScheme.primary
                                    } else if (isDark) {
                                        MaterialTheme.colorScheme.surface
                                    } else {
                                        Color.White
                                    },
                                cornerRadius = 16.dp,
                                elevation = if (!isEqual) 6.dp else 2.dp,
                                isDark = isDark,
                            )
                            .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Por Consumo",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color =
                            if (!isEqual) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dynamic tip and extra input fields directly in final screen
        if (!state.isClosed) {
            var tipInput by remember(state.tipPercentage) { mutableStateOf(state.tipPercentage.toString()) }
            var extraInput by remember(state.fixedExtraCost) { mutableStateOf(state.fixedExtraCost.toString()) }
            var cubiertoInput by remember(
                state.cubiertoPerPerson,
            ) { mutableStateOf(state.cubiertoPerPerson.toString()) }

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .claymorphic(
                            backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                            cornerRadius = 20.dp,
                            elevation = 3.dp,
                            isDark = isDark,
                        )
                        .padding(12.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ajustar Vaquita",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (state.tableType == TableType.RESTAURANT) {
                            OutlinedTextField(
                                value = tipInput,
                                onValueChange = {
                                    tipInput = it
                                    val tip = it.toDoubleOrNull() ?: 0.0
                                    val extra = extraInput.toDoubleOrNull() ?: 0.0
                                    val cubierto = cubiertoInput.toDoubleOrNull() ?: 0.0
                                    viewModel.updateTipAndExtra(tip, extra, cubierto)
                                },
                                label = { Text("Propina %") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                            )
                            OutlinedTextField(
                                value = cubiertoInput,
                                onValueChange = {
                                    cubiertoInput = it
                                    val tip = tipInput.toDoubleOrNull() ?: 0.0
                                    val extra = extraInput.toDoubleOrNull() ?: 0.0
                                    val cubierto = it.toDoubleOrNull() ?: 0.0
                                    viewModel.updateTipAndExtra(tip, extra, cubierto)
                                },
                                label = { Text("Cubierto $") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                            )
                        } else {
                            OutlinedTextField(
                                value = extraInput,
                                onValueChange = {
                                    extraInput = it
                                    val tip = tipInput.toDoubleOrNull() ?: 0.0
                                    val extra = it.toDoubleOrNull() ?: 0.0
                                    val cubierto = cubiertoInput.toDoubleOrNull() ?: 0.0
                                    viewModel.updateTipAndExtra(tip, extra, cubierto)
                                },
                                label = { Text("Costo Extra Fijo $") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Balances and transactions card
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .claymorphic(
                        backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                        cornerRadius = 24.dp,
                        elevation = 4.dp,
                        isDark = isDark,
                    ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Quién pone y quién se lleva",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                state.billSummary?.let { summary ->
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        item {
                            Text(
                                text = "Saldos Finales",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        items(summary.friendBills) { bill ->
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = bill.friendName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text =
                                            "Garpó: $${bill.amountPaid.format(2)} | " +
                                                "Consumió: $${bill.total.format(2)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    )
                                }

                                val balance = bill.balance
                                val balanceColor =
                                    if (balance > 0.01) {
                                        emeraldGreen
                                    } else if (balance < -0.01) {
                                        coralRed
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                val balanceText =
                                    if (balance > 0.01) {
                                        "+$${balance.format(2)}"
                                    } else if (balance < -0.01) {
                                        "-$${(-balance).format(2)}"
                                    } else {
                                        "$0.00"
                                    }

                                Text(
                                    text = balanceText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = balanceColor,
                                )
                            }
                            Divider()
                        }

                        if (summary.transactions.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Transferencias para saldar deudas:",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            items(summary.transactions) { tx ->
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .claymorphic(
                                                backgroundColor =
                                                    if (isDark) {
                                                        MaterialTheme.colorScheme.surfaceVariant
                                                    } else {
                                                        Color(0xFFF0F4F8)
                                                    },
                                                cornerRadius = 14.dp,
                                                elevation = 1.dp,
                                                isDark = isDark,
                                            )
                                            .padding(8.dp),
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = tx.fromFriendName,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "le transfiere a",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline,
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = tx.toFriendName,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = "$${tx.amount.format(2)}",
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.bodyLarge,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Subtotal: ", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = "$${summary.subtotal.format(2)}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Propina para el Mozo: ", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = "$${summary.totalTip.format(2)}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Extras/Cubiertos: ", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = "$${summary.totalExtra.format(2)}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Total de la Vaquita: ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "$${summary.total.format(2)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                } ?: Text(text = "Cargando divisiones...", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            ClayButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(state.formattedShareText))
                    viewModel.onClipboardCopied()
                },
                modifier = Modifier.weight(1.2f),
                backgroundColor =
                    if (state.isCopied) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                contentColor = if (state.isCopied) Color.Black else MaterialTheme.colorScheme.onPrimary,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = if (state.isCopied) Icons.Default.Check else Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (state.isCopied) "¡Copiado!" else "Copiar Resumen",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            ClayButton(
                onClick = {
                    if (!state.isClosed) {
                        viewModel.closeTable()
                    }
                    onRestart()
                },
                modifier = Modifier.weight(1f),
                backgroundColor =
                    if (state.isClosed) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                contentColor = if (state.isClosed) MaterialTheme.colorScheme.onPrimary else Color.White,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = if (state.isClosed) Icons.Default.Home else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (state.isClosed) "Volver a Juntadas" else "Cerrar Vaquita",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
