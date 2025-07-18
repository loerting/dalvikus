package me.lkl.dalvikus.ui.editor

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.android.tools.smali.smali.smaliFlexLexer
import com.android.tools.smali.smali.smaliParser
import com.materialkolor.ktx.darken
import me.lkl.dalvikus.settings.DalvikusSettings
import org.antlr.runtime.CommonToken
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.RecognitionException

data class CodeHighlightColors(
    val onSurface: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val quaternary: Color,
    val quinary: Color,
    val senary: Color
)

@Composable
fun defaultCodeHighlightColors(): CodeHighlightColors {
    return CodeHighlightColors(
        onSurface = MaterialTheme.colorScheme.onSurface,
        primary = MaterialTheme.colorScheme.primary,
        secondary = MaterialTheme.colorScheme.secondary,
        tertiary = MaterialTheme.colorScheme.tertiary,
        quaternary = Color(0xff686de0),
        quinary = Color(0xffbadc58),
        senary = Color(0xfff0932b)
    )
}

fun highlightCode(editable: Code, colors: CodeHighlightColors): AnnotatedString {
    val code = editable.code

    return buildAnnotatedString {
        // Base style
        withStyle(SpanStyle(color = colors.onSurface)) {
            append(code)
        }

        when (editable.codeType.lowercase()) {
            "json" -> {
                val stringRe = Regex("\"(?:[^\\\"\\\\]|\\\\.)*\"")
                val numberRe = Regex("-?\\d+(\\.\\d+)?([eE][-+]?\\d+)?")
                val boolRe = Regex("\\b(true|false)\\b")
                val nullRe = Regex("\\bnull\\b")

                stringRe.findAll(code).forEach {
                    addStyle(SpanStyle(color = colors.primary), it.range.first, it.range.last + 1)
                }
                numberRe.findAll(code).forEach {
                    addStyle(SpanStyle(color = colors.tertiary), it.range.first, it.range.last + 1)
                }
                boolRe.findAll(code).forEach {
                    addStyle(SpanStyle(color = colors.secondary), it.range.first, it.range.last + 1)
                }
                nullRe.findAll(code).forEach {
                    addStyle(SpanStyle(color = colors.onSurface.copy(alpha = 0.5f)), it.range.first, it.range.last + 1)
                }
            }

            "xml", "html" -> {
                val tagNameRe = Regex("</?([A-Za-z0-9_:-]+)")
                val attrNameRe = Regex("([A-Za-z_:][-\\w:]*)=")
                val attrValueRe = Regex("\"[^\"]*\"")
                val commentRe = Regex("<!--.*?-->", RegexOption.DOT_MATCHES_ALL)

                tagNameRe.findAll(code).forEach { match ->
                    val nameRange = match.groups[1]?.range ?: return@forEach
                    addStyle(SpanStyle(color = colors.primary), nameRange.first, nameRange.last + 1)
                }
                attrNameRe.findAll(code).forEach { match ->
                    val nameRange = match.groups[1]?.range ?: return@forEach
                    addStyle(SpanStyle(color = colors.secondary), nameRange.first, nameRange.last + 1)
                }
                attrValueRe.findAll(code).forEach {
                    addStyle(SpanStyle(color = colors.tertiary), it.range.first, it.range.last + 1)
                }
                commentRe.findAll(code).forEach {
                    addStyle(SpanStyle(color = colors.onSurface.copy(alpha = 0.5f)), it.range.first, it.range.last + 1)
                }
            }

            "smali" -> {
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

                val result = parser.smali_file()

                for (token in tokens.tokens) {
                    if (token !is CommonToken) continue
                    if (token.type < 0 || token.text.isBlank()) continue

                    val isError = token in errorTokens

                    if(isError) {
                        addStringAnnotation("error", errorTokens[token]!!, token.startIndex, token.stopIndex + 1)
                        addStyle(
                            SpanStyle(
                                color = Color.Red,
                                textDecoration = TextDecoration.Underline
                            ),
                            token.startIndex, token.stopIndex + 1
                        )
                    } else {
                        getToken(token.type, colors)?.let {
                            addStyle(it, token.startIndex, token.stopIndex + 1)
                        }
                    }
                }

            }

        }
    }
}

private fun getTokenName(tokenType: Int): String? {
    return smaliParser::class.java.fields
        .firstOrNull { it.type == Int::class.java && it.get(null) == tokenType }
        ?.name
}

private fun getToken(tokenType: Int, colors: CodeHighlightColors): SpanStyle? {
    val tokenName = getTokenName(tokenType) ?: return null
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
        "LINE_COMMENT" -> FontStyle.Italic
        "SIMPLE_NAME" -> FontStyle.Italic
        else -> FontStyle.Normal
    }


    return SpanStyle(
        color = color,
        fontWeight = weight,
        fontStyle = style,
    )
}