package com.champeic.weeklyrecipes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.champeic.weeklyrecipes.data.models.MealType
import com.champeic.weeklyrecipes.data.models.Recipe
import com.champeic.weeklyrecipes.di.AppContainer
import com.champeic.weeklyrecipes.navigation.ModalScreen
import com.champeic.weeklyrecipes.navigation.rememberAppNavigator
import com.champeic.weeklyrecipes.ui.screens.*
import com.champeic.weeklyrecipes.ui.theme.SpoonfeederTheme
import com.champeic.weeklyrecipes.viewmodel.RecipeViewModel
import com.champeic.weeklyrecipes.viewmodel.SettingsViewModel
import kotlinx.datetime.LocalDate

@Composable
fun App(container: AppContainer) {
    SpoonfeederTheme {
        val recipeViewModel: RecipeViewModel = viewModel {
            RecipeViewModel(container.recipeRepository, container.plannedMealRepository)
        }
        val settingsViewModel: SettingsViewModel = viewModel {
            SettingsViewModel(container.userPreferencesRepository)
        }
        val recipes by recipeViewModel.recipes.collectAsStateWithLifecycle()
        val plannedMeals by recipeViewModel.plannedMeals.collectAsStateWithLifecycle()
        val preferences by settingsViewModel.preferences.collectAsStateWithLifecycle()
        val navigator = rememberAppNavigator()

        var selectedTab by remember { mutableStateOf(BottomNavTab.WEEKLY) }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    BottomNavTab.entries.forEach { tab ->
                        NavigationBarItem(
                            icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            selected = selectedTab == tab,
                            onClick = {
                                selectedTab = tab
                                navigator.clear()
                            },
                        )
                    }
                }
            },
        ) { paddingValues ->
            Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                val current = navigator.current
                if (current != null) {
                    RenderModal(
                        modal = current,
                        recipes = recipes,
                        onSaveNew = { recipe ->
                            recipeViewModel.addRecipe(recipe)
                            navigator.pop()
                        },
                        onSaveEdit = { recipe ->
                            recipeViewModel.updateRecipe(recipe)
                            navigator.pop()
                        },
                        onDelete = { id ->
                            recipeViewModel.deleteRecipe(id)
                            navigator.clear()
                        },
                        onEditFromDetail = { id ->
                            navigator.push(ModalScreen.RecipeForm(existingRecipeId = id))
                        },
                        onStartCooking = { id ->
                            navigator.push(ModalScreen.Cooking(recipeId = id))
                        },
                        onRecipePicked = { date, mealType, recipeId ->
                            recipeViewModel.planMeal(date, mealType, recipeId)
                            navigator.pop()
                        },
                        onBack = navigator::pop,
                    )
                } else {
                    when (selectedTab) {
                        BottomNavTab.WEEKLY -> WeeklyPlanScreen(
                            recipes = recipes,
                            plannedMeals = plannedMeals,
                            onPlanMeal = { date, mealType ->
                                navigator.push(ModalScreen.PickRecipe(date, mealType))
                            },
                            onUnplanMeal = recipeViewModel::unplanMeal,
                            onRecipeClick = { recipe ->
                                navigator.push(ModalScreen.RecipeDetail(recipe.id))
                            },
                        )
                        BottomNavTab.RECIPES -> AllRecipesScreen(
                            recipes = recipes,
                            onRecipeClick = { recipe ->
                                navigator.push(ModalScreen.RecipeDetail(recipe.id))
                            },
                            onAddRecipe = { navigator.push(ModalScreen.RecipeForm()) },
                        )
                        BottomNavTab.SETTINGS -> PreferencesScreen(
                            preferences = preferences,
                            onAddDislike = settingsViewModel::addDislike,
                            onRemoveDislikeAt = settingsViewModel::removeDislikeAt,
                            onDietChange = settingsViewModel::setDiet,
                            onToggleTool = settingsViewModel::toggleTool,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderModal(
    modal: ModalScreen,
    recipes: List<Recipe>,
    onSaveNew: (Recipe) -> Unit,
    onSaveEdit: (Recipe) -> Unit,
    onDelete: (String) -> Unit,
    onEditFromDetail: (String) -> Unit,
    onStartCooking: (String) -> Unit,
    onRecipePicked: (LocalDate, MealType, String) -> Unit,
    onBack: () -> Unit,
) {
    when (modal) {
        is ModalScreen.RecipeForm -> {
            val existing = modal.existingRecipeId?.let { id -> recipes.firstOrNull { it.id == id } }
            RecipeFormScreen(
                existingRecipe = existing,
                prefilledMeal = modal.prefilledMeal,
                onSave = { recipe ->
                    if (existing != null) onSaveEdit(recipe) else onSaveNew(recipe)
                },
                onBack = onBack,
            )
        }
        is ModalScreen.RecipeDetail -> {
            val recipe = recipes.firstOrNull { it.id == modal.recipeId }
            if (recipe == null) {
                onBack()
            } else {
                RecipeDetailScreen(
                    recipe = recipe,
                    onEdit = { onEditFromDetail(recipe.id) },
                    onDelete = { onDelete(recipe.id) },
                    onBack = onBack,
                    onStartCooking = { onStartCooking(recipe.id) },
                )
            }
        }
        is ModalScreen.Cooking -> {
            val recipe = recipes.firstOrNull { it.id == modal.recipeId }
            if (recipe == null) {
                onBack()
            } else {
                CookingScreen(recipe = recipe, onBack = onBack)
            }
        }
        is ModalScreen.PickRecipe -> {
            PickRecipeScreen(
                date = modal.date,
                mealType = modal.mealType,
                recipes = recipes,
                onRecipePicked = { recipe ->
                    onRecipePicked(modal.date, modal.mealType, recipe.id)
                },
                onBack = onBack,
            )
        }
    }
}

enum class BottomNavTab(val label: String, val icon: ImageVector) {
    WEEKLY("Weekly", Icons.Filled.CalendarMonth),
    RECIPES("Recipes", Icons.AutoMirrored.Filled.MenuBook),
    SETTINGS("Settings", Icons.Filled.Settings),
}
