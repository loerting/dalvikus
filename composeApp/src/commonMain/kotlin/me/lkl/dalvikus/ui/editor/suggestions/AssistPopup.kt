package me.lkl.dalvikus.ui.editor.suggestions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import me.lkl.dalvikus.ui.editor.EditorViewModel
import me.lkl.dalvikus.ui.editor.highlight.CodeHighlightColors

@Composable
fun AssistPopup(
    viewModel: EditorViewModel,
    lastLayoutResult: TextLayoutResult?,
    textStyle: TextStyle,
    highlightColors: CodeHighlightColors
) {
    if (lastLayoutResult == null || !viewModel.isLoaded) return
    if (viewModel.getFileType() != "smali") return

    var popupDismissed by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.internalContent) {
        popupDismissed = false
    }

    if (popupDismissed) return

    val density = LocalDensity.current
    val cursorIndex = viewModel.internalContent.selection.start

    if (cursorIndex == 0) return // Not optimal, but prevents the popup to appear when user didn't click text yet.
    val cursorRect = lastLayoutResult.getCursorRect(cursorIndex)
    if (cursorRect == Rect.Zero) return

    val codeSuggester = CodeSuggester(cursorIndex, viewModel.internalContent.text, highlightColors)
    val suggestions = codeSuggester.suggestNext()
    if (suggestions.isEmpty()) return


    val selectedIndex = remember { mutableStateOf(0) }

    val listKeyModifier = Modifier.onPreviewKeyEvent { event ->
        when (event.type) {
            KeyEventType.KeyDown if event.key == Key.DirectionDown -> {
                selectedIndex.value = (selectedIndex.value + 1) % suggestions.size
                true
            }

            KeyEventType.KeyDown if event.key == Key.DirectionUp -> {
                selectedIndex.value = (selectedIndex.value - 1 + suggestions.size) % suggestions.size
                true
            }

            KeyEventType.KeyDown if event.key == Key.Enter -> {
                viewModel.insertAtCaret(suggestions[selectedIndex.value].text)
                true
            }

            else -> false
        }
    }

    val cursorOffset = cursorIndex - codeSuggester.currentToken.startIndex
    if (cursorOffset < 0 || cursorOffset > codeSuggester.currentToken.text.length) return
    val negativeOffset = getTextWidth(codeSuggester.currentToken.text.substring(0, cursorOffset), textStyle)

    Popup(
        offset = with(density) {
            val iconWidth = 26.dp.roundToPx()
            IntOffset(
                cursorRect.left.toInt() + 1 - iconWidth - negativeOffset,
                cursorRect.bottom.toInt() + 1
            )
        },
        properties = PopupProperties(
            clippingEnabled = false,
            focusable = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = {
            popupDismissed = true
        },
    ) {
        Surface(
            modifier = Modifier
                .size(400.dp, 200.dp)
                .then(listKeyModifier),
            shadowElevation = 2.dp,
            shape = RoundedCornerShape(4.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                itemsIndexed(suggestions) { index, suggestion ->
                    val isSelected = index == selectedIndex.value
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .padding(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            IconForSuggestion(suggestion.type), contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = AnnotatedString(
                                    suggestion.text,
                                    spanStyle = suggestion.spanStyle ?: SpanStyle()
                                ),
                                style = textStyle,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                            )
                            if (suggestion.description != null) {
                                Text(
                                    text = suggestion.description,
                                    style = textStyle.copy(
                                        color = textStyle.color.copy(alpha = 0.6f),
                                        fontSize = textStyle.fontSize * 0.75f
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getTextWidth(text: String, textStyle: TextStyle): Int {
    val textMeasurer = rememberTextMeasurer()

    val result = textMeasurer.measure(
        text = text,
        style = textStyle
    )

    return result.size.width
}

fun IconForSuggestion(type: SuggestionType): ImageVector {
    return when (type) {
        SuggestionType.Instruction -> Icons.Default.Code
        SuggestionType.Directive -> Icons.Default.Directions
        SuggestionType.Register -> Icons.Default.Memory
        SuggestionType.Access -> Icons.Default.Api
        SuggestionType.LabelOrType -> Icons.Default.Class
    }
}