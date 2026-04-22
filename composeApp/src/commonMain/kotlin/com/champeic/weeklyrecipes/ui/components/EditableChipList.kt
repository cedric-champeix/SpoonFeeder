package com.champeic.weeklyrecipes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

private val chipPalette = listOf(
    Color(0xFFB5EAD7) to Color(0xFF1B5E20),
    Color(0xFFFFC8DD) to Color(0xFF880E4F),
    Color(0xFFBDE0FE) to Color(0xFF0D47A1),
    Color(0xFFFFDAB8) to Color(0xFFBF360C),
    Color(0xFFE2C8F8) to Color(0xFF4A148C),
    Color(0xFFFFF3B0) to Color(0xFF827717),
    Color(0xFFB8F2E6) to Color(0xFF004D40),
    Color(0xFFFFCDD2) to Color(0xFFB71C1C),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditableChipList(
    items: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (Int) -> Unit,
    title: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    numbered: Boolean = false,
) {
    var input by remember { mutableStateOf("") }
    val submit = {
        if (input.isNotBlank()) {
            onAdd(input)
            input = ""
        }
    }
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text(placeholder) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrectEnabled = true,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { submit() }),
            )
            Button(onClick = { submit() }) {
                Text("Add")
            }
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items.forEachIndexed { index, item ->
                val (containerColor, contentColor) = chipPalette[index % chipPalette.size]
                InputChip(
                    selected = false,
                    onClick = { onRemove(index) },
                    label = {
                        Text(
                            text = if (numbered) "${index + 1}. $item" else item,
                            style = MaterialTheme.typography.bodyMedium,
                            color = contentColor,
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove $item",
                            modifier = Modifier.size(16.dp),
                            tint = contentColor,
                        )
                    },
                    shape = CircleShape,
                    colors = InputChipDefaults.inputChipColors(
                        containerColor = containerColor,
                        labelColor = contentColor,
                        trailingIconColor = contentColor,
                        selectedContainerColor = containerColor,
                    ),
                    border = InputChipDefaults.inputChipBorder(
                        enabled = true,
                        selected = false,
                        borderColor = Color.Transparent,
                        selectedBorderColor = Color.Transparent,
                    ),
                )
            }
        }
    }
}
