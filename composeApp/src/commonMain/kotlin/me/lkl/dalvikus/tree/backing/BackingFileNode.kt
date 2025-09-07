package me.lkl.dalvikus.tree.backing

import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.tabs.CodeTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.FileNode
import me.lkl.dalvikus.theme.getFileExtensionMeta
import me.lkl.dalvikus.tree.Metadata
import me.lkl.dalvikus.util.guessIfEditableTextually

open class BackingFileNode(
    override val name: String,
    private val backing: Backing,
    override val parent: ContainerNode?
) : FileNode() {

    override val icon: ImageVector
        get() = getFileExtensionMeta(name).icon

    override suspend fun getContent(): ByteArray {
        return backing.read()
    }

    override suspend fun writeContent(newContent: ByteArray) {
        backing.write(newContent)
        parent?.notifyChanged()
    }

    override suspend fun createTab(): TabElement {
        return CodeTab(
            tabName = name,
            tabId = name + "@" + backing.hashCode(),
            tabIcon = icon,
            contentProvider = this
        )
    }

    override fun getSizeEstimate(): Long {
        return backing.size()
    }

    override fun isDisplayable(): Boolean = isEditable()
    override fun isEditable(): Boolean {
        // For backing-based files, we'll assume they're editable if they pass the text check
        // This requires reading the content, which is not ideal, but necessary for the check
        return try {
            guessIfEditableTextually(backing.inputStream())
        } catch (e: Exception) {
            false
        }
    }

    override fun getMetadata(): Set<Pair<Metadata, Any>> {
        if (backing is FileBacking) {
            return setOf(
                Metadata.FILE_SIZE to backing.size(),
                Metadata.LAST_EDITED to backing.file.lastModified(),
            )
        } else if (backing is ZipBacking) {
            return setOf(
                Metadata.FILE_SIZE to backing.size()
            )
        }
        return emptySet()
    }
}