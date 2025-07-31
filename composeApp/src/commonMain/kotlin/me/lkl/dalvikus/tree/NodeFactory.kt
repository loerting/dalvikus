package me.lkl.dalvikus.tree

import me.lkl.dalvikus.tree.archive.ApkNode
import me.lkl.dalvikus.tree.archive.ZipNode
import me.lkl.dalvikus.tree.backing.FileBacking
import me.lkl.dalvikus.tree.dex.DexFileNode
import me.lkl.dalvikus.tree.filesystem.FileSystemFileNode
import java.io.File

object NodeFactory {
    fun createNode(file: File, parent: ContainerNode): Node {
        return when (file.extension.lowercase()) {
            "apk"-> ApkNode(file.name, file, parent)
            "aab", "jar", "zip", "xapk", "apks" -> ZipNode(file.name, file, parent)
            "dex", "odex" -> DexFileNode(file.name, FileBacking(file), parent)
            else -> FileSystemFileNode(file.name, file, parent)
        }
    }
}
