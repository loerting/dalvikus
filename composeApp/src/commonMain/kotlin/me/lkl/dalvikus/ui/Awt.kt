package me.lkl.dalvikus.ui

import androidx.compose.ui.graphics.Color
import java.io.InputStream
import java.awt.Font as AwtFont

fun Color.toAwt(): java.awt.Color =
    java.awt.Color(
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt(),
        (alpha * 255).toInt()
    )


fun loadAwtFontFromResource(resourcePath: String, size: Float): AwtFont {
    val stream: InputStream = object {}.javaClass.getResourceAsStream(resourcePath)
        ?: throw IllegalArgumentException("Font resource not found: $resourcePath")
    return AwtFont.createFont(AwtFont.TRUETYPE_FONT, stream).deriveFont(size)
}