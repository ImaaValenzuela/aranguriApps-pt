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
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitimiti.app.domain.model.Table
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.presentation.theme.claymorphic

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

    // Calculate a simulated average spend or total amount
    val totalFriendsInvolved = tables.sumOf { it.friends.size }
    val averageFriendsPerTable = if (totalVaquitas > 0) {
        totalFriendsInvolved.toDouble() / totalVaquitas
    } else {
        0.0
    }

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
            // General metrics cards
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
                                .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Juntadas",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalVaquitas",
                                style = MaterialTheme.typography.displaySmall,
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
                                .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Prom. Amigos",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${averageFriendsPerTable.format(1)}",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }

            // Vaquitas status layout
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
                        Divider()
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

            // Category Split / Distribution Card
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
                        Divider()

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
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
                                        }
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
                                        }
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

private fun calculatePercentage(count: Int, total: Int): Int {
    if (total == 0) return 0
    return ((count.toDouble() / total) * 100).toInt()
}

internal fun Double.format(digits: Int): String {
    val raw = this.toString()
    val parts = raw.split(".")
    if (parts.size < 2) return raw
    val decimals = parts[1]
    return if (decimals.length >= digits) {
        parts[0] + "." + decimals.substring(0, digits)
    } else {
        parts[0] + "." + decimals + "0".repeat(digits - decimals.length)
    }
}
