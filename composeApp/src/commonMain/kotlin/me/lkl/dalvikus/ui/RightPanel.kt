package me.lkl.dalvikus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.lkl.dalvikus.ui.tabs.TabManager
import me.lkl.dalvikus.ui.tabs.TabView

@Composable
internal fun RightPanelContent(tabManager: TabManager) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TabView(
            tabManager
        )
    }
}