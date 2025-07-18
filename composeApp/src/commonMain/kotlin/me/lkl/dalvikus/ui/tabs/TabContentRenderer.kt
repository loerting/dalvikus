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
import me.lkl.dalvikus.io.IOChannel
import me.lkl.dalvikus.tabs.CodeTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tabs.WelcomeTab
import me.lkl.dalvikus.ui.editor.Code
import me.lkl.dalvikus.ui.editor.Editor
import me.lkl.dalvikus.settings.DalvikusSettings
import me.lkl.dalvikus.tabs.SmaliTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabContentRenderer(tab: TabElement) {
    when (tab) {
        is WelcomeTab -> {
            val dalvikusSettings = remember { DalvikusSettings() }
            Editor(Code(IOChannel.readOnly {
                Res.readBytes("files/welcome.md").decodeToString() +
                        Res.readBytes("files/changelog.md").decodeToString()
            }, "md"), dalvikusSettings)
        }

        is CodeTab -> {
            val dalvikusSettings = remember { DalvikusSettings() }
            val fileExtension = tab.tabName().substringAfterLast(".", "").lowercase()
            val code = Code(tab.makeIOChannel(), fileExtension)
            Editor(code, dalvikusSettings)
        }
        is SmaliTab -> {
            val dalvikusSettings = remember { DalvikusSettings() }
            val code = Code(tab.makeIOChannel(), "smali")
            Editor(code, dalvikusSettings)
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

