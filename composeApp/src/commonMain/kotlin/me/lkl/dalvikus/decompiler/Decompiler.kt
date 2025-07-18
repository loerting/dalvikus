package me.lkl.dalvikus.decompiler

import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedClassDef
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.DexFile
import com.android.tools.smali.dexlib2.writer.io.MemoryDataStore
import com.android.tools.smali.dexlib2.writer.pool.DexPool
import jadx.api.CommentsLevel
import jadx.api.JadxArgs
import jadx.api.JadxDecompiler
import jadx.api.impl.NoOpCodeCache
import me.lkl.dalvikus.io.IOChannel
import java.io.File
import java.nio.file.Files

class Decompiler {
    companion object {

        fun provideInput(classDef: DexBackedClassDef): IOChannel<String> {
            return IOChannel.readOnly {
                try {
                    // Create a temporary DexFile that contains only this classDef
                    val tempDex = TemporarySoloDexFile(classDef.dexFile, classDef)

                    // Write DexFile to memory
                    val dataStore = MemoryDataStore()
                    DexPool.writeTo(dataStore, tempDex)

                    // Write to a temporary file
                    val tempDexFile = File.createTempFile("dalvikus", ".dex")
                    Files.write(tempDexFile.toPath(), dataStore.data)

                    // Setup Jadx arguments
                    val jadxArgs = JadxArgs().apply {
                        inputFiles = listOf(tempDexFile)
                        commentsLevel = CommentsLevel.WARN
                        isShowInconsistentCode = true
                        isDebugInfo = false
                        codeCache = NoOpCodeCache()
                    }

                    JadxDecompiler(jadxArgs).use { jadx ->
                        jadx.load()
                        // Return the decompiled source of the first class found
                        val decompiled = jadx.classes.firstOrNull()?.code
                        decompiled ?: "Error: No classes found during decompilation."
                    }
                } catch (e: Exception) {
                    // Handle exceptions, could wrap with a translation call or simple error message
                    "Decompilation error: ${e.message}"
                }
            }
        }

        private class TemporarySoloDexFile(
            private val original: DexFile,
            private val classDef: ClassDef
        ) : DexFile {
            override fun getClasses(): Set<ClassDef> = setOf(classDef)
            override fun getOpcodes(): Opcodes = original.opcodes
        }
    }
}