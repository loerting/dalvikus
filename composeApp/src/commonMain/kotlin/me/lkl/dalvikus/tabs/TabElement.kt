package me.lkl.dalvikus.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.tree.FileNode
import me.lkl.dalvikus.tree.Node

interface TabElement {
    val treeNode: FileNode?

    val tabId: String

    @Composable
    fun tabName(): String

    val tabIcon: ImageVector

    val hasUnsavedChanges: MutableState<Boolean>
}