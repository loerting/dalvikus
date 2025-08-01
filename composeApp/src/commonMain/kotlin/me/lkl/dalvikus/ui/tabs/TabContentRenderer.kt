package me.lkl.dalvikus.ui.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.lkl.dalvikus.tabs.CodeTab
import me.lkl.dalvikus.tabs.ImageTab
import me.lkl.dalvikus.tabs.SmaliTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tabs.WelcomeTab
import me.lkl.dalvikus.ui.editor.EditorView
import me.lkl.dalvikus.ui.image.ImageView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabContentRenderer(tab: TabElement) {
    when (tab) {
        is WelcomeTab -> WelcomeView()
        is CodeTab -> EditorView(tab)
        is SmaliTab -> EditorView(tab)
        is ImageTab -> ImageView(tab)
        else -> throw IllegalArgumentException("Unsupported tab type: ${tab::class.simpleName}")
    }
}

