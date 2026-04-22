package com.champeic.weeklyrecipes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.champeic.weeklyrecipes.data.models.MealType
import com.champeic.weeklyrecipes.data.models.PlannedMeal
import com.champeic.weeklyrecipes.data.models.PlannedMealIdFactory
import com.champeic.weeklyrecipes.data.models.Recipe
import com.champeic.weeklyrecipes.data.repository.InMemoryPlannedMealRepository
import com.champeic.weeklyrecipes.data.repository.InMemoryRecipeRepository
import com.champeic.weeklyrecipes.data.repository.PlannedMealRepository
import com.champeic.weeklyrecipes.data.repository.RecipeRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class RecipeViewModel(
    private val recipeRepository: RecipeRepository = InMemoryRecipeRepository(),
    private val plannedMealRepository: PlannedMealRepository = InMemoryPlannedMealRepository(),
) : ViewModel() {
    val recipes: StateFlow<List<Recipe>> = recipeRepository.recipes
    val plannedMeals: StateFlow<List<PlannedMeal>> = plannedMealRepository.plannedMeals

    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch { recipeRepository.addRecipe(recipe) }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch { recipeRepository.updateRecipe(recipe) }
    }

    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch {
            plannedMealRepository.unplanAllForRecipe(recipeId)
            recipeRepository.deleteRecipe(recipeId)
        }
    }

    fun getRecipeById(id: String): Recipe? = recipeRepository.getRecipeById(id)

    fun planMeal(date: LocalDate, mealType: MealType, recipeId: String) {
        viewModelScope.launch {
            plannedMealRepository.plan(
                PlannedMeal(
                    id = PlannedMealIdFactory.newId(),
                    date = date,
                    mealType = mealType,
                    recipeId = recipeId,
                ),
            )
        }
    }

    fun unplanMeal(plannedMealId: String) {
        viewModelScope.launch { plannedMealRepository.unplan(plannedMealId) }
    }
}
