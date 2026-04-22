package com.champeic.weeklyrecipes.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import weeklyrecipes.composeapp.generated.resources.Fredoka_Regular
import weeklyrecipes.composeapp.generated.resources.Nunito_Bold
import weeklyrecipes.composeapp.generated.resources.Nunito_ExtraBold
import weeklyrecipes.composeapp.generated.resources.Nunito_Regular
import weeklyrecipes.composeapp.generated.resources.Nunito_SemiBold
import weeklyrecipes.composeapp.generated.resources.Res

@Composable
fun rememberDisplayFamily(): FontFamily = FontFamily(
    Font(Res.font.Fredoka_Regular, FontWeight.Normal),
)

@Composable
fun rememberBodyFamily(): FontFamily = FontFamily(
    Font(Res.font.Nunito_Regular,   FontWeight.Normal),
    Font(Res.font.Nunito_SemiBold,  FontWeight.SemiBold),
    Font(Res.font.Nunito_Bold,      FontWeight.Bold),
    Font(Res.font.Nunito_ExtraBold, FontWeight.ExtraBold),
)

@Composable
fun spoonFeederTypography(): Typography {
    val display = rememberDisplayFamily()
    val body = rememberBodyFamily()
    return Typography(
        // Display / hero — used sparingly (splash, onboarding)
        displayLarge  = TextStyle(fontFamily = display, fontSize = 72.sp, fontWeight = FontWeight.Normal),
        displayMedium = TextStyle(fontFamily = display, fontSize = 52.sp, fontWeight = FontWeight.Normal),
        displaySmall  = TextStyle(fontFamily = display, fontSize = 40.sp, fontWeight = FontWeight.Normal),

        // Headlines — screen titles
        headlineLarge  = TextStyle(fontFamily = display, fontSize = 36.sp, fontWeight = FontWeight.Normal),
        headlineMedium = TextStyle(fontFamily = display, fontSize = 32.sp, fontWeight = FontWeight.Normal),
        headlineSmall  = TextStyle(fontFamily = display, fontSize = 26.sp, fontWeight = FontWeight.Normal),

        // Titles — card headers, meal names
        titleLarge  = TextStyle(fontFamily = display, fontSize = 24.sp, fontWeight = FontWeight.Normal),
        titleMedium = TextStyle(fontFamily = display, fontSize = 18.sp, fontWeight = FontWeight.Normal),
        titleSmall  = TextStyle(fontFamily = display, fontSize = 14.sp, fontWeight = FontWeight.Normal),

        // Body — Nunito
        bodyLarge  = TextStyle(fontFamily = body, fontSize = 16.sp, fontWeight = FontWeight.Normal),
        bodyMedium = TextStyle(fontFamily = body, fontSize = 14.sp, fontWeight = FontWeight.Normal),
        bodySmall  = TextStyle(fontFamily = body, fontSize = 12.sp, fontWeight = FontWeight.Normal),

        // Labels — UI labels, buttons, hints
        labelLarge  = TextStyle(fontFamily = body, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold),
        labelMedium = TextStyle(fontFamily = body, fontSize = 12.sp, fontWeight = FontWeight.Bold),
        labelSmall  = TextStyle(
            fontFamily = body,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 3.sp,
        ),
    )
}
