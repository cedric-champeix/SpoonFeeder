package com.champeic.weeklyrecipes.data.repository

import com.champeic.weeklyrecipes.data.models.PlannedMeal
import kotlinx.coroutines.flow.StateFlow

interface PlannedMealRepository {
    val plannedMeals: StateFlow<List<PlannedMeal>>

    suspend fun plan(plannedMeal: PlannedMeal)
    suspend fun unplan(plannedMealId: String)
    suspend fun unplanAllForRecipe(recipeId: String)
}
