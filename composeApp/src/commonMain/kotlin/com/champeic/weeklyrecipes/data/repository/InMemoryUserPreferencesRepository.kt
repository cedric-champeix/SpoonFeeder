package com.champeic.weeklyrecipes.data.repository

import com.champeic.weeklyrecipes.data.models.CookingTool
import com.champeic.weeklyrecipes.data.models.DietType
import com.champeic.weeklyrecipes.data.models.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryUserPreferencesRepository : UserPreferencesRepository {
    private val _preferences = MutableStateFlow(UserPreferences())
    override val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    override suspend fun addDislikedIngredient(ingredient: String) {
        val trimmed = ingredient.trim()
        if (trimmed.isEmpty()) return
        _preferences.update { prefs ->
            if (prefs.dislikedIngredients.any { it.equals(trimmed, ignoreCase = true) }) {
                prefs
            } else {
                prefs.copy(dislikedIngredients = prefs.dislikedIngredients + trimmed)
            }
        }
    }

    override suspend fun removeDislikedIngredientAt(index: Int) {
        _preferences.update { prefs ->
            if (index !in prefs.dislikedIngredients.indices) return@update prefs
            prefs.copy(
                dislikedIngredients = prefs.dislikedIngredients.toMutableList()
                    .also { it.removeAt(index) },
            )
        }
    }

    override suspend fun setDiet(diet: DietType) {
        _preferences.update { it.copy(diet = diet) }
    }

    override suspend fun toggleTool(tool: CookingTool) {
        _preferences.update { prefs ->
            val tools = if (tool in prefs.availableTools) prefs.availableTools - tool else prefs.availableTools + tool
            prefs.copy(availableTools = tools)
        }
    }
}
