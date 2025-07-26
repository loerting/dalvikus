package me.lkl.dalvikus.tree.dex

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DataObject
import com.android.tools.smali.baksmali.Adaptors.ClassDefinition
import com.android.tools.smali.baksmali.BaksmaliOptions
import com.android.tools.smali.baksmali.formatter.BaksmaliWriter
import com.android.tools.smali.dexlib2.iface.ClassDef
import me.lkl.dalvikus.tabs.SmaliTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.FileNode
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

    override suspend fun writeContent(smaliText: ByteArray) {
        TODO("Not yet implemented")
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
