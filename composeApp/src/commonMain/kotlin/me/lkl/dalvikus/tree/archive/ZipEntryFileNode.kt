package me.lkl.dalvikus.tree.archive

import me.lkl.dalvikus.tabs.CodeTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.FileNode
import me.lkl.dalvikus.theme.getFileExtensionMeta
import me.lkl.dalvikus.util.guessIfEditableTextually

class ZipEntryFileNode(
    override val name: String,
    private val fullPath: String,
    private val zipRoot: ZipNode,
    override val parent: ContainerNode?
) : FileNode() {

    override val icon = getFileExtensionMeta(name).icon
    override val editableContent = true

    override suspend fun getContent(): ByteArray {
        return zipRoot.readEntry(fullPath)
    }

    override suspend fun writeContent(newContent: ByteArray) {
        zipRoot.updateEntry(fullPath, newContent)
        parent?.notifyChanged()
    }

    override suspend fun createTab(): TabElement {
        return CodeTab(
            tabName = name,
            tabId = fullPath,
            tabIcon = icon,
            contentProvider = this
        )
    }

    override fun getSizeEstimate(): Long {
        return zipRoot.readEntry(fullPath).size.toLong()
    }

    override fun isDisplayable(): Boolean {
        return isEditable()
    }

    override fun isEditable(): Boolean {
        return name.endsWith(".xml") || name.endsWith(".smali") || name.endsWith(".txt") || name.endsWith(".java") || guessIfEditableTextually(zipRoot.readEntry(fullPath).inputStream())
    }
}
