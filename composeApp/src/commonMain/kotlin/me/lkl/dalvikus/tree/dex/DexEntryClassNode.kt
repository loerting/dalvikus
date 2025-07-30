package me.lkl.dalvikus.tree.dex

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DataObject
import co.touchlab.kermit.Logger
import com.android.tools.smali.baksmali.Adaptors.ClassDefinition
import com.android.tools.smali.baksmali.BaksmaliOptions
import com.android.tools.smali.baksmali.formatter.BaksmaliWriter
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder
import com.android.tools.smali.smali.smaliFlexLexer
import com.android.tools.smali.smali.smaliTreeWalker
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.smali.ErrorHandlingSmaliParser
import me.lkl.dalvikus.snackbarManager
import me.lkl.dalvikus.tabs.SmaliTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.FileNode
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.tree.CommonTree
import org.antlr.runtime.tree.CommonTreeNodeStream
import java.io.BufferedWriter
import java.io.StringWriter

class DexEntryClassNode(
    override var name: String,
    private var fullPath: String, // com/example/MyClass
    private val root: DexFileNode,
    override val parent: ContainerNode?
) : FileNode() {

    override val icon = Icons.Outlined.DataObject
    override val editableContent = true

    fun getClassDef(): ClassDef = root.readEntry(fullPath)

    override suspend fun getContent(): ByteArray {
        val classDefinition = ClassDefinition(BaksmaliOptions(), getClassDef())

        val stringWriter = StringWriter()
        val bufferedWriter = BufferedWriter(stringWriter)

        val writer = BaksmaliWriter(bufferedWriter, getClassDef().type)

        classDefinition.writeTo(writer)

        bufferedWriter.flush()
        stringWriter.toString().let { content ->
            return content.toByteArray(Charsets.UTF_8)
        }
    }

    override suspend fun writeContent(newContent: ByteArray) {
        val apiLevel = dalvikusSettings["api_level"] as Int
        val lexer = smaliFlexLexer(newContent.inputStream().reader(), apiLevel)
        val tokens = CommonTokenStream(lexer)

        val parser = ErrorHandlingSmaliParser(tokens)
        parser.setVerboseErrors(true)
        parser.setAllowOdex(true)
        parser.setApiLevel(apiLevel)

        val result = parser.smali_file()

        if (parser.errorLines.isNotEmpty()) {
            Logger.e("Failed to parse smali content for assembly. " +
                        "Lexer errors: ${lexer.numberOfSyntaxErrors}, " +
                        "Parser errors: ${parser.numberOfSyntaxErrors}")

            snackbarManager?.showAssembleError(parser.errorLines)
            return
        }

        val t: CommonTree = result.getTree()

        val treeStream = CommonTreeNodeStream(t)
        treeStream.tokenStream = tokens

        val dexGen = smaliTreeWalker(treeStream)
        dexGen.setApiLevel(apiLevel)

        dexGen.setVerboseErrors(true)
        val dexBuilder = DexBuilder(Opcodes.forApi(apiLevel))

        dexGen.setDexBuilder(dexBuilder)
        val classDef = dexGen.smali_file()

        root.updateEntry(path = fullPath, newClassDef = classDef)
        reflectChanges(classDef)
    }

    private suspend fun reflectChanges(newClassDef: ClassDef) {
        // Update the name and fullPath based on the classDef type
        val newType = newClassDef.type.removePrefix("L").removeSuffix(";")
        val newName = newType.substringAfterLast('/')
        if (newName != name) {
            name = newName
            fullPath = newType
            // TODO actually we would have to update the parent as well, but currently it is not important.
        }
        notifyChanged()
    }

    override suspend fun notifyChanged() {
        parent?.notifyChanged()
    }

    override fun getFileType(): String {
        return "smali"
    }

    override suspend fun createTab(): TabElement {
        return SmaliTab(
            tabId = fullPath,
            dexEntryClassNode = this
        )
    }

    override fun getSizeEstimate(): Long {
        return 64 * 1024 // 64 kB
    }

    override fun isEditableTextually(): Boolean {
        return true // Smali files are always editable textually
    }
}
