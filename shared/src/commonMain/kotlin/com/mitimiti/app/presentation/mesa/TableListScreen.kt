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
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mitimiti.app.domain.model.Table
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.presentation.theme.claymorphic

// Beautiful custom illustration representing a shared receipt/vaquita with friends
private val EmptyStateIllustration: ImageVector =
    ImageVector.Builder(
        name = "EmptyStateIllustration",
        defaultWidth = 120.dp,
        defaultHeight = 120.dp,
        viewportWidth = 120f,
        viewportHeight = 120f,
    ).apply {
        // Person 1 (Celeste Argentina)
        path(fill = SolidColor(Color(0xFF74ACDF))) {
            moveTo(40f, 45f)
            arcToRelative(14f, 14f, 0f, true, true, 0f, -28f)
            arcToRelative(14f, 14f, 0f, false, true, 0f, 28f)
            close()
            moveTo(40f, 52f)
            curveTo(26f, 52f, 8f, 59f, 8f, 73f)
            verticalLineToRelative(7f)
            horizontalLineToRelative(64f)
            verticalLineToRelative(-7f)
            curveTo(72f, 59f, 54f, 52f, 40f, 52f)
            close()
        }
        // Person 2 (Sol de Mayo - Gold)
        path(fill = SolidColor(Color(0xFFF1C40F))) {
            moveTo(80f, 40f)
            arcToRelative(12f, 12f, 0f, true, true, 0f, -24f)
            arcToRelative(12f, 12f, 0f, false, true, 0f, 24f)
            close()
            moveTo(80f, 46f)
            curveTo(70f, 46f, 56f, 51f, 56f, 62f)
            verticalLineToRelative(6f)
            horizontalLineToRelative(48f)
            verticalLineToRelative(-6f)
            curveTo(104f, 51f, 90f, 46f, 80f, 46f)
            close()
        }
        // Shared Dollar sign in the foreground representing splitting
        path(fill = SolidColor(Color(0xFF2E7D32))) {
            moveTo(60f, 65f)
            curveTo(54.48f, 65f, 50f, 69.48f, 50f, 75f)
            curveTo(50f, 80.52f, 54.48f, 85f, 60f, 85f)
            curveTo(65.52f, 85f, 70f, 80.52f, 70f, 75f)
            curveTo(70f, 69.48f, 65.52f, 65f, 60f, 65f)
            close()
            moveTo(61f, 81f)
            horizontalLineTo(59f)
            verticalLineTo(79.9f)
            curveTo(57.65f, 79.75f, 56.4f, 78.9f, 56f, 77.5f)
            lineTo(57.8f, 76.75f)
            curveTo(58.05f, 77.55f, 58.75f, 78.1f, 60f, 78.1f)
            curveTo(61.25f, 78.1f, 61.9f, 77.5f, 61.9f, 76.7f)
            curveTo(61.9f, 74.5f, 56.2f, 74.9f, 56.2f, 71.3f)
            curveTo(56.2f, 69.8f, 57.4f, 68.8f, 59f, 68.5f)
            verticalLineTo(67f)
            horizontalLineTo(61f)
            verticalLineTo(68.5f)
            curveTo(62.25f, 68.7f, 63.3f, 69.4f, 63.7f, 70.5f)
            lineTo(61.9f, 71.25f)
            curveTo(61.65f, 70.65f, 61.1f, 70.3f, 60f, 70.3f)
            curveTo(58.95f, 70.3f, 58.3f, 70.8f, 58.3f, 71.5f)
            curveTo(58.3f, 73.5f, 64f, 73.1f, 64f, 76.6f)
            curveTo(64f, 78.2f, 62.75f, 79.4f, 61f, 79.9f)
            verticalLineTo(81f)
            close()
        }
    }.build()

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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp),
                ) {
                    Icon(
                        imageVector = EmptyStateIllustration,
                        contentDescription = null,
                        modifier = Modifier.size(110.dp),
                        tint = Color.Unspecified,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "¡Aún no tenés juntadas!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text =
                            "Creá una mesa nueva o\n" +
                                "unite a una existente con el botón (+) de abajo para empezar a dividir tus gastos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
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
                            RestaurantIcon
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
