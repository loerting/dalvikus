package me.lkl.dalvikus.tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.fife.ui.rsyntaxtextarea.SyntaxConstants

abstract class CodeTab(
    val tabName: String,
    override val tabId: String,
    override val tabIcon: ImageVector
) : TabElement {
    @Composable
    override fun tabName(): String = tabName

    fun getSyntaxEditingStyle(): String {
        return when (tabName.substringAfterLast('.').lowercase()) {
            "java" -> SyntaxConstants.SYNTAX_STYLE_JAVA
            "js", "javascript" -> SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT
            "py", "python" -> SyntaxConstants.SYNTAX_STYLE_PYTHON
            "cpp", "cc", "cxx" -> SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS
            "c" -> SyntaxConstants.SYNTAX_STYLE_C
            "cs", "csharp" -> SyntaxConstants.SYNTAX_STYLE_CSHARP
            "html", "htm" -> SyntaxConstants.SYNTAX_STYLE_HTML
            "xml" -> SyntaxConstants.SYNTAX_STYLE_XML
            "rb", "ruby" -> SyntaxConstants.SYNTAX_STYLE_RUBY
            "php" -> SyntaxConstants.SYNTAX_STYLE_PHP
            "kt", "kts" -> SyntaxConstants.SYNTAX_STYLE_KOTLIN
            "scala" -> SyntaxConstants.SYNTAX_STYLE_SCALA
            "sql" -> SyntaxConstants.SYNTAX_STYLE_SQL
            "sh", "bash" -> SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL
            "css" -> SyntaxConstants.SYNTAX_STYLE_CSS
            "json" -> SyntaxConstants.SYNTAX_STYLE_JSON
            "yaml", "yml" -> SyntaxConstants.SYNTAX_STYLE_YAML
            "txt", "text" -> SyntaxConstants.SYNTAX_STYLE_NONE
            else -> SyntaxConstants.SYNTAX_STYLE_NONE
        }
    }

    abstract suspend fun fileContent(): String
}