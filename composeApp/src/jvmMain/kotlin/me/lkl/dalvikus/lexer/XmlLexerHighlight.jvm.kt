package me.lkl.dalvikus.lexer

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import me.lkl.dalvikus.ui.editor.CodeHighlightColors

actual fun highlightXmlCode(code: String, colors: CodeHighlightColors): AnnotatedString {
    val lexer = XMLLexer(CharStreams.fromString(code))
    val tokens = CommonTokenStream(lexer)
    tokens.fill()

    return buildAnnotatedString {
        append(code)

        for (token in tokens.tokens) {
            if (token.type == -1 || token.text.isNullOrBlank()) continue

            getXmlTokenStyle(token.type, colors)?.let {
                addStyle(it, token.startIndex, token.stopIndex + 1)
            }
        }
    }
}

private fun getXmlTokenStyle(tokenType: Int, colors: CodeHighlightColors): SpanStyle? {
    return when (tokenType) {
        // === Comments ===
        XMLLexer.COMMENT -> SpanStyle(
            color = colors.onSurface.copy(alpha = 0.5f),
            fontStyle = FontStyle.Italic
        )

        // === CDATA & DTD ===
        XMLLexer.CDATA,
        XMLLexer.DTD -> SpanStyle(color = colors.secondary)

        // === Entity and Character references ===
        XMLLexer.EntityRef,
        XMLLexer.CharRef -> SpanStyle(color = colors.tertiary)

        // === Opening & closing brackets and delimiters ===
        XMLLexer.OPEN,          // <
        XMLLexer.CLOSE,         // >
        XMLLexer.SPECIAL_CLOSE, // ?>
        XMLLexer.SLASH_CLOSE,   // />
        XMLLexer.SLASH,         // /
        XMLLexer.EQUALS         // =
            -> SpanStyle(color = colors.onSurface.copy(alpha = 0.6f))

        // === Strings (attribute values) ===
        XMLLexer.STRING -> SpanStyle(color = colors.primary)

        // === Tag/Attribute Names ===
        XMLLexer.Name -> SpanStyle(
            color = colors.quaternary,
            fontWeight = FontWeight.Medium
        )

        // === XML declaration open: <?xml
        XMLLexer.XMLDeclOpen -> SpanStyle(
            color = colors.quinary,
            fontWeight = FontWeight.Bold
        )

        // === Processing Instructions ===
        XMLLexer.PI -> SpanStyle(
            color = colors.senary,
            fontStyle = FontStyle.Italic
        )

        // === Text Content ===
        XMLLexer.TEXT -> SpanStyle(color = colors.onSurface)

        // === Whitespace (ignored) ===
        XMLLexer.SEA_WS,
        XMLLexer.S -> null

        else -> null
    }
}
