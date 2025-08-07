package me.lkl.dalvikus.tree

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.EscalatorWarning
import androidx.compose.material.icons.filled.Route
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.tools.smali.dexlib2.iface.Annotation
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import com.android.tools.smali.dexlib2.iface.value.StringEncodedValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.lkl.dalvikus.icons.FamilyHistory
import me.lkl.dalvikus.icons.ThreadUnread
import me.lkl.dalvikus.tree.dex.DexEntryClassNode
import me.lkl.dalvikus.util.SearchOptions
import me.lkl.dalvikus.util.createSearchMatcher

enum class TreeSearchResultType {
    TREE_NODE,
    STRING_VALUE,
    LITERAL,
    REFERENCE,
    CLASS_BY_PARENT;

    val icon: ImageVector
        get() = when (this) {
            TREE_NODE -> Icons.Default.FamilyHistory
            STRING_VALUE -> Icons.Default.Abc
            LITERAL -> Icons.Default.ThreadUnread
            REFERENCE -> Icons.Default.Route
            CLASS_BY_PARENT -> Icons.Default.EscalatorWarning
        }
}

data class TreeSearchResult(val node: Node, val snippet: String, val type: TreeSearchResultType) {
    val icon: ImageVector = when (type) {
        TreeSearchResultType.TREE_NODE -> node.icon
        else -> type.icon
    }
    val path: String = node.getPathHistory()
}

fun searchTreeBFS(
    root: Node,
    query: String,
    options: SearchOptions,
    maxResults: Int = 500
): Flow<TreeSearchResult> = flow {
    // TODO search for resources by resource name, not by literal
    val matcher: (String) -> Boolean = createSearchMatcher(query, options) ?: return@flow

    val queue = ArrayDeque<Node>()
    queue.add(root)

    var resultsFound = 0
    while (queue.isNotEmpty() && resultsFound < maxResults) {
        val current = queue.removeFirst()
        val path = current.getPathHistory()

        if (matcher(path)) {
            emit(TreeSearchResult(current, current.name, TreeSearchResultType.TREE_NODE))
            resultsFound++
        }

        if (current is DexEntryClassNode) {
            val classDef = current.getClassDef()

            // Check parent class
            classDef.superclass?.let { parentClass ->
                if (matcher(parentClass)) {
                    emit(TreeSearchResult(current, ".super $parentClass", TreeSearchResultType.CLASS_BY_PARENT))
                    resultsFound++
                }
            }

            // Search string constants
            classDef.getStringPool().forEach { string ->
                if (matcher(string)) {
                    emit(TreeSearchResult(current, "\"$string\"", TreeSearchResultType.STRING_VALUE))
                    resultsFound++
                }
            }

            classDef.methods.forEach { method ->
                method.implementation?.instructions?.forEach { instruction ->
                    if (instruction is ReferenceInstruction) {
                        val ref = instruction.reference
                        if (ref is MethodReference) {
                            val methodSig =
                                "${ref.definingClass}->${ref.name}(${ref.parameterTypes.joinToString("")})${ref.returnType}"
                            if (matcher(methodSig)) {
                                emit(TreeSearchResult(current, methodSig, TreeSearchResultType.REFERENCE))
                                resultsFound++
                            }
                        } else if (ref is FieldReference) {
                            val fieldSig = "${ref.definingClass}->${ref.name}:${ref.type}"
                            if (matcher(fieldSig)) {
                                emit(TreeSearchResult(current, fieldSig, TreeSearchResultType.REFERENCE))
                                resultsFound++
                            }
                        }
                    } else if (instruction is WideLiteralInstruction) {
                        val value = instruction.wideLiteral.toString()
                        val sign = if (value.startsWith("-")) "-" else ""
                        val hexValue = "${sign}0x${instruction.wideLiteral.toString(16).removePrefix(sign)}"
                        if (matcher(value) || matcher(hexValue)) {
                            emit(TreeSearchResult(current, "$value (dec) / $hexValue (hex)", TreeSearchResultType.LITERAL))
                            resultsFound++
                        }
                    }
                }
            }
        }

        if (current is ContainerNode) {
            current.loadChildren()
            queue.addAll(current.childrenFlow.value)
        }
    }
}


fun ClassDef.getStringPool(): List<String> {
    val stringPool = mutableSetOf<String>()

    this.methods.forEach { method ->
        method.implementation?.instructions?.forEach { instruction ->
            if (instruction is ReferenceInstruction) {
                val reference = instruction.reference
                if (reference is StringReference) {
                    stringPool.add(reference.string)
                }
            }
        }
    }

    this.fields.forEach { field ->
        field.initialValue?.let { encodedValue ->
            if (encodedValue is StringEncodedValue) {
                stringPool.add(encodedValue.value)
            }
        }
    }

    fun extractStringsFromAnnotations(annotations: Set<Annotation>) {
        for (annotation in annotations) {
            for (element in annotation.elements) {
                val value = element.value
                if (value is StringEncodedValue) {
                    stringPool.add(value.value)
                }
            }
        }
    }

    extractStringsFromAnnotations(this.annotations)

    this.fields.forEach { field ->
        extractStringsFromAnnotations(field.annotations)
    }

    this.methods.forEach { method ->
        extractStringsFromAnnotations(method.annotations)
    }

    return stringPool.toList()
}