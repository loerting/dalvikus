package me.lkl.dalvikus.ui.editor.suggestions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import me.lkl.dalvikus.theme.getSuggestionTypeIcon
import me.lkl.dalvikus.ui.editor.EditorViewModel
import me.lkl.dalvikus.ui.editor.LayoutSnapshot
import me.lkl.dalvikus.ui.editor.highlight.CodeHighlightColors
import me.lkl.dalvikus.util.defaultHazeStyle
import me.lkl.dalvikus.util.getTextWidth

data class AssistPopupState(
    val selectedIndex: Int = 0,
    val popupDismissed: Boolean = true,
    val currentSuggestions: List<AssistSuggestion> = emptyList(),
    val enterRequest: Boolean = false,
)

const val MaximumAssistSuggestions = 5

@Composable
fun AssistPopup(
    assistPopupState: MutableState<AssistPopupState>,
    viewModel: EditorViewModel,
    lastLayoutSnapshot: LayoutSnapshot?,
    textStyle: TextStyle,
    hazeState: HazeState,
    highlightColors: CodeHighlightColors
) {
    if (lastLayoutSnapshot == null || !viewModel.isLoaded) return
    if (viewModel.getFileType() != "smali") return

    if (assistPopupState.value.popupDismissed) return

    val cursorIndex = lastLayoutSnapshot.textFieldValue.selection.start

    if (cursorIndex == 0) return
    val cursorRect = lastLayoutSnapshot.layout.getCursorRect(cursorIndex)
    if (cursorRect == Rect.Zero) return
    // add a \n as a little hack to ensure the token text on the last line is not empty
    val codeSuggester = CodeSuggester(cursorIndex, lastLayoutSnapshot.textFieldValue.text + "\n", highlightColors)
    val suggestions = codeSuggester.suggestNext().take(MaximumAssistSuggestions)

    val coroutineScope = rememberCoroutineScope()

    if(assistPopupState.value.enterRequest) {
        if (assistPopupState.value.selectedIndex < suggestions.size) {
            val suggestion = suggestions[assistPopupState.value.selectedIndex]

            viewModel.replaceTextAndMoveCaret(
                codeSuggester.currentToken.startIndex,
                codeSuggester.currentToken.stopIndex + 1,
                suggestion.text,
                coroutineScope
            )
        }

        // reset to default state
        assistPopupState.value = AssistPopupState()
        return
    }

    if (suggestions.isEmpty()) {
        assistPopupState.value = assistPopupState.value.copy(popupDismissed = true)
        return
    }
    val selectedIndex = assistPopupState.value.selectedIndex.coerceIn(0, suggestions.size - 1)
    val cursorOffset = cursorIndex - codeSuggester.currentToken.startIndex
    if (cursorOffset < 0 || cursorOffset > codeSuggester.currentToken.text.length) {
        assistPopupState.value = assistPopupState.value.copy(popupDismissed = true)
        return
    }
    val negativeOffset = getTextWidth(codeSuggester.currentToken.text.substring(0, cursorOffset), textStyle)

    val color = highlightColors.senary
    val density = LocalDensity.current
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
            assistPopupState.value = assistPopupState.value.copy(popupDismissed = true)
        }
    ) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .heightIn(min = 0.dp, max = 200.dp)
                .widthIn(max = 400.dp)
                .hazeEffect(
                    state = hazeState,
                    style = defaultHazeStyle(lerp(color, MaterialTheme.colorScheme.surface, 0.9f))
                )
                .border(1.dp, color.copy(alpha = 0.8f))
        ) {
            LazyColumn(
                modifier = Modifier
            ) {
                itemsIndexed(suggestions) { index, suggestion ->
                    val isSelected = index == selectedIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .padding(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            getSuggestionTypeIcon(suggestion.type), contentDescription = null,
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

