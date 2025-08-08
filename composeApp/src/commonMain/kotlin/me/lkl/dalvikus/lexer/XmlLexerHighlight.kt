package me.lkl.dalvikus.lexer

import androidx.compose.ui.text.AnnotatedString
import me.lkl.dalvikus.theme.CodeHighlightColors

expect fun highlightXmlCode(code: String, colors: CodeHighlightColors): AnnotatedString