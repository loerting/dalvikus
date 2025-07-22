package me.lkl.dalvikus.tree

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.io.IOChannel
import me.lkl.dalvikus.tabs.CodeTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.ui.tree.IconForFileExtension
import java.io.File

class FileTreeNode(
    val file: File, override val parent: TreeElement?
) : TreeElement {

    override val name: String
        get() = file.name.ifEmpty { file.path }

    override val icon: ImageVector
        get() = when {
            file.isDirectory -> Icons.Filled.Folder
            else -> IconForFileExtension(name)
        }

    override val isContainer: Boolean
        get() = file.isDirectory

    override suspend fun getChildren(): List<TreeElement> {
        return when {
            file.isDirectory -> {
                file.listFiles()
                    ?.filterNotNull()
                    ?.map { FileTreeNode(it, this) }
                    ?.sortedWith(compareBy({ !it.isContainer }, { it.name.lowercase() }))
                    ?: emptyList()
            }

            else -> throw IllegalStateException("FileTreeNode can only have children if it is a directory: $name")
        }
    }

    override val isClickable: Boolean
        get() = isContainer || file.extension.lowercase() in plaintextFileExtensions

    override fun createTab(): TabElement {
        if (!isClickable) throw IllegalStateException("Cannot create a tab for a non-clickable FileTreeNode: $name")

        return object : CodeTab(
            tabId = file.path,
            tabIcon = icon,
            tabName = name,
            tabSource = this
        ) {
            override fun makeIOChannel(): IOChannel<String> {
                return IOChannel.fromFile(file).also {
                    it.setName(name)
                }
            }
        }
    }
}
