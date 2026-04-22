package com.champeic.weeklyrecipes.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.champeic.weeklyrecipes.data.models.CookingTool
import com.champeic.weeklyrecipes.data.models.DietType
import com.champeic.weeklyrecipes.data.models.UserPreferences
import com.champeic.weeklyrecipes.db.WeeklyRecipesDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class SqlUserPreferencesRepository(
    private val database: WeeklyRecipesDatabase,
    scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : UserPreferencesRepository {

    private val queries get() = database.userPreferencesQueries

    init {
        queries.insertPreferencesRow()
    }

    override val preferences: StateFlow<UserPreferences> = combine(
        queries.selectPreferencesRow().asFlow().mapToOneOrNull(ioDispatcher),
        queries.selectAllDislikes().asFlow().mapToList(ioDispatcher),
        queries.selectAllTools().asFlow().mapToList(ioDispatcher),
    ) { prefsRow, dislikeRows, toolRows ->
        UserPreferences(
            dislikedIngredients = dislikeRows
                .sortedBy { it.position }
                .map { it.value_ },
            diet = prefsRow?.diet
                ?.let { runCatching { DietType.valueOf(it) }.getOrNull() }
                ?: DietType.NONE,
            availableTools = toolRows
                .mapNotNull { runCatching { CookingTool.valueOf(it) }.getOrNull() }
                .toSet(),
        )
    }.stateIn(scope, SharingStarted.Eagerly, UserPreferences())

    override suspend fun addDislikedIngredient(ingredient: String) = withContext(ioDispatcher) {
        val trimmed = ingredient.trim()
        if (trimmed.isEmpty()) return@withContext
        database.transaction {
            val existing = queries.selectAllDislikes().executeAsList()
            if (existing.any { it.value_.equals(trimmed, ignoreCase = true) }) return@transaction
            val nextPosition = (existing.maxOfOrNull { it.position } ?: -1) + 1
            queries.insertDislike(position = nextPosition, value_ = trimmed)
        }
    }

    override suspend fun removeDislikedIngredientAt(index: Int) = withContext(ioDispatcher) {
        database.transaction {
            val existing = queries.selectAllDislikes().executeAsList()
                .sortedBy { it.position }
            val target = existing.getOrNull(index) ?: return@transaction
            queries.deleteDislikeAtPosition(target.position)
        }
    }

    override suspend fun setDiet(diet: DietType) {
        withContext(ioDispatcher) { queries.updateDiet(diet.name) }
    }

    override suspend fun toggleTool(tool: CookingTool) = withContext(ioDispatcher) {
        database.transaction {
            val tools = queries.selectAllTools().executeAsList().toSet()
            if (tool.name in tools) {
                queries.deleteTool(tool.name)
            } else {
                queries.insertTool(tool.name)
            }
        }
    }
}
