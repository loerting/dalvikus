package me.lkl.dalvikus.tree

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class SearchOptions(
    val caseSensitive: Boolean = false,
    val useRegex: Boolean = false
)

data class TreeSearchResult(val node: Node)

fun searchTreeBFS(
    root: Node,
    query: String,
    options: SearchOptions
): Flow<TreeSearchResult> = flow {
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
            emit(TreeSearchResult(current))
            resultsFound++
        }

        if (current is ContainerNode) {
            current.loadChildren()
            queue.addAll(current.childrenFlow.value)
        }
    }
}
