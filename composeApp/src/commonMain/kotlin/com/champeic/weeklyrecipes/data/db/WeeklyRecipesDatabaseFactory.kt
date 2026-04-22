package com.champeic.weeklyrecipes.data.db

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import com.champeic.weeklyrecipes.db.Recipe
import com.champeic.weeklyrecipes.db.Recipe_ingredient
import com.champeic.weeklyrecipes.db.Recipe_step
import com.champeic.weeklyrecipes.db.User_disliked_ingredient
import com.champeic.weeklyrecipes.db.WeeklyRecipesDatabase

fun createWeeklyRecipesDatabase(factory: DatabaseDriverFactory): WeeklyRecipesDatabase {
    val driver = factory.create()
    return WeeklyRecipesDatabase(
        driver = driver,
        recipeAdapter = Recipe.Adapter(
            min_prep_timeAdapter = IntColumnAdapter,
            max_prep_timeAdapter = IntColumnAdapter,
        ),
        recipe_ingredientAdapter = Recipe_ingredient.Adapter(
            positionAdapter = IntColumnAdapter,
        ),
        recipe_stepAdapter = Recipe_step.Adapter(
            positionAdapter = IntColumnAdapter,
        ),
        user_disliked_ingredientAdapter = User_disliked_ingredient.Adapter(
            positionAdapter = IntColumnAdapter,
        ),
    )
}
