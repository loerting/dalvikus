package me.lkl.dalvikus.ui.tabs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import me.lkl.dalvikus.tabs.CodeTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tabs.WelcomeTab
import me.lkl.dalvikus.ui.editor.Code
import me.lkl.dalvikus.ui.editor.Editor
import me.lkl.dalvikus.ui.editor.ViewerSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabContentRenderer(tab: TabElement) {
    when (tab) {
        is WelcomeTab -> {
            val viewerSettings = remember { ViewerSettings() }
            Editor(Code({
                Res.readBytes("files/welcome.md").decodeToString() +
                        Res.readBytes("files/changelog.md").decodeToString()
            }, "md", isEditable = false), viewerSettings)
        }

        is CodeTab -> {
            val viewerSettings = remember { ViewerSettings() }
            val fileExtension = tab.tabName.substringAfterLast(".", "").lowercase()
            Editor(Code({ tab.fileContent() }, fileExtension) {
                // TODO on update code.
            }, viewerSettings)
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

