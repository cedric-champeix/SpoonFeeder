package com.champeic.weeklyrecipes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SpoonfeederColorScheme = lightColorScheme(
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

@Composable
fun SpoonfeederTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SpoonfeederColorScheme,
        typography = spoonFeederTypography(),
        shapes = SpoonfeederShapes,
        content = content,
    )
}
