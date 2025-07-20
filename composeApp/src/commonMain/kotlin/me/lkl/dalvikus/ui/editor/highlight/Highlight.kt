package me.lkl.dalvikus.ui.editor.highlight

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import me.lkl.dalvikus.lexer.highlightJavaCode
import me.lkl.dalvikus.lexer.highlightJsonCode
import me.lkl.dalvikus.lexer.highlightSmaliCode
import me.lkl.dalvikus.lexer.highlightXmlCode
import me.lkl.dalvikus.ui.editor.Code

data class CodeHighlightColors(
    val onSurface: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val quaternary: Color,
    val quinary: Color,
    val senary: Color,
    val error: Color
)

@Composable
fun defaultCodeHighlightColors(): CodeHighlightColors {
    return CodeHighlightColors(
        onSurface = MaterialTheme.colorScheme.onSurface,
        primary = MaterialTheme.colorScheme.primary,
        secondary = MaterialTheme.colorScheme.secondary,
        tertiary = MaterialTheme.colorScheme.tertiary,
        quaternary = Color(0xFF7B6FB2),
        quinary = Color(0xFF9EAD6F),
        senary = Color(0xFFBD875B),
        error = MaterialTheme.colorScheme.error
    )
}

fun highlightCode(editable: Code, colors: CodeHighlightColors): AnnotatedString {
    val code = editable.code
    if (code.trim().isEmpty()) return AnnotatedString(code)
    return when (editable.codeType.lowercase()) {
        "json" -> highlightJsonCode(code, colors)
        "xml", "html" -> highlightXmlCode(code, colors)
        "java" -> highlightJavaCode(code, colors)
        "smali" -> highlightSmaliCode(code, colors)
        else -> AnnotatedString(code)
    }
}