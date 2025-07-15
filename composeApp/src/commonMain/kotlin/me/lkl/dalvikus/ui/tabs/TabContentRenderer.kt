package me.lkl.dalvikus.ui.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor
import dalvikus.composeapp.generated.resources.JetBrainsMono_Regular
import dalvikus.composeapp.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import me.lkl.dalvikus.tabs.CodeTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tabs.WelcomeTab
import me.lkl.dalvikus.ui.AwtFontFromRes
import me.lkl.dalvikus.ui.toAwt
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.Theme
import org.fife.ui.rtextarea.RTextScrollPane
import org.jetbrains.compose.resources.rememberResourceEnvironment


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
    val caretColor = MaterialTheme.colorScheme.primary

    var environment = rememberResourceEnvironment()
    val coroutineScope = rememberCoroutineScope()

    var syntaxTextArea by remember { mutableStateOf<RSyntaxTextArea?>(null) }
    LaunchedEffect(tab) {
        syntaxTextArea = null // Reset the text area when the tab changes
        coroutineScope.launch {
            val font = AwtFontFromRes(14f, environment, Res.font.JetBrainsMono_Regular)
            val content = tab.fileContent()

            withContext(Dispatchers.Swing) {
                val textArea = RSyntaxTextArea()
                val theme = Theme.load(
                    javaClass.getResourceAsStream(
                        "/org/fife/ui/rsyntaxtextarea/themes/idea.xml"
                    )
                )
                theme.apply(textArea)

                textArea.syntaxEditingStyle = tab.getSyntaxEditingStyle()
                textArea.isCodeFoldingEnabled = true
                textArea.antiAliasingEnabled = true
                textArea.tabSize = 4
                textArea.background = backgroundColor.toAwt()
                textArea.foreground = foregroundColor.toAwt()
                textArea.caretColor = caretColor.toAwt()
                textArea.font = font

                textArea.text = content

                syntaxTextArea = textArea
            }
        }
    }
    if (syntaxTextArea != null) {
        SwingPanel(
            modifier = modifier,
            factory = {
                RTextScrollPane(syntaxTextArea).apply {

                    viewportBorder = null
                    border = null
                }
            },
        )
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.width(64.dp))
        }
    }
}