package com.mitimiti.app.presentation.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitimiti.app.domain.model.Table
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.presentation.theme.claymorphic
import com.mitimiti.app.utils.format

// Restaurant icon locally defined
private val RestaurantIcon: ImageVector =
    ImageVector.Builder(
        name = "RestaurantIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            // Fork
            moveTo(11f, 9f)
            horizontalLineTo(9f)
            verticalLineTo(2f)
            horizontalLineTo(7f)
            verticalLineTo(9f)
            horizontalLineTo(5f)
            verticalLineTo(2f)
            horizontalLineTo(3f)
            verticalLineTo(9f)
            curveTo(3f, 11.12f, 4.66f, 12.84f, 6.75f, 12.97f)
            verticalLineTo(22f)
            horizontalLineTo(9.25f)
            verticalLineTo(12.97f)
            curveTo(11.34f, 12.84f, 13f, 11.12f, 13f, 9f)
            verticalLineTo(2f)
            horizontalLineTo(11f)
            close()
            // Knife
            moveTo(16f, 2f)
            verticalLineTo(22f)
            horizontalLineTo(18.5f)
            verticalLineTo(15f)
            horizontalLineTo(21f)
            curveTo(21f, 15f, 21f, 2f, 16f, 2f)
            close()
        }
    }.build()

@Composable
@Suppress("FunctionNaming", "LongMethod")
fun StatsScreen(
    tables: List<Table>,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()

    // Calculate real stats from the list of tables
    val totalVaquitas = tables.size
    val activeVaquitas = tables.count { !it.isClosed }
    val closedVaquitas = tables.count { it.isClosed }

    // Count by table type
    val restaurantCount = tables.count { it.type == TableType.RESTAURANT }
    val homemadeCount = tables.count { it.type == TableType.HOME_MADE }

    // Friends average
    val totalFriendsInvolved = tables.sumOf { it.friends.size }
    val averageFriendsPerTable =
        if (totalVaquitas > 0) {
            totalFriendsInvolved.toDouble() / totalVaquitas
        } else {
            0.0
        }

    // Real financial data
    val totalSpent =
        tables.sumOf { table ->
            table.expenses.sumOf { it.cost }
        }

    val averageSpendPerTable =
        if (totalVaquitas > 0) {
            totalSpent / totalVaquitas
        } else {
            0.0
        }

    val mostExpensiveTable =
        tables.maxByOrNull { table ->
            table.expenses.sumOf { it.cost }
        }
    val mostExpensiveTableName = mostExpensiveTable?.name ?: "Ninguna"
    val mostExpensiveTableCost = mostExpensiveTable?.expenses?.sumOf { it.cost } ?: 0.0

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Estadísticas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── HERO CARD: HISTORICAL TOTAL SPEND ──────────────────────────────────
            item {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .claymorphic(
                                backgroundColor = MaterialTheme.colorScheme.primary,
                                cornerRadius = 24.dp,
                                elevation = 6.dp,
                                isDark = isDark,
                            )
                            .padding(20.dp),
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
                                text = "Gasto Total Histórico",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$${totalSpent.format(2)}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 32.sp,
                        )
                        Text(
                            text = "Monto total de gastos registrados",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                        )
                    }
                }
            }

            // ── GRID OF GENERAL METRICS ──────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Total Juntadas Card
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .claymorphic(
                                    backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                                    cornerRadius = 20.dp,
                                    elevation = 3.dp,
                                    isDark = isDark,
                                )
                                .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Juntadas",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalVaquitas",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    // Average spend Card
                    Box(
                        modifier =
                            Modifier
                                .weight(1.2f)
                                .claymorphic(
                                    backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                                    cornerRadius = 20.dp,
                                    elevation = 3.dp,
                                    isDark = isDark,
                                )
                                .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Prom. Gasto",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$${averageSpendPerTable.format(0)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    // Friends average Card
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .claymorphic(
                                    backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                                    cornerRadius = 20.dp,
                                    elevation = 3.dp,
                                    isDark = isDark,
                                )
                                .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Prom. Amigos",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${averageFriendsPerTable.format(1)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }

            // ── MOST EXPENSIVE JUNTADA CARD ──────────────────────────────────────
            if (totalSpent > 0.0) {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .claymorphic(
                                    backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                                    cornerRadius = 24.dp,
                                    elevation = 4.dp,
                                    isDark = isDark,
                                )
                                .padding(16.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color(0xFFF1C40F),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Juntada más Cara",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            HorizontalDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mostExpensiveTableName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "Record de gasto en una sola mesa",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    )
                                }
                                Text(
                                    text = "$${mostExpensiveTableCost.format(2)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }

            // ── VAQUITAS STATUS CARD ─────────────────────────────────────────────
            item {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .claymorphic(
                                backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                                cornerRadius = 24.dp,
                                elevation = 4.dp,
                                isDark = isDark,
                            )
                            .padding(16.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Estado de las Vaquitas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Activas",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = "$activeVaquitas",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF2E7D32),
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Cerradas",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = "$closedVaquitas",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFD32F2F),
                                )
                            }
                        }
                    }
                }
            }

            // ── CATEGORY SPLIT CARD ──────────────────────────────────────────────
            item {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .claymorphic(
                                backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                                cornerRadius = 24.dp,
                                elevation = 4.dp,
                                isDark = isDark,
                            )
                            .padding(16.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Distribución de Salidas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        HorizontalDivider()

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                imageVector = RestaurantIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Restaurante ($restaurantCount)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "${calculatePercentage(restaurantCount, totalVaquitas)}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Black,
                            )
                        }

                        // Restaurant progress bar
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        if (isDark) {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        } else {
                                            Color(0xFFE0E0E0)
                                        },
                                    ),
                        ) {
                            val fraction = if (totalVaquitas > 0) restaurantCount.toFloat() / totalVaquitas else 0f
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth(fraction)
                                        .fillMaxHeight()
                                        .background(MaterialTheme.colorScheme.primary),
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Asado / Casa ($homemadeCount)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "${calculatePercentage(homemadeCount, totalVaquitas)}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Black,
                            )
                        }

                        // Homemade progress bar
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        if (isDark) {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        } else {
                                            Color(0xFFE0E0E0)
                                        },
                                    ),
                        ) {
                            val fraction = if (totalVaquitas > 0) homemadeCount.toFloat() / totalVaquitas else 0f
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth(fraction)
                                        .fillMaxHeight()
                                        .background(MaterialTheme.colorScheme.secondary),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun calculatePercentage(
    count: Int,
    total: Int,
): Int {
    if (total == 0) return 0
    return ((count.toDouble() / total) * 100).toInt()
}
