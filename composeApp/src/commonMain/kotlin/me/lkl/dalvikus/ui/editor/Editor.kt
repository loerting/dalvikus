package me.lkl.dalvikus.ui.editor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.lkl.dalvikus.theme.JetBrainsMono

/**
 * A composable code editor with line numbers, scroll syncing,
 * multiline editing, horizontal + vertical scrolling, and syntax highlighting.
 */
@Composable
fun Editor(
    editable: EditableCode,
    viewerSettings: ViewerSettings
) {
    var loaded by remember(editable) { mutableStateOf(false) }

    LaunchedEffect(editable) {
        editable.loadCode()
        loaded = true
    }

    if (!loaded) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Scroll states
    val vertState = rememberScrollState()
    val horState = rememberScrollState()
    val coroutine = rememberCoroutineScope()

    var highlightedText by remember { mutableStateOf(AnnotatedString("")) }
    val highlightColors = defaultCodeHighlightColors()

    LaunchedEffect(editable.textFieldValue.text) {
        // TODO this is good but can be better. instead of setting the whole document black while recoloring, we can make only the changes black while recoloring.
        highlightedText = AnnotatedString(editable.textFieldValue.text)
        val highlighted = withContext(Dispatchers.Default) {
            highlightCode(editable, highlightColors)
        }
        highlightedText = highlighted
    }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxSize()) {

            // Line Numbers
            LineNumberColumn(
                code = editable.textFieldValue.text,
                scrollState = vertState,
                viewerSettings = viewerSettings
            )
            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            )

            // Editor + Scrollbars Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(end = 12.dp, bottom = 12.dp) // leave room for scrollbars
            ) {
                // Scrollable content
                Box(
                    modifier = Modifier
                        .verticalScroll(vertState)
                        .horizontalScroll(horState)
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    BasicTextField(
                        value = editable.textFieldValue,
                        onValueChange = { new ->
                            coroutine.launch { editable.updateCode(new) }
                        },
                        modifier = Modifier.fillMaxSize(),
                        textStyle = TextStyle(
                            fontFamily = JetBrainsMono(),
                            fontSize = viewerSettings.fontSize,
                            lineHeight = viewerSettings.fontSize * 1.5f,
                            color = Color.Transparent
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { inner ->
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(1.dp)
                            ) {
                                // Highlight background
                                Text(
                                    text = highlightedText,
                                    modifier = Modifier.matchParentSize(),
                                    style = TextStyle(
                                        fontFamily = JetBrainsMono(),
                                        fontSize = viewerSettings.fontSize,
                                        lineHeight = viewerSettings.fontSize * 1.5f,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                inner()
                            }
                        }
                    )
                }

                // Vertical Scrollbar
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(vertState),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .padding(end = 0.dp, top = 8.dp),
                    style = LocalScrollbarStyle.current.copy(
                        minimalHeight = 24.dp,
                        thickness = 12.dp,
                        unhoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
                        hoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.50f)
                    )
                )

                // Horizontal Scrollbar
                HorizontalScrollbar(
                    adapter = rememberScrollbarAdapter(horState),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(bottom = 0.dp, start = 8.dp),
                    style = LocalScrollbarStyle.current.copy(
                        minimalHeight = 24.dp,
                        thickness = 12.dp,
                        unhoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
                        hoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.50f)
                    )
                )
            }
        }
    }
}


@Composable
private fun LineNumberColumn(
    code: String,
    scrollState: ScrollState,
    viewerSettings: ViewerSettings,
) {
    val lines = code.lines().size
    val lineHeightDp = with(LocalDensity.current) { viewerSettings.fontSizeDp * 1.5f }
    val maxNumDigits = lines.toString().length

    DisableSelection {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(top = 8.dp, end = 8.dp)
                .width(IntrinsicSize.Min)
        ) {
            repeat(lines) { i ->

                Box(modifier = Modifier.height(lineHeightDp)) {
                        // to account for the width of the line numbers
                        LineNumber(
                            number = "9".repeat(maxNumDigits),
                            modifier = Modifier.alpha(0f),
                            viewerSettings = viewerSettings
                        )
                        LineNumber(
                            number = "${i + 1}",
                            modifier = Modifier.align(Alignment.CenterEnd),
                            viewerSettings = viewerSettings
                        )
                    }

            }
        }
    }
}

@Composable
private fun LineNumber(number: String, modifier: Modifier, viewerSettings: ViewerSettings) {
    androidx.compose.material.Text(
        text = number,
        fontFamily = JetBrainsMono(),
        fontSize = viewerSettings.fontSize,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        modifier = modifier.padding(start = 12.dp)
    )
}