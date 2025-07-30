package me.lkl.dalvikus.tree

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.outlined.Abc
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Interests
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material.icons.outlined.Water
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.tools.smali.dexlib2.iface.Annotation
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import com.android.tools.smali.dexlib2.iface.value.StringEncodedValue

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.lkl.dalvikus.tree.dex.DexEntryClassNode

data class SearchOptions(
    val caseSensitive: Boolean = false,
    val useRegex: Boolean = false
)

enum class TreeSearchResultType {
    TREE_NODE,
    STRING_VALUE,
    REFERENCE
}

data class TreeSearchResult(val node: Node, val snippet: String, val type: TreeSearchResultType) {
    val icon: ImageVector = when (type) {
        TreeSearchResultType.TREE_NODE -> node.icon
        TreeSearchResultType.STRING_VALUE -> Icons.Default.Abc
        TreeSearchResultType.REFERENCE -> Icons.Outlined.Interests
    }
    val path: String = node.getPathHistory()
}

fun searchTreeBFS(
    root: Node,
    query: String,
    options: SearchOptions
): Flow<TreeSearchResult> = flow {
    // TODO search for resources.
    val matcher: (String) -> Boolean = if (options.useRegex) {
        // Compile regex with case option
        val regex = try {
            Regex(
                query,
                if (options.caseSensitive) setOf() else setOf(RegexOption.IGNORE_CASE)
            )
        } catch (_: Exception) {
            return@flow // Invalid regex: skip search
        }
        { input -> regex.containsMatchIn(input) }
    } else {
        { input ->
            input.contains(query, ignoreCase = !options.caseSensitive)
        }
    }

    val queue = ArrayDeque<Node>()
    queue.add(root)

    var resultsFound = 0
    while (queue.isNotEmpty() && resultsFound < 100) {
        val current = queue.removeFirst()
        val path = current.getPathHistory()

        if (matcher(path)) {
            emit(TreeSearchResult(current, current.name, TreeSearchResultType.TREE_NODE))
            resultsFound++
        }

        if (current is DexEntryClassNode) {
            val classDef = current.getClassDef()

            // Search string constants
            classDef.getStringPool().forEach { string ->
                if (matcher(string)) {
                    emit(TreeSearchResult(current, "\"$string\"", TreeSearchResultType.STRING_VALUE))
                    resultsFound++
                }
            }

            // Search method references
            classDef.methods.forEach { method ->
                method.implementation?.instructions?.forEach { instruction ->
                    if (instruction is ReferenceInstruction) {
                        val ref = instruction.reference
                        if (ref is MethodReference) {
                            val methodSig = "${ref.definingClass}->${ref.name}(${ref.parameterTypes.joinToString("")})${ref.returnType}"
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