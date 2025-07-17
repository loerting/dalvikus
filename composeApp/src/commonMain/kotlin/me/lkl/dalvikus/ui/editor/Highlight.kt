package me.lkl.dalvikus.ui.editor

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.android.tools.smali.smali.smaliFlexLexer
import com.android.tools.smali.smali.smaliParser

data class CodeHighlightColors(
    val onSurface: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color
)

@Composable
fun defaultCodeHighlightColors(): CodeHighlightColors {
    return CodeHighlightColors(
        onSurface = MaterialTheme.colorScheme.onSurface,
        primary = MaterialTheme.colorScheme.primary,
        secondary = MaterialTheme.colorScheme.secondary,
        tertiary = MaterialTheme.colorScheme.tertiary
    )
}

fun highlightCode(editable: Code, colors: CodeHighlightColors): AnnotatedString {
    val code = editable.code  //.replace("\t", " ") // Normalize tabs to spaces

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

                // TODO https://github.com/vaibhavpandeyvpz/apkstudio/commit/698e2de5cff19c96692b44b703d752291a17dc6a#diff-9d86366ee99ae761db887b8fee92b4348a137bd0534c69c8c67a6353ecd5ab58
            }
        }
    }
}