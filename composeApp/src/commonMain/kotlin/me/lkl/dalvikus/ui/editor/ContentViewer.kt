package me.lkl.dalvikus.ui.editor

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.theme.JetBrainsMono
import me.lkl.dalvikus.ui.editor.Editor.Content
import me.lkl.dalvikus.ui.editor.Editor.Lines
import me.lkl.dalvikus.ui.util.withoutWidthConstraints


class Editor(val loader: suspend () -> String) {
    data class Line(val number: Int, val content: Content)
    data class Lines(val size: Int, val lineNumberDigitCount: Int, val lines: List<Line>)

    sealed class Content(val value: AnnotatedString) {
        class Markdown(value: AnnotatedString) : Content(value)
        class Code(value: AnnotatedString, val fileExtension: String) : Content(value)
        class Plain(value: AnnotatedString) : Content(value)
    }
}

suspend fun loadContent(
    editor: Editor,
    fileExtension: String
): Lines {
    val text = editor.loader()
    val split = text.lines()
    val digits = split.size.toString().length

    return Lines(
        size = split.size,
        lineNumberDigitCount = digits,
        lines = split.mapIndexed { idx, line ->
            Editor.Line(
                number = idx + 1,
                content = when (fileExtension) {
                    "txt", "log" -> Content.Plain(AnnotatedString(line))
                    "md" -> Content.Markdown(AnnotatedString(line))
                    else -> Content.Code(AnnotatedString(line), fileExtension)
                }
            )
        }
    )
}

@Composable
fun CodeViewer(tab: TabElement, content: suspend () -> String, fileExtension: String, viewerSettings: ViewerSettings) {
    val model = remember(tab) { Editor(content) }
    var lines by remember { mutableStateOf<Lines?>(null) }

    LaunchedEffect(model) {
        withContext(Dispatchers.IO) {
            lines = loadContent(model, fileExtension)
        }
    }

    Viewer(lines, viewerSettings)
}

@Composable
private fun Viewer(lines: Lines?, viewerSettings: ViewerSettings) {
    with(LocalDensity.current) {
        Spacer(modifier = Modifier.height(viewerSettings.fontSizeDp * 0.5f))
        SelectionContainer {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (lines != null) {
                    Lines(lines, viewerSettings)
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun Lines(linesData: Lines, viewerSettings: ViewerSettings) {
    // TODO add horizontal scroll functionality.
    val scrollState = rememberLazyListState()
    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState
        ) {
            items(linesData.size) { index ->
                Box(Modifier.height(viewerSettings.fontSizeDp * 1.6f)) {
                    Line(
                        modifier = Modifier.align(Alignment.CenterStart),
                        maxNumDigits = linesData.lineNumberDigitCount,
                        line = linesData.lines[index],
                        viewerSettings = viewerSettings
                    )
                }
            }
        }
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.align(Alignment.CenterEnd).padding(8.dp),
            style = LocalScrollbarStyle.current.copy(
                minimalHeight = 24.dp,
                thickness = 12.dp,
                unhoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
                hoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.50f)
            )
        )
    }
}

@Composable
private fun Line(
    modifier: Modifier,
    maxNumDigits: Int,
    line: Editor.Line,
    viewerSettings: ViewerSettings
) {
    Row(modifier = modifier) {
        DisableSelection {
            Box {
                // to account for the width of the line numbers
                LineNumber(
                    number = "9".repeat(maxNumDigits),
                    modifier = Modifier.alpha(0f),
                    viewerSettings = viewerSettings
                )
                LineNumber(
                    number = line.number.toString(),
                    modifier = Modifier.align(Alignment.CenterEnd),
                    viewerSettings = viewerSettings
                )
            }
        }
        LineContent(
            content = line.content,
            modifier = Modifier
                .weight(1f)
                .withoutWidthConstraints()
                .padding(start = 28.dp, end = 12.dp),
            viewerSettings = viewerSettings
        )
    }
}

@Composable
private fun LineNumber(number: String, modifier: Modifier, viewerSettings: ViewerSettings) {
    Text(
        text = number,
        fontFamily = JetBrainsMono(),
        fontSize = viewerSettings.fontSize,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        modifier = modifier.padding(start = 12.dp)
    )
}

private val headlinePattern = Regex("^#{1,6}\\s")
private val linkPattern = Regex("\\[([^\\]]+)]\\(([^\\)]+)\\)")

@Composable
private fun LineContent(
    content: Editor.Content,
    modifier: Modifier,
    viewerSettings: ViewerSettings
) {
    when (content) {
        is Editor.Content.Markdown -> {
            MarkdownLine(content.value.text, modifier, viewerSettings)
        }
        is Editor.Content.Code -> {
            Text(
                text = codeString(content),
                fontSize = viewerSettings.fontSize,
                fontFamily = JetBrainsMono(),
                maxLines = 1,
                overflow = TextOverflow.Visible,
                softWrap = false,
                modifier = modifier
            )
        }
        else -> {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                        append(content.value.text)
                    }
                },
                fontSize = viewerSettings.fontSize,
                fontFamily = JetBrainsMono(),
                maxLines = 1,
                overflow = TextOverflow.Visible,
                softWrap = false,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun MarkdownLine(
    text: String,
    modifier: Modifier,
    viewerSettings: ViewerSettings
) {
    val headlineMatch = remember(text) { headlinePattern.find(text) }
    val isHeadline = headlineMatch != null && headlineMatch.range.first == 0
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val uriHandler = LocalUriHandler.current

    val annotatedText = buildMarkdownAnnotatedString(text, isHeadline)
    Text(
        text = annotatedText,
        fontSize = if (isHeadline) viewerSettings.fontSize * 1.2f else viewerSettings.fontSize,
        fontFamily = JetBrainsMono(),
        fontWeight = if (isHeadline) FontWeight.Bold else FontWeight.Normal,
        maxLines = 1,
        overflow = TextOverflow.Visible,
        softWrap = false,
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { tapOffset: Offset ->
                layoutResult?.let { layout ->
                    val position = layout.getOffsetForPosition(tapOffset)
                    annotatedText.getStringAnnotations(
                        tag = "URL",
                        start = position,
                        end = position
                    ).firstOrNull()?.let { annotation ->
                        uriHandler.openUri(annotation.item)
                    }
                }
            }
        },
        onTextLayout = { layoutResult = it }
    )
}

@Composable
private fun buildMarkdownAnnotatedString(text: String, isHeadline: Boolean): AnnotatedString {
    return buildAnnotatedString {
        val headlineMatch = headlinePattern.find(text)
        val startIndex = if (headlineMatch != null && headlineMatch.range.first == 0) {
            headlineMatch.range.last + 1
        } else {
            0
        }

        val remainingText = text.substring(startIndex)
        var currentIndex = 0

        for (match in linkPattern.findAll(remainingText)) {
            // Append text before the link
            if (match.range.first > currentIndex) {
                append(remainingText.substring(currentIndex, match.range.first))
            }

            // Add link annotation and style
            val linkText = match.groupValues[1]
            val url = match.groupValues[2]

            pushStringAnnotation(
                tag = "URL",
                annotation = url
            )
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(linkText)
            }
            pop()

            currentIndex = match.range.last + 1
        }

        // Append remaining text after last link
        if (currentIndex < remainingText.length) {
            append(remainingText.substring(currentIndex))
        }
    }
}

@Composable
private fun codeString(code: Editor.Content.Code): AnnotatedString = buildAnnotatedString {
    val str = code.value.text
    val formatted = str.replace("\t", " ")
    // Base style
    withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
        append(formatted)
    }
    when(code.fileExtension) {
        "xml", "html" -> {
            // tags: <tag or </tag
            val tagNameRegex = Regex("</?([A-Za-z0-9_:-]+)")
            tagNameRegex.findAll(formatted).forEach { match ->
                val nameRange = match.groups[1]?.range ?: return@forEach
                addStyle(SpanStyle(color = MaterialTheme.colorScheme.primary), nameRange.first, nameRange.last + 1)
            }
            // attributes: name=
            val attrNameRegex = Regex("([A-Za-z_:][-\\w:]*)=")
            attrNameRegex.findAll(formatted).forEach { match ->
                val nameRange = match.groups[1]?.range ?: return@forEach
                addStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary), nameRange.first, nameRange.last + 1)
            }
            // attribute values: "..."
            val attrValueRegex = Regex("\"[^\"]*\"")
            attrValueRegex.findAll(formatted).forEach { match ->
                addStyle(SpanStyle(color = MaterialTheme.colorScheme.tertiary), match.range.first, match.range.last + 1)
            }
            // comments <!-- ... -->
            val commentRegex = Regex("<!--.*?-->")
            commentRegex.findAll(formatted).forEach { match ->
                addStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)), match.range.first, match.range.last + 1)
            }
        }
        "json" -> {
            // Strings (keys and values)
            val stringRegex = Regex("\"(?:[^\"\\\\]|\\\\.)*\"")
            stringRegex.findAll(formatted).forEach { match ->
                addStyle(
                    style = SpanStyle(color = MaterialTheme.colorScheme.primary),
                    start = match.range.first,
                    end = match.range.last + 1
                )
            }

            // Numbers
            val numberRegex = Regex("-?\\d+(\\.\\d+)?([eE][-+]?\\d+)?")
            numberRegex.findAll(formatted).forEach { match ->
                addStyle(
                    style = SpanStyle(color = MaterialTheme.colorScheme.tertiary),
                    start = match.range.first,
                    end = match.range.last + 1
                )
            }

            // Booleans (true/false)
            val booleanRegex = Regex("\\b(true|false)\\b")
            booleanRegex.findAll(formatted).forEach { match ->
                addStyle(
                    style = SpanStyle(color = MaterialTheme.colorScheme.secondary),
                    start = match.range.first,
                    end = match.range.last + 1
                )
            }

            // Null
            val nullRegex = Regex("\\bnull\\b")
            nullRegex.findAll(formatted).forEach { match ->
                addStyle(
                    style = SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
                    start = match.range.first,
                    end = match.range.last + 1
                )
            }
        }
    }
}

// Helpers unchanged
private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: String) {
    addStyle(style, text, Regex.fromLiteral(regexp))
}

private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: Regex) {
    regexp.findAll(text).forEach { result ->
        addStyle(style, result.range.first, result.range.last + 1)
    }
}
