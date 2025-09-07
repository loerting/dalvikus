package me.lkl.dalvikus.tree.archive

import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.backing.BackingFileNode
import me.lkl.dalvikus.tree.backing.ZipBacking

open class ZipEntryFileNode(
    name: String,
    path: String,
    zipRoot: ZipNode,
    parent: ContainerNode?
) : BackingFileNode(
    name,
    ZipBacking(path, zipRoot),
    parent
)