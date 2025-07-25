package me.lkl.dalvikus.tree.root

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.Node
import me.lkl.dalvikus.tree.sortedTree

class HiddenRoot(
    private val _backingChildren: MutableList<Node> = mutableStateListOf()
) : ContainerNode() {

    constructor(vararg children: Node) : this(mutableStateListOf(*children))

    init {
        _childrenFlow.value = _backingChildren.sortedTree()
    }

    override val parent: ContainerNode? = null
    override val name: String = "root"
    override val icon: ImageVector = Icons.Outlined.FolderSpecial

    override val editableContent: Boolean = false
    override val changesWithChildren: Boolean = false
    override val isRoot: Boolean = true

    override suspend fun loadChildrenInternal(): List<Node> = _backingChildren

    override suspend fun rebuild() {
        // No-op; HiddenRoot does not handle writing
    }

    fun addChild(node: Node) {
        _backingChildren.add(node)
        _childrenFlow.value = _backingChildren.sortedTree()
    }

    fun removeChild(node: Node) {
        _backingChildren.remove(node)
        _childrenFlow.value = _backingChildren.sortedTree()
    }

    fun clear() {
        _backingChildren.clear()
    }
}
