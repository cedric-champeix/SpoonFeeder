package com.champeic.weeklyrecipes.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val SpoonfeederShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp), // Inputs
    small      = RoundedCornerShape(14.dp), // Chips
    medium     = RoundedCornerShape(20.dp), // Buttons / small cards
    large      = RoundedCornerShape(24.dp), // Cards
    extraLarge = RoundedCornerShape(24.dp), // Sheets
)

object SpoonfeederRadii {
    val Input   = RoundedCornerShape(12.dp)
    val Chip    = RoundedCornerShape(14.dp)
    val Button  = RoundedCornerShape(20.dp)
    val Card    = RoundedCornerShape(24.dp)
    val Pill    = RoundedCornerShape(50)
}
