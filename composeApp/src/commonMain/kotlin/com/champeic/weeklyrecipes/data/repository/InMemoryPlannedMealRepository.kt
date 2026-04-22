package com.champeic.weeklyrecipes.data.repository

import com.champeic.weeklyrecipes.data.models.PlannedMeal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryPlannedMealRepository : PlannedMealRepository {
    private val _plannedMeals = MutableStateFlow<List<PlannedMeal>>(emptyList())
    override val plannedMeals: StateFlow<List<PlannedMeal>> = _plannedMeals.asStateFlow()

    override suspend fun plan(plannedMeal: PlannedMeal) {
        _plannedMeals.update { it + plannedMeal }
    }

    override suspend fun unplan(plannedMealId: String) {
        _plannedMeals.update { list -> list.filterNot { it.id == plannedMealId } }
    }

    override suspend fun unplanAllForRecipe(recipeId: String) {
        _plannedMeals.update { list -> list.filterNot { it.recipeId == recipeId } }
    }
}
