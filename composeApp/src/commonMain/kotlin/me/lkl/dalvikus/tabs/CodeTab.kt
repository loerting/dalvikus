package me.lkl.dalvikus.tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.io.IOChannel

abstract class CodeTab(
    private val tabName: String,
    override val tabId: String,
    override val tabIcon: ImageVector
) : TabElement {
    @Composable
    override fun tabName(): String = tabName

    abstract fun makeIOChannel(): IOChannel<String>
}