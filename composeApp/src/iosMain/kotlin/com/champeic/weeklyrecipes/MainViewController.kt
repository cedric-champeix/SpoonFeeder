package com.champeic.weeklyrecipes

import androidx.compose.ui.window.ComposeUIViewController
import com.champeic.weeklyrecipes.data.db.DatabaseDriverFactory
import com.champeic.weeklyrecipes.di.AppContainer

private val container: AppContainer by lazy {
    AppContainer.fromDriverFactory(DatabaseDriverFactory())
}

fun MainViewController() = ComposeUIViewController { App(container = container) }
