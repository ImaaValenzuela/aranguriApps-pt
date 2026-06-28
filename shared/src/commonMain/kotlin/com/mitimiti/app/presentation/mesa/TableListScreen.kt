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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitimiti.app.domain.model.Table
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.presentation.theme.ClayButton
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

    var selectedTab by remember { mutableStateOf(0) } // 0: Mis Juntadas, 1: Armar Juntada, 2: Sumarse

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Miti y Miti",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
            )
            TextButton(onClick = onSignOut) {
                Text("Salir de la cuenta", color = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Claymorphic Tab Selection Row
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .claymorphic(
                        backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                        cornerRadius = 20.dp,
                        elevation = 2.dp,
                        isDark = isDark,
                    )
                    .padding(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val tabs = listOf("Mis Juntadas", "Armar Juntada", "Sumarse")
                val tabIcons = listOf(Icons.Default.List, Icons.Default.Add, Icons.Default.Person)
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    val tabBg = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    val textColor =
                        if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }

                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .clickable { selectedTab = index }
                                .then(
                                    if (isSelected) {
                                        Modifier.claymorphic(
                                            backgroundColor = tabBg,
                                            cornerRadius = 16.dp,
                                            elevation = 4.dp,
                                            isDark = isDark,
                                        )
                                    } else {
                                        Modifier
                                    },
                                )
                                .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = tabIcons[index],
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = textColor,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (selectedTab) {
            0 -> {
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
                                text = "Armá o sumate a una juntada para arrancar.",
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
            }
            1 -> {
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
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Detalles de la Juntada",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )

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
                                            elevation = if (isRestaurantSelected) 6.dp else 2.dp,
                                            isDark = isDark,
                                        )
                                        .padding(12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp),
                                        tint = restTextCol,
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Restaurante",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
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
                                            elevation = if (isHomemadeSelected) 6.dp else 2.dp,
                                            isDark = isDark,
                                        )
                                        .padding(12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp),
                                        tint = homeTextCol,
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Asado / Casa",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = homeTextCol,
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = tableNameInput,
                            onValueChange = { tableNameInput = it },
                            label = { Text("Nombre de la juntada") },
                            placeholder = { Text("Ej: Asadito de viernes, Previa, Birras...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                        )

                        OutlinedTextField(
                            value = creatorNickname,
                            onValueChange = { creatorNickname = it },
                            label = { Text("Tu apodo (El organizador)") },
                            placeholder = { Text("Ej: Juan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                        )

                        if (selectedType == TableType.RESTAURANT) {
                            OutlinedTextField(
                                value = tipPercentageInput,
                                onValueChange = { tipPercentageInput = it },
                                label = { Text("Propina sugerida (%)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                            )
                            OutlinedTextField(
                                value = cubiertoInput,
                                onValueChange = { cubiertoInput = it },
                                label = { Text("Cubierto por persona ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                            )
                        } else {
                            OutlinedTextField(
                                value = fixedCostInput,
                                onValueChange = { fixedCostInput = it },
                                label = { Text("Costo extra fijo común ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                            )
                        }

                        state.error?.let { err ->
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        ClayButton(
                            onClick = {
                                viewModel.createTable(
                                    name = tableNameInput,
                                    type = selectedType,
                                    tipPercentage = tipPercentageInput.toDoubleOrNull() ?: 10.0,
                                    fixedExtraCost = fixedCostInput.toDoubleOrNull() ?: 0.0,
                                    cubiertoPerPerson = cubiertoInput.toDoubleOrNull() ?: 0.0,
                                    hostName = creatorNickname,
                                    onSuccess = { onNavigateToLobby(it) },
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = tableNameInput.isNotEmpty() && creatorNickname.isNotEmpty(),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text("Armar Juntada", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
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
            2 -> {
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
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Sumarse a una juntada existente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )

                        OutlinedTextField(
                            value = joinCodeInput,
                            onValueChange = { joinCodeInput = it },
                            label = { Text("Código de la juntada (6 dígitos)") },
                            placeholder = { Text("Ej: 492041") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                        )

                        OutlinedTextField(
                            value = joinNicknameInput,
                            onValueChange = { joinNicknameInput = it },
                            label = { Text("Tu apodo / nombre") },
                            placeholder = { Text("Ej: Maria") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                        )

                        state.error?.let { err ->
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        ClayButton(
                            onClick = {
                                viewModel.joinTable(
                                    code = joinCodeInput,
                                    nickname = joinNicknameInput,
                                    onSuccess = { onNavigateToLobby(it) },
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = joinCodeInput.length >= 5 && joinNicknameInput.isNotEmpty(),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text("Sumarme a la Juntada", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    }
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
