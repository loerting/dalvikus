package me.lkl.dalvikus.ui

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.getFontResourceBytes
import java.awt.Font as AwtFont

fun Color.toAwt(): java.awt.Color =
    java.awt.Color(
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt(),
        (alpha * 255).toInt()
    )


suspend fun AwtFontFromRes(size: Float, environment: ResourceEnvironment, fontRes: FontResource): AwtFont {
    var fontBytes = getFontResourceBytes(environment, fontRes)
    return AwtFont.createFont(AwtFont.TRUETYPE_FONT, fontBytes.inputStream())
        .deriveFont(size)
}