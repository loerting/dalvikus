package me.lkl.dalvikus.util

data class SearchOptions(
    val caseSensitive: Boolean = false,
    val useRegex: Boolean = false
)

fun createSearchMatcher(query: String, options: SearchOptions): ((String) -> Boolean)? {
    return if (options.useRegex) {
        try {
            val regex = if (options.caseSensitive) {
                Regex(query)
            } else {
                Regex(query, RegexOption.IGNORE_CASE)
            }
            { input -> regex.containsMatchIn(input) }
        } catch (e: Exception) {
            null // Invalid regex
        }
    } else {
        { input -> input.contains(query, ignoreCase = !options.caseSensitive) }
    }
}