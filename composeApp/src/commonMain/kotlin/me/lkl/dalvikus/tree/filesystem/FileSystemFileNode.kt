package me.lkl.dalvikus.tree.filesystem

import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.tabs.CodeTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.FileNode
import me.lkl.dalvikus.theme.getFileExtensionMeta
import me.lkl.dalvikus.tree.Metadata
import me.lkl.dalvikus.util.guessIfEditableTextually
import java.io.File

class FileSystemFileNode(
    override val name: String,
    val file: File,
    override val parent: ContainerNode?
) : FileNode() {

    override val icon: ImageVector = getFileExtensionMeta(name).icon

    override suspend fun getContent(): ByteArray {
        return file.readBytes()
    }

    override suspend fun writeContent(newContent: ByteArray) {
        file.writeBytes(newContent)
    }

    override suspend fun createTab(): TabElement {
        return CodeTab(
            tabName = name,
            tabId = file.absolutePath,
            tabIcon = icon,
            contentProvider = this
        )
    }

    override fun getSizeEstimate(): Long {
        return file.length()
    }

    override fun isDisplayable(): Boolean = isEditable()
    override fun isEditable(): Boolean = guessIfEditableTextually(file.inputStream())

    override fun getMetadata(): Set<Pair<Metadata, Any>> {
        return setOf(
            Metadata.FILE_SIZE to file.length(),
            Metadata.LAST_EDITED to file.lastModified(),
        )
    }
}
