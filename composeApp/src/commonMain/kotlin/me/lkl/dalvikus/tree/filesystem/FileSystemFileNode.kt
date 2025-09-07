package me.lkl.dalvikus.tree.filesystem

import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.backing.BackingFileNode
import me.lkl.dalvikus.tree.backing.FileBacking
import java.io.File

class FileSystemFileNode(
    val file: File,
    parent: ContainerNode?
) : BackingFileNode(
    file.name,
    FileBacking(file),
    parent
)