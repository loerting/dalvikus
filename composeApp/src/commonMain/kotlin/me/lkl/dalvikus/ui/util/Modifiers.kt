package me.lkl.dalvikus.ui.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.layout

fun Modifier.onSaveShortcut(
    enabled: Boolean = true,
    onSave: () -> Unit
): Modifier = this.then(
    if (enabled) Modifier.onPreviewKeyEvent { event ->
        if (
            event.type == KeyEventType.KeyDown &&
            (event.isCtrlPressed || event.isMetaPressed) &&
            event.key == Key.S
        ) {
            onSave()
            true // consume event
        } else {
            false // pass event down
        }
    } else Modifier
)


fun Modifier.withoutWidthConstraints() = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints.copy(maxWidth = Int.MAX_VALUE))
    layout(constraints.maxWidth, placeable.height) {
        placeable.place(0, 0)
    }
}