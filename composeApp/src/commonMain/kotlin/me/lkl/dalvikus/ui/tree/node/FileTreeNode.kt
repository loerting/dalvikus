package me.lkl.dalvikus.ui.tree.node

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.ui.tree.IconForFileExtension
import me.lkl.dalvikus.ui.tree.TreeElement
import java.io.File

class FileTreeNode(
    private val file: File
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
                    ?.map(toTreeElement())
                    ?.sortedWith(compareBy({ !it.isContainer }, { it.name.lowercase() }))
                    ?: emptyList()
            }
            else -> throw IllegalStateException("FileTreeNode can only have children if it is a directory: $name")
        }
    }

    private fun toTreeElement(): (File) -> TreeElement = { file ->
        val extension = file.extension.lowercase()
        val isZipLike = file.isFile && extension in setOf("zip", "jar", "apk", "war", "aar")

        if (isZipLike) {
            ZipFileTreeNode(file)
        } else {
            FileTreeNode(file)
        }
    }
}
