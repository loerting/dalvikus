package me.lkl.dalvikus.ui.editor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import me.lkl.dalvikus.theme.JetBrainsMono
import me.lkl.dalvikus.ui.editor.highlight.defaultCodeHighlightColors
import me.lkl.dalvikus.ui.util.onSaveShortcut

@Composable
fun EditorScreen(editable: Code) {
    val highlightColors = defaultCodeHighlightColors()
    val viewModel = remember(editable) { EditorViewModel(editable, highlightColors) }
    val coroutine = rememberCoroutineScope()

    var firstLoad by remember { mutableStateOf(true) }
    LaunchedEffect(firstLoad) {
        if (firstLoad) {
            viewModel.loadCode()
            firstLoad = false
        }
    }

    // Highlight code on every code content change
    LaunchedEffect(editable.code) {
        // delay to wait until user finished typing.
        delay(200)
        if (viewModel.isLoaded) {
            viewModel.refreshHighlight()
        }
    }

    if (!viewModel.isLoaded) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }

    val vertState = rememberScrollState()
    val horState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxSize()) {

            LineNumberColumn(
                code = viewModel.getCode(),
                scrollState = vertState,
                fontSize = viewModel.fontSize
            )

            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(end = 12.dp, bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .verticalScroll(vertState)
                        .horizontalScroll(horState)
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    BasicTextField(
                        value = viewModel.getCode(),
                        readOnly = !viewModel.isEditable(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Ascii
                        ),
                        onValueChange = { newText ->
                            viewModel.onCodeChanged(editable.code, newText, coroutine)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .onSaveShortcut(enabled = viewModel.isEditable()) {
                                viewModel.saveCode(coroutine)
                            },
                        textStyle = TextStyle(
                            fontFamily = JetBrainsMono(),
                            fontSize = viewModel.fontSize,
                            lineHeight = viewModel.fontSize * 1.5f,
                            color = Color.Transparent
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { inner ->
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(1.dp)
                            ) {
                                Text(
                                    text = viewModel.highlightedText,
                                    modifier = Modifier.matchParentSize(),
                                    style = TextStyle(
                                        fontFamily = JetBrainsMono(),
                                        fontSize = viewModel.fontSize,
                                        lineHeight = viewModel.fontSize * 1.5f,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                inner()
                            }
                        }
                    )
                }

                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(vertState),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .padding(end = 0.dp, top = 8.dp)
                )

                HorizontalScrollbar(
                    adapter = rememberScrollbarAdapter(horState),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(bottom = 0.dp, start = 8.dp)
                )
            }
        }
    }
}
