package me.lkl.dalvikus.ui.tabs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor
import dalvikus.composeapp.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import me.lkl.dalvikus.tabs.CodeTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tabs.WelcomeTab
import me.lkl.dalvikus.ui.toAwt
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rtextarea.RTextScrollPane

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabContentRenderer(tab: TabElement) {
    when (tab) {
        is WelcomeTab -> {
            val state = rememberRichTextState()
            LaunchedEffect(Unit) {
                state.insertMarkdownAfterSelection(Res.readBytes("files/welcome.md").decodeToString())
                // state.insertHtmlAfterSelection("</br></br></br>")
                state.insertMarkdownAfterSelection(Res.readBytes("files/changelog.md").decodeToString())

            }
            state.config.linkColor = MaterialTheme.colorScheme.primary
            BasicRichTextEditor(
                state = state,
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.fillMaxSize().padding(16.dp),
                enabled = true,
                readOnly = true,
            )
        }

        is CodeTab -> {
            RSyntaxTextEditor(
                tab,
                modifier = Modifier.fillMaxSize().padding(1.dp)
            )
        }

        else -> {
            Icon(
                imageVector = Icons.Default.BugReport,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().padding(16.dp)
            )
        }
    }
}

@Composable
fun RSyntaxTextEditor(tab: CodeTab, modifier: Modifier = Modifier) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val foregroundColor = MaterialTheme.colorScheme.onBackground
    val highlightColor = MaterialTheme.colorScheme.surfaceVariant
    val caretColor = MaterialTheme.colorScheme.primary
    val selectionColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)

    //val awtFont = loadAwtFontFromResource("/font/JetBrainsMono-Regular.ttf", 14f)
    val coroutineScope = rememberCoroutineScope()

    SwingPanel(
        modifier = modifier,
        factory = {
            // TODO hide textarea and show loading indicator while content is being loaded
            // TODO font
            // TODO use compose scrollbars instead of RTextScrollPane

            val textArea = RSyntaxTextArea().apply {
                syntaxEditingStyle = tab.getSyntaxEditingStyle()
                isCodeFoldingEnabled = true
                antiAliasingEnabled = true
                //this.font = awtFont
                tabSize = 4
                background = backgroundColor.toAwt()
                foreground = foregroundColor.toAwt()
                currentLineHighlightColor = highlightColor.toAwt()
                this.caretColor = caretColor.toAwt()
                selectedTextColor = selectionColor.toAwt()
            }

            coroutineScope.launch {
                val content = tab.fileContent()  // suspend function call
                withContext(Dispatchers.Swing) {
                    textArea.text = content  // update UI on main thread
                }
            }

            RTextScrollPane(textArea).apply {
                viewportBorder = null
                border = null
            }
        }
    )
}