package me.lkl.dalvikus.tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

interface TabElement {
    val tabId: String

    @Composable
    fun tabName(): String

    val tabIcon: ImageVector
}