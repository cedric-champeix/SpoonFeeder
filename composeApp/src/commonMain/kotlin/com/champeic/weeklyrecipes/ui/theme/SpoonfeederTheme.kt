package com.champeic.weeklyrecipes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary            = SpoonfeederColors.Cocoa,
    onPrimary          = SpoonfeederColors.Cream,
    primaryContainer   = SpoonfeederColors.Butter,
    onPrimaryContainer = SpoonfeederColors.Cocoa,

    secondary            = SpoonfeederColors.Mint,
    onSecondary          = SpoonfeederColors.Cocoa,
    secondaryContainer   = SpoonfeederColors.Mint,
    onSecondaryContainer = SpoonfeederColors.Cocoa,

    tertiary            = SpoonfeederColors.Peach,
    onTertiary          = SpoonfeederColors.Cocoa,
    tertiaryContainer   = SpoonfeederColors.Peach,
    onTertiaryContainer = SpoonfeederColors.Cocoa,

    background   = SpoonfeederColors.Cream,
    onBackground = SpoonfeederColors.Text,

    surface            = SpoonfeederColors.Cream,
    onSurface          = SpoonfeederColors.Text,
    surfaceVariant     = SpoonfeederColors.SoftGray,
    onSurfaceVariant   = SpoonfeederColors.TextLight,
    surfaceTint        = SpoonfeederColors.Cocoa,

    outline        = SpoonfeederColors.SoftGray,
    outlineVariant = SpoonfeederColors.SoftGray,
)

private val DarkColorScheme = darkColorScheme(
    primary            = SpoonfeederColors.Clay,
    onPrimary          = SpoonfeederColors.Cream,
    primaryContainer   = SpoonfeederColors.Raised,
    onPrimaryContainer = SpoonfeederColors.Cream,

    secondary            = SpoonfeederColors.Mint,
    onSecondary          = SpoonfeederColors.Cocoa,
    secondaryContainer   = SpoonfeederColors.Raised,
    onSecondaryContainer = SpoonfeederColors.Cream,

    tertiary            = SpoonfeederColors.Peach,
    onTertiary          = SpoonfeederColors.Cocoa,
    tertiaryContainer   = SpoonfeederColors.Raised,
    onTertiaryContainer = SpoonfeederColors.Cream,

    background   = SpoonfeederColors.DarkBg,
    onBackground = SpoonfeederColors.Cream,

    surface            = SpoonfeederColors.Surface,
    onSurface          = SpoonfeederColors.Cream,
    surfaceVariant     = SpoonfeederColors.Raised,
    onSurfaceVariant   = SpoonfeederColors.Fg2,
    surfaceTint        = SpoonfeederColors.Clay,

    outline        = SpoonfeederColors.DarkBorder,
    outlineVariant = SpoonfeederColors.DarkBorder,

    scrim = SpoonfeederColors.Void,
)

@Composable
fun SpoonfeederTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = spoonFeederTypography(),
        shapes = SpoonfeederShapes,
        content = content,
    )
}
