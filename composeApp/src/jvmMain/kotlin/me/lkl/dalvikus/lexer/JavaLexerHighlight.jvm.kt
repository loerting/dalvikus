package me.lkl.dalvikus.lexer

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import me.lkl.dalvikus.lexer.java.JavaLexer
import me.lkl.dalvikus.lexer.java.JavaParser
import me.lkl.dalvikus.theme.CodeHighlightColors
import me.lkl.dalvikus.util.base10To0xOrNull
import me.lkl.dalvikus.util.is0xHex
import org.antlr.v4.runtime.*

actual fun highlightJavaCode(code: String, colors: CodeHighlightColors): AnnotatedString {
    val lexer = JavaLexer(CharStreams.fromString(code))
    val tokens = CommonTokenStream(lexer)

    // Parse to get semantic information
    val parser = JavaParser(tokens)
    parser.removeErrorListeners() // Suppress error messages for better UX
    val tree = parser.compilationUnit()

    val semanticInfo = JavaHighlightParserVisitor()
    semanticInfo.visit(tree)

    // Reset tokens for highlighting
    tokens.seek(0)
    tokens.fill()

    return buildAnnotatedString {
        withStyle(SpanStyle(color = colors.onSurface)) {
            append(code)
        }

        for (token in tokens.tokens) {
            if (token.type == -1 || token.text.isBlank()) continue

            val start = token.startIndex
            val end = token.stopIndex + 1
            val tokenRange = Pair(start, end)

            // Handle hex conversion for decimal literals
            if (token.type == JavaLexer.DECIMAL_LITERAL) {
                if (token.text.is0xHex()) {
                    addStringAnnotation("hex", token.text, start, end)
                } else {
                    token.text.base10To0xOrNull()?.let {
                        addStringAnnotation("hex", it, start, end)
                    }
                }
            }

            // Apply semantic highlighting first (if available)
            val semanticStyle = getSemanticStyle(tokenRange, semanticInfo, colors)
            if (semanticStyle != null) {
                addStyle(semanticStyle, start, end)
            } else {
                // Fallback to basic token-based highlighting
                getJavaTokenStyle(token.type, colors)?.let {
                    addStyle(it, start, end)
                }
            }
        }
    }
}

private fun getSemanticStyle(tokenRange: Pair<Int, Int>, semanticInfo: JavaHighlightParserVisitor, colors: CodeHighlightColors): SpanStyle? {
    return when {
        tokenRange in semanticInfo.methodNames -> SpanStyle(
            color = colors.senary,
        )
        tokenRange in semanticInfo.classNames -> SpanStyle(
            color = colors.septenary,
        )
        tokenRange in semanticInfo.fieldNames -> SpanStyle(
            color = colors.tertiary,
        )
        else -> null
    }
}

private fun getJavaTokenStyle(tokenType: Int, colors: CodeHighlightColors): SpanStyle? {
    return when (tokenType) {
        // === Keywords ===
        JavaLexer.ABSTRACT, JavaLexer.ASSERT, JavaLexer.BOOLEAN, JavaLexer.BREAK,
        JavaLexer.BYTE, JavaLexer.CASE, JavaLexer.CATCH, JavaLexer.CHAR,
        JavaLexer.CLASS, JavaLexer.CONST, JavaLexer.CONTINUE, JavaLexer.DEFAULT,
        JavaLexer.DO, JavaLexer.DOUBLE, JavaLexer.ELSE, JavaLexer.ENUM,
        JavaLexer.EXPORTS, JavaLexer.EXTENDS, JavaLexer.FINAL, JavaLexer.FINALLY,
        JavaLexer.FLOAT, JavaLexer.FOR, JavaLexer.GOTO, JavaLexer.IF,
        JavaLexer.IMPLEMENTS, JavaLexer.IMPORT, JavaLexer.INSTANCEOF, JavaLexer.INT,
        JavaLexer.INTERFACE, JavaLexer.LONG, JavaLexer.MODULE, JavaLexer.NATIVE,
        JavaLexer.NEW, JavaLexer.NON_SEALED, JavaLexer.OPEN, JavaLexer.OPENS,
        JavaLexer.PACKAGE, JavaLexer.PERMITS, JavaLexer.PRIVATE, JavaLexer.PROTECTED,
        JavaLexer.PROVIDES, JavaLexer.PUBLIC, JavaLexer.RECORD, JavaLexer.REQUIRES,
        JavaLexer.RETURN, JavaLexer.SEALED, JavaLexer.SHORT, JavaLexer.STATIC,
        JavaLexer.STRICTFP, JavaLexer.SUPER, JavaLexer.SWITCH, JavaLexer.SYNCHRONIZED,
        JavaLexer.THIS, JavaLexer.THROW, JavaLexer.THROWS, JavaLexer.TO,
        JavaLexer.TRANSIENT, JavaLexer.TRANSITIVE, JavaLexer.TRY, JavaLexer.USES,
        JavaLexer.VAR, JavaLexer.VOID, JavaLexer.VOLATILE, JavaLexer.WHEN,
        JavaLexer.WHILE, JavaLexer.WITH, JavaLexer.YIELD -> SpanStyle(
            color = colors.primary,
            fontWeight = FontWeight.Bold
        )

        // === Literals ===
        JavaLexer.DECIMAL_LITERAL, JavaLexer.HEX_LITERAL, JavaLexer.OCT_LITERAL,
        JavaLexer.BINARY_LITERAL -> SpanStyle(
            color = colors.secondary
        )

        JavaLexer.FLOAT_LITERAL, JavaLexer.HEX_FLOAT_LITERAL -> SpanStyle(
            color = colors.secondary
        )

        JavaLexer.BOOL_LITERAL, JavaLexer.NULL_LITERAL -> SpanStyle(
            color = colors.secondary,
            fontWeight = FontWeight.Bold
        )

        JavaLexer.CHAR_LITERAL -> SpanStyle(
            color = colors.secondary
        )

        // === Strings & Text Blocks ===
        JavaLexer.STRING_LITERAL, JavaLexer.TEXT_BLOCK -> SpanStyle(
            color = colors.tertiary
        )

        // === Identifiers (fallback - should be handled by semantic analysis) ===
        /*JavaLexer.IDENTIFIER -> SpanStyle(
            color = colors.quinary
        )*/

        // === Operators & Symbols ===
        JavaLexer.LPAREN, JavaLexer.RPAREN,
        JavaLexer.LBRACE, JavaLexer.RBRACE,
        JavaLexer.LBRACK, JavaLexer.RBRACK,
        JavaLexer.SEMI, JavaLexer.COMMA,
        JavaLexer.DOT, JavaLexer.ELLIPSIS,
        JavaLexer.COLON, JavaLexer.COLONCOLON,
        JavaLexer.ARROW -> SpanStyle(
            color = colors.onSurface.copy(alpha = 0.6f)
        )

        // === Assignment and Comparison Operators ===
        JavaLexer.ASSIGN, JavaLexer.GT, JavaLexer.LT, JavaLexer.BANG,
        JavaLexer.TILDE, JavaLexer.QUESTION, JavaLexer.EQUAL, JavaLexer.LE,
        JavaLexer.GE, JavaLexer.NOTEQUAL, JavaLexer.AND, JavaLexer.OR,
        JavaLexer.INC, JavaLexer.DEC, JavaLexer.ADD, JavaLexer.SUB,
        JavaLexer.MUL, JavaLexer.DIV, JavaLexer.BITAND, JavaLexer.BITOR,
        JavaLexer.CARET, JavaLexer.MOD -> SpanStyle(
            color = colors.onSurface.copy(alpha = 0.7f)
        )

        // === Assignment Operators ===
        JavaLexer.ADD_ASSIGN, JavaLexer.SUB_ASSIGN, JavaLexer.MUL_ASSIGN,
        JavaLexer.DIV_ASSIGN, JavaLexer.AND_ASSIGN, JavaLexer.OR_ASSIGN,
        JavaLexer.XOR_ASSIGN, JavaLexer.MOD_ASSIGN, JavaLexer.LSHIFT_ASSIGN,
        JavaLexer.RSHIFT_ASSIGN, JavaLexer.URSHIFT_ASSIGN -> SpanStyle(
            color = colors.onSurface.copy(alpha = 0.7f)
        )

        // === Annotations ===
        JavaLexer.AT -> SpanStyle(
            color = colors.quaternary
        )

        // === Comments ===
        JavaLexer.COMMENT, JavaLexer.LINE_COMMENT -> SpanStyle(
            color = colors.onSurface.copy(alpha = 0.5f),
            fontStyle = FontStyle.Italic
        )

        else -> null
    }
}