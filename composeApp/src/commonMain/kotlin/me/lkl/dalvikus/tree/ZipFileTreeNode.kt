package me.lkl.dalvikus.tree

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.ui.tree.IconForFileExtension
import java.io.File
import java.util.zip.ZipFile

class ZipFileTreeNode(
    private val zipFile: File,
    private val path: String = ""
) : TreeElement {
    private val zip: ZipFile by lazy { ZipFile(zipFile) }

    override val name: String
        get() = path.substringAfterLast('/').ifEmpty { zipFile.name }

    override val icon: ImageVector
        get() = if (!isZipRoot() && isContainer) Icons.Filled.Folder else IconForFileExtension(name)

    override val isContainer: Boolean
        get() = isZipRoot() || zip.entries().asSequence().any { it.name != path && it.name.startsWith("$path/") }

    override suspend fun getChildren(): List<TreeElement> {
        val prefix = if (isZipRoot()) "" else "$path/"
        val seen = mutableSetOf<String>()
        val children = mutableListOf<TreeElement>()

        for (entry in zip.entries()) {
            if (!entry.name.startsWith(prefix) || entry.name == prefix) continue

            val relativePath = entry.name.removePrefix(prefix)
            val topLevel = relativePath.substringBefore('/', relativePath)

            if (seen.add(topLevel)) {
                val childPath = if (isZipRoot()) topLevel else "$path/$topLevel"
                children.add(ZipFileTreeNode(zipFile, childPath))
            }
        }
        return children.sortedWith(compareBy({ !it.isContainer }, { it.name.lowercase() }))
    }

    private fun isZipRoot(): Boolean = path.isEmpty()

    override val isClickable: Boolean
        get() = isContainer || name.substringAfterLast('.').lowercase() in plaintextFileExtensions
}
