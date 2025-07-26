package me.lkl.dalvikus.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.tools.smali.dexlib2.iface.ClassDef
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.lkl.dalvikus.tabs.contentprovider.ContentProvider
import me.lkl.dalvikus.tree.dex.DexEntryClassNode

class SmaliTab(
    val classDef: ClassDef,
    override val tabId: String,
    val dexEntryClassNode: DexEntryClassNode
) : TabElement {
    override var hasUnsavedChanges: MutableState<Boolean> = mutableStateOf(false)
    override val contentProvider: ContentProvider = SwitchingSmaliContentProvider(dexEntryClassNode)

    @Composable
    override fun tabName(): String = classDef.type.removeSurrounding("L", ";").substringAfterLast('/')
    override val tabIcon: ImageVector = Icons.Outlined.DataObject
}

class SwitchingSmaliContentProvider(val dexEntryClassNode: DexEntryClassNode) : ContentProvider() {
    val decompilerMode = true
    override val editableContent: Boolean
        get() = if(decompilerMode) false else dexEntryClassNode.editableContent

    protected val _contentFlow = MutableStateFlow<ByteArray>(ByteArray(0))
    val contentFlow: StateFlow<ByteArray> = _contentFlow.asStateFlow()

    override suspend fun loadContent() {
        if(decompilerMode) {

        } else {

        }
    }

    override fun getFileType(): String = if(decompilerMode) "java" else "smali"

    override fun getSourcePath(): String? = dexEntryClassNode.getSourcePath()

}