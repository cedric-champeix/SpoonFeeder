package com.champeic.weeklyrecipes.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.champeic.weeklyrecipes.db.WeeklyRecipesDatabase

actual class DatabaseDriverFactory {
    actual fun create(): SqlDriver =
        NativeSqliteDriver(
            schema = WeeklyRecipesDatabase.Schema,
            name = "weekly_recipes.db",
        )
}
