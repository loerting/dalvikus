package me.lkl.dalvikus.tree.archive

import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.Node
import me.lkl.dalvikus.tree.backing.ZipBacking
import me.lkl.dalvikus.tree.buildChildNodes
import me.lkl.dalvikus.tree.dex.DexFileNode
import me.lkl.dalvikus.theme.getFileExtensionMeta
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

open class ZipNode(
    override val name: String,
    open val zipFile: File, // TODO change this to Backing.
    override val parent: ContainerNode?
) : ContainerNode() {

    val entries = mutableMapOf<String, ByteArray>()

    override val icon
        get() = getFileExtensionMeta(name).icon
    override val changesWithChildren = true
    override val editableContent = false

    override suspend fun loadChildrenInternal(): List<Node> {
        entries.clear()
        // TODO when zipFile is changed to backing, read and copy to temp file (or directly take file from backing if possible)
        val zip = ZipFile(zipFile)

        zip.entries().toList().forEach { entry ->
            val name = entry.name
            val bytes = if (!entry.isDirectory) zip.getInputStream(entry).readBytes() else null
            if (bytes != null) entries[name] = bytes
        }

        zip.close()

        return buildChildNodes(
            entries = entries,
            prefix = "",
            onFolder = { name, path ->
                ZipEntryFolderNode(name, path, this, this)
            },
            onFile = { name, path, bytes ->
                when {
                    name.endsWith(".dex") -> DexFileNode(name, ZipBacking(path, this), this)
                    else -> ZipEntryFileNode(name, path, this, this)
                }
            }
        )

    }

    open fun readEntry(path: String): ByteArray {
        return entries[path] ?: error("Entry not found: $path")
    }

    open suspend fun updateEntry(path: String, newContent: ByteArray) {
        entries[path] = newContent
        rebuild()
    }

    override suspend fun rebuild() {
        val tmp = File.createTempFile("rebuild", ".zip")
        ZipOutputStream(tmp.outputStream()).use { zos ->
            for ((path, data) in entries) {
                zos.putNextEntry(ZipEntry(path))
                zos.write(data)
                zos.closeEntry()
            }
        }
        tmp.copyTo(zipFile, overwrite = true)
        tmp.delete()
    }
}
