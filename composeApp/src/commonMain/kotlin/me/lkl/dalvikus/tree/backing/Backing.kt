package me.lkl.dalvikus.tree.backing

import me.lkl.dalvikus.tree.archive.ZipNode
import java.io.File
import java.io.InputStream

interface Backing {
    suspend fun read(): ByteArray
    suspend fun write(data: ByteArray)
    suspend fun getFileOrCreateTemp(suffix: String): File
    fun inputStream(): InputStream
    fun size(): Long
}

class FileBacking(val file: File) : Backing {
    override suspend fun read(): ByteArray = file.readBytes()
    override suspend fun write(data: ByteArray) = file.writeBytes(data)
    override suspend fun getFileOrCreateTemp(suffix: String): File = file
    override fun inputStream(): InputStream = file.inputStream()
    override fun size(): Long = file.length()
}

class ZipBacking(
    private val entryPath: String,
    private val zipNode: ZipNode
) : Backing {
    override suspend fun read(): ByteArray = zipNode.readEntry(entryPath)
    override suspend fun write(data: ByteArray) = zipNode.updateEntry(entryPath, data)
    override suspend fun getFileOrCreateTemp(suffix: String): File {
        val tempFile = File.createTempFile("dalvikus_temp_", suffix)
        tempFile.writeBytes(read())
        return tempFile
    }

    override fun inputStream(): InputStream = zipNode.readEntry(entryPath).inputStream()
    override fun size(): Long = zipNode.readEntry(entryPath).size.toLong()
}