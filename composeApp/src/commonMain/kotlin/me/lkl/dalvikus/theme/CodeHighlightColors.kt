package me.lkl.dalvikus.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.ktx.harmonize

data class CodeHighlightColors(
    val onSurface: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val quaternary: Color,
    val quinary: Color,
    val senary: Color,
    val septenary: Color,
    val error: Color
)

@Composable
fun defaultCodeHighlightColors(darkTheme: Boolean): CodeHighlightColors {
    return CodeHighlightColors(
        onSurface = MaterialTheme.colorScheme.onSurface,
        primary = MaterialTheme.colorScheme.primary,
        secondary = MaterialTheme.colorScheme.secondary,
        tertiary = MaterialTheme.colorScheme.tertiary,
        quaternary = Color(0xFF7B6FB2).harmonize(MaterialTheme.colorScheme.primary),
        quinary = Color(0xFF9EAD6F).harmonize(MaterialTheme.colorScheme.primary),
        senary = Color(0xFFBD875B).harmonize(MaterialTheme.colorScheme.primary),
        septenary = Color(0xFF5BAEA0).harmonize(MaterialTheme.colorScheme.primary),
        error = MaterialTheme.colorScheme.error
    )
}