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
import com.android.tools.smali.smali.smaliParser
import com.android.tools.smali.smali.smaliTreeWalker
import me.lkl.dalvikus.dalvikusSettings
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
    override val name: String,
    private val fullPath: String, // com/example/MyClass
    private val root: DexFileNode,
    override val parent: ContainerNode?
) : FileNode() {

    override val icon = Icons.Outlined.DataObject
    override val editableContent = true

    suspend fun getClassDef(): ClassDef = root.readEntry(fullPath)

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

        val parser = smaliParser(tokens)
        parser.setVerboseErrors(true)
        parser.setAllowOdex(true)
        parser.setApiLevel(apiLevel)

        val result = parser.smali_file()

        if (parser.numberOfSyntaxErrors > 0 || lexer.numberOfSyntaxErrors > 0) {
            Logger.e("SmaliIOChannel") {
                "Failed to parse smali content. " +
                        "Lexer errors: ${lexer.numberOfSyntaxErrors}, " +
                        "Parser errors: ${parser.numberOfSyntaxErrors}"
            }
            snackbarManager?.showAssembleError(lexer.numberOfSyntaxErrors, parser.numberOfSyntaxErrors)
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
    }

    override suspend fun notifyChanged() {
        parent?.notifyChanged()
    }

    override fun getFileType(): String {
        return "smali"
    }

    override suspend fun createTab(): TabElement {
        return SmaliTab(
            classDef = getClassDef(),
            tabId = fullPath,
            dexEntryClassNode = this
        )
    }
}
