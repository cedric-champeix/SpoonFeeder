package com.champeic.weeklyrecipes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.champeic.weeklyrecipes.data.models.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipe: Recipe,
    onEdit: (Recipe) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit,
    onStartCooking: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(recipe.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (recipe.suitableMealTypes.isNotEmpty()) {
                Column {
                    Text("Suitable for", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = recipe.suitableMealTypes
                            .sortedBy { it.ordinal }
                            .joinToString(" · ") { it.displayName },
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            if (recipe.minPrepTimeMinutes > 0 || recipe.maxPrepTimeMinutes > 0) {
                Column {
                    Text("Prep time", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "${recipe.minPrepTimeMinutes}-${recipe.maxPrepTimeMinutes} min",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            if (recipe.ingredients.isNotEmpty()) {
                Column {
                    Text("Ingredients", style = MaterialTheme.typography.titleMedium)
                    recipe.ingredients.forEach { ingredient ->
                        Text(
                            text = "• $ingredient",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            if (recipe.steps.isNotEmpty()) {
                Column {
                    Text("Steps", style = MaterialTheme.typography.titleMedium)
                    recipe.steps.forEachIndexed { index, step ->
                        Text(
                            text = "${index + 1}. $step",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (onStartCooking != null && recipe.steps.isNotEmpty()) {
                    Button(
                        onClick = onStartCooking,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Start cooking")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Delete")
                    }
                    Button(
                        onClick = { onEdit(recipe) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Edit")
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete recipe?") },
            text = { Text("This will permanently remove \"${recipe.name}\" from your plan.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete(recipe.id)
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}
