package com.champeic.weeklyrecipes.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.champeic.weeklyrecipes.data.models.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookingScreen(
    recipe: Recipe,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentStepIndex by remember { mutableStateOf(0) }
    val checkedIngredients = remember { mutableStateMapOf<Int, Boolean>() }
    val steps = recipe.steps.filter { it.isNotBlank() }

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
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Prep time", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = "${recipe.minPrepTimeMinutes}-${recipe.maxPrepTimeMinutes} min",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total steps", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = "${steps.size}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                if (recipe.ingredients.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Ingredients", style = MaterialTheme.typography.titleMedium)
                        recipe.ingredients.forEachIndexed { index, ingredient ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val checked = checkedIngredients[index] == true
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { checkedIngredients[index] = it },
                                )
                                Text(
                                    text = ingredient,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }

                if (steps.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Step ${currentStepIndex + 1} of ${steps.size}",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = steps[currentStepIndex],
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(12.dp)
                                .fillMaxWidth(),
                        )
                    }
                } else {
                    Text(
                        text = "No cooking steps available",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            if (steps.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (currentStepIndex > 0) {
                        Button(
                            onClick = { currentStepIndex-- },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Previous")
                        }
                    }

                    if (currentStepIndex < steps.size - 1) {
                        Button(
                            onClick = { currentStepIndex++ },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Next")
                        }
                    } else {
                        Button(
                            onClick = {
                                currentStepIndex = 0
                                checkedIngredients.clear()
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Restart")
                        }
                    }
                }
            }
        }
    }
}
