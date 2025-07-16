package me.lkl.dalvikus.tree.dex

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tree.TreeElement
import java.io.BufferedInputStream
import java.io.File

class DexFileTreeNode(
private val file: File
) : TreeElement {
    private var openDexFile: DexBackedDexFile? = null

    override val name: String
        get() = file.name.ifEmpty { file.path }

    override val icon: ImageVector
        get() = Icons.Filled.Adb

    override val isContainer: Boolean
        get() = true

    override val isClickable: Boolean
        get() = true

    override suspend fun getChildren(): List<TreeElement> {
        openDexFileIfNull()
        return openDexFile!!.classes.map { classDef ->
            DexClassTreeNode(classDef, file)
        }.sortedBy { it.name.lowercase() }
    }

    private fun openDexFileIfNull() {
        if (openDexFile == null) {
            val dexStream = BufferedInputStream(file.inputStream())
            openDexFile = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), dexStream)
        }
    }

    override fun onCollapse() {
        // TODO unsure if we should close and set openDexFile to null. This may have possible side effects in the editor when something is still open.
    }
}