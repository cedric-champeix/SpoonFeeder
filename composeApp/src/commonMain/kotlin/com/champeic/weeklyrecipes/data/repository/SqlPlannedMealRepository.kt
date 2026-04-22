package com.champeic.weeklyrecipes.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.champeic.weeklyrecipes.data.models.MealType
import com.champeic.weeklyrecipes.data.models.PlannedMeal
import com.champeic.weeklyrecipes.db.WeeklyRecipesDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

class SqlPlannedMealRepository(
    private val database: WeeklyRecipesDatabase,
    scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : PlannedMealRepository {

    private val queries get() = database.plannedMealQueries

    override val plannedMeals: StateFlow<List<PlannedMeal>> = queries
        .selectAllPlannedMeals()
        .asFlow()
        .mapToList(ioDispatcher)
        .map { rows ->
            rows.mapNotNull { row ->
                val date = runCatching { LocalDate.parse(row.date) }.getOrNull() ?: return@mapNotNull null
                val mealType = runCatching { MealType.valueOf(row.meal_type) }.getOrNull() ?: return@mapNotNull null
                PlannedMeal(
                    id = row.id,
                    date = date,
                    mealType = mealType,
                    recipeId = row.recipe_id,
                )
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override suspend fun plan(plannedMeal: PlannedMeal) {
        withContext(ioDispatcher) {
            queries.insertPlannedMeal(
                id = plannedMeal.id,
                date = plannedMeal.date.toString(),
                meal_type = plannedMeal.mealType.name,
                recipe_id = plannedMeal.recipeId,
            )
        }
    }

    override suspend fun unplan(plannedMealId: String) {
        withContext(ioDispatcher) { queries.deletePlannedMealById(plannedMealId) }
    }

    override suspend fun unplanAllForRecipe(recipeId: String) {
        withContext(ioDispatcher) { queries.deletePlannedMealsForRecipe(recipeId) }
    }
}
