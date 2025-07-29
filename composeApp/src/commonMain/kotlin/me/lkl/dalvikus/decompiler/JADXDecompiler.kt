package me.lkl.dalvikus.decompiler
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.writer.io.MemoryDataStore
import com.android.tools.smali.dexlib2.writer.pool.DexPool
import jadx.api.CommentsLevel
import jadx.api.JadxArgs
import jadx.api.JadxDecompiler
import jadx.api.impl.NoOpCodeCache
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.smali.SoloDexFileWrapper
import java.io.File
import java.nio.file.Files

class JADXDecompiler : Decompiler {
    override suspend fun decompile(classDef: ClassDef): String {
        return try {
            // Create a temporary DexFile that contains only this classDef
            val tempDex = SoloDexFileWrapper(classDef)

            // Write DexFile to memory
            val dataStore = MemoryDataStore().also {
                DexPool.writeTo(it, tempDex)
            }

            // Write to a temporary file
            val tempDexFile = File.createTempFile("dalvikus", ".dex").apply {
                writeBytes(dataStore.data)
                deleteOnExit()
            }
            // Setup Jadx arguments
            val jadxArgs = JadxArgs().apply {
                inputFiles = listOf(tempDexFile)
                commentsLevel =
                    if (dalvikusSettings["decompiler_verbose"]) CommentsLevel.DEBUG else CommentsLevel.WARN
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

