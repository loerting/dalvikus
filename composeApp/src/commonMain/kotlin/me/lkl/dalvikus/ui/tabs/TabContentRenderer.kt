package me.lkl.dalvikus.ui.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.wrong_mode
import me.lkl.dalvikus.decompiler.Decompiler
import me.lkl.dalvikus.io.IOChannel
import me.lkl.dalvikus.tabs.CodeTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tabs.WelcomeTab
import me.lkl.dalvikus.ui.editor.Code
import me.lkl.dalvikus.ui.editor.Editor
import me.lkl.dalvikus.settings.DalvikusSettings
import me.lkl.dalvikus.tabs.SmaliTab
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabContentRenderer(selectedNavItem: String, tab: TabElement) {
    when (tab) {
        is WelcomeTab -> {
            val dalvikusSettings = remember { DalvikusSettings() }
            Editor(Code(IOChannel.readOnly {
                Res.readBytes("files/welcome.md").decodeToString() +
                        Res.readBytes("files/changelog.md").decodeToString()
            }, "md"), dalvikusSettings)
        }

        is CodeTab -> {
            if(selectedNavItem == "Decompiler") {
                Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Block, // or Icons.Default.Construction
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            stringResource(Res.string.wrong_mode),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                val dalvikusSettings = remember { DalvikusSettings() }
                val fileExtension = tab.tabName().substringAfterLast(".", "").lowercase()
                val code = Code(tab.makeIOChannel(), fileExtension)
                Editor(code, dalvikusSettings)
            }
        }
        is SmaliTab -> {

            val dalvikusSettings = remember { DalvikusSettings() }
            if(selectedNavItem == "Decompiler") {
                val code = Code(Decompiler.provideInput(tab.classDef),"java")
                Editor(code, dalvikusSettings)
            } else {
                val code = Code(tab.makeIOChannel(), "smali")
                Editor(code, dalvikusSettings)
            }
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

