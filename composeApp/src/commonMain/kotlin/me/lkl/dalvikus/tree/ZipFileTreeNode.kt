package me.lkl.dalvikus.tree

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.tabs.CodeTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.ui.tree.IconForFileExtension
import java.io.File
import java.util.zip.ZipFile

class ZipFileTreeNode(
    private val file: File,
    private val path: String = "",
    private var zip: ZipFile? = null,
) : TreeElement {

    fun openZipIfNull() {
        if (zip == null) {
            if (!isZipRoot()) throw IllegalStateException("ZipFile has to be opened for children: $name")
            zip = ZipFile(file)
        }
    }

    override val name: String
        get() = path.substringAfterLast('/').ifEmpty { file.name }

    override val icon: ImageVector
        get() = if (!isZipRoot() && isContainer) Icons.Filled.Folder else IconForFileExtension(name)

    override val isContainer: Boolean
        get() = isZipRoot() || zip?.entries()?.asSequence()
            ?.any { it.name != path && it.name.startsWith("$path/") } == true

    override suspend fun getChildren(): List<TreeElement> {
        openZipIfNull()
        val prefix = if (isZipRoot()) "" else "$path/"
        val seen = mutableSetOf<String>()
        val children = mutableListOf<TreeElement>()

        for (entry in zip!!.entries()) {
            if (!entry.name.startsWith(prefix) || entry.name == prefix) continue

            val relativePath = entry.name.removePrefix(prefix)
            val topLevel = relativePath.substringBefore('/', relativePath)

            if (seen.add(topLevel)) {
                val childPath = if (isZipRoot()) topLevel else "$path/$topLevel"
                children.add(ZipFileTreeNode(file, childPath, zip!!))
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
                openZipIfNull()
                return zip!!.getInputStream(zip!!.getEntry(path)).bufferedReader().use { it.readText() }
            }
        }
    }

    override fun onCollapse() {
        if (isZipRoot()) {
            // it the outermost zip node is collapsed we can close the ZipFile and set it to null
            // when it is expanded again, it will be reopened
            zip?.close()
            zip = null
        }
    }
}
