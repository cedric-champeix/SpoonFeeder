package com.champeic.weeklyrecipes.ui.components

import androidx.compose.foundation.layout.*
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
import com.champeic.weeklyrecipes.ui.theme.SpoonfeederRadii

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
                shape = SpoonfeederRadii.Input,
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
                InputChip(
                    selected = false,
                    onClick = { onRemove(index) },
                    label = {
                        Text(
                            text = if (numbered) "${index + 1}. $item" else item,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove $item",
                            modifier = Modifier.size(16.dp),
                        )
                    },
                    shape = SpoonfeederRadii.Pill,
                    colors = InputChipDefaults.inputChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
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
