package me.lkl.dalvikus.ui.editor.suggestions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import me.lkl.dalvikus.ui.editor.EditorViewModel
import me.lkl.dalvikus.ui.editor.LayoutSnapshot

@Composable
fun EditorAnnotationPopup(
    lastLayoutSnapshot: LayoutSnapshot?,
    annotation: AnnotatedString.Range<String>,
    viewModel: EditorViewModel,
    content: @Composable () -> Unit,
    contentWidthEstimate: Int
) {
    val density = LocalDensity.current
    val layoutResult = lastLayoutSnapshot?.layout ?: return
    val start = annotation.start
    val boxStart = layoutResult.getBoundingBox(start)
    val boxEnd = layoutResult.getBoundingBox(annotation.end.coerceAtMost(viewModel.highlightedText.length - 1))

    Popup(
        offset = with(density) {
            IntOffset(
                x = ((boxStart.left + boxEnd.left) / 2).toInt() - contentWidthEstimate / 2 - 12.dp.toPx().toInt(),
                y = (boxStart.top - 48.dp.toPx()).toInt()
            )
        },
        properties = PopupProperties(
            clippingEnabled = true,
            focusable = false
        )
    ) {
        content()
    }
}