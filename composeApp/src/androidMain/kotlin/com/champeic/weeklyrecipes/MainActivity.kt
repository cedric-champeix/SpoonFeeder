package com.champeic.weeklyrecipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.champeic.weeklyrecipes.data.db.DatabaseDriverFactory
import com.champeic.weeklyrecipes.di.AppContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val container = AppContainer.fromDriverFactory(
            DatabaseDriverFactory(applicationContext),
        )

        setContent {
            App(container = container)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(container = remember { AppContainer.inMemory() })
}
