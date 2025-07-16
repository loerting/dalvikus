package me.lkl.dalvikus.tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

abstract class CodeTab(
    val tabName: String,
    override val tabId: String,
    override val tabIcon: ImageVector
) : TabElement {
    @Composable
    override fun tabName(): String = tabName

    abstract suspend fun fileContent(): String
}