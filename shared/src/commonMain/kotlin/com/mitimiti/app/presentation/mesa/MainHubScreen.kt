package com.mitimiti.app.presentation.mesa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.presentation.amigos.FriendsScreen
import com.mitimiti.app.presentation.perfil.ProfileScreen
import com.mitimiti.app.presentation.stats.StatsScreen
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.claymorphic
import com.mitimiti.app.rememberQRScanner

private val CameraIcon: ImageVector =
    ImageVector.Builder(
        name = "CameraIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(12f, 12f)
            arcToRelative(3f, 3f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 6f)
            arcToRelative(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -6f)
            close()
            moveTo(9f, 2f)
            lineTo(7.17f, 4f)
            lineTo(4f, 4f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2f, 6f)
            verticalLineTo(18f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 4f, 20f)
            horizontalLineTo(20f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 22f, 18f)
            verticalLineTo(6f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 20f, 4f)
            horizontalLineTo(16.83f)
            lineTo(15f, 2f)
            horizontalLineTo(9f)
            close()
            moveTo(12f, 17f)
            arcTo(5f, 5f, 0f, isMoreThanHalf = true, isPositiveArc = false, 12f, 7f)
            arcTo(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12f, 17f)
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
    val isDark = isSystemInDarkTheme()

    var selectedTab by remember { mutableStateOf(0) } // 0: Home, 1: Friends, 2: Plus (Overlay), 3: Stats, 4: Profile
    var showBottomSheet by remember { mutableStateOf(false) }
    var bottomSheetMode by remember { mutableStateOf(0) } // 0: Create, 1: Join

    // Form states for Create Table
    var tableNameInput by remember { mutableStateOf("") }
    var creatorNickname by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TableType.RESTAURANT) }
    var tipPercentageInput by remember { mutableStateOf("10") }
    var fixedCostInput by remember { mutableStateOf("0") }
    var cubiertoInput by remember { mutableStateOf("0") }

    // Form states for Join Table
    var joinCodeInput by remember { mutableStateOf("") }
    var joinNicknameInput by remember { mutableStateOf("") }

    val qrScanner =
        rememberQRScanner { result ->
            joinCodeInput =
                when {
                    result.contains("tableId=") -> result.substringAfter("tableId=").substringBefore("&")
                    result.contains("/table_lobby/") -> result.substringAfter("/table_lobby/").substringBefore("/")
                    result.contains("/table/") -> result.substringAfter("/table/").substringBefore("/")
                    else -> result.trim()
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
                    )
                1 ->
                    FriendsScreen(
                        onAddFriend = { name -> viewModel.addFrequentFriend(name) },
                        onRemoveFriend = { name -> viewModel.removeFrequentFriend(name) },
                    )
                3 -> StatsScreen(tables = tables)
                4 ->
                    ProfileScreen(
                        userEmail = userEmail,
                        onSaveProfile = { alias, cbu -> viewModel.saveUserProfile(alias, cbu) },
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
                                .clickable {
                                    showBottomSheet = true
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
                    // we use info as stats icon since stats icon is in extended pack
                    icon = Icons.Default.Info,
                    label = "Stats",
                    isSelected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    isDark = isDark,
                    modifier = Modifier.weight(1f),
                )

                // Tab 4: Profile (Perfil)
                BottomNavItem(
                    // Home represents configuration or account details
                    icon = Icons.Default.Home,
                    label = "Perfil",
                    isSelected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    isDark = isDark,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Custom Bottom Sheet Overlay dialog
        AnimatedVisibility(
            visible = showBottomSheet,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { showBottomSheet = false },
            )
        }

        AnimatedVisibility(
            visible = showBottomSheet,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 12.dp)
                        .claymorphic(
                            backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                            cornerRadius = 28.dp,
                            elevation = 10.dp,
                            isDark = isDark,
                        )
                        // Prevent closing sheet when clicking inside
                        .clickable(enabled = false) {},
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (bottomSheetMode == 0) "Armar Juntada" else "Sumarse a Juntada",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        IconButtonWrapper(
                            onClick = { showBottomSheet = false },
                            isDark = isDark,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }

                    // Toggle mode tabs in BottomSheet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        val isCreate = bottomSheetMode == 0
                        Box(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .clickable { bottomSheetMode = 0 }
                                    .claymorphic(
                                        backgroundColor =
                                            if (isCreate) {
                                                MaterialTheme.colorScheme.primary
                                            } else if (isDark) {
                                                MaterialTheme.colorScheme.surface
                                            } else {
                                                Color.White
                                            },
                                        cornerRadius = 14.dp,
                                        elevation = if (isCreate) 4.dp else 1.dp,
                                        isDark = isDark,
                                    )
                                    .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Nueva Juntada",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isCreate) Color.White else MaterialTheme.colorScheme.onSurface,
                            )
                        }

                        Box(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .clickable { bottomSheetMode = 1 }
                                    .claymorphic(
                                        backgroundColor =
                                            if (!isCreate) {
                                                MaterialTheme.colorScheme.primary
                                            } else if (isDark) {
                                                MaterialTheme.colorScheme.surface
                                            } else {
                                                Color.White
                                            },
                                        cornerRadius = 14.dp,
                                        elevation = if (!isCreate) 4.dp else 1.dp,
                                        isDark = isDark,
                                    )
                                    .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Unirse a Código",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (!isCreate) Color.White else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }

                    Divider()

                    // Content based on bottomSheetMode
                    if (bottomSheetMode == 0) {
                        // Create Table Form
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                val isRestaurantSelected = selectedType == TableType.RESTAURANT
                                val restBg =
                                    if (isRestaurantSelected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else if (isDark) {
                                        MaterialTheme.colorScheme.surface
                                    } else {
                                        Color.White
                                    }
                                val restTextCol =
                                    if (isRestaurantSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }

                                Box(
                                    modifier =
                                        Modifier
                                            .weight(1f)
                                            .clickable { selectedType = TableType.RESTAURANT }
                                            .claymorphic(
                                                backgroundColor = restBg,
                                                cornerRadius = 16.dp,
                                                elevation = if (isRestaurantSelected) 4.dp else 1.dp,
                                                isDark = isDark,
                                            )
                                            .padding(8.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingCart,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = restTextCol,
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Restaurante",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = restTextCol,
                                        )
                                    }
                                }

                                val isHomemadeSelected = selectedType == TableType.HOME_MADE
                                val homeBg =
                                    if (isHomemadeSelected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else if (isDark) {
                                        MaterialTheme.colorScheme.surface
                                    } else {
                                        Color.White
                                    }
                                val homeTextCol =
                                    if (isHomemadeSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }

                                Box(
                                    modifier =
                                        Modifier
                                            .weight(1f)
                                            .clickable { selectedType = TableType.HOME_MADE }
                                            .claymorphic(
                                                backgroundColor = homeBg,
                                                cornerRadius = 16.dp,
                                                elevation = if (isHomemadeSelected) 4.dp else 1.dp,
                                                isDark = isDark,
                                            )
                                            .padding(8.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = homeTextCol,
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Asado / Casa",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = homeTextCol,
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = tableNameInput,
                                onValueChange = { tableNameInput = it },
                                label = { Text("Nombre de la juntada") },
                                placeholder = { Text("Ej: Asado del viernes, Cena de fin de año") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                            )

                            OutlinedTextField(
                                value = creatorNickname,
                                onValueChange = { creatorNickname = it },
                                label = { Text("Tu apodo") },
                                placeholder = { Text("Ej: Juan") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                            )

                            if (selectedType == TableType.RESTAURANT) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = tipPercentageInput,
                                        onValueChange = { tipPercentageInput = it },
                                        label = { Text("Propina %") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                    )
                                    OutlinedTextField(
                                        value = cubiertoInput,
                                        onValueChange = { cubiertoInput = it },
                                        label = { Text("Cubierto $") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                    )
                                }
                            } else {
                                OutlinedTextField(
                                    value = fixedCostInput,
                                    onValueChange = { fixedCostInput = it },
                                    label = { Text("Costo extra común fijo ($)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            ClayButton(
                                onClick = {
                                    viewModel.createTable(
                                        name = tableNameInput,
                                        type = selectedType,
                                        tipPercentage = tipPercentageInput.toDoubleOrNull() ?: 10.0,
                                        fixedExtraCost = fixedCostInput.toDoubleOrNull() ?: 0.0,
                                        cubiertoPerPerson = cubiertoInput.toDoubleOrNull() ?: 0.0,
                                        hostName = creatorNickname,
                                        onSuccess = {
                                            showBottomSheet = false
                                            onNavigateToLobby(it)
                                        },
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = tableNameInput.isNotEmpty() && creatorNickname.isNotEmpty(),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Text("Crear Juntada", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    } else {
                        // Join Table Form
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                OutlinedTextField(
                                    value = joinCodeInput,
                                    onValueChange = { joinCodeInput = it },
                                    label = { Text("Código de la Juntada") },
                                    placeholder = { Text("Ej: 492041") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                )

                                ClayButton(
                                    onClick = {
                                        qrScanner()
                                    },
                                    modifier = Modifier.height(56.dp),
                                    cornerRadius = 16.dp,
                                    backgroundColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = Color.Black,
                                ) {
                                    Icon(
                                        imageVector = CameraIcon,
                                        contentDescription = "Escanear código QR",
                                        tint = Color.Black,
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = joinNicknameInput,
                                onValueChange = { joinNicknameInput = it },
                                label = { Text("Tu apodo / nombre") },
                                placeholder = { Text("Ej: Maria") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            ClayButton(
                                onClick = {
                                    viewModel.joinTable(
                                        code = joinCodeInput,
                                        nickname = joinNicknameInput,
                                        onSuccess = {
                                            showBottomSheet = false
                                            onNavigateToLobby(it)
                                        },
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = joinCodeInput.length >= 5 && joinNicknameInput.isNotEmpty(),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Text("Unirme a la Juntada", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
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
    val color =
        if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.5f,
            )
        }
    Column(
        modifier =
            modifier
                .clickable { onClick() }
                .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
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
private fun IconButtonWrapper(
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
