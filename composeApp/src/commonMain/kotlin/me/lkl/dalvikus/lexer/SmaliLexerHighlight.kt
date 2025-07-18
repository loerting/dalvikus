package me.lkl.dalvikus.lexer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.android.tools.smali.smali.smaliFlexLexer
import com.android.tools.smali.smali.smaliParser
import me.lkl.dalvikus.settings.DalvikusSettings
import me.lkl.dalvikus.ui.editor.CodeHighlightColors
import org.antlr.runtime.CommonToken
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.RecognitionException

fun highlightSmaliCode(code: String, colors: CodeHighlightColors): AnnotatedString {
    val apiLevel = DalvikusSettings().apiLevel
    val lexer = smaliFlexLexer(code.reader(), apiLevel)
    val errorTokens = mutableMapOf<CommonToken, String>()
    val tokens = CommonTokenStream(lexer)

    val parser = object : smaliParser(tokens) {
        override fun reportError(e: RecognitionException?) {
            super.reportError(e)
            val offending = e?.token
            if (offending is CommonToken) {
                errorTokens[offending] = getErrorMessage(e, tokenNames)
            }
        }
    }

    parser.setVerboseErrors(true)
    parser.setAllowOdex(true)
    parser.setApiLevel(apiLevel)
    parser.smali_file()

    return buildAnnotatedString {
        append(code)

        for (token in tokens.tokens) {
            if (token !is CommonToken) continue
            if (token.type < 0 || token.text.isNullOrBlank()) continue

            val start = token.startIndex
            val end = token.stopIndex + 1

            if (token in errorTokens) {
                addStringAnnotation("error", errorTokens[token]!!, start, end)
                addStyle(
                    SpanStyle(
                        color = colors.error,
                        textDecoration = TextDecoration.Underline
                    ),
                    start, end
                )
            } else {
                getSmaliTokenStyle(token.type, colors)?.let { style ->
                    addStyle(style, start, end)
                }
            }
        }
    }
}

private fun getSmaliTokenName(tokenType: Int): String? {
    return smaliParser::class.java.fields
        .firstOrNull { it.type == Int::class.java && it.get(null) == tokenType }
        ?.name
}

private fun getSmaliTokenStyle(tokenType: Int, colors: CodeHighlightColors): SpanStyle? {
    val tokenName = getSmaliTokenName(tokenType) ?: return null

    val color = when {
        tokenName.endsWith("_DIRECTIVE") -> colors.primary
        tokenName == "REGISTER" -> colors.secondary
        tokenName.endsWith("_LITERAL") -> colors.tertiary
        tokenName == "CLASS_DESCRIPTOR" || tokenName == "PRIMITIVE_TYPE" || tokenName == "ARRAY_TYPE_PREFIX" -> colors.quaternary
        tokenName == "SIMPLE_NAME" -> colors.quinary
        tokenName.startsWith("INSTRUCTION_") -> colors.senary
        tokenName in listOf("COLON", "COMMA", "OPEN_PAREN", "CLOSE_PAREN") -> colors.onSurface.copy(alpha = 0.7f)
        tokenName == "LINE_COMMENT" -> colors.onSurface.copy(alpha = 0.5f)
        else -> return null
    }

    val weight = when {
        tokenName == "ARROW" -> FontWeight.Bold
        tokenName.startsWith("INSTRUCTION_") -> FontWeight.Medium
        tokenName == "REGISTER" -> FontWeight.Medium
        else -> FontWeight.Normal
    }

    val style = when (tokenName) {
        "LINE_COMMENT", "SIMPLE_NAME" -> FontStyle.Italic
        else -> FontStyle.Normal
    }

    return SpanStyle(color = color, fontWeight = weight, fontStyle = style)
}
