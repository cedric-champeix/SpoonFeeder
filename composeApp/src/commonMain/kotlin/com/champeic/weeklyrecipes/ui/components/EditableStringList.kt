package com.champeic.weeklyrecipes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

@Composable
fun EditableStringList(
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
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { submit() }),
            )
            Button(onClick = { submit() }) {
                Text("Add")
            }
        }
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (numbered) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { onRemove(index) }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove $item",
                    )
                }
            }
        }
    }
}
