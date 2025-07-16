package me.lkl.dalvikus.tree.archive

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.tabs.CodeTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tree.TreeElement
import me.lkl.dalvikus.tree.plaintextFileExtensions
import me.lkl.dalvikus.ui.tree.IconForFileExtension
import java.io.File

class ArchiveTreeNode(
    private val file: File,
    private val path: String = "",
    private var archive: LazyArchiveFile = LazyArchiveFile(file),
) : TreeElement {

    override val name: String
        get() = path.substringAfterLast('/').ifEmpty { file.name }

    override val icon: ImageVector
        get() = if (!isZipRoot() && isContainer) Icons.Filled.Folder else IconForFileExtension(name)

    override val isContainer: Boolean
        get() = isZipRoot() || archive.isFolder(path)

    override suspend fun getChildren(): List<TreeElement> {
        val prefix = if (isZipRoot()) "" else "$path/"
        val seen = mutableSetOf<String>()
        val children = mutableListOf<TreeElement>()

        val entriesMap = archive.getEntriesMap()

        for (entryName in entriesMap.keys) {
            if (!entryName.startsWith(prefix) || entryName == prefix) continue

            val relativePath = entryName.removePrefix(prefix)
            val topLevel = relativePath.substringBefore('/', relativePath)

            if (seen.add(topLevel)) {
                val childPath = if (isZipRoot()) topLevel else "$path/$topLevel"
                children.add(ArchiveTreeNode(file, childPath, archive))
                // TODO support DEX inside archives
            }
        }

        return children.sortedWith(compareBy({ !it.isContainer }, { it.name.lowercase() }))
    }

    private fun isZipRoot(): Boolean = path.isEmpty()

    override val isClickable: Boolean
        get() = isContainer || name.substringAfterLast('.').lowercase() in plaintextFileExtensions

    override fun createTab(): TabElement {
        if (!isClickable) throw IllegalStateException("Cannot create a tab for a non-clickable FileTreeNode: $name")
        return object : CodeTab(
            tabId = "${file.path}#$path",
            tabIcon = icon,
            tabName = name
        ) {
            override suspend fun fileContent(): String {
                return archive.readEntry(path).decodeToString()
            }
        }
    }

    override fun onCollapse() {
        if (isZipRoot()) {
            archive.close()
        }
    }
}
