package com.xsc.sdk.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object DynamicThemeParser {

    fun parseHexColor(hexString: String?, defaultColor: Color): Color {
        if (hexString.isNullOrBlank()) return defaultColor
        return try {
            val cleanHex = hexString.removePrefix("#").trim()
            val colorInt = when (cleanHex.length) {
                6 -> (0xFF000000 or cleanHex.toLong(16)).toInt()
                8 -> cleanHex.toLong(16).toInt()
                else -> return defaultColor
            }
            Color(colorInt)
        } catch (e: Exception) {
            defaultColor
        }
    }

    fun createDynamicLightColorScheme(
        primaryHex: String?,
        secondaryHex: String?,
        surfaceHex: String?,
        backgroundHex: String?
    ): ColorScheme {
        val primary = parseHexColor(primaryHex, OneAppPrimaryLight)
        val secondary = parseHexColor(secondaryHex, OneAppSecondaryLight)
        val surface = parseHexColor(surfaceHex, OneAppSurfaceLight)
        val background = parseHexColor(backgroundHex, OneAppBackgroundLight)

        return lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            secondary = secondary,
            onSecondary = Color.White,
            surface = surface,
            background = background
        )
    }

    fun createDynamicDarkColorScheme(
        primaryHex: String?,
        secondaryHex: String?,
        surfaceHex: String?,
        backgroundHex: String?
    ): ColorScheme {
        val primary = parseHexColor(primaryHex, OneAppPrimaryDark)
        val secondary = parseHexColor(secondaryHex, OneAppSecondaryDark)
        val surface = parseHexColor(surfaceHex, OneAppSurfaceDark)
        val background = parseHexColor(backgroundHex, OneAppBackgroundDark)

        return darkColorScheme(
            primary = primary,
            onPrimary = Color.Black,
            secondary = secondary,
            onSecondary = Color.Black,
            surface = surface,
            background = background
        )
    }
}
