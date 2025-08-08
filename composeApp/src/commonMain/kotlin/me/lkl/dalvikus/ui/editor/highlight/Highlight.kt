package me.lkl.dalvikus.ui.editor.highlight

import androidx.compose.ui.text.AnnotatedString
import me.lkl.dalvikus.lexer.highlightJavaCode
import me.lkl.dalvikus.lexer.highlightJsonCode
import me.lkl.dalvikus.lexer.highlightSmaliCode
import me.lkl.dalvikus.lexer.highlightXmlCode
import me.lkl.dalvikus.theme.CodeHighlightColors

fun highlightCode(code: String, codeType: String, colors: CodeHighlightColors): AnnotatedString {
    if (code.trim().isEmpty()) return AnnotatedString(code)
    return when (codeType.lowercase()) {
        "json" -> highlightJsonCode(code, colors)
        "xml", "html" -> highlightXmlCode(code, colors)
        "java" -> highlightJavaCode(code, colors)
        "smali" -> highlightSmaliCode(code, colors)
        else -> AnnotatedString(code)
    }
}