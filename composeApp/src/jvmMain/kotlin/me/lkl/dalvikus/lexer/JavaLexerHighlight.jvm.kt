package me.lkl.dalvikus.lexer

// jvmMain/kotlin/me/lkl/dalvikus/ui/editor/JavaLexerWrapperJvm.kt

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import me.lkl.dalvikus.lexer.java.Java20Lexer
import me.lkl.dalvikus.theme.CodeHighlightColors
import me.lkl.dalvikus.util.base10To0xOrNull
import me.lkl.dalvikus.util.is0xHex

actual fun highlightJavaCode(code: String, colors: CodeHighlightColors): AnnotatedString {
    val lexer = Java20Lexer(CharStreams.fromString(code))
    val tokens = CommonTokenStream(lexer)
    tokens.fill()

    return buildAnnotatedString {
        withStyle(SpanStyle(color = colors.onSurface)) {
            append(code)
        }

        for (token in tokens.tokens) {
            if (token.type == -1 || token.text.isBlank()) continue

            val start = token.startIndex
            val end = token.stopIndex + 1

            if (token.type == Java20Lexer.IntegerLiteral) {
                if (token.text.is0xHex()) {
                    addStringAnnotation("hex", token.text, start, end)
                } else {
                    token.text.base10To0xOrNull()?.let {
                        addStringAnnotation("hex", it, start, end)
                    }
                }
            }

            getJava20TokenStyle(token.type, colors)?.let {
                addStyle(it, start, end)
            }
        }
    }
}

private fun getJava20TokenStyle(tokenType: Int, colors: CodeHighlightColors): SpanStyle? {
    return when (tokenType) {

        // === Keywords ===
        in 1..67 -> SpanStyle(
            color = colors.primary,
            fontWeight = FontWeight.Bold
        )

        // === Literals ===
        Java20Lexer.IntegerLiteral,
        Java20Lexer.FloatingPointLiteral,
        Java20Lexer.BooleanLiteral,
        Java20Lexer.NullLiteral,
        Java20Lexer.CharacterLiteral -> SpanStyle(
            color = colors.secondary
        )

        // === Strings & Text Blocks ===
        Java20Lexer.StringLiteral,
        Java20Lexer.TextBlock -> SpanStyle(
            color = colors.tertiary
        )

        // === Identifiers ===
        Java20Lexer.Identifier -> SpanStyle(
            color = colors.quinary
        )

        // === Operators & Symbols ===
        Java20Lexer.LPAREN, Java20Lexer.RPAREN,
        Java20Lexer.LBRACE, Java20Lexer.RBRACE,
        Java20Lexer.LBRACK, Java20Lexer.RBRACK,
        Java20Lexer.SEMI, Java20Lexer.COMMA,
        Java20Lexer.DOT, Java20Lexer.ELLIPSIS,
        Java20Lexer.COLON, Java20Lexer.COLONCOLON,
        Java20Lexer.ARROW -> SpanStyle(
            color = colors.onSurface.copy(alpha = 0.6f)
        )

        // === Annotations ===
        Java20Lexer.AT -> SpanStyle(
            color = colors.quaternary
        )

        // === Comments ===
        Java20Lexer.COMMENT,
        Java20Lexer.LINE_COMMENT -> SpanStyle(
            color = colors.onSurface.copy(alpha = 0.5f),
            fontStyle = FontStyle.Italic
        )

        // === Underscore (var placeholder) ===
        Java20Lexer.UNDER_SCORE -> SpanStyle(
            color = colors.onSurface.copy(alpha = 0.3f),
            fontStyle = FontStyle.Italic
        )

        else -> null
    }
}
