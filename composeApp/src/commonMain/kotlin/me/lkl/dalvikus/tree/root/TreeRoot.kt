package me.lkl.dalvikus.tree.root

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.tree.TreeElement

class TreeRoot(
    val children: MutableList<TreeElement> = mutableStateListOf()
) : TreeElement {

    constructor(vararg children: TreeElement) : this(mutableStateListOf<TreeElement>(*children))

    override val parent: TreeElement? = null
    override val name: String = "root"

    override val icon: ImageVector = Icons.Outlined.FolderSpecial
    override val isContainer: Boolean = true

    override suspend fun getChildren(): List<TreeElement> {
        return children
    }

    override val isClickable: Boolean = true
    override val isRoot: Boolean = true
}