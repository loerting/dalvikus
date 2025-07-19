package me.lkl.dalvikus.io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import java.io.*
import java.util.*
import org.apache.commons.compress.archivers.zip.ZipFile as CommonsZipFile

val archiveExtensions = setOf(
    "zip", "jar", "war", "ear", "apk", "aar", "tar", "gz", "tgz", "bz2", "xz"
)

val zipLikeExtensions = setOf(
    "zip", "jar", "war", "ear", "apk", "aar"
)


class LazyArchiveFile(val file: File) : Closeable {
    private var contents: Map<String, ArchiveEntry>? = null
    private var zipFile: CommonsZipFile? = null

    /**
     * Lazily loads and caches archive entries mapped by their names.
     * Uses CommonsZipFile for zip/apk, stream for others.
     */
    fun getEntriesMap(): Map<String, ArchiveEntry> {
        if (contents != null) return contents!!

        if (!file.exists() || !file.isFile)
            throw FileNotFoundException("Archive file does not exist or is not a file: ${file.path}")

        val extension = file.name.substringAfterLast(".").lowercase(Locale.ROOT)
        if (extension in zipLikeExtensions) {
            // Use CommonsZipFile for random access and better APK support
            zipFile = CommonsZipFile.builder().setFile(file).get()
            val entries = LinkedHashMap<String, ArchiveEntry>()
            val enumeration = zipFile!!.entries
            while (enumeration.hasMoreElements()) {
                val entry = enumeration.nextElement()
                entries[entry.name] = entry
            }
            contents = entries
            return entries
        } else {
            // Use stream-based ArchiveStreamFactory for other archive types
            val entries = LinkedHashMap<String, ArchiveEntry>()
            val input = BufferedInputStream(FileInputStream(file))
            val archiveStream =
                ArchiveStreamFactory().createArchiveInputStream(input) as ArchiveInputStream<ArchiveEntry>
            archiveStream.use { stream ->
                var entry = stream.nextEntry
                while (entry != null) {
                    entries[entry.name] = entry
                    entry = stream.nextEntry
                }
            }
            input.close()
            contents = entries
            return entries
        }
    }

    /**
     * Checks if the archive contains an entry with the specified name.
     */
    fun contains(name: String): Boolean {
        return getEntriesMap().containsKey(name)
    }

    /**
     * Checks if the entry with the specified name is a folder.
     */
    fun isFolder(name: String): Boolean {
        // e.g. APK files can have directories which don't exist as an entry
        val entry = getEntriesMap()[name] ?: return getEntriesMap().keys.any { it.startsWith("$name/") }
        return entry.isDirectory || name.endsWith("/")
    }

    /**
     * Reads entry content by name, using appropriate method based on archive type.
     */
    suspend fun readEntry(name: String): ByteArray {
        val entry = getEntriesMap()[name]
            ?: throw FileNotFoundException("Entry $name not found in archive ${file.name}")
        return readEntry(entry)
    }

    /**
     * Reads entry content by ArchiveEntry.
     */
    suspend fun readEntry(entry: ArchiveEntry): ByteArray = withContext(Dispatchers.IO) {
        val extension = file.name.substringAfterLast(".").lowercase(Locale.ROOT)
        if (extension in zipLikeExtensions && zipFile != null) {
            // Use CommonsZipFile for reading zip/apk entry content
            zipFile!!.getInputStream(entry as ZipArchiveEntry).use { inputStream ->
                return@withContext inputStream.readBytes()
            }
        } else {
            // Stream-based reading for other archive types: reopen and scan to entry
            val input = BufferedInputStream(FileInputStream(file))
            val archiveStream = ArchiveStreamFactory()
                .createArchiveInputStream(input) as ArchiveInputStream<ArchiveEntry>

            archiveStream.use { stream ->
                var current = stream.nextEntry
                while (current != null) {
                    if (current.name == entry.name) {
                        val buffer = ByteArrayOutputStream()
                        val data = ByteArray(DEFAULT_BUFFER_SIZE)
                        var len: Int
                        while (stream.read(data).also { len = it } != -1) {
                            buffer.write(data, 0, len)
                        }
                        return@withContext buffer.toByteArray()
                    }
                    current = stream.nextEntry
                }
            }
            input.close()
            throw FileNotFoundException("Entry ${entry.name} not found in archive ${file.name}")
        }
    }

    /**
     * Clears cached data and closes any open ZipFile.
     */
    override fun close() {
        contents = null
        zipFile?.close()
        zipFile = null
    }
}
