package com.champeic.weeklyrecipes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.champeic.weeklyrecipes.data.models.MealType
import com.champeic.weeklyrecipes.data.models.Recipe
import com.champeic.weeklyrecipes.ui.components.RecipeCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AllRecipesScreen(
    recipes: List<Recipe>,
    onRecipeClick: (Recipe) -> Unit,
    onAddRecipe: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf<MealType?>(null) }

    val filtered = remember(recipes, query, selectedMealType) {
        recipes
            .filter { recipe ->
                selectedMealType == null ||
                    recipe.suitableMealTypes.isEmpty() ||
                    selectedMealType in recipe.suitableMealTypes
            }
            .filter { it.name.contains(query, ignoreCase = true) }
            .sortedBy { it.name.lowercase() }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("All recipes") })
        },
        floatingActionButton = {
            if (onAddRecipe != null) {
                FloatingActionButton(onClick = onAddRecipe) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add recipe")
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search recipes…") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedMealType == null,
                    onClick = { selectedMealType = null },
                    label = { Text("All") },
                )
                MealType.entries.forEach { mealType ->
                    FilterChip(
                        selected = selectedMealType == mealType,
                        onClick = {
                            selectedMealType = if (selectedMealType == mealType) null else mealType
                        },
                        label = { Text(mealType.displayName) },
                    )
                }
            }

            when {
                recipes.isEmpty() -> EmptyState(
                    title = "No recipes yet",
                    subtitle = "Tap + to add your first recipe.",
                )
                filtered.isEmpty() -> EmptyState(
                    title = "No matches",
                    subtitle = "Try another search or meal type.",
                )
                else -> Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    filtered.forEach { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe) },
                            showMealTypes = true,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
