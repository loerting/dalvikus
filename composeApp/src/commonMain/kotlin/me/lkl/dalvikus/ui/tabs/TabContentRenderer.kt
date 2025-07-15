package me.lkl.dalvikus.ui.tabs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor
import dalvikus.composeapp.generated.resources.Res
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tabs.WelcomeTab

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

        else -> {
            Text("Unknown Tab Type")
        }
    }
}