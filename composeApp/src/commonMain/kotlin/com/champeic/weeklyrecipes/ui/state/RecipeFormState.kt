package com.champeic.weeklyrecipes.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.champeic.weeklyrecipes.data.models.MealType
import com.champeic.weeklyrecipes.data.models.Recipe
import com.champeic.weeklyrecipes.data.models.RecipeIdFactory

@Stable
class RecipeFormState(
    initialName: String = "",
    initialIngredients: List<String> = emptyList(),
    initialSteps: List<String> = emptyList(),
    initialMinPrepTime: String = "",
    initialMaxPrepTime: String = "",
    initialMealTypes: Set<MealType> = emptySet(),
) {
    var name by mutableStateOf(initialName)
    var ingredients by mutableStateOf(initialIngredients)
    var steps by mutableStateOf(initialSteps)
    var minPrepTime by mutableStateOf(initialMinPrepTime)
    var maxPrepTime by mutableStateOf(initialMaxPrepTime)
    var suitableMealTypes by mutableStateOf(initialMealTypes)

    private val minValue: Int? get() = minPrepTime.toIntOrNull()
    private val maxValue: Int? get() = maxPrepTime.toIntOrNull()

    val minInvalid: Boolean
        get() = minPrepTime.isNotBlank() && (minValue == null || (minValue ?: 0) < 0)
    val maxInvalid: Boolean
        get() = maxPrepTime.isNotBlank() && (maxValue == null || (maxValue ?: 0) < 0)
    val rangeInvalid: Boolean
        get() {
            val min = minValue ?: return false
            val max = maxValue ?: return false
            return max in 1..<min
        }

    val isValid: Boolean
        get() = name.isNotBlank() && !minInvalid && !maxInvalid && !rangeInvalid

    fun toggleMealType(mealType: MealType) {
        suitableMealTypes = if (mealType in suitableMealTypes) {
            suitableMealTypes - mealType
        } else {
            suitableMealTypes + mealType
        }
    }

    fun addIngredient(value: String) {
        val trimmed = value.trim()
        if (trimmed.isNotBlank()) ingredients = ingredients + trimmed
    }

    fun removeIngredientAt(index: Int) {
        ingredients = ingredients.filterIndexed { i, _ -> i != index }
    }

    fun addStep(value: String) {
        val trimmed = value.trim()
        if (trimmed.isNotBlank()) steps = steps + trimmed
    }

    fun removeStepAt(index: Int) {
        steps = steps.filterIndexed { i, _ -> i != index }
    }

    fun toRecipe(existingId: String?): Recipe = Recipe(
        id = existingId ?: RecipeIdFactory.newId(),
        name = name.trim(),
        ingredients = ingredients,
        steps = steps,
        minPrepTimeMinutes = minValue ?: 0,
        maxPrepTimeMinutes = maxValue ?: 0,
        suitableMealTypes = suitableMealTypes,
    )
}

@Composable
fun rememberRecipeFormState(
    existingRecipe: Recipe? = null,
    prefilledMeal: MealType? = null,
): RecipeFormState = remember(existingRecipe?.id, prefilledMeal) {
    RecipeFormState(
        initialName = existingRecipe?.name ?: "",
        initialIngredients = existingRecipe?.ingredients ?: emptyList(),
        initialSteps = existingRecipe?.steps ?: emptyList(),
        initialMinPrepTime = existingRecipe?.minPrepTimeMinutes
            ?.takeIf { it > 0 }?.toString() ?: "",
        initialMaxPrepTime = existingRecipe?.maxPrepTimeMinutes
            ?.takeIf { it > 0 }?.toString() ?: "",
        initialMealTypes = existingRecipe?.suitableMealTypes
            ?: prefilledMeal?.let { setOf(it) }
            ?: emptySet(),
    )
}
