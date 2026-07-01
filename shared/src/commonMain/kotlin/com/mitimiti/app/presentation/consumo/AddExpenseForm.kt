package com.mitimiti.app.presentation.consumo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mitimiti.app.domain.model.Friend
import com.mitimiti.app.presentation.theme.ClayButton
import com.mitimiti.app.presentation.theme.claymorphic

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Suppress("FunctionNaming", "LongMethod")
fun AddExpenseForm(
    friends: List<Friend>,
    isDark: Boolean,
    itemNameInput: String,
    onItemNameInputChange: (String) -> Unit,
    itemCostInput: String,
    onItemCostInputChange: (String) -> Unit,
    selectedPayerId: String,
    onSelectedPayerIdChange: (String) -> Unit,
    selectedFriendIds: List<String>,
    onToggleFriendSharer: (String) -> Unit,
    onSelectAllFriends: () -> Unit,
    onClearFriendsSharers: () -> Unit,
    editingExpenseId: String?,
    onCancelEdit: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCostValid = itemCostInput.toDoubleOrNull() != null
    val isValidForm =
        itemNameInput.isNotEmpty() &&
            isCostValid &&
            selectedFriendIds.isNotEmpty() &&
            selectedPayerId.isNotEmpty()

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .claymorphic(
                    backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                    cornerRadius = 24.dp,
                    elevation = 4.dp,
                    isDark = isDark,
                ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (editingExpenseId != null) Icons.Default.Edit else Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (editingExpenseId != null) "Editar Gasto" else "Cargar un Gasto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = itemNameInput,
                    onValueChange = onItemNameInputChange,
                    label = { Text("Gasto (Ej: Vacío, Fernet...)") },
                    modifier = Modifier.weight(1.4f),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = itemCostInput,
                    onValueChange = onItemCostInputChange,
                    label = { Text("Precio ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Quién garpó:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                friends.forEach { friend ->
                    val isPayer = selectedPayerId == friend.id
                    Box(
                        modifier =
                            Modifier
                                .clickable { onSelectedPayerIdChange(friend.id) }
                                .claymorphic(
                                    backgroundColor =
                                        if (isPayer) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else if (isDark) {
                                            MaterialTheme.colorScheme.surface
                                        } else {
                                            Color.White
                                        },
                                    cornerRadius = 14.dp,
                                    elevation = if (isPayer) 4.dp else 1.dp,
                                    isDark = isDark,
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = friend.name,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isPayer) FontWeight.Bold else FontWeight.Normal,
                            color =
                                if (isPayer) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Quiénes consumieron:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                TextButton(
                    onClick = {
                        if (selectedFriendIds.size == friends.size) {
                            onClearFriendsSharers()
                        } else {
                            onSelectAllFriends()
                        }
                    },
                ) {
                    Text(
                        text = if (selectedFriendIds.size == friends.size) "Ninguno" else "Todos",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                friends.forEach { friend ->
                    val isChecked = selectedFriendIds.contains(friend.id)
                    Box(
                        modifier =
                            Modifier
                                .clickable { onToggleFriendSharer(friend.id) }
                                .claymorphic(
                                    backgroundColor =
                                        if (isChecked) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else if (isDark) {
                                            MaterialTheme.colorScheme.surface
                                        } else {
                                            Color.White
                                        },
                                    cornerRadius = 14.dp,
                                    elevation = if (isChecked) 4.dp else 1.dp,
                                    isDark = isDark,
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { onToggleFriendSharer(friend.id) },
                            )
                            Text(
                                text = friend.name,
                                style = MaterialTheme.typography.bodySmall,
                                color =
                                    if (isChecked) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (editingExpenseId != null) {
                    TextButton(
                        onClick = onCancelEdit,
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.error)
                    }
                }

                ClayButton(
                    onClick = onSubmit,
                    enabled = isValidForm,
                    cornerRadius = 16.dp,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (editingExpenseId != null) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (editingExpenseId != null) "Guardar Gasto" else "Sumar Gasto",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
