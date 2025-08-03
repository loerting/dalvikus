package me.lkl.dalvikus.ui.editor.suggestions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import me.lkl.dalvikus.ui.editor.LayoutSnapshot

@Composable
fun EditorAnnotationPopup(
    lastLayoutSnapshot: LayoutSnapshot?,
    annotation: AnnotatedString.Range<String>,
    content: @Composable () -> Unit,
    contentWidthEstimate: Int
) {
    val density = LocalDensity.current
    val layoutResult = lastLayoutSnapshot?.layout ?: return

    val layoutTextLength = layoutResult.layoutInput.text.length
    val safeStart = annotation.start.coerceIn(0, layoutTextLength - 1)
    val safeEnd = annotation.end.coerceIn(0, layoutTextLength - 1)

    val boxStart = layoutResult.getBoundingBox(safeStart)
    val boxEnd = layoutResult.getBoundingBox(safeEnd)

    Popup(
        offset = with(density) {
            IntOffset(
                x = ((boxStart.left + boxEnd.left) / 2).toInt() - contentWidthEstimate / 2 - 40.dp.toPx().toInt(),
                y = (boxStart.top - 60.dp.toPx()).toInt()
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

@Composable
fun ModernPopupContainer(
    color: Color,
    icon: ImageVector? = null,
    text: String,
    textStyle: TextStyle,
    action: String? = null,
    actionIcon: ImageVector? = null,
    actionOnClick: (() -> Unit)? = null,
) {
    Surface(
        tonalElevation = 6.dp,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 8.dp,
        modifier = Modifier
            .wrapContentSize()
            .background(color.copy(alpha = 0.025f), shape = RoundedCornerShape(12.dp))
            .border(1.dp, color, RoundedCornerShape(12.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                )
            }

            SelectionContainer {
                Text(
                    text = text,
                    style = textStyle.copy(color = color),
                    modifier = Modifier
                        .padding(8.dp)
                )
            }

            if (action != null && actionOnClick != null && actionIcon != null) {
                val buttonShape = RoundedCornerShape(16.dp)
                val buttonColors = ButtonDefaults.textButtonColors(
                    contentColor = color.copy(alpha = 0.8f)
                )

                TextButton(
                    onClick = actionOnClick,
                    shape = buttonShape,
                    colors = buttonColors,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(action, softWrap = false)
                }
            }
        }
    }
}
