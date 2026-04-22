package com.champeic.weeklyrecipes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList

@Stable
class AppNavigator internal constructor() {
    private val _modalStack: SnapshotStateList<ModalScreen> = mutableStateListOf()
    val modalStack: List<ModalScreen> get() = _modalStack
    val current: ModalScreen? get() = _modalStack.lastOrNull()

    fun push(screen: ModalScreen) {
        _modalStack.add(screen)
    }

    fun replaceTop(screen: ModalScreen) {
        if (_modalStack.isNotEmpty()) _modalStack.removeAt(_modalStack.lastIndex)
        _modalStack.add(screen)
    }

    fun pop() {
        if (_modalStack.isNotEmpty()) _modalStack.removeAt(_modalStack.lastIndex)
    }

    fun clear() {
        _modalStack.clear()
    }
}

@Composable
fun rememberAppNavigator(): AppNavigator = remember { AppNavigator() }
