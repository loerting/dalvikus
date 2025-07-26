package me.lkl.dalvikus.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.tabs.contentprovider.ContentProvider

interface TabElement {
    val tabId: String

    @Composable
    fun tabName(): String

    val tabIcon: ImageVector

    var hasUnsavedChanges: MutableState<Boolean>

    val contentProvider: ContentProvider
}