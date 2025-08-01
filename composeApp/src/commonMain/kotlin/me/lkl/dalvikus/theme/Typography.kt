package me.lkl.dalvikus.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dalvikus.composeapp.generated.resources.*
import org.jetbrains.compose.resources.Font

@Composable
fun Monaspace() = FontFamily(
    Font(Res.font.MonaspaceNeonFrozen_Bold, FontWeight.Bold, FontStyle.Normal),
    Font(Res.font.MonaspaceNeonFrozen_BoldItalic, FontWeight.Bold, FontStyle.Italic),
    Font(Res.font.MonaspaceNeonFrozen_ExtraBold, FontWeight.ExtraBold, FontStyle.Normal),
    Font(Res.font.MonaspaceNeonFrozen_ExtraBoldItalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(Res.font.MonaspaceNeonFrozen_ExtraLight, FontWeight.ExtraLight, FontStyle.Normal),
    Font(Res.font.MonaspaceNeonFrozen_ExtraLightItalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(Res.font.MonaspaceNeonFrozen_Italic, FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.MonaspaceNeonFrozen_Light, FontWeight.Light, FontStyle.Normal),
    Font(Res.font.MonaspaceNeonFrozen_LightItalic, FontWeight.Light, FontStyle.Italic),
    Font(Res.font.MonaspaceNeonFrozen_Medium, FontWeight.Medium, FontStyle.Normal),
    Font(Res.font.MonaspaceNeonFrozen_MediumItalic, FontWeight.Medium, FontStyle.Italic),
    Font(Res.font.MonaspaceNeonFrozen_Regular, FontWeight.Normal, FontStyle.Normal),
    Font(Res.font.MonaspaceNeonFrozen_SemiBold, FontWeight.SemiBold, FontStyle.Normal),
    Font(Res.font.MonaspaceNeonFrozen_SemiBoldItalic, FontWeight.SemiBold, FontStyle.Italic),
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