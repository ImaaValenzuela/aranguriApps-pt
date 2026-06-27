package com.mitimiti.app.presentation.cierre

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mitimiti.app.domain.model.SplitType

@Composable
@Suppress("FunctionNaming")
fun SummaryScreen(
    tableId: String,
    viewModel: SummaryViewModel,
    onRestart: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current

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
                Text("← Volver al Detalle")
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Cuenta Final",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Split Strategy Toggle Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val isEqual = state.splitType == SplitType.EQUAL
            Button(
                onClick = { viewModel.updateSplitType(SplitType.EQUAL) },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            if (isEqual) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                        contentColor =
                            if (isEqual) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                    ),
                modifier = Modifier.weight(1f),
            ) {
                Text("Partes Iguales", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.updateSplitType(SplitType.BY_CONSUMPTION) },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            if (!isEqual) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                        contentColor =
                            if (!isEqual) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                    ),
                modifier = Modifier.weight(1f),
            ) {
                Text("Por Consumo", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Balances de Comensales",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(8.dp))

                state.billSummary?.let { summary ->
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        item {
                            Text(
                                text = "Saldos Individuales",
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
                                            "Pagó: $${bill.amountPaid.format(2)} | " +
                                                "Consume: $${bill.total.format(2)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary,
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
                                Text(
                                    text = "💸 Transferencias para Saldar",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            items(summary.transactions) { tx ->
                                Card(
                                    colors =
                                        CardDefaults.cardColors(
                                            containerColor =
                                                MaterialTheme.colorScheme.surfaceVariant
                                                    .copy(alpha = 0.5f),
                                        ),
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = tx.fromFriendName,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "paga a",
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
                            Text(text = "Propina total: ", style = MaterialTheme.typography.bodyMedium)
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
                                text = "Total de la Mesa: ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "$${summary.total.format(2)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                } ?: Text(text = "Cargando divisiones...", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(state.formattedShareText))
                    viewModel.onClipboardCopied()
                },
                modifier = Modifier.weight(1.2f),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            if (state.isCopied) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                    ),
            ) {
                Text(if (state.isCopied) "¡Copiado!" else "Copiar Resumen")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onRestart,
                modifier = Modifier.weight(1f),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
            ) {
                Text("Cerrar Mesa")
            }
        }
    }
}
