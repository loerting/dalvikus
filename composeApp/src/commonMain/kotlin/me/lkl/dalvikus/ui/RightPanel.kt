package me.lkl.dalvikus.ui

import SettingsView
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.lkl.dalvikus.ui.packaging.PackagingView
import me.lkl.dalvikus.ui.tabs.TabView
import me.lkl.dalvikus.ui.util.DefaultCard

@Composable
internal fun RightPanelContent(selectedNavItem: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedContent(targetState = selectedNavItem, label = "NavItem Animation") { targetTab ->
            when (targetTab) {
                "Editor", "Decompiler" -> DefaultCard { TabView(selectedNavItem) }
                "Settings" -> SettingsView()
                "Packaging" -> PackagingView()
                else -> throw IllegalArgumentException(
                    "Unsupported selectedNavItem: $selectedNavItem. "
                )
            }
        }
    }
}

