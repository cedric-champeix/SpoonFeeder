package com.champeic.weeklyrecipes.data.models

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalDate

data class PlannedMeal(
    val id: String,
    val date: LocalDate,
    val mealType: MealType,
    val recipeId: String,
)

object PlannedMealIdFactory {
    @OptIn(ExperimentalUuidApi::class)
    fun newId(): String = Uuid.random().toString()
}
