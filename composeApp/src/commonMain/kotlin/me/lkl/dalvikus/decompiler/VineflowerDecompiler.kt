package me.lkl.dalvikus.decompiler

import co.touchlab.kermit.Logger
import me.lkl.dalvikus.dalvikusSettings
import org.benf.cfr.reader.api.CfrDriver
import org.benf.cfr.reader.api.ClassFileSource
import org.benf.cfr.reader.api.OutputSinkFactory
import org.benf.cfr.reader.api.SinkReturns
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair
import org.jetbrains.java.decompiler.api.Decompiler
import org.jetbrains.java.decompiler.main.extern.IContextSource
import org.jetbrains.java.decompiler.main.extern.IResultSaver
import java.io.InputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import java.util.jar.Manifest

class VineflowerDecompiler : JavaDecompiler {
    override suspend fun decompileJava(internalName: String, bytes: ByteArray): String {
        try {
            val verbose = dalvikusSettings["decompiler_verbose"] as Boolean

            var result: String? = null
            val decompiler = Decompiler.builder().inputs(object : IContextSource {
                override fun getName(): String = "VineflowerDecompiler"

                override fun getEntries(): IContextSource.Entries {
                    return IContextSource.Entries(
                        listOf<IContextSource.Entry>(
                            IContextSource.Entry.parse(internalName)
                        ),
                        emptyList(),
                        emptyList()
                    )
                }

                override fun getInputStream(resource: String): InputStream {
                    if (resource != "$internalName.class") {
                        throw IllegalArgumentException("Resource $resource does not match expected $internalName.class")
                    }
                    return bytes.inputStream()
                }

                override fun createOutputSink(saver: IResultSaver?): IContextSource.IOutputSink? {
                    return object : IContextSource.IOutputSink {
                        override fun begin() {
                        }

                        override fun acceptClass(
                            qualifiedName: String?,
                            fileName: String?,
                            content: String?,
                            mapping: IntArray?
                        ) {
                            saver?.saveClassFile(
                                null,
                                qualifiedName,
                                fileName,
                                content,
                                mapping
                            )
                        }

                        override fun acceptDirectory(directory: String?) {
                        }

                        override fun acceptOther(path: String?) {
                        }

                        override fun close() {
                            // No-op
                        }
                    }
                }

            }).output(object : IResultSaver {
                override fun saveFolder(path: String?) = throw UnsupportedOperationException()


                override fun copyFile(source: String?, path: String?, entryName: String?) =
                    throw UnsupportedOperationException()

                override fun saveClassFile(
                    path: String?,
                    qualifiedName: String?,
                    entryName: String?,
                    content: String?,
                    mapping: IntArray?
                ) {
                    if (entryName != "$internalName.java") {
                        throw IllegalArgumentException("Entry name $entryName does not match expected $internalName.java")
                    }
                    result = content
                }

                override fun createArchive(
                    path: String?,
                    archiveName: String?,
                    manifest: Manifest?
                ) = throw UnsupportedOperationException()

                override fun saveDirEntry(
                    path: String?,
                    archiveName: String?,
                    entryName: String?
                ) = throw UnsupportedOperationException()

                override fun copyEntry(
                    source: String?,
                    path: String?,
                    archiveName: String?,
                    entry: String?
                ) = throw UnsupportedOperationException()

                override fun saveClassEntry(
                    path: String?,
                    archiveName: String?,
                    qualifiedName: String?,
                    entryName: String?,
                    content: String?
                ) = throw UnsupportedOperationException()

                override fun closeArchive(path: String?, archiveName: String?) = throw UnsupportedOperationException()
            })
                .option("decompiler-comments", verbose.toString())
                .build()

            decompiler.decompile()

            return result?.takeIf { it.isNotBlank() } ?: "No Vineflower output received"
        } catch (e: Throwable) {
            Logger.e("Vineflower decompilation error", e)
            val sw = StringWriter().also { e.printStackTrace(PrintWriter(it)) }
            return "Decompilation error: ${e.message}\n$sw"
        }
    }
}
