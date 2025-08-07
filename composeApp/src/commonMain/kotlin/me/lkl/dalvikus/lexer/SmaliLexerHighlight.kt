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
            else if (token.type in listOf(
                    smaliParser.INTEGER_LITERAL,
                    smaliParser.POSITIVE_INTEGER_LITERAL,
                    smaliParser.SHORT_LITERAL,
                    smaliParser.LONG_LITERAL,
                ) && token.text.startsWith("0x") || token.text.startsWith("-0x")) {
                addStringAnnotation("hex", token.text, start, end)
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
        tokenName.startsWith("INSTRUCTION_") -> colors.senary
        tokenName.endsWith("_DIRECTIVE") -> colors.primary
        tokenName.endsWith("_LITERAL") -> colors.tertiary
        tokenName.endsWith("_TYPE") || tokenName == "CLASS_DESCRIPTOR" || tokenName == "ARRAY_TYPE_PREFIX" -> colors.quaternary
        tokenName.endsWith("_NAME") -> colors.quinary

        tokenName == "REGISTER" -> colors.secondary
        tokenName == "ACCESS_SPEC" || tokenName == "ANNOTATION_VISIBILITY" -> colors.septenary

        tokenName in listOf("COLON", "COMMA", "OPEN_PAREN", "CLOSE_PAREN") -> colors.onSurface.copy(alpha = 0.7f)
        tokenName == "LINE_COMMENT" -> colors.onSurface.copy(alpha = 0.5f)
        else -> colors.onSurface
    }

    val weight = when {
        tokenName == "ARROW" -> FontWeight.Bold
        tokenName.startsWith("INSTRUCTION_") -> FontWeight.Medium
        tokenName == "ACCESS_SPEC" || tokenName == "ANNOTATION_VISIBILITY" -> FontWeight.SemiBold
        tokenName.endsWith("_DIRECTIVE") -> FontWeight.SemiBold

        else -> FontWeight.Normal
    }

    val style = when (tokenName) {
        "LINE_COMMENT", "SIMPLE_NAME" -> FontStyle.Italic
        else -> FontStyle.Normal
    }

    return SpanStyle(color = color, fontWeight = weight, fontStyle = style)
}
