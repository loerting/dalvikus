package me.lkl.dalvikus.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.layout

fun Modifier.handleFocusedCtrlShortcuts(
    enabled: Boolean = true,
    keyActionMap: Map<Key, () -> Unit>
): Modifier = this.then(
    if (enabled) Modifier.onPreviewKeyEvent(ctrlShortcuts(keyActionMap)) else Modifier
)

fun ctrlShortcuts(
    keyActionMap: Map<Key, () -> Unit>
): (KeyEvent) -> Boolean = { event ->
    if (
        event.type == KeyEventType.KeyDown &&
        (event.isCtrlPressed || event.isMetaPressed)
    ) {
        keyActionMap[event.key]?.let {
            it.invoke()
            true
        }
    }
    false
}


fun Modifier.withoutWidthConstraints() = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints.copy(maxWidth = Int.MAX_VALUE))
    layout(constraints.maxWidth, placeable.height) {
        placeable.place(0, 0)
    }
}