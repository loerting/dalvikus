package me.lkl.dalvikus.tree.dex

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderSpecial
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.Node
import me.lkl.dalvikus.tree.buildChildNodes

open class DexEntryPackageNode(
    override val name: String,
    private val packagePath: String, // e.g., com/example/
    private val root: DexFileNode,
    override val parent: ContainerNode?
) : ContainerNode() {

    override val icon = Icons.Default.FolderSpecial
    override val changesWithChildren = false

    override suspend fun loadChildrenInternal(): List<Node> {
        return buildChildNodes(
            entries = root.entries,
            prefix = packagePath,
            onFolder = { name, path ->
                DexEntryPackageNode(name, path, root, this)
            },
            onFile = { name, path, classDef ->
                DexEntryClassNode(name, path, root, this)
            }
        )
    }


    override suspend fun rebuild() {
        // No-op; DexFileNode handles writing
    }
}
