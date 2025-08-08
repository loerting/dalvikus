package me.lkl.dalvikus.lexer

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import me.lkl.dalvikus.theme.CodeHighlightColors

actual fun highlightJsonCode(code: String, colors: CodeHighlightColors): AnnotatedString {
    val lexer = JSONLexer(CharStreams.fromString(code))
    val tokens = CommonTokenStream(lexer)
    tokens.fill()

    return buildAnnotatedString {
        append(code)

        for (token in tokens.tokens) {
            if (token.type == -1 || token.text.isNullOrBlank()) continue

            getJsonTokenStyle(token.type, colors)?.let {
                addStyle(it, token.startIndex, token.stopIndex + 1)
            }
        }
    }
}

private fun getJsonTokenStyle(tokenType: Int, colors: CodeHighlightColors): SpanStyle? {
    return when (tokenType) {

        // === Punctuation: { } [ ] , : ===
        JSONLexer.T__0, // {
        JSONLexer.T__1, // }
        JSONLexer.T__2, // [
        JSONLexer.T__3, // ]
        JSONLexer.T__4, // :
        JSONLexer.T__5  // ,
            -> SpanStyle(color = colors.onSurface.copy(alpha = 0.6f))

        // === Keywords: true, false, null ===
        JSONLexer.T__6, // true
        JSONLexer.T__7, // false
        JSONLexer.T__8  // null
            -> SpanStyle(color = colors.secondary)

        // === Strings ===
        JSONLexer.STRING -> SpanStyle(color = colors.primary)

        // === Numbers ===
        JSONLexer.NUMBER -> SpanStyle(color = colors.tertiary)

        // === Whitespace (ignored) ===
        JSONLexer.WS -> null

        else -> null
    }
}
