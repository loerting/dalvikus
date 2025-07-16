package me.lkl.dalvikus.tree.dex

import com.android.tools.smali.baksmali.Adaptors.ClassDefinition
import com.android.tools.smali.baksmali.BaksmaliOptions
import com.android.tools.smali.baksmali.formatter.BaksmaliWriter
import com.android.tools.smali.dexlib2.dexbacked.DexBackedClassDef
import com.android.tools.smali.smali.smaliParser
import com.android.tools.smali.smali.smaliTreeWalker
import me.lkl.dalvikus.tabs.CodeTab
import me.lkl.dalvikus.tree.TreeElement
import me.lkl.dalvikus.ui.tree.IconForFileExtension
import java.io.BufferedWriter
import java.io.File
import java.io.StringWriter

class DexClassTreeNode(
    private val classDef: DexBackedClassDef,
    private val file: File
) : TreeElement {
    override val name: String
        get() = classDef.type

    override val icon: androidx.compose.ui.graphics.vector.ImageVector
        get() = IconForFileExtension(name)

    override val isContainer: Boolean
        get() = false

    override suspend fun getChildren(): List<TreeElement> {
        // Classes do not have children in the tree structure
        return emptyList()
    }

    override val isClickable: Boolean
        get() = true

    override fun createTab(): me.lkl.dalvikus.tabs.TabElement {
        // Implement tab creation logic if needed
        return object : CodeTab(
            tabId = "${file.path}#$name",
            tabIcon = icon,
            tabName = name
        ) {
            override suspend fun fileContent(): String {
                // TODO writing using https://github.com/JesusFreke/smali/blob/master/smali/src/main/java/org/jf/smali/Smali.java
                val classDefinition = ClassDefinition(BaksmaliOptions(), classDef)

                val stringWriter = StringWriter()
                val bufferedWriter = BufferedWriter(stringWriter)

                val writer = BaksmaliWriter(bufferedWriter, classDef.type)

                classDefinition.writeTo(writer)

                bufferedWriter.flush()
                return stringWriter.toString()
            }
        }
    }
}