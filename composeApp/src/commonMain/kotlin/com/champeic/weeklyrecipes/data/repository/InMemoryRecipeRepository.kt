package com.champeic.weeklyrecipes.data.repository

import com.champeic.weeklyrecipes.data.models.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryRecipeRepository : RecipeRepository {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    override val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    override suspend fun addRecipe(recipe: Recipe) {
        _recipes.update { it + recipe }
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        _recipes.update { list -> list.map { if (it.id == recipe.id) recipe else it } }
    }

    override suspend fun deleteRecipe(recipeId: String) {
        _recipes.update { list -> list.filterNot { it.id == recipeId } }
    }

    override fun getRecipeById(id: String): Recipe? =
        _recipes.value.find { it.id == id }
}
