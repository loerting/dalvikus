package me.lkl.dalvikus.tree.dex

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import me.lkl.dalvikus.theme.PackageOrange
import me.lkl.dalvikus.tree.TreeElement

class DexPackageTreeNode(
    private val nameSegment: String,
    private val childrenMap: Map<String, List<TreeElement>>, // Can be DexPackageTreeNode or DexClassTreeNode
    private val fullPath: String
) : TreeElement {

    override val name: String
        get() = nameSegment

    override val icon: ImageVector
        get() = Icons.Outlined.FolderSpecial

    override val isContainer: Boolean
        get() = true

    override val isClickable: Boolean
        get() = true

    override suspend fun getChildren(): List<TreeElement> {
        return childrenMap[fullPath] ?: emptyList()
    }

    @Composable
    override fun getColor(): Color = PackageOrange

    override fun onCollapse() {}
}
