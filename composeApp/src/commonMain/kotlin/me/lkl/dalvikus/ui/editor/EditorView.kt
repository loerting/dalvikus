package me.lkl.dalvikus.ui.editor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAs
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.editor_cannot_open
import dalvikus.composeapp.generated.resources.fab_save_and_assemble
import kotlinx.coroutines.delay
import me.lkl.dalvikus.settings.shortcutSave
import me.lkl.dalvikus.tabs.SmaliTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.theme.LocalThemeIsDark
import me.lkl.dalvikus.theme.Monaspace
import me.lkl.dalvikus.ui.editor.highlight.defaultCodeHighlightColors
import me.lkl.dalvikus.ui.editor.suggestions.AssistPopup
import me.lkl.dalvikus.ui.editor.suggestions.ErrorPopup
import me.lkl.dalvikus.ui.editor.suggestions.HexPopup
import me.lkl.dalvikus.ui.editor.suggestions.LookupPopup
import me.lkl.dalvikus.util.handleFocusedCtrlShortcuts
import org.jetbrains.compose.resources.stringResource

data class LayoutSnapshot(val layout: TextLayoutResult, val textFieldValue: TextFieldValue)

@Composable
fun EditorView(tabElement: TabElement) {


    val isDarkState: MutableState<Boolean> = LocalThemeIsDark.current

    val viewModel = remember(tabElement) { EditorViewModel(tabElement) }
    viewModel.highlightColors = defaultCodeHighlightColors(isDarkState.value)


    if (!viewModel.openable) {
        EditorCannotOpen()
        return
    }

    LaunchedEffect(isDarkState.value) {
        viewModel.refreshHighlight()
    }

    val coroutine = rememberCoroutineScope()

    var firstLoad by remember(tabElement) { mutableStateOf(true) }

    LaunchedEffect(tabElement) {
        if (firstLoad) {
            viewModel.loadCode()
            firstLoad = false
        }
    }

    // Highlight code on every code content change.
    LaunchedEffect(viewModel.internalContent) {
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

    var lastLayoutSnapshot by remember { mutableStateOf<LayoutSnapshot?>(null) }

    val textStyle = TextStyle(
        fontFamily = Monaspace(),
        fontSize = viewModel.fontSize,
        lineHeight = viewModel.fontSize * 1.5f,
        color = MaterialTheme.colorScheme.onSurface
    )
    val textMeasurer = rememberTextMeasurer()

    val vertState = rememberScrollState()
    val horState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            if(viewModel.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp).padding(bottom = 8.dp, end = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                return@Scaffold
            }
            if (viewModel.hasUnsavedChanges())
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(bottom = 8.dp, end = 8.dp),
                    onClick = { viewModel.saveCode(coroutine) },
                    icon = {
                        Icon(
                            Icons.Default.SaveAs,
                            contentDescription = stringResource(Res.string.fab_save_and_assemble)
                        )
                    },
                    text = {
                        Text(stringResource(Res.string.fab_save_and_assemble))
                    }
                )
        },
        modifier = Modifier.fillMaxSize().then(viewModel.popupKeyEvents)
    ) {
        val textContentPadding = PaddingValues(
            start = 1.dp,
            top = 4.dp,
            end = 32.dp,
            bottom = 32.dp
        )

        Row {
            LineNumberColumn(
                lastLayoutSnapshot?.layout,
                scrollState = vertState,
                textContentPadding = textContentPadding,
                textStyle = textStyle
            )

            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            )
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                val viewportHeight = constraints.maxHeight.toFloat()
                Box(
                    modifier = Modifier
                        .horizontalScroll(horState)
                        .verticalScroll(vertState)
                        .fillMaxSize()
                        .drawBehind {
                            if (viewModel.highlightedText.isEmpty()) return@drawBehind
                            val scrollY = vertState.value
                            val lineHeight = textStyle.lineHeight.toPx()
                            val startLine = (scrollY / lineHeight).toInt()
                            val endLine = ((scrollY + viewportHeight) / lineHeight).toInt()

                            clipRect {
                                // This is a crazy hack, but it works fine.
                                val annotatedText = viewModel.getClippedHighlightedText(startLine - 1, endLine + 1)
                                val layoutResult = textMeasurer.measure(
                                    text = annotatedText,
                                    style = textStyle,
                                    constraints = Constraints(maxWidth = size.width.toInt()),
                                )

                                drawText(
                                    textLayoutResult = layoutResult,
                                    topLeft = Offset(textContentPadding.calculateLeftPadding(LayoutDirection.Ltr).toPx(), textContentPadding.calculateTopPadding().toPx()),
                                    brush = SolidColor(Color.Black),
                                    alpha = 1f,
                                    shadow = null,
                                    textDecoration = null,
                                    drawStyle = null,
                                    blendMode = BlendMode.SrcOver
                                )
                            }
                        }
                ) {
                    BasicTextField(
                        value = viewModel.internalContent,
                        readOnly = !viewModel.editable,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Ascii
                        ),
                        onValueChange = { newText ->
                            viewModel.changeContent(newText, coroutine)
                        },
                        modifier = Modifier
                            .padding(textContentPadding)
                            .fillMaxSize()
                            .focusRequester(focusRequester)
                            .handleFocusedCtrlShortcuts(
                                enabled = viewModel.editable,
                                mapOf(shortcutSave to { viewModel.saveCode(coroutine) })
                            ),

                        textStyle = textStyle.copy(
                            color = Color.Transparent
                        ),
                        onTextLayout = {
                            lastLayoutSnapshot = LayoutSnapshot(
                                layout = it,
                                textFieldValue = viewModel.internalContent
                            )
                        },
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                    )
                    if (tabElement is SmaliTab) {
                        AssistPopup(
                            assistPopupState = viewModel.assistPopupState,
                            viewModel = viewModel,
                            lastLayoutSnapshot = lastLayoutSnapshot,
                            textStyle = textStyle,
                            highlightColors = viewModel.highlightColors
                        )
                    }
                    val start = viewModel.internalContent.selection.start
                    val end = viewModel.internalContent.selection.end

                    // these are annotated in the smali highlighter
                    listOf("error", "class", "hex").forEach { tag ->
                        viewModel.highlightedText.getStringAnnotations(tag, start, end).firstOrNull()?.let {
                            when (tag) {
                                "error" -> ErrorPopup(lastLayoutSnapshot, it, viewModel, textStyle)
                                "class" -> {
                                    if (tabElement is SmaliTab) {
                                        LookupPopup(tabElement, lastLayoutSnapshot, it, viewModel, textStyle)
                                    }
                                }

                                "hex" -> HexPopup(it, textStyle, lastLayoutSnapshot, viewModel)
                            }
                            return@forEach // stops after showing the first popup
                        }
                    }

                }

                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(vertState),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .padding(bottom = 8.dp, end = 8.dp)
                )

                HorizontalScrollbar(
                    adapter = rememberScrollbarAdapter(horState),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, end = 8.dp)
                )
            }
        }
    }
}

@Composable
fun EditorCannotOpen() {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.SentimentDissatisfied,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = stringResource(Res.string.editor_cannot_open),
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(Res.string.editor_cannot_open),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Companion.Center
            )
        }
    }
}