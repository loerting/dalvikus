package me.lkl.dalvikus.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties


data class AssistSuggestion(
    val text: String,
    val description: String? = null,
    val icon: ImageVector,
)
@Composable
fun AssistPopup(
    viewModel: EditorViewModel,
    lastLayoutResult: TextLayoutResult?,
    textStyle: TextStyle
) {
    if (lastLayoutResult == null || !viewModel.isLoaded) return

    var popupDismissed by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.internalContent) {
        popupDismissed = false
    }

    if(popupDismissed) return

    val density = LocalDensity.current
    val cursorRect = lastLayoutResult.getCursorRect(viewModel.internalContent.selection.start)
    if (cursorRect == Rect.Zero) return

    val suggestions = viewModel.getSuggestions()
    if (suggestions.isEmpty()) return

    // State to track selected index
    val selectedIndex = remember { mutableStateOf(0) }

    // Handle key events
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

    Popup(
        offset = with(density) {
            val iconWidth = 22.dp.roundToPx()
            IntOffset(
                cursorRect.left.toInt() + 1 - iconWidth,
                cursorRect.bottom.toInt() + 1
            )
        },
        properties = PopupProperties(
            focusable = true,
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
                            .padding(3.dp)
                    ) {
                        Icon(
                            suggestion.icon, contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = suggestion.text + (suggestion.description?.let { " - $it" } ?: ""),
                            style = textStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}