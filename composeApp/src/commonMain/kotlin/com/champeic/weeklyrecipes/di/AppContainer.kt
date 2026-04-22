package com.champeic.weeklyrecipes.di

import com.champeic.weeklyrecipes.data.db.DatabaseDriverFactory
import com.champeic.weeklyrecipes.data.db.createWeeklyRecipesDatabase
import com.champeic.weeklyrecipes.data.repository.InMemoryPlannedMealRepository
import com.champeic.weeklyrecipes.data.repository.InMemoryRecipeRepository
import com.champeic.weeklyrecipes.data.repository.InMemoryUserPreferencesRepository
import com.champeic.weeklyrecipes.data.repository.PlannedMealRepository
import com.champeic.weeklyrecipes.data.repository.RecipeRepository
import com.champeic.weeklyrecipes.data.repository.SqlPlannedMealRepository
import com.champeic.weeklyrecipes.data.repository.SqlRecipeRepository
import com.champeic.weeklyrecipes.data.repository.SqlUserPreferencesRepository
import com.champeic.weeklyrecipes.data.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppContainer(
    val recipeRepository: RecipeRepository,
    val plannedMealRepository: PlannedMealRepository,
    val userPreferencesRepository: UserPreferencesRepository,
) {
    companion object {
        fun fromDriverFactory(driverFactory: DatabaseDriverFactory): AppContainer {
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
            val database = createWeeklyRecipesDatabase(driverFactory)
            return AppContainer(
                recipeRepository = SqlRecipeRepository(database, scope),
                plannedMealRepository = SqlPlannedMealRepository(database, scope),
                userPreferencesRepository = SqlUserPreferencesRepository(database, scope),
            )
        }

        fun inMemory(): AppContainer = AppContainer(
            recipeRepository = InMemoryRecipeRepository(),
            plannedMealRepository = InMemoryPlannedMealRepository(),
            userPreferencesRepository = InMemoryUserPreferencesRepository(),
        )
    }
}
