package me.lkl.dalvikus.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import me.lkl.dalvikus.tabs.contentprovider.ContentProvider
import me.lkl.dalvikus.ui.editor.EditorViewModel

interface TabElement {
    val tabId: String

    @Composable
    fun tabName(): String

    val tabIcon: ImageVector

    var hasUnsavedChanges: MutableState<Boolean>

    val contentProvider: ContentProvider

    var editorViewModel: EditorViewModel?
}