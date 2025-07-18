package me.lkl.dalvikus.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dalvikus.composeapp.generated.resources.*
import dalvikus.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun JetBrainsMono() = FontFamily(
    Font(Res.font.JetBrainsMono_Bold, FontWeight.Bold, FontStyle.Normal),
    Font(Res.font.JetBrainsMono_BoldItalic, FontWeight.Bold, FontStyle.Italic),
    Font(Res.font.JetBrainsMono_ExtraBold, FontWeight.ExtraBold, FontStyle.Normal),
    Font(Res.font.JetBrainsMono_ExtraBoldItalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(Res.font.JetBrainsMono_ExtraLight, FontWeight.ExtraLight, FontStyle.Normal),
    Font(Res.font.JetBrainsMono_ExtraLightItalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(Res.font.JetBrainsMono_Italic, FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.JetBrainsMono_Light, FontWeight.Light, FontStyle.Normal),
    Font(Res.font.JetBrainsMono_LightItalic, FontWeight.Light, FontStyle.Italic),
    Font(Res.font.JetBrainsMono_Medium, FontWeight.Medium, FontStyle.Normal),
    Font(Res.font.JetBrainsMono_MediumItalic, FontWeight.Medium, FontStyle.Italic),
    Font(Res.font.JetBrainsMono_Regular, FontWeight.Normal, FontStyle.Normal),
    Font(Res.font.JetBrainsMono_SemiBold, FontWeight.SemiBold, FontStyle.Normal),
    Font(Res.font.JetBrainsMono_SemiBoldItalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(Res.font.JetBrainsMono_Thin, FontWeight.Thin, FontStyle.Normal),
    Font(Res.font.JetBrainsMono_ThinItalic, FontWeight.Thin, FontStyle.Italic)
    )

fun Typography.withFontFamily(fontFamily: FontFamily): Typography {
    return Typography(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily)
    )
}