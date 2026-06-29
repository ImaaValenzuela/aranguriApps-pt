package com.mitimiti.app.presentation.mesa

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mitimiti.app.domain.model.Table
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.presentation.theme.claymorphic

@Composable
@Suppress("LongMethod", "FunctionNaming")
fun TableListScreen(
    viewModel: TableViewModel,
    onNavigateToLobby: (String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val tables by viewModel.tables.collectAsState()
    val isDark = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        viewModel.observeUserTables()
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Miti y Miti",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Mis Juntadas (${tables.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start),
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (tables.isEmpty()) {
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No tenés juntadas registradas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Presioná el botón central (+) para armar una.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(tables) { table ->
                    TableHistoryCard(
                        table = table,
                        onClick = { onNavigateToLobby(table.id) },
                    )
                }
            }
        }

        if (state.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TableHistoryCard(
    table: Table,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) MaterialTheme.colorScheme.surface else Color.White

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .claymorphic(
                    backgroundColor = cardBg,
                    cornerRadius = 20.dp,
                    elevation = 4.dp,
                    isDark = isDark,
                )
                .clickable(onClick = onClick)
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector =
                        if (table.type == TableType.RESTAURANT) {
                            Icons.Default.ShoppingCart
                        } else {
                            Icons.Default.Home
                        },
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = table.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Código: ${table.id} • ${table.friends.size} amigos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Box(modifier = Modifier.padding(start = 8.dp)) {
                if (table.isClosed) {
                    Box(
                        modifier =
                            Modifier
                                .claymorphic(
                                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                                    cornerRadius = 12.dp,
                                    elevation = 2.dp,
                                    isDark = isDark,
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Cerrada",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                } else {
                    Box(
                        modifier =
                            Modifier
                                .claymorphic(
                                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                    cornerRadius = 12.dp,
                                    elevation = 2.dp,
                                    isDark = isDark,
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Activa",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}
