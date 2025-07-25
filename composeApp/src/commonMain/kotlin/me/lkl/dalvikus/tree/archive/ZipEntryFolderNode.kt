package me.lkl.dalvikus.tree.archive

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.Node
import me.lkl.dalvikus.tree.backing.ZipBacking
import me.lkl.dalvikus.tree.buildChildNodes
import me.lkl.dalvikus.tree.dex.DexFileNode

class ZipEntryFolderNode(
    override val name: String,
    private val folderPath: String, // ends with "/"
    private val zipRoot: ZipNode,
    override val parent: ContainerNode?
) : ContainerNode() {

    override val icon = Icons.Filled.Folder
    override val changesWithChildren = false
    override val editableContent = false

    override suspend fun loadChildrenInternal(): List<Node> {
        return buildChildNodes(
            entries = zipRoot.entries,
            prefix = folderPath,
            onFolder = { name, path ->
                ZipEntryFolderNode(name, path, zipRoot, this)
            },
            onFile = { name, path, bytes ->
                when {
                    name.endsWith(".dex") -> DexFileNode(name, ZipBacking(path, zipRoot), this)
                    else -> ZipEntryFileNode(name, path, zipRoot, this)
                }
            }
        )
    }


    override suspend fun rebuild() {
        // folders don’t rebuild directly - handled by ZipNode
    }
}
