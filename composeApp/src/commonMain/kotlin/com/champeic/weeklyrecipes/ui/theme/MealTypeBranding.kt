package com.champeic.weeklyrecipes.ui.theme

import androidx.compose.ui.graphics.Color
import com.champeic.weeklyrecipes.data.models.MealType

fun MealType.brandColor(): Color = when (this) {
    MealType.BREAKFAST -> SpoonfeederColors.Butter
    MealType.LUNCH     -> SpoonfeederColors.Mint
    MealType.DINNER    -> SpoonfeederColors.Peach
    MealType.SNACK     -> SpoonfeederColors.Blush
}
