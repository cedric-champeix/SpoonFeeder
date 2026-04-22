package com.champeic.weeklyrecipes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.champeic.weeklyrecipes.data.models.MealType
import com.champeic.weeklyrecipes.data.models.Recipe
import com.champeic.weeklyrecipes.data.models.displayName
import com.champeic.weeklyrecipes.ui.components.RecipeCard
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickRecipeScreen(
    date: LocalDate,
    mealType: MealType,
    recipes: List<Recipe>,
    onRecipePicked: (Recipe) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val candidates = remember(recipes, mealType) {
        recipes
            .filter { it.suitableMealTypes.isEmpty() || mealType in it.suitableMealTypes }
            .sortedBy { it.name.lowercase() }
    }

    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(candidates, searchQuery) {
        if (searchQuery.isBlank()) candidates
        else candidates.filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Pick a recipe") },
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
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search recipes…") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "${date.dayOfWeek.displayName()} · ${mealType.displayName}",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "${date.monthNumber}/${date.dayOfMonth}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (filtered.isEmpty()) {
                EmptyPicker(recipesTotal = recipes.size)
            } else {
                filtered.forEach { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipePicked(recipe) },
                        showMealTypes = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyPicker(recipesTotal: Int) {
    Column(
        modifier = Modifier.padding(vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = if (recipesTotal == 0) "No recipes yet" else "No matching recipes",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = if (recipesTotal == 0) {
                "Add a recipe from the Recipes tab first."
            } else {
                "None of your recipes are marked as suitable for this meal."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
