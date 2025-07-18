package me.lkl.dalvikus.ui.editor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.lkl.dalvikus.settings.DalvikusSettings
import me.lkl.dalvikus.theme.JetBrainsMono
import kotlin.math.max

/**
 * A composable code editor with line numbers, scroll syncing,
 * multiline editing, horizontal + vertical scrolling, and syntax highlighting.
 */
@Composable
fun Editor(
    editable: Code,
    dalvikusSettings: DalvikusSettings
) {
    var loaded by remember(editable) { mutableStateOf(false) }

    LaunchedEffect(editable) {
        withContext(Dispatchers.Default) {
            editable.loadCode()
        }
        loaded = true
    }

    if (!loaded) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }

    // Scroll states
    val vertState = rememberScrollState()
    val horState = rememberScrollState()
    val coroutine = rememberCoroutineScope()

    val highlightColors = defaultCodeHighlightColors()
    var highlightedText by remember(editable) {
        mutableStateOf(AnnotatedString(editable.code))
    }

    LaunchedEffect(editable.code) {
        highlightedText = withContext(Dispatchers.Default) {
            highlightCode(editable, highlightColors)
        }
    }

    val fontSize = (dalvikusSettings["font_size"] as Int).sp
    Box(modifier = Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxSize()) {

            // Line Numbers
            LineNumberColumn(
                code = editable.code,
                scrollState = vertState,
                fontSize = fontSize
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
                        value = editable.code,
                        readOnly = !editable.isEditable,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Ascii
                        ),
                        onValueChange = { newText ->
                            // intermediate highlightedText with updated text and old highlighting.
                            val maxTextLength = max(newText.length, highlightedText.text.length)
                            val textPlaceholder = newText.padEnd(maxTextLength, ' ')
                            highlightedText = AnnotatedString(
                                textPlaceholder,
                                highlightedText.spanStyles,
                                highlightedText.paragraphStyles
                            )

                            editable.updateCode(newText)
                            if(editable.isEditable) {
                            coroutine.launch { editable.saveCode() }
                                }
                        },
                        modifier = Modifier.fillMaxSize(),
                        textStyle = TextStyle(
                            fontFamily = JetBrainsMono(),
                            fontSize = fontSize,
                            lineHeight = fontSize * 1.5f,
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
                                        fontSize = fontSize,
                                        lineHeight = fontSize * 1.5f,
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
    fontSize: TextUnit,
) {
    val lines = code.lines().size
    val lineHeightDp = with(LocalDensity.current) { fontSize.toDp() * 1.5f }
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
                        fontSize = fontSize,
                    )
                    LineNumber(
                        number = "${i + 1}",
                        modifier = Modifier.align(Alignment.CenterEnd),
                        fontSize = fontSize,
                    )
                }

            }
        }
    }
}

@Composable
private fun LineNumber(number: String, modifier: Modifier, fontSize: TextUnit) {
    androidx.compose.material.Text(
        text = number,
        fontFamily = JetBrainsMono(),
        fontSize = fontSize,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        modifier = modifier.padding(start = 12.dp)
    )
}