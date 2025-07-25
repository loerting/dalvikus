package me.lkl.dalvikus.tree.dex

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.ClassDef
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.Node
import me.lkl.dalvikus.tree.backing.Backing
import me.lkl.dalvikus.tree.buildChildNodes

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
        TODO("Not yet implemented")
        /**
         *                 val apiLevel = dalvikusSettings["api_level"] as Int
         *                 val lexer = smaliFlexLexer(content.reader(), apiLevel)
         *                 val tokens = CommonTokenStream(lexer)
         *
         *                 val parser = smaliParser(tokens)
         *                 parser.setVerboseErrors(true)
         *                 parser.setAllowOdex(true)
         *                 parser.setApiLevel(apiLevel)
         *
         *                 val result = parser.smali_file()
         *
         *                 if (parser.numberOfSyntaxErrors > 0 || lexer.numberOfSyntaxErrors > 0) {
         *                     Logger.e("SmaliIOChannel") {
         *                         "Failed to parse smali content. " +
         *                                 "Lexer errors: ${lexer.numberOfSyntaxErrors}, " +
         *                                 "Parser errors: ${parser.numberOfSyntaxErrors}"
         *                     }
         *                     return@IOChannel false
         *                 }
         *
         *                 val t: CommonTree = result.getTree()
         *
         *                 val treeStream = CommonTreeNodeStream(t)
         *                 treeStream.tokenStream = tokens
         *
         *                 val dexGen = smaliTreeWalker(treeStream)
         *                 dexGen.setApiLevel(apiLevel)
         *
         *                 dexGen.setVerboseErrors(true)
         *                 val dexBuilder = DexBuilder(Opcodes.forApi(apiLevel))
         *
         *                 dexGen.setDexBuilder(dexBuilder)
         *                 val classDef = dexGen.smali_file()
         *
         *                 print(classDef.javaClass.name)
         *
         *
         *                 // TODO use the smaliFile or the dexBuilder to write back to the DEX file
         *
         *                 return@IOChannel true
         */
    }

    suspend fun readEntry(path: String): ClassDef {
        return entries[path] ?: error("Class not found: $path")
    }

    suspend fun updateEntry(path: String, newClassDef: ClassDef) {
        entries[path] = newClassDef
        rebuild()
    }
}
