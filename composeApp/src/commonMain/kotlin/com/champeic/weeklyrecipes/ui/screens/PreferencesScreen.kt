package com.champeic.weeklyrecipes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.champeic.weeklyrecipes.data.models.CookingTool
import com.champeic.weeklyrecipes.data.models.DietType
import com.champeic.weeklyrecipes.data.models.UserPreferences
import com.champeic.weeklyrecipes.ui.components.EditableChipList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PreferencesScreen(
    preferences: UserPreferences,
    onAddDislike: (String) -> Unit,
    onRemoveDislikeAt: (Int) -> Unit,
    onDietChange: (DietType) -> Unit,
    onToggleTool: (CookingTool) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Settings") }) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DislikesSection(
                dislikes = preferences.dislikedIngredients,
                onAdd = onAddDislike,
                onRemoveAt = onRemoveDislikeAt,
            )

            DietSection(
                selected = preferences.diet,
                onSelect = onDietChange,
            )

            ToolsSection(
                selected = preferences.availableTools,
                onToggle = onToggleTool,
            )

            Text(
                text = "Version 1.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DislikesSection(
    dislikes: List<String>,
    onAdd: (String) -> Unit,
    onRemoveAt: (Int) -> Unit,
) {
    SectionCard {
        Text(
            text = "Dislikes & allergies",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "Ingredients you want to avoid. Future suggestions will skip recipes that include them.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        EditableChipList(
            items = dislikes,
            onAdd = onAdd,
            onRemove = onRemoveAt,
            title = "Ingredients",
            placeholder = "e.g. peanuts, shellfish",
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DietSection(
    selected: DietType,
    onSelect: (DietType) -> Unit,
) {
    SectionCard {
        Text(
            text = "Diet",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "Pick the one that best describes how you eat.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DietType.entries.forEach { diet ->
                FilterChip(
                    selected = selected == diet,
                    onClick = { onSelect(diet) },
                    label = { Text(diet.displayName) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ToolsSection(
    selected: Set<CookingTool>,
    onToggle: (CookingTool) -> Unit,
) {
    SectionCard {
        Text(
            text = "Available tools",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "Select everything you have in your kitchen. Recipes needing other tools will be deprioritized.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CookingTool.entries.forEach { tool ->
                FilterChip(
                    selected = tool in selected,
                    onClick = { onToggle(tool) },
                    label = { Text(tool.displayName) },
                )
            }
        }
    }
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            content()
        }
    }
}
