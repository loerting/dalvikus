package me.lkl.dalvikus.ui.editor.suggestions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.launch
import me.lkl.dalvikus.ui.editor.EditorViewModel
import me.lkl.dalvikus.ui.editor.LayoutSnapshot
import me.lkl.dalvikus.util.getTextWidth
import java.awt.datatransfer.StringSelection

@Composable
fun ErrorPopup(
    lastLayoutSnapshot: LayoutSnapshot?,
    annotation: AnnotatedString.Range<String>,
    viewModel: EditorViewModel,
    textStyle: TextStyle
) {
    val density = LocalDensity.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    val layoutResult = lastLayoutSnapshot?.layout ?: return
    val start = annotation.start
    val boxStart = layoutResult.getBoundingBox(start)
    val boxEnd = layoutResult.getBoundingBox(annotation.end.coerceAtMost(viewModel.highlightedText.length - 1))

    Popup(
        offset = with(density) {
            IntOffset(
                x = ((boxStart.left + boxEnd.left) / 2).toInt() - getTextWidth(annotation.item, textStyle) / 2 - 12.dp.toPx().toInt(),
                y = (boxStart.top - 48.dp.toPx()).toInt()
            )
        },
        properties = PopupProperties(
            clippingEnabled = true,
            focusable = false
        )
    ) {
        Surface(
            modifier = Modifier.Companion
                .wrapContentSize()
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(4.dp)),
            shadowElevation = 4.dp,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = annotation.item,
                    style = textStyle.copy(color = MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(8.dp)
                )
                IconButton(onClick = {
                    scope.launch {
                        clipboard.setClipEntry(ClipEntry(StringSelection(annotation.item)))
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy to clipboard",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}