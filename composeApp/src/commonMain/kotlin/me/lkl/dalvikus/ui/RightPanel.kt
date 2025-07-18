package me.lkl.dalvikus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.lkl.dalvikus.ui.tabs.TabManager
import me.lkl.dalvikus.ui.tabs.TabView

@Composable
internal fun RightPanelContent(tabManager: TabManager, selectedNavItem: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        when (selectedNavItem) {
            "Editor", "Decompiler" -> {
                TabView(
                    tabManager,
                    selectedNavItem
                )
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // work in progress
                    Text(
                        text = "WIP",
                        modifier = Modifier.fillMaxWidth(0.8f),
                        style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}