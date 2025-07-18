package me.lkl.dalvikus.io

import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveOutputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class IOChannel<T>(
    val read: suspend () -> T,
    val writingSupported: Boolean = true,
    val write: suspend (T) -> Unit,
    val setName: (String) -> Unit = { _ -> } // Default no-op implementation
) {

    companion object {
        fun <T> readOnly(reader: suspend () -> T): IOChannel<T> {
            return IOChannel(
                read = reader,
                writingSupported = false,
                write = {
                    throw UnsupportedOperationException("This IOChannel is read-only.")
                },
                setName = {
                    throw UnsupportedOperationException("This IOChannel is read-only and does not support setting a name.")
                }
            )
        }

        fun fromFile(file: File): IOChannel<String> {
            return IOChannel(
                read = {
                    file.readText()
                },
                writingSupported = true,
                write = { content ->
                    file.writeText(content)
                },
                setName = { newName ->
                    val newFile = file.resolveSibling(newName)
                    if (!file.renameTo(newFile)) {
                        throw IllegalStateException("Failed to rename file from ${file.name} to $newName")
                    }
                }
            )
        }

        fun fromArchive(archive: LazyArchiveFile, path: String): IOChannel<String> {
            val archiveFile = archive.file
            val extension = archiveFile.extension.lowercase()

            return IOChannel(
                read = {
                    val bytes = archive.readEntry(path)
                    bytes.toString(Charsets.UTF_8)
                },
                writingSupported = true,
                write = { content ->
                    val tempFile = File.createTempFile("modified-", ".$extension")
                    val newEntryData = content.toByteArray(Charsets.UTF_8)
                    val entries = archive.getEntriesMap()

                    // Create appropriate output stream
                    BufferedOutputStream(FileOutputStream(tempFile)).use { outStream ->
                        val archiveOut = ArchiveStreamFactory().createArchiveOutputStream(extension, outStream) as ArchiveOutputStream<ArchiveEntry>

                        archiveOut.use { aos ->
                            for ((entryName, entry) in entries) {
                                if (entryName == path) continue // Skip the one we're replacing
                                val data = archive.readEntry(entry)

                                val outEntry = aos.createArchiveEntry(File(entryName), entryName)
                                aos.putArchiveEntry(outEntry)
                                aos.write(data)
                                aos.closeArchiveEntry()
                            }

                            // Add new/modified entry
                            val newEntry = aos.createArchiveEntry(File(path), path)
                            aos.putArchiveEntry(newEntry)
                            aos.write(newEntryData)
                            aos.closeArchiveEntry()

                            aos.finish()
                        }
                    }

                    archive.close()

                    if (!tempFile.renameTo(archiveFile)) {
                        throw IOException("Failed to replace original archive with modified version.")
                    }
                },
                setName = {
                    throw UnsupportedOperationException("Renaming archive entries is not yet supported.")
                }
            )
        }

    }
}