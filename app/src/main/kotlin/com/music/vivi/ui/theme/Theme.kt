/**
 * vivimusic Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.music.vivi.ui.theme

import android.graphics.Bitmap
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import com.materialkolor.score.Score
import androidx.compose.runtime.getValue
import com.music.vivi.constants.SelectedFontKey
import com.music.vivi.constants.CustomFontPathKey
import com.music.vivi.constants.AppFont
import com.music.vivi.utils.rememberPreference
import androidx.compose.ui.text.font.FontFamily
import android.graphics.Typeface as AndroidTypeface
import androidx.compose.ui.text.font.Typeface as ComposeTypeface

val DefaultThemeColor = Color(0xFF000000)

@Composable
fun vivimusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlack: Boolean = false,
    themeColor: Color = DefaultThemeColor,
    content: @Composable () -> Unit,
) {
    val selectedFontValue by rememberPreference(SelectedFontKey, AppFont.SYSTEM.value)
    val customFontPath by rememberPreference(CustomFontPathKey, "")

    val brandFont = remember(selectedFontValue, customFontPath) {
        when (AppFont.fromValue(selectedFontValue)) {
            AppFont.SYSTEM -> FontFamily.Default
            AppFont.OUTFIT -> OutfitFontFamily
            AppFont.PLUS_JAKARTA_SANS -> PlusJakartaSansFontFamily
            AppFont.CUSTOM -> {
                try {
                    if (customFontPath.isNotEmpty() && java.io.File(customFontPath).exists()) {
                        val typeface = AndroidTypeface.createFromFile(customFontPath)
                        FontFamily(ComposeTypeface(typeface))
                    } else {
                        FontFamily.Default
                    }
                } catch (e: Exception) {
                    FontFamily.Default
                }
            }
        }
    }

    val typography = remember(brandFont) {
        getTypography(brandFont = brandFont, plainFont = brandFont)
    }

    // AURA uses a fixed monochrome palette; wallpaper and artwork colors never recolor the interface.
    val colorScheme = remember(darkTheme, pureBlack) {
        if (darkTheme) {
            androidx.compose.material3.darkColorScheme(
                primary = Color(0xFFE5E5E5),
                onPrimary = Color(0xFF171717),
                secondary = Color(0xFFBDBDBD),
                onSecondary = Color(0xFF1C1C1C),
                tertiary = Color(0xFF9E9E9E),
                background = Color(0xFF101010),
                onBackground = Color(0xFFF5F5F5),
                surface = if (pureBlack) Color.Black else Color(0xFF151515),
                onSurface = Color(0xFFF5F5F5),
                surfaceVariant = Color(0xFF292929),
                onSurfaceVariant = Color(0xFFCACACA),
                outline = Color(0xFF777777)
            )
        } else {
            androidx.compose.material3.lightColorScheme(
                primary = Color(0xFF262626),
                onPrimary = Color.White,
                secondary = Color(0xFF555555),
                onSecondary = Color.White,
                tertiary = Color(0xFF737373),
                background = Color(0xFFF7F7F7),
                onBackground = Color(0xFF171717),
                surface = Color.White,
                onSurface = Color(0xFF171717),
                surfaceVariant = Color(0xFFEAEAEA),
                onSurfaceVariant = Color(0xFF454545),
                outline = Color(0xFF858585)
            )
        }
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = typography,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}

fun Bitmap.extractThemeColor(): Color {
    val colorsToPopulation = Palette.from(this)
        .maximumColorCount(8)
        .generate()
        .swatches
        .associate { it.rgb to it.population }
    val rankedColors = Score.score(colorsToPopulation)
    return Color(rankedColors.first())
}

fun Bitmap.extractGradientColors(): List<Color> {
    val extractedColors = Palette.from(this)
        .maximumColorCount(64)
        .generate()
        .swatches
        .associate { it.rgb to it.population }

    val orderedColors = Score.score(extractedColors, 2, 0xff4285f4.toInt(), true)
        .sortedByDescending { Color(it).luminance() }

    return if (orderedColors.size >= 2)
        listOf(Color(orderedColors[0]), Color(orderedColors[1]))
    else
        listOf(Color(0xFF595959), Color(0xFF0D0D0D))
}

fun ColorScheme.pureBlack(apply: Boolean) =
    if (apply) copy(
        surface = Color.Black,
        background = Color.Black
    ) else this

val ColorSaver = object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)
    override fun SaverScope.save(value: Color): Int = value.toArgb()
}
