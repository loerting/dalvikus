package me.lkl.dalvikus.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import co.touchlab.kermit.Logger
import com.android.tools.smali.baksmali.Adaptors.ClassDefinition
import com.android.tools.smali.baksmali.BaksmaliOptions
import com.android.tools.smali.baksmali.formatter.BaksmaliWriter
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedClassDef
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder
import com.android.tools.smali.smali.smaliFlexLexer
import com.android.tools.smali.smali.smaliParser
import com.android.tools.smali.smali.smaliTreeWalker
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.io.IOChannel
import me.lkl.dalvikus.settings.DalvikusSettings
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.Token
import org.antlr.runtime.tree.CommonTree
import org.antlr.runtime.tree.CommonTreeNodeStream
import java.io.BufferedWriter
import java.io.StringWriter

class SmaliTab(
    val classDef: DexBackedClassDef,
    override val tabId: String
) : TabElement {
    override val hasUnsavedChanges: MutableState<Boolean> = mutableStateOf(false)
    @Composable
    override fun tabName(): String = classDef.type.removeSurrounding("L", ";").substringAfterLast('/')
    override val tabIcon: ImageVector = Icons.Outlined.DataObject

    fun makeIOChannel(): IOChannel<String> {
        return IOChannel(
            read = {
                val classDefinition = ClassDefinition(BaksmaliOptions(), classDef)

                val stringWriter = StringWriter()
                val bufferedWriter = BufferedWriter(stringWriter)

                val writer = BaksmaliWriter(bufferedWriter, classDef.type)

                classDefinition.writeTo(writer)

                bufferedWriter.flush()
                stringWriter.toString()
            },
            writingSupported = true,
            write = { content ->
                val apiLevel = dalvikusSettings["api_level"] as Int
                val lexer = smaliFlexLexer(content.reader(), apiLevel)
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
                    return@IOChannel
                }

                val t: CommonTree = result.getTree()

                val treeStream = CommonTreeNodeStream(t)
                treeStream.tokenStream = tokens

                val dexGen = smaliTreeWalker(treeStream)
                dexGen.setApiLevel(apiLevel)

                dexGen.setVerboseErrors(true)
                val dexBuilder = DexBuilder(Opcodes.forApi(apiLevel))
                dexGen.setDexBuilder(dexBuilder)
                val smaliFile = dexGen.smali_file()
                // TODO use the smaliFile or the dexBuilder to write back to the DEX file

                hasUnsavedChanges.value = false
            },
            setName = {
                throw UnsupportedOperationException("Renaming archive entries is not supported.")
            }
        )
    }
}