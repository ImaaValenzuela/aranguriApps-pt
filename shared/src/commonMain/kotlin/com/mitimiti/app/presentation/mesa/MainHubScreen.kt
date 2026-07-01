package com.mitimiti.app.presentation.mesa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitimiti.app.presentation.amigos.FriendsScreen
import com.mitimiti.app.presentation.perfil.ProfileScreen
import com.mitimiti.app.presentation.stats.StatsScreen
import com.mitimiti.app.presentation.theme.claymorphic
import kotlinx.coroutines.delay

// Bar chart icon for Stats tab (no extended icons dependency needed)
private val BarChartIcon: ImageVector =
    ImageVector.Builder(
        name = "BarChartIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(5f, 9.5f)
            curveTo(5f, 9.22f, 5.22f, 9f, 5.5f, 9f)
            horizontalLineTo(7.5f)
            curveTo(7.78f, 9f, 8f, 9.22f, 8f, 9.5f)
            verticalLineTo(19f)
            horizontalLineTo(5f)
            close()
            moveTo(11f, 3.5f)
            curveTo(11f, 3.22f, 11.22f, 3f, 11.5f, 3f)
            horizontalLineTo(13.5f)
            curveTo(13.78f, 3f, 14f, 3.22f, 14f, 3.5f)
            verticalLineTo(19f)
            horizontalLineTo(11f)
            close()
            moveTo(17f, 13.5f)
            curveTo(17f, 13.22f, 17.22f, 13f, 17.5f, 13f)
            horizontalLineTo(19.5f)
            curveTo(19.78f, 13f, 20f, 13.22f, 20f, 13.5f)
            verticalLineTo(19f)
            horizontalLineTo(17f)
            close()
            moveTo(3f, 19f)
            horizontalLineTo(21f)
            verticalLineTo(21f)
            horizontalLineTo(3f)
            close()
        }
    }.build()

// Restaurant/fork icon for Restaurante type
internal val RestaurantIcon: ImageVector =
    ImageVector.Builder(
        name = "RestaurantIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
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
@Suppress("LongMethod", "FunctionNaming", "ComplexMethod")
fun MainHubScreen(
    viewModel: TableViewModel,
    userEmail: String?,
    onNavigateToLobby: (String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val tables by viewModel.tables.collectAsState()
    val avatarBytes by viewModel.avatarBytes.collectAsState()
    val isDark = isSystemInDarkTheme()

    var selectedTab by remember { mutableStateOf(0) } // 0: Home, 1: Friends, 2: Plus (Overlay), 3: Stats, 4: Profile
    var showCreateSheet by remember { mutableStateOf(false) }
    var showJoinSheet by remember { mutableStateOf(false) }
    var showTooltip by remember { mutableStateOf(false) }

    LaunchedEffect(showTooltip) {
        if (showTooltip) {
            delay(2000)
            showTooltip = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.observeUserTables()
    }

    Box(modifier = modifier.fillMaxSize().navigationBarsPadding()) {
        // Main Screen Content based on selected tab
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    // Space for bottom navigation bar
                    .padding(bottom = 80.dp),
        ) {
            when (selectedTab) {
                0 ->
                    TableListScreen(
                        viewModel = viewModel,
                        onNavigateToLobby = onNavigateToLobby,
                        onSignOut = onSignOut,
                        onCreateTableClick = { showCreateSheet = true },
                    )
                1 ->
                    FriendsScreen(
                        viewModel = viewModel,
                    )
                3 -> StatsScreen(tables = tables)
                4 ->
                    ProfileScreen(
                        userEmail = userEmail,
                        avatarBytes = avatarBytes,
                        onSaveProfile = { alias, cbu -> viewModel.saveUserProfile(alias, cbu) },
                        onUploadAvatar = { bytes -> viewModel.uploadAvatar(bytes) },
                        onSignOut = onSignOut,
                    )
            }
        }

        // 5-element Bottom Navigation Bar
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(72.dp)
                    .claymorphic(
                        backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                        cornerRadius = 24.dp,
                        elevation = 8.dp,
                        isDark = isDark,
                    ),
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Tab 0: Home (Mis Juntadas)
                BottomNavItem(
                    icon = Icons.Default.List,
                    label = "Juntadas",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    isDark = isDark,
                    modifier = Modifier.weight(1f),
                )

                // Tab 1: Friends (Social)
                BottomNavItem(
                    icon = Icons.Default.Person,
                    label = "Amigos",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    isDark = isDark,
                    modifier = Modifier.weight(1f),
                )

                // Tab 2: Convex Center Action Button '+'
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .offset(y = (-18).dp),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showTooltip,
                        enter = fadeIn() + slideInVertically { it / 2 },
                        exit = fadeOut() + slideOutVertically { it / 2 },
                        modifier = Modifier.offset(y = (-52).dp),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .claymorphic(
                                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                        cornerRadius = 10.dp,
                                        elevation = 4.dp,
                                        isDark = isDark,
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = "Nueva Juntada",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }

                    Box(
                        modifier =
                            Modifier
                                .size(56.dp)
                                .claymorphic(
                                    backgroundColor = MaterialTheme.colorScheme.primary,
                                    cornerRadius = 28.dp,
                                    elevation = 6.dp,
                                    isDark = isDark,
                                )
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = { showTooltip = true },
                                        onTap = { showCreateSheet = true },
                                    )
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Nueva Juntada",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }

                // Tab 3: Stats (Estadísticas)
                BottomNavItem(
                    icon = BarChartIcon,
                    label = "Stats",
                    isSelected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    isDark = isDark,
                    modifier = Modifier.weight(1f),
                )

                // Tab 4: Profile (Perfil)
                BottomNavItem(
                    icon = Icons.Default.Person,
                    label = "Perfil",
                    isSelected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    isDark = isDark,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        CreateTableBottomSheet(
            visible = showCreateSheet,
            onDismissRequest = { showCreateSheet = false },
            onSwitchToJoin = {
                showCreateSheet = false
                showJoinSheet = true
            },
            viewModel = viewModel,
            onNavigateToLobby = onNavigateToLobby,
        )

        JoinTableBottomSheet(
            visible = showJoinSheet,
            onDismissRequest = { showJoinSheet = false },
            onSwitchToCreate = {
                showJoinSheet = false
                showCreateSheet = true
            },
            viewModel = viewModel,
            onNavigateToLobby = onNavigateToLobby,
        )
    }
}

@Composable
@Suppress("FunctionNaming")
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier,
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val color = if (isSelected) activeColor else inactiveColor
    val pillBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)

    Column(
        modifier =
            modifier
                .clickable { onClick() }
                .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Pill background behind icon when active
        Box(
            modifier =
                if (isSelected) {
                    Modifier
                        .background(color = pillBg, shape = CircleShape)
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                } else {
                    Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = color,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
@Suppress("FunctionNaming")
internal fun IconButtonWrapper(
    onClick: () -> Unit,
    isDark: Boolean,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(36.dp)
                .claymorphic(
                    backgroundColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF5F5F5),
                    cornerRadius = 18.dp,
                    elevation = 2.dp,
                    isDark = isDark,
                )
                .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
