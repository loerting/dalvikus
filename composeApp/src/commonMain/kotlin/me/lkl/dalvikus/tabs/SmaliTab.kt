package me.lkl.dalvikus.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.tools.smali.dexlib2.iface.ClassDef
import me.lkl.dalvikus.tree.dex.DexEntryClassNode

class SmaliTab(
    val classDef: ClassDef,
    override val tabId: String,
    override val treeNode: DexEntryClassNode
) : TabElement {
    override val hasUnsavedChanges: MutableState<Boolean> = mutableStateOf(false)

    @Composable
    override fun tabName(): String = classDef.type.removeSurrounding("L", ";").substringAfterLast('/')
    override val tabIcon: ImageVector = Icons.Outlined.DataObject
}