package me.lkl.dalvikus.tree.backing

import me.lkl.dalvikus.tree.archive.ZipNode
import java.io.File

interface Backing {
    suspend fun read(): ByteArray
    suspend fun write(data: ByteArray)
}

class FileBacking(private val file: File) : Backing {
    override suspend fun read(): ByteArray = file.readBytes()
    override suspend fun write(data: ByteArray) = file.writeBytes(data)
}

class ZipBacking(
    private val entryPath: String,
    private val zipNode: ZipNode
) : Backing {
    override suspend fun read(): ByteArray = zipNode.readEntry(entryPath)
    override suspend fun write(data: ByteArray) = zipNode.updateEntry(entryPath, data)
}