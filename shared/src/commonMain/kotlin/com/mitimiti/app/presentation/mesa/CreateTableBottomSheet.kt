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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mitimiti.app.domain.model.TableType
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.claymorphic

@Composable
@Suppress("LongMethod", "FunctionNaming")
fun CreateTableBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    onSwitchToJoin: () -> Unit,
    viewModel: TableViewModel,
    onNavigateToLobby: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    val formState by viewModel.createFormState.collectAsState()

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onDismissRequest() },
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
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
                        .clickable(enabled = false) {},
            ) {
                Column(
                    modifier =
                        Modifier
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState())
                            .imePadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Armar Juntada",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        IconButtonWrapper(
                            onClick = onDismissRequest,
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
                        Box(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .claymorphic(
                                        backgroundColor = MaterialTheme.colorScheme.primary,
                                        cornerRadius = 14.dp,
                                        elevation = 4.dp,
                                        isDark = isDark,
                                    )
                                    .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Nueva Juntada",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                            )
                        }

                        Box(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .clickable { onSwitchToJoin() }
                                    .claymorphic(
                                        backgroundColor =
                                            if (isDark) {
                                                MaterialTheme.colorScheme.surface
                                            } else {
                                                Color.White
                                            },
                                        cornerRadius = 14.dp,
                                        elevation = 1.dp,
                                        isDark = isDark,
                                    )
                                    .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Unirse a Código",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }

                    Divider()

                    // Create Table Form
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            val isRestaurantSelected = formState.selectedType == TableType.RESTAURANT
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
                                        .clickable { viewModel.updateCreateForm(selectedType = TableType.RESTAURANT) }
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
                                        imageVector = RestaurantIcon,
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

                            val isHomemadeSelected = formState.selectedType == TableType.HOME_MADE
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
                                        .clickable { viewModel.updateCreateForm(selectedType = TableType.HOME_MADE) }
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
                            value = formState.tableNameInput,
                            onValueChange = { viewModel.updateCreateForm(tableNameInput = it) },
                            label = { Text("Nombre de la juntada") },
                            placeholder = { Text("Ej: Asado del viernes, Cena de fin de año") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                        )

                        OutlinedTextField(
                            value = formState.creatorNickname,
                            onValueChange = { viewModel.updateCreateForm(creatorNickname = it) },
                            label = { Text("Tu apodo") },
                            placeholder = { Text("Ej: Juan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                        )

                        if (formState.selectedType == TableType.RESTAURANT) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = formState.tipPercentageInput,
                                    onValueChange = { viewModel.updateCreateForm(tipPercentageInput = it) },
                                    label = { Text("Propina %") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                )
                                OutlinedTextField(
                                    value = formState.cubiertoInput,
                                    onValueChange = { viewModel.updateCreateForm(cubiertoInput = it) },
                                    label = { Text("Cubierto $") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = formState.fixedCostInput,
                                onValueChange = { viewModel.updateCreateForm(fixedCostInput = it) },
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
                                    name = formState.tableNameInput,
                                    type = formState.selectedType,
                                    tipPercentage = formState.tipPercentageInput.toDoubleOrNull() ?: 10.0,
                                    fixedExtraCost = formState.fixedCostInput.toDoubleOrNull() ?: 0.0,
                                    cubiertoPerPerson = formState.cubiertoInput.toDoubleOrNull() ?: 0.0,
                                    hostName = formState.creatorNickname,
                                    onSuccess = {
                                        viewModel.clearCreateForm()
                                        onNavigateToLobby(it)
                                    },
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = formState.tableNameInput.isNotEmpty() && formState.creatorNickname.isNotEmpty(),
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
                }
            }
        }
    }
}
