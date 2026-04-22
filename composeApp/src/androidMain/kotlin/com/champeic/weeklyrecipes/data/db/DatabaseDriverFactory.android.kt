package com.champeic.weeklyrecipes.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.champeic.weeklyrecipes.db.WeeklyRecipesDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun create(): SqlDriver =
        AndroidSqliteDriver(
            schema = WeeklyRecipesDatabase.Schema,
            context = context,
            name = "weekly_recipes.db",
        )
}
