package com.champeic.weeklyrecipes.data.db

import app.cash.sqldelight.db.SqlDriver

/**
 * Platform-specific driver factory. Each platform module provides an `actual` that
 * produces a driver bound to the native SQLite implementation.
 */
expect class DatabaseDriverFactory {
    fun create(): SqlDriver
}
