package me.lkl.dalvikus.decompiler

import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.writer.io.MemoryDataStore
import com.android.tools.smali.dexlib2.writer.pool.DexPool
import com.googlecode.d2j.dex.Dex2jar
import com.googlecode.d2j.reader.MultiDexFileReader
import com.googlecode.dex2jar.tools.BaksmaliBaseDexExceptionHandler
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.smali.SoloDexFileWrapper
import java.io.File
import java.util.jar.JarFile

interface JavaDecompiler : Decompiler {

    override suspend fun decompile(classDef: ClassDef): String {
        try {
            val verbose = dalvikusSettings["decompiler_verbose"] as Boolean
            // Build a tiny .dex containing only this one ClassDef
            val tempDex = SoloDexFileWrapper(classDef)
            val dataStore = MemoryDataStore().also {
                DexPool.writeTo(it, tempDex)
            }

            val jarFile = File.createTempFile("dalvikus", ".jar").apply {
                deleteOnExit()
            }

            val reader = MultiDexFileReader.open(dataStore.data)
            val handler = BaksmaliBaseDexExceptionHandler()
            Dex2jar.from(reader)
                .withExceptionHandler(handler)
                .topoLogicalSort()
                .skipDebug(!verbose)
                .to(jarFile.toPath())

            if (handler.hasException()) {
                val tempErrorFile = File.createTempFile("dalvikus_error", ".txt").apply {
                    handler.dump(toPath(), arrayOf<String>())
                    deleteOnExit()
                }
                val tempErrorText = tempErrorFile.readText()
                return "Error during dex2jar conversion: $tempErrorText\n"
            }

            val internalName = classDef.type.substring(1, classDef.type.length - 1)
            val classPath = "$internalName.class"
            val bytes = JarFile(jarFile).use { jar ->
                jar.entries()
                    .toList()
                    .firstOrNull { it.name == classPath }
                    ?.let { entry ->
                        jar.getInputStream(entry).use { inputStream ->
                            inputStream.readBytes()
                        }
                    }
                    ?: return "Error: could not find $classPath inside dex2jar output"
            }

            return decompileJava(internalName, bytes)
        } catch (e: Exception) {
            return "Decompilation error: ${e.message}"
        }
    }

    suspend fun decompileJava(internalName: String, bytes: ByteArray): String
}
