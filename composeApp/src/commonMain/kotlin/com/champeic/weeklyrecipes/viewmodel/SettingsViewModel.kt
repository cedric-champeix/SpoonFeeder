package com.champeic.weeklyrecipes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.champeic.weeklyrecipes.data.models.CookingTool
import com.champeic.weeklyrecipes.data.models.DietType
import com.champeic.weeklyrecipes.data.models.UserPreferences
import com.champeic.weeklyrecipes.data.repository.InMemoryUserPreferencesRepository
import com.champeic.weeklyrecipes.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: UserPreferencesRepository = InMemoryUserPreferencesRepository(),
) : ViewModel() {
    val preferences: StateFlow<UserPreferences> = repository.preferences

    fun addDislike(ingredient: String) {
        viewModelScope.launch { repository.addDislikedIngredient(ingredient) }
    }

    fun removeDislikeAt(index: Int) {
        viewModelScope.launch { repository.removeDislikedIngredientAt(index) }
    }

    fun setDiet(diet: DietType) {
        viewModelScope.launch { repository.setDiet(diet) }
    }

    fun toggleTool(tool: CookingTool) {
        viewModelScope.launch { repository.toggleTool(tool) }
    }
}
