package com.champeic.weeklyrecipes.data.models

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Recipe(
    val id: String,
    val name: String,
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val minPrepTimeMinutes: Int = 0,
    val maxPrepTimeMinutes: Int = 0,
    val suitableMealTypes: Set<MealType> = emptySet(),
)

object RecipeIdFactory {
    @OptIn(ExperimentalUuidApi::class)
    fun newId(): String = Uuid.random().toString()
}
