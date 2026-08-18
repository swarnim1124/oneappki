package com.xsc.sdk.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Whether dark mode is currently active. Provided by MainActivity at the root of the
 * composition; screens simply read `LocalDarkTheme.current`.
 */
val LocalDarkTheme = staticCompositionLocalOf { false }

/**
 * Callback that flips [LocalDarkTheme]. Provided by MainActivity alongside LocalDarkTheme.
 */
val LocalThemeToggle = compositionLocalOf<() -> Unit> { {} }

/**
 * Success/warning tones resolved for the active mode.
 *
 * Material 3's ColorScheme has no success or warning role, so these sit alongside it.
 * The flat [OneAppSuccess]/[OneAppWarning] constants remain for existing call sites;
 * new code should read this local so a status pill stays legible in both modes.
 */
@Immutable
data class OneAppStatusColors(
    val success: Color,
    val warning: Color
)

val LocalStatusColors = staticCompositionLocalOf {
    OneAppStatusColors(success = OneAppSuccess, warning = OneAppWarning)
}

/**
 * Spacing scale on a 4dp base grid, exposed as named steps so screens stop inventing
 * one-off padding values - the existing screens between them use 4, 6, 8, 10, 11, 12,
 * 14, 16, 18, 20, 24, 28 and 32dp, which is why alignment drifts between sections.
 */
@Immutable
data class OneAppSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    /** Standard horizontal inset for screen content. */
    val screenHorizontal: Dp = 20.dp,
    /** Max content width before centring - keeps line length readable on tablets. */
    val contentMaxWidth: Dp = 640.dp
)

val LocalSpacing = staticCompositionLocalOf { OneAppSpacing() }

private val LightColors = lightColorScheme(
    primary = OneAppPrimaryLight,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = OneAppSecondaryLight,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = OneAppTertiaryLight,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    background = OneAppBackgroundLight,
    onBackground = LightOnBackground,
    surface = OneAppSurfaceLight,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceTint = LightSurfaceTint,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary,
    error = OneAppErrorLight,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    scrim = LightScrim
)

private val DarkColors = darkColorScheme(
    primary = OneAppPrimaryDark,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = OneAppSecondaryDark,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = OneAppTertiaryDark,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = OneAppBackgroundDark,
    onBackground = DarkOnBackground,
    surface = OneAppSurfaceDark,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceTint = DarkSurfaceTint,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = DarkInversePrimary,
    error = OneAppErrorDark,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    scrim = DarkScrim
)

/**
 * Root theme.
 *
 * The signature is unchanged - `OneAppTheme(darkTheme = isDarkMode) { ... }` in
 * MainActivity still compiles exactly as before. It now additionally supplies the type
 * scale, shape scale, spacing and status colours that were previously left to
 * Material's defaults or hardcoded per screen.
 *
 * Dynamic colour is deliberately *not* wired in. It is the modern default for consumer
 * apps, but this is an institutional ERP where the indigo is the customer's brand; a
 * wallpaper-derived palette would render the same product differently on two students'
 * devices. Swap the scheme selection here if that tradeoff ever changes.
 */
@Composable
fun OneAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColorScheme: androidx.compose.material3.ColorScheme? = null,
    content: @Composable () -> Unit
) {
    val colors = dynamicColorScheme ?: if (darkTheme) DarkColors else LightColors
    val statusColors = if (darkTheme) {
        OneAppStatusColors(success = OneAppSuccessOnDark, warning = OneAppWarningOnDark)
    } else {
        OneAppStatusColors(success = OneAppSuccessOnLight, warning = OneAppWarningOnLight)
    }

    CompositionLocalProvider(
        LocalStatusColors provides statusColors,
        LocalSpacing provides OneAppSpacing()
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = OneAppTypography,
            shapes = OneAppShapes,
            content = content
        )
    }
}
