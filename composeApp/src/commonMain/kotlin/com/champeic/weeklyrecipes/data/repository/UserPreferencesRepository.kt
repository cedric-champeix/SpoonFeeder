package com.champeic.weeklyrecipes.data.repository

import com.champeic.weeklyrecipes.data.models.CookingTool
import com.champeic.weeklyrecipes.data.models.DietType
import com.champeic.weeklyrecipes.data.models.UserPreferences
import kotlinx.coroutines.flow.StateFlow

interface UserPreferencesRepository {
    val preferences: StateFlow<UserPreferences>

    suspend fun addDislikedIngredient(ingredient: String)
    suspend fun removeDislikedIngredientAt(index: Int)
    suspend fun setDiet(diet: DietType)
    suspend fun toggleTool(tool: CookingTool)
}
