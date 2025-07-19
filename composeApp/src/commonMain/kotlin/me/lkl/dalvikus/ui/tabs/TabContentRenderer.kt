package me.lkl.dalvikus.ui.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BugReport
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
import me.lkl.dalvikus.tabs.SmaliTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tabs.WelcomeTab
import me.lkl.dalvikus.ui.editor.Code
import me.lkl.dalvikus.ui.editor.Editor
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabContentRenderer(selectedNavItem: String, tab: TabElement) {
    when (tab) {
        is WelcomeTab -> {
            val editable = remember(tab) {
                Code(tab, IOChannel.readOnly {
                    Res.readBytes("files/welcome.md").decodeToString() +
                            Res.readBytes("files/changelog.md").decodeToString()
                }, "md")
            }
            Editor(editable)
        }

        is CodeTab -> {
            if (selectedNavItem == "Decompiler") {
                UnavailableFeature()
            } else {
                val fileExtension = tab.tabName().substringAfterLast(".", "").lowercase()
                val code = remember(tab) { Code(tab, tab.makeIOChannel(), fileExtension) }
                Editor(code)
            }
        }

        is SmaliTab -> {

            if (selectedNavItem == "Decompiler") {
                val code = remember(tab) { Code(tab, Decompiler.provideInput(tab.classDef), "java") }
                Editor(code)
            } else {
                val code = remember(tab) { Code(tab, tab.makeIOChannel(), "smali") }
                Editor(code)
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

@Composable
fun UnavailableFeature() {
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
}

