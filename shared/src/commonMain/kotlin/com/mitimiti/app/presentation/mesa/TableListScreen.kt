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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mitimiti.app.domain.model.Table
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.claymorphic

// Beautiful custom illustration representing a shared Mate cup (Argentine theme)
private val MateIllustration: ImageVector =
    ImageVector.Builder(
        name = "MateIllustration",
        defaultWidth = 120.dp,
        defaultHeight = 120.dp,
        viewportWidth = 120f,
        viewportHeight = 120f,
    ).apply {
        // Gourd Body (Marrón/Madera o Verde mate)
        path(fill = SolidColor(Color(0xFF8D6E63))) { // Gourd Brown
            moveTo(60f, 95f)
            curveTo(40f, 95f, 35f, 75f, 35f, 55f)
            horizontalLineTo(85f)
            curveTo(85f, 75f, 80f, 95f, 60f, 95f)
            close()
        }
        // Gourd Metal Rim (Gris metal)
        path(fill = SolidColor(Color(0xFFB0BEC5))) { // Metal Silver
            moveTo(33f, 50f)
            curveTo(33f, 47f, 45f, 45f, 60f, 45f)
            curveTo(75f, 45f, 87f, 47f, 87f, 50f)
            curveTo(87f, 53f, 75f, 55f, 60f, 55f)
            curveTo(45f, 55f, 33f, 53f, 33f, 50f)
            close()
        }
        // Yerba inside (Verde)
        path(fill = SolidColor(Color(0xFF4CAF50))) { // Yerba Green
            moveTo(38f, 50f)
            curveTo(38f, 50f, 50f, 48f, 60f, 50f)
            curveTo(70f, 52f, 82f, 50f, 82f, 50f)
            curveTo(82f, 50f, 75f, 53f, 60f, 53f)
            curveTo(45f, 53f, 38f, 50f, 38f, 50f)
            close()
        }
        // Bombilla (Metal Straw)
        path(
            stroke = SolidColor(Color(0xFFCFD8DC)),
            strokeLineWidth = 4f,
            strokeLineCap = StrokeCap.Round,
        ) {
            moveTo(60f, 50f)
            lineTo(78f, 20f)
        }
        // Bombilla Spoon Tip/Filter (Metal Gold/Yellow)
        path(fill = SolidColor(Color(0xFFFFD54F))) {
            moveTo(58f, 53f)
            arcToRelative(4f, 4f, 0f, true, true, 4f, -4f)
            close()
        }
        // Bombilla Curved Top Mouthpiece
        path(
            stroke = SolidColor(Color(0xFFFFD54F)),
            strokeLineWidth = 4f,
            strokeLineCap = StrokeCap.Round,
        ) {
            moveTo(78f, 20f)
            lineTo(83f, 18f)
        }
        // Warm Steam lines (Gris suave / Blanco)
        path(
            stroke = SolidColor(Color(0xFF90A4AE).copy(alpha = 0.5f)),
            strokeLineWidth = 3f,
            strokeLineCap = StrokeCap.Round,
        ) {
            // Steam line 1
            moveTo(45f, 35f)
            quadToRelative(-3f, -5f, 0f, -10f)
            quadToRelative(3f, -5f, 0f, -10f)
            // Steam line 2
            moveTo(55f, 32f)
            quadToRelative(-3f, -5f, 0f, -10f)
            quadToRelative(3f, -5f, 0f, -10f)
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
    onCreateTableClick: () -> Unit,
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
                        imageVector = MateIllustration,
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
                            "Creá una mesa nueva o unite a una existente para " +
                                "empezar a dividir tus gastos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    ClayButton(
                        onClick = onCreateTableClick,
                        modifier = Modifier.fillMaxWidth(0.8f),
                        cornerRadius = 16.dp,
                    ) {
                        Text(
                            text = "Armar una juntada 🎉",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
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
