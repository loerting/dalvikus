package me.lkl.dalvikus.tree

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.lkl.dalvikus.tabs.contentprovider.ContentProvider
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tree.root.HiddenRoot
import me.lkl.dalvikus.ui.tree.ColorForFileExtension
import me.lkl.dalvikus.util.guessIfEditableTextually

sealed interface Node {
    val name: String
    val icon: ImageVector // path or identifier
    val parent: ContainerNode?
    val editableContent: Boolean

    val isRoot: Boolean
        get() = parent == null

    val color: Color?
        get() = ColorForFileExtension(name) // Default color, can be overridden

    suspend fun notifyChanged()
}

abstract class ContainerNode : Node {
    protected val _childrenFlow = MutableStateFlow<List<Node>>(emptyList())
    val childrenFlow: StateFlow<List<Node>> = _childrenFlow.asStateFlow()
    abstract val changesWithChildren: Boolean

    protected abstract suspend fun loadChildrenInternal(): List<Node>

    suspend fun loadChildren() {
        val rawChildren = loadChildrenInternal()
        _childrenFlow.value = rawChildren.sortedTree()
    }

    open suspend fun replaceChild(old: Node, new: Node) {
        val updated = _childrenFlow.value.map { if (it == old) new else it }
        _childrenFlow.value = updated
        onChildChanged(new)
    }

    protected open suspend fun onChildChanged(child: Node) {
        // Default behavior: propagate upward
        if (changesWithChildren) {
            rebuild()
        }
        parent?.notifyChanged()
    }

    override suspend fun notifyChanged() {
        rebuild()
        parent?.notifyChanged()
    }

    protected abstract suspend fun rebuild()
}

abstract class FileNode : Node, ContentProvider() {
    override suspend fun loadContent() {
        _contentFlow.value = getContent()
    }

    override suspend fun updateContent(newContent: ByteArray) {
        if (!editableContent) {
            throw UnsupportedOperationException("This file is not editable")
        }
        _contentFlow.value = newContent
        writeContent(newContent)
        parent?.notifyChanged()
    }

    protected abstract suspend fun getContent(): ByteArray
    protected abstract suspend fun writeContent(newContent: ByteArray)

    override suspend fun notifyChanged() {
        parent?.notifyChanged()
    }

    override fun getFileType(): String {
        return name.substringAfterLast(".").lowercase()
    }

    override fun getSourcePath(): String? {
        return this.getPathHistory()
    }

    abstract suspend fun createTab(): TabElement

    abstract fun getSizeEstimate(): Long

    abstract fun isEditableTextually(): Boolean
}

typealias PathMap<T> = Map<String, T>

fun <T> buildChildNodes(
    entries: PathMap<T>,
    prefix: String,
    onFolder: (folderName: String, fullPath: String) -> Node,
    onFile: (fileName: String, fullPath: String, value: T) -> Node
): List<Node> {
    val folders = mutableSetOf<String>()
    val children = mutableListOf<Node>()

    for ((fullPath, value) in entries) {
        if (!fullPath.startsWith(prefix)) continue

        val relative = fullPath.removePrefix(prefix)
        val parts = relative.split('/')

        when {
            parts.size == 1 -> {
                // Direct file
                children.add(onFile(parts[0], fullPath, value))
            }
            parts.size > 1 -> {
                // Nested folder
                val folderName = parts[0]
                if (folders.add(folderName)) {
                    val folderPath = if (prefix.isEmpty()) "$folderName/" else "$prefix$folderName/"
                    children.add(onFolder(folderName, folderPath))
                }
            }
        }
    }

    return children.sortedBy { it.name }
}


fun List<Node>.sortedTree(): List<Node> {
    return sortedWith(
        compareBy<Node> { it !is ContainerNode } // folders first (false < true)
            .thenBy { it.name.lowercase() }       // then alphabetical (case-insensitive)
    )
}

/**
 * Concats all parent names into a path-like string.
 */
fun Node.getPathHistory(): String {
    val path = mutableListOf<String>()
    var current: Node? = this
    while (current != null && current !is HiddenRoot) {
        path.add(current.name)
        current = current.parent
    }
    return path.reversed().joinToString(separator = "/")
}