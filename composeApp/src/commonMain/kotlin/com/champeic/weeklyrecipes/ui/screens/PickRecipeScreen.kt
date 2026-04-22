package com.champeic.weeklyrecipes.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.champeic.weeklyrecipes.ui.theme.SpoonfeederRadii
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.champeic.weeklyrecipes.data.models.MealType
import com.champeic.weeklyrecipes.data.models.Recipe
import com.champeic.weeklyrecipes.data.models.displayName
import com.champeic.weeklyrecipes.ui.components.RecipeCard
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import weeklyrecipes.composeapp.generated.resources.Res
import weeklyrecipes.composeapp.generated.resources.empty_state_no_meals_dark
import weeklyrecipes.composeapp.generated.resources.empty_state_no_meals_light
import weeklyrecipes.composeapp.generated.resources.empty_state_no_recipes_dark
import weeklyrecipes.composeapp.generated.resources.empty_state_no_recipes_light

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
                    shape = SpoonfeederRadii.Pill,
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val dark = isSystemInDarkTheme()
        val art = when {
            recipesTotal == 0 && dark -> Res.drawable.empty_state_no_recipes_dark
            recipesTotal == 0         -> Res.drawable.empty_state_no_recipes_light
            dark                      -> Res.drawable.empty_state_no_meals_dark
            else                      -> Res.drawable.empty_state_no_meals_light
        }
        Image(
            painter = painterResource(art),
            contentDescription = null,
            modifier = Modifier.size(180.dp),
        )
        Text(
            text = if (recipesTotal == 0) "No recipes yet" else "Nothing matches this meal",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = if (recipesTotal == 0) {
                "Add one from the Recipes tab and we'll take it from there."
            } else {
                "Tag a recipe as suitable for this meal and it'll show up here."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
