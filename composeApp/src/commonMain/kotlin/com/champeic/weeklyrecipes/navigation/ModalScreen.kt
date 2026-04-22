package com.champeic.weeklyrecipes.navigation

import com.champeic.weeklyrecipes.data.models.MealType
import kotlinx.datetime.LocalDate

sealed interface ModalScreen {
    data class RecipeForm(
        val existingRecipeId: String? = null,
        val prefilledMeal: MealType? = null,
    ) : ModalScreen

    data class RecipeDetail(val recipeId: String) : ModalScreen

    data class Cooking(val recipeId: String) : ModalScreen

    data class PickRecipe(val date: LocalDate, val mealType: MealType) : ModalScreen
}
