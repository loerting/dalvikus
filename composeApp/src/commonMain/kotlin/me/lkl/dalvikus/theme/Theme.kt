package me.lkl.dalvikus.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.PaletteStyle
import me.lkl.dalvikus.dalvikusSettings

internal val SeedColor = Color(0xFF924A92)

internal val LocalThemeIsDark = compositionLocalOf { mutableStateOf(true) }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AppTheme(
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()
    val isDarkState = remember(systemIsDark) { mutableStateOf(systemIsDark) }

    val selectedPaletteStyle = dalvikusSettings["theme_palette_style"] as String

    CompositionLocalProvider(
        LocalThemeIsDark provides isDarkState
    ) {
        val isDark by isDarkState
        SystemAppearance(!isDark)
        DynamicMaterialExpressiveTheme(
            typography = Typography().withFontFamily(Monaspace()),
            seedColor = SeedColor,
            style = PaletteStyle.entries.firstOrNull { it.name == selectedPaletteStyle }
                ?: PaletteStyle.TonalSpot,
            motionScheme = MotionScheme.expressive(),
            isDark = isDark,
            animate = true,
            content = { Surface(content = content) },
        )
    }
}

@Composable
internal expect fun SystemAppearance(isDark: Boolean)
