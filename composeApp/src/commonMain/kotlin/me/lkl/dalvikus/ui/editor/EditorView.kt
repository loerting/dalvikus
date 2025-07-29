package me.lkl.dalvikus.ui.editor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAs
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.fab_save_and_assemble
import kotlinx.coroutines.delay
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.theme.JetBrainsMono
import me.lkl.dalvikus.ui.editor.highlight.defaultCodeHighlightColors
import me.lkl.dalvikus.util.handleFocusedCtrlShortcuts
import me.lkl.dalvikus.settings.shortcutSave
import me.lkl.dalvikus.theme.LocalThemeIsDark
import me.lkl.dalvikus.ui.editor.suggestions.AssistPopup
import me.lkl.dalvikus.ui.editor.suggestions.ErrorPopup
import org.jetbrains.compose.resources.stringResource

data class LayoutSnapshot(val layout: TextLayoutResult, val textFieldValue: TextFieldValue)

const val maxEditorFileSize = 128 * 1024 // 128 KiB

@Composable
fun EditorScreen(editable: TabElement) {
    val isDarkState: MutableState<Boolean> = LocalThemeIsDark.current

    val viewModel = remember(editable) { EditorViewModel(editable) }
    viewModel.highlightColors = defaultCodeHighlightColors(isDarkState.value)

    LaunchedEffect(isDarkState.value) {
        viewModel.refreshHighlight()
    }

    val coroutine = rememberCoroutineScope()

    var firstLoad by remember(editable) { mutableStateOf(true) }

    LaunchedEffect(editable) {
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
        fontFamily = JetBrainsMono(),
        fontSize = viewModel.fontSize,
        lineHeight = viewModel.fontSize * 1.5f,
        color = MaterialTheme.colorScheme.onSurface
    )

    val vertState = rememberScrollState()
    val horState = rememberScrollState()

    //Box(modifier = Modifier.fillMaxSize().then(viewModel.popupKeyEvents)) {

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            if(viewModel.hasUnsavedChanges())
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(bottom = 8.dp, end = 8.dp),
                onClick = { viewModel.saveCode(coroutine) },
                icon = {
                    Icon(Icons.Default.SaveAs, contentDescription = stringResource(Res.string.fab_save_and_assemble))
                },
                text = {
                    Text(stringResource(Res.string.fab_save_and_assemble))
                }
            )
        },
        modifier = Modifier.fillMaxSize().then(viewModel.popupKeyEvents)
    ) {
        Row {
            LineNumberColumn(
                lastLayoutSnapshot?.layout,
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
                        value = viewModel.internalContent,
                        readOnly = !viewModel.isEditable(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Ascii
                        ),
                        onValueChange = { newText ->
                            viewModel.changeContent(newText, coroutine)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .handleFocusedCtrlShortcuts(enabled = viewModel.isEditable(),
                                mapOf(shortcutSave to { viewModel.saveCode(coroutine) })),

                        textStyle = textStyle.copy(
                            color = Color.Black.copy(alpha = 0.0f)
                        ),
                        onTextLayout = {
                            lastLayoutSnapshot = LayoutSnapshot(
                                layout = it,
                                textFieldValue = viewModel.internalContent
                            )
                        },
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { inner ->
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(1.dp)
                            ) {
                                Text(
                                    text = viewModel.highlightedText,
                                    maxLines = Int.MAX_VALUE,
                                    softWrap = false,
                                    overflow = TextOverflow.Clip,
                                    modifier = Modifier.matchParentSize(),
                                    style = textStyle
                                )
                                inner()
                            }
                        }
                    )
                    AssistPopup(
                        assistPopupState = viewModel.assistPopupState,
                        viewModel = viewModel,
                        lastLayoutSnapshot = lastLayoutSnapshot,
                        textStyle = textStyle,
                        highlightColors = viewModel.highlightColors
                    )
                    viewModel.highlightedText.getStringAnnotations(
                        tag = "error",
                        start = viewModel.internalContent.selection.start,
                        end = viewModel.internalContent.selection.end
                    ).forEach { annotation ->
                        ErrorPopup(lastLayoutSnapshot, annotation, viewModel, textStyle)
                    }
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

