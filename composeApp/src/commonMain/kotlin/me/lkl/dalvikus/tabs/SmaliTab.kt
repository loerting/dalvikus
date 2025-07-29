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
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.decompiler.CFRDecompiler
import me.lkl.dalvikus.decompiler.Decompiler
import me.lkl.dalvikus.decompiler.DecompilerContentProvider
import me.lkl.dalvikus.decompiler.JADXDecompiler
import me.lkl.dalvikus.decompiler.VineflowerDecompiler
import me.lkl.dalvikus.selectedNavItem
import me.lkl.dalvikus.tabs.contentprovider.ContentProvider
import me.lkl.dalvikus.tabs.contentprovider.DualContentProvider
import me.lkl.dalvikus.tree.dex.DexEntryClassNode

class SmaliTab(
    val classDef: ClassDef,
    override val tabId: String,
    val dexEntryClassNode: DexEntryClassNode
) : TabElement {
    override var hasUnsavedChanges: MutableState<Boolean> = mutableStateOf(false)
    override val contentProvider: DualContentProvider = object : DualContentProvider(dexEntryClassNode,
        DecompilerContentProvider(dexEntryClassNode) { getDecompiler() }
    ) {
        override var firstContentProvider = true
            get() = selectedNavItem != "Decompiler"
    }

    private fun getDecompiler(): Decompiler {
        return when(dalvikusSettings["decompiler_implementation"] as String) {
            "jadx" -> JADXDecompiler()
            "cfr" -> CFRDecompiler()
            "vineflower" -> VineflowerDecompiler()
            // TODO implement LLM decompiler https://huggingface.co/collections/MoxStone/smalillm-68550b87817dfb046f790cdf
            else -> throw IllegalArgumentException("Selected decompiler not implemented yet")
        }
    }

    @Composable
    override fun tabName(): String = classDef.type.removeSurrounding("L", ";").substringAfterLast('/')
    override val tabIcon: ImageVector = Icons.Outlined.DataObject
}