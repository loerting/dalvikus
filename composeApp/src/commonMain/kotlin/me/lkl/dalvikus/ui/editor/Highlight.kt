package me.lkl.dalvikus.ui.editor

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.android.tools.smali.smali.smaliFlexLexer
import com.android.tools.smali.smali.smaliParser
import com.materialkolor.ktx.darken
import me.lkl.dalvikus.lexer.highlightJavaCode
import me.lkl.dalvikus.lexer.highlightJsonCode
import me.lkl.dalvikus.lexer.highlightSmaliCode
import me.lkl.dalvikus.lexer.highlightXmlCode
import me.lkl.dalvikus.settings.DalvikusSettings
import org.antlr.runtime.CommonToken
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.RecognitionException

data class CodeHighlightColors(
    val onSurface: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val quaternary: Color,
    val quinary: Color,
    val senary: Color,
    val error: Color
) {
}

@Composable
fun defaultCodeHighlightColors(): CodeHighlightColors {
    return CodeHighlightColors(
        onSurface = MaterialTheme.colorScheme.onSurface,
        primary = MaterialTheme.colorScheme.primary,
        secondary = MaterialTheme.colorScheme.secondary,
        tertiary = MaterialTheme.colorScheme.tertiary,
        quaternary = Color(0xff686de0),
        quinary = Color(0xffbadc58),
        senary = Color(0xfff0932b),
        error = MaterialTheme.colorScheme.error
    )
}

fun highlightCode(editable: Code, colors: CodeHighlightColors): AnnotatedString {
    val code = editable.code
    return when (editable.codeType.lowercase()) {
        "json" -> highlightJsonCode(code, colors)
        "xml", "html" -> highlightXmlCode(code, colors)
        "java" -> highlightJavaCode(code, colors)
        "smali" -> highlightSmaliCode(code, colors)
        else -> AnnotatedString(code)
    }
}