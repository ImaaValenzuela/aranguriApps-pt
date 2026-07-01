package com.mitimiti.app.presentation.consumo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mitimiti.app.presentation.theme.claymorphic

@Composable
@Suppress("FunctionNaming")
fun TipAdjustCard(
    tipInput: String,
    onTipInputChange: (String) -> Unit,
    extraInput: String,
    onExtraInputChange: (String) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .claymorphic(
                    backgroundColor =
                        if (isDark) {
                            MaterialTheme.colorScheme.surface
                        } else {
                            Color.White
                        },
                    cornerRadius = 20.dp,
                    elevation = 2.dp,
                    isDark = isDark,
                ),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = tipInput,
                onValueChange = onTipInputChange,
                label = { Text("Propina %") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                value = extraInput,
                onValueChange = onExtraInputChange,
                label = { Text("Extras Fijos ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
            )
        }
    }
}
