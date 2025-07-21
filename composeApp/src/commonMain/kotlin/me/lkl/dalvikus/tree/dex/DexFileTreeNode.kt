package me.lkl.dalvikus.tree.dex

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.tree.TreeElement
import java.io.BufferedInputStream
import java.io.File

class DexFileTreeNode(
    private val file: File, override val parent: TreeElement?
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

        val packageChildrenMap = mutableMapOf<String, MutableList<TreeElement>>()
        val createdPackageNodes = mutableMapOf<String, DexPackageTreeNode>()

        for (classDef in openDexFile!!.classes) {
            val fqcn = classDef.type.removeSurrounding("L", ";").replace('/', '.')
            val parts = fqcn.split('.')
            val packageParts = parts.dropLast(1)

            var currentPath = ""
            var parentNode: TreeElement = this

            // Build or reuse package nodes
            for (part in packageParts) {
                val nextPath = if (currentPath.isEmpty()) part else "$currentPath.$part"
                val pkgNode = createdPackageNodes.getOrPut(nextPath) {
                    val node = DexPackageTreeNode(
                        nameSegment = part,
                        childrenMap = packageChildrenMap,
                        fullPath = nextPath,
                        parent = parentNode
                    )
                    packageChildrenMap.getOrPut(currentPath) { mutableListOf() }.add(node)
                    node
                }
                parentNode = pkgNode
                currentPath = nextPath
            }

            // Add class node to the package children list
            val packagePath = packageParts.joinToString(".")
            val classNode = DexClassTreeNode(classDef, file, parent = parentNode)
            packageChildrenMap.getOrPut(packagePath) { mutableListOf() }.add(classNode)
        }

        return packageChildrenMap[""]?.sortedBy { it.name.lowercase() } ?: emptyList()
    }



    private fun openDexFileIfNull() {
        if (openDexFile == null) {
            val dexStream = BufferedInputStream(file.inputStream())
            openDexFile = DexBackedDexFile.fromInputStream(Opcodes.forApi(dalvikusSettings["api_level"]), dexStream)
        }
    }

    override fun onCollapse() {
        // TODO unsure if we should close and set openDexFile to null. This may have possible side effects in the editor when something is still open.
    }
}