package me.lkl.dalvikus.lexer

import androidx.compose.ui.text.AnnotatedString
import me.lkl.dalvikus.ui.editor.highlight.CodeHighlightColors

expect fun highlightJavaCode(code: String, colors: CodeHighlightColors): AnnotatedString