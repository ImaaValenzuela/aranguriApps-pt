package com.mitimiti.app.presentation.cierre

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitimiti.app.domain.model.SplitType
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.claymorphic
import com.mitimiti.app.rememberTextSharer

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
    val isDark = isSystemInDarkTheme()
    val textSharer = rememberTextSharer()

    val emeraldGreen = Color(0xFF2E7D32)
    val coralRed = Color(0xFFD32F2F)

    LaunchedEffect(tableId) {
        viewModel.calculateSplit(tableId)
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        // Scrollable content area containing all card information
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── HERO CARD ────────────────────────────────────────────────────────
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .claymorphic(
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            cornerRadius = 20.dp,
                            elevation = 6.dp,
                            isDark = isDark,
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = state.tableName.ifBlank { "Resumen de la Juntada" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val totalText = state.billSummary?.total?.let { "\$${it.format(2)}" } ?: "—"
                    Text(
                        text = totalText,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 36.sp,
                    )
                    Text(
                        text = "Total de la vaquita",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    )
                }
            }

            // ── ESTADO CERRADA / TOGGLE DE DIVISIÓN ──────────────────────────────
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

            // ── AJUSTAR VAQUITA (propina/extras) ─────────────────────────────────
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
            }

            // ── RESUMEN FINAL (saldos + transferencias) ───────────────────────────
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .claymorphic(
                            backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                            cornerRadius = 24.dp,
                            elevation = 4.dp,
                            isDark = isDark,
                        ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Resumen Final",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    state.billSummary?.let { summary ->
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // ── Sub-Sección: Saldos ──────────────────────────────
                            Column {
                                Text(
                                    text = "Saldos individuales",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline,
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                summary.friendBills.forEachIndexed { index, bill ->
                                    Row(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        // Letter avatar
                                        Box(
                                            modifier =
                                                Modifier
                                                    .size(40.dp)
                                                    .background(
                                                        color = MaterialTheme.colorScheme.primaryContainer,
                                                        shape = RoundedCornerShape(20.dp),
                                                    ),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = bill.friendName.take(1).uppercase(),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            )
                                        }

                                        // Friend details
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = bill.friendName,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            ) {
                                                Text(
                                                    text = "Puso: \$${bill.amountPaid.format(2)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color =
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                            .copy(alpha = 0.8f),
                                                )
                                                Text(
                                                    text = "•",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color =
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                            .copy(alpha = 0.5f),
                                                )
                                                Text(
                                                    text = "Consumió: \$${bill.total.format(2)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color =
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                            .copy(alpha = 0.8f),
                                                )
                                            }
                                        }

                                        // Balance status badge
                                        val balance = bill.balance
                                        val balanceBgColor =
                                            when {
                                                balance > 0.01 -> emeraldGreen.copy(alpha = 0.12f)
                                                balance < -0.01 -> coralRed.copy(alpha = 0.12f)
                                                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                                            }
                                        val balanceTextColor =
                                            when {
                                                balance > 0.01 -> emeraldGreen
                                                balance < -0.01 -> coralRed
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        val balanceText =
                                            when {
                                                balance > 0.01 -> "+\$${balance.format(2)}"
                                                balance < -0.01 -> "-\$${(-balance).format(2)}"
                                                else -> "\$0.00"
                                            }
                                        val balanceLabel =
                                            when {
                                                balance > 0.01 -> "Recibe"
                                                balance < -0.01 -> "Debe"
                                                else -> "Al día"
                                            }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .background(balanceBgColor, shape = RoundedCornerShape(12.dp))
                                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                            ) {
                                                Text(
                                                    text = balanceText,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Black,
                                                    color = balanceTextColor,
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = balanceLabel,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = balanceTextColor.copy(alpha = 0.9f),
                                            )
                                        }
                                    }
                                    if (index < summary.friendBills.lastIndex) {
                                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                    }
                                }
                            }

                            // ── Sub-Sección: Totales ──────────────────────────────
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(16.dp),
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(16.dp),
                                        )
                                        .padding(14.dp),
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "Subtotal consumido",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = "\$${summary.subtotal.format(2)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "Propina para el Mozo",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = "\$${summary.totalTip.format(2)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "Extras y Cubiertos",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = "\$${summary.totalExtra.format(2)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                    Divider(
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                        modifier = Modifier.padding(vertical = 4.dp),
                                    )
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "Total Final",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = "\$${summary.total.format(2)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                }
                            }

                            // ── Sub-Sección: ¿Quién le transfiere a quién? ────────
                            if (summary.transactions.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                        Text(
                                            text = "¿Quién le transfiere a quién?",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }

                                    summary.transactions.forEach { tx ->
                                        Row(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .claymorphic(
                                                        backgroundColor =
                                                            if (isDark) {
                                                                MaterialTheme.colorScheme.surfaceVariant
                                                            } else {
                                                                Color(0xFFF0F4F8)
                                                            },
                                                        cornerRadius = 16.dp,
                                                        elevation = 1.dp,
                                                        isDark = isDark,
                                                    )
                                                    .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            // Debtor info
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = tx.fromFriendName,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                                Text(
                                                    text = "Debe transferir",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.outline,
                                                )
                                            }

                                            // Directional arrow icon
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .size(32.dp)
                                                        .background(
                                                            color =
                                                                MaterialTheme.colorScheme.primary
                                                                    .copy(alpha = 0.15f),
                                                            shape = RoundedCornerShape(16.dp),
                                                        ),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowForward,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp),
                                                )
                                            }

                                            // Creditor info
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = tx.toFriendName,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                                Text(
                                                    text = "Recibe el pago",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.outline,
                                                )
                                            }

                                            // Amount
                                            Text(
                                                text = "\$${tx.amount.format(2)}",
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.primary,
                                                style = MaterialTheme.typography.titleMedium,
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = emeraldGreen.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(16.dp),
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = emeraldGreen.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(16.dp),
                                            )
                                            .padding(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Text(
                                            text = "🎉",
                                            fontSize = 24.sp,
                                        )
                                        Column {
                                            Text(
                                                text = "¡Están a mano!",
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = emeraldGreen,
                                            )
                                            Text(
                                                text = "No hay deudas ni transferencias pendientes.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = emeraldGreen.copy(alpha = 0.8f),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } ?: Text(text = "Cargando divisiones...", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── BOTONES DE ACCIÓN ─────────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth()) {
            ClayButton(
                onClick = {
                    textSharer(state.formattedShareText)
                },
                modifier = Modifier.weight(1.2f),
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir",
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Compartir Resumen",
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
