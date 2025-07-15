package me.lkl.dalvikus.tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class CodeTab(
    val fileContent: String,
    val tabName: String,
    override val tabId: String = "ct:$tabName",
    override val tabIcon: ImageVector
) : TabElement {
    @Composable
    override fun tabName(): String = tabName

    fun getSyntaxEditingStyle(tabName: String): String {
        return when (tabName.substringAfterLast('.').lowercase()) {
            "java" -> "text/java"
            "js", "javascript" -> "text/javascript"
            "py", "python" -> "text/python"
            "cpp", "cc", "cxx" -> "text/cpp"
            "c" -> "text/c"
            "cs", "csharp" -> "text/cs"
            "html", "htm" -> "text/html"
            "xml" -> "text/xml"
            "rb", "ruby" -> "text/ruby"
            "php" -> "text/php"
            "kt", "kts" -> "text/kotlin"
            "swift" -> "text/swift"
            "scala" -> "text/scala"
            "sql" -> "text/sql"
            "sh", "bash" -> "text/unix"
            "css" -> "text/css"
            "json" -> "text/json"
            "yaml", "yml" -> "text/yaml"
            "txt", "text" -> "text/plain"
            else -> "text/plain"
        }
    }

}