package com.champeic.weeklyrecipes.data.repository

import com.champeic.weeklyrecipes.data.models.Recipe
import kotlinx.coroutines.flow.StateFlow

interface RecipeRepository {
    val recipes: StateFlow<List<Recipe>>

    suspend fun addRecipe(recipe: Recipe)
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipeId: String)
    fun getRecipeById(id: String): Recipe?
}
