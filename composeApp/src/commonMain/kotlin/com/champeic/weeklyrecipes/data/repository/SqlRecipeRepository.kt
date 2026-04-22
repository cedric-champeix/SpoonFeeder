package com.champeic.weeklyrecipes.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.champeic.weeklyrecipes.data.models.MealType
import com.champeic.weeklyrecipes.data.models.Recipe
import com.champeic.weeklyrecipes.db.WeeklyRecipesDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class SqlRecipeRepository(
    private val database: WeeklyRecipesDatabase,
    scope: CoroutineScope,
    private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.Default,
) : RecipeRepository {

    private val recipeQueries get() = database.recipeQueries

    override val recipes: StateFlow<List<Recipe>> = combine(
        recipeQueries.selectAllRecipes().asFlow().mapToList(ioDispatcher),
        recipeQueries.selectAllIngredients().asFlow().mapToList(ioDispatcher),
        recipeQueries.selectAllSteps().asFlow().mapToList(ioDispatcher),
        recipeQueries.selectAllRecipeMealTypes().asFlow().mapToList(ioDispatcher),
    ) { recipeRows, ingredientRows, stepRows, mealTypeRows ->
        val ingredientsByRecipe = ingredientRows
            .groupBy { it.recipe_id }
            .mapValues { (_, rows) -> rows.sortedBy { it.position }.map { it.value_ } }
        val stepsByRecipe = stepRows
            .groupBy { it.recipe_id }
            .mapValues { (_, rows) -> rows.sortedBy { it.position }.map { it.value_ } }
        val mealTypesByRecipe = mealTypeRows
            .groupBy { it.recipe_id }
            .mapValues { (_, rows) -> rows.mapNotNull { runCatching { MealType.valueOf(it.meal_type) }.getOrNull() }.toSet() }

        recipeRows.map { row ->
            Recipe(
                id = row.id,
                name = row.name,
                ingredients = ingredientsByRecipe[row.id].orEmpty(),
                steps = stepsByRecipe[row.id].orEmpty(),
                minPrepTimeMinutes = row.min_prep_time,
                maxPrepTimeMinutes = row.max_prep_time,
                suitableMealTypes = mealTypesByRecipe[row.id].orEmpty(),
            )
        }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    override suspend fun addRecipe(recipe: Recipe) = withContext(ioDispatcher) {
        database.transaction {
            recipeQueries.insertRecipe(
                id = recipe.id,
                name = recipe.name,
                min_prep_time = recipe.minPrepTimeMinutes,
                max_prep_time = recipe.maxPrepTimeMinutes,
            )
            writeRelations(recipe)
        }
    }

    override suspend fun updateRecipe(recipe: Recipe) = withContext(ioDispatcher) {
        database.transaction {
            recipeQueries.updateRecipe(
                name = recipe.name,
                min_prep_time = recipe.minPrepTimeMinutes,
                max_prep_time = recipe.maxPrepTimeMinutes,
                id = recipe.id,
            )
            recipeQueries.deleteIngredientsForRecipe(recipe.id)
            recipeQueries.deleteStepsForRecipe(recipe.id)
            recipeQueries.deleteMealTypesForRecipe(recipe.id)
            writeRelations(recipe)
        }
    }

    override suspend fun deleteRecipe(recipeId: String) = withContext(ioDispatcher) {
        database.transaction {
            // ingredient/step/meal_type rows cascade via FK; planned_meal rows also cascade.
            recipeQueries.deleteRecipeById(recipeId)
        }
    }

    override fun getRecipeById(id: String): Recipe? {
        val row = recipeQueries.selectRecipeById(id).executeAsOneOrNull() ?: return null
        val ingredients = recipeQueries.selectIngredientsForRecipe(id).executeAsList()
            .sortedBy { it.position }
            .map { it.value_ }
        val steps = recipeQueries.selectStepsForRecipe(id).executeAsList()
            .sortedBy { it.position }
            .map { it.value_ }
        val mealTypes = recipeQueries.selectMealTypesForRecipe(id).executeAsList()
            .mapNotNull { runCatching { MealType.valueOf(it) }.getOrNull() }
            .toSet()
        return Recipe(
            id = row.id,
            name = row.name,
            ingredients = ingredients,
            steps = steps,
            minPrepTimeMinutes = row.min_prep_time,
            maxPrepTimeMinutes = row.max_prep_time,
            suitableMealTypes = mealTypes,
        )
    }

    private fun writeRelations(recipe: Recipe) {
        recipe.ingredients.forEachIndexed { index, value ->
            recipeQueries.insertIngredient(
                recipe_id = recipe.id,
                position = index,
                value_ = value,
            )
        }
        recipe.steps.forEachIndexed { index, value ->
            recipeQueries.insertStep(
                recipe_id = recipe.id,
                position = index,
                value_ = value,
            )
        }
        recipe.suitableMealTypes.forEach { mealType ->
            recipeQueries.insertMealType(
                recipe_id = recipe.id,
                meal_type = mealType.name,
            )
        }
    }
}
