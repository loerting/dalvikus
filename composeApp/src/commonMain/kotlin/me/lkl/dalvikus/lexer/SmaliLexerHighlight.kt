package me.lkl.dalvikus.lexer

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.android.tools.smali.smali.smaliFlexLexer
import com.android.tools.smali.smali.smaliParser
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.smali.ErrorHandlingSmaliParser
import me.lkl.dalvikus.ui.editor.highlight.CodeHighlightColors
import org.antlr.runtime.*


fun highlightSmaliCode(code: String, colors: CodeHighlightColors): AnnotatedString {
    val apiLevel = dalvikusSettings["api_level"] as Int
    val lexer = smaliFlexLexer(code.reader(), apiLevel)
    val tokens = CommonTokenStream(lexer)

    val parser = ErrorHandlingSmaliParser(tokens)

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

            if (token.type == smaliParser.CLASS_DESCRIPTOR) {
                val clazz = token.text.removePrefix("L").removeSuffix(";")
                addStringAnnotation("class", clazz, start, end)
            }

            if (token in parser.errorTokens) {
                addStringAnnotation("error", parser.errorTokens[token]!!, start, end)
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

fun getSmaliTokenStyle(tokenType: Int, colors: CodeHighlightColors): SpanStyle? {
    val tokenName = getSmaliTokenName(tokenType) ?: return null

    val color = when {
        tokenName.endsWith("_DIRECTIVE") -> colors.primary
        tokenName == "REGISTER" -> colors.secondary
        tokenName.endsWith("_LITERAL") -> colors.tertiary
        tokenName == "CLASS_DESCRIPTOR" || tokenName == "PRIMITIVE_TYPE" || tokenName == "ARRAY_TYPE_PREFIX" -> colors.quaternary
        tokenName == "SIMPLE_NAME" -> colors.quinary
        tokenName == "ACCESS_SPEC" -> colors.septenary
        tokenName.startsWith("INSTRUCTION_") -> colors.senary
        tokenName in listOf("COLON", "COMMA", "OPEN_PAREN", "CLOSE_PAREN") -> colors.onSurface.copy(alpha = 0.7f)
        tokenName == "LINE_COMMENT" -> colors.onSurface.copy(alpha = 0.5f)
        tokenName == "ANNOTATION_VISIBILITY" -> colors.primary
        else -> return null
    }

    val weight = when {
        tokenName == "ARROW" -> FontWeight.Bold
        tokenName.startsWith("INSTRUCTION_") -> FontWeight.Medium
        tokenName == "ACCESS_SPEC" -> FontWeight.SemiBold
        tokenName.endsWith("_DIRECTIVE") -> FontWeight.SemiBold

        else -> FontWeight.Normal
    }

    val style = when (tokenName) {
        "LINE_COMMENT", "SIMPLE_NAME" -> FontStyle.Italic
        else -> FontStyle.Normal
    }

    return SpanStyle(color = color, fontWeight = weight, fontStyle = style)
}
