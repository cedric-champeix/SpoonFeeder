package com.champeic.weeklyrecipes.data.models

data class UserPreferences(
    val dislikedIngredients: List<String> = emptyList(),
    val diet: DietType = DietType.NONE,
    val availableTools: Set<CookingTool> = emptySet(),
)
