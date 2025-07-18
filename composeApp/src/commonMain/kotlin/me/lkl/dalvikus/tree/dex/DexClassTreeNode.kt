package me.lkl.dalvikus.tree.dex

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.tools.smali.dexlib2.dexbacked.DexBackedClassDef
import me.lkl.dalvikus.tabs.SmaliTab
import me.lkl.dalvikus.tree.TreeElement
import java.io.File


class DexClassTreeNode(
    private val classDef: DexBackedClassDef,
    private val file: File
) : TreeElement {
    override val name: String
        get() = classDef.type.removeSurrounding("L", ";").substringAfterLast('/')

    override val icon: ImageVector
        get() = Icons.Outlined.DataObject

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
        return SmaliTab(
            classDef,
            tabId = "${file.path}#${classDef.type}"
        )
    }
}