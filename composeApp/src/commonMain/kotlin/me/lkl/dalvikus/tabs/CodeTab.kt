package me.lkl.dalvikus.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.io.IOChannel

abstract class CodeTab(
    private val tabName: String,
    override val tabId: String,
    override val tabIcon: ImageVector
) : TabElement {
    override val hasUnsavedChanges: MutableState<Boolean> = mutableStateOf(false)

    @Composable
    override fun tabName(): String = tabName

    abstract fun makeIOChannel(): IOChannel<String>
}