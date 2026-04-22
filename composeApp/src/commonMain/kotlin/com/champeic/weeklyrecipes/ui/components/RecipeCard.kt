package com.champeic.weeklyrecipes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.champeic.weeklyrecipes.data.models.Recipe
import com.champeic.weeklyrecipes.ui.theme.SpoonfeederColors
import com.champeic.weeklyrecipes.ui.theme.SpoonfeederRadii
import com.champeic.weeklyrecipes.ui.theme.brandColor

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showMealTypes: Boolean = false,
) {
    val accent = recipe.suitableMealTypes
        .sortedBy { it.ordinal }
        .firstOrNull()
        ?.brandColor()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = SpoonfeederRadii.Card,
                ambientColor = Color(0x123A2518),
                spotColor = Color(0x123A2518),
            )
            .clickable(onClick = onClick),
        shape = SpoonfeederRadii.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            if (accent != null) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .height(72.dp)
                        .background(accent),
                )
            }
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (showMealTypes && recipe.suitableMealTypes.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        recipe.suitableMealTypes.sortedBy { it.ordinal }.forEach { meal ->
                            Box(
                                modifier = Modifier
                                    .background(meal.brandColor(), RoundedCornerShape(50))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = meal.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SpoonfeederColors.Cocoa,
                                )
                            }
                        }
                    }
                }
                if (recipe.minPrepTimeMinutes > 0 || recipe.maxPrepTimeMinutes > 0) {
                    Text(
                        text = "${recipe.minPrepTimeMinutes}-${recipe.maxPrepTimeMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
