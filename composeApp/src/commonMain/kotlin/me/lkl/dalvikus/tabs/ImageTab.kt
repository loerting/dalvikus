package me.lkl.dalvikus.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.tree.archive.ZipEntryImageNode
import me.lkl.dalvikus.ui.editor.EditorViewModel

class ImageTab(
    val tabName: String,
    override val tabId: String,
    override val contentProvider: ZipEntryImageNode,
) : TabElement {
    override var hasUnsavedChanges: MutableState<Boolean> = mutableStateOf(false)

    override var editorViewModel: EditorViewModel? = null
        set(value) {
            throw IllegalStateException("ImageTab does not support editor view model")
        }

    @Composable
    override fun tabName(): String = tabName
    override val tabIcon: ImageVector = Icons.Filled.Image
}