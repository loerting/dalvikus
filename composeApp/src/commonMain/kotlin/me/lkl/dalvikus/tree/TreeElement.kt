package me.lkl.dalvikus.tree

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.ui.tree.ColorForFileExtension

interface TreeElement {
    val parent: TreeElement?
    val name: String
    val icon: ImageVector

    @Composable
    fun getColor(): Color = ColorForFileExtension(name)

    val isContainer: Boolean
    suspend fun getChildren(): List<TreeElement>

    val isClickable: Boolean

    fun createTab(): TabElement {
        throw NotImplementedError("createTab() is not implemented for this TreeElement")
    }

    fun onCollapse() {
        // Default implementation does nothing
    }

    fun getSourceDescription(): String {
        if(parent == null) {
            return name
        }
        return "${parent!!.getSourceDescription()}/$name"
    }
}

val plaintextFileExtensions = setOf(
    "txt", "md", "html", "xml", "json", "yml", "yaml", "properties",
)

val dexFileExtensions = setOf(
    "dex", "odex"
)