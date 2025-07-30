package me.lkl.dalvikus.tree.dex

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.writer.io.FileDataStore
import com.android.tools.smali.dexlib2.writer.io.MemoryDataStore
import com.android.tools.smali.dexlib2.writer.pool.DexPool
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.Node
import me.lkl.dalvikus.tree.backing.Backing
import me.lkl.dalvikus.tree.buildChildNodes
import java.io.File

class DexFileNode(
    override val name: String,
    private val backing: Backing,
    override val parent: ContainerNode?
) : ContainerNode() {

    override val icon = Icons.Filled.Adb
    override val editableContent = false
    override val changesWithChildren = true

    val entries = mutableMapOf<String, ClassDef>() // Key: com/example/MyClass

    override suspend fun loadChildrenInternal(): List<Node> {
        val input = backing.read().inputStream().buffered()
        val dexFile = DexBackedDexFile.fromInputStream(
            Opcodes.forApi(dalvikusSettings["api_level"] as Int),
            input
        )

        entries.clear()
        dexFile.classes.forEach { classDef ->
            val path = classDef.type.removePrefix("L").removeSuffix(";") // com/example/Foo
            entries[path] = classDef
        }

        return buildChildNodes(
            entries = entries,
            prefix = "",
            onFolder = { name, path ->
                DexEntryPackageNode(name, path, this, this)
            },
            onFile = { name, path, classDef ->
                DexEntryClassNode(name, path, this, this)
            }
        )

    }

    override suspend fun rebuild() {
        val newBytes: ByteArray = rebuildDex()
        backing.write(newBytes)
    }

    private fun rebuildDex(): ByteArray {
        val apiLevel = dalvikusSettings["api_level"] as Int
        val dexPool = DexPool(Opcodes.forApi(apiLevel))
        val memoryDataStore = MemoryDataStore(524288)

        entries.values.forEach { classDef ->
            dexPool.internClass(classDef)
        }

        dexPool.writeTo(memoryDataStore)
        return memoryDataStore.data
    }

    fun readEntry(path: String): ClassDef {
        return entries[path] ?: error("Class not found: $path")
    }

    suspend fun updateEntry(path: String, newClassDef: ClassDef) {
        entries[path] = newClassDef
        rebuild()
        // to ensure e.g. class name changes are reflected in the UI.
        loadChildren()
    }
}
