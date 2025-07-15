package me.lkl.dalvikus.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dalvikus.composeapp.generated.resources.JetBrainsMono_Bold
import dalvikus.composeapp.generated.resources.JetBrainsMono_Italic
import dalvikus.composeapp.generated.resources.JetBrainsMono_Regular
import dalvikus.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun JetBrainsMono() = FontFamily(
    Font(Res.font.JetBrainsMono_Regular, FontWeight.Normal, FontStyle.Normal),
    Font(Res.font.JetBrainsMono_Bold, FontWeight.Bold, FontStyle.Normal),
    Font(Res.font.JetBrainsMono_Italic, FontWeight.Normal, FontStyle.Italic)
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