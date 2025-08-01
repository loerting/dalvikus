package me.lkl.dalvikus.tree.filesystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.Node
import java.io.File

class FileSystemFolderNode(
    override val name: String,
    private val file: File,
    override val parent: ContainerNode?
) : ContainerNode() {

    override val icon: ImageVector
        get() = Icons.Filled.Folder // Use appropriate icon for folder
    override val changesWithChildren: Boolean = false

    override suspend fun loadChildrenInternal(): List<Node> {
        return file.listFiles()?.map {
            if (it.isDirectory) {
                FileSystemFolderNode(it.name, it, this)
            } else {
                FileSystemFileNode(it.name, it, this)
            }
        } ?: emptyList()
    }

    override suspend fun rebuild() {
        // Folders usually don't need rebuilding — NOP
    }
}
