package me.lkl.dalvikus.ui

import SettingsView
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.lkl.dalvikus.selectedNavItem
import me.lkl.dalvikus.ui.packaging.PackagingView
import me.lkl.dalvikus.ui.packaging.PackagingViewModel
import me.lkl.dalvikus.ui.tabs.TabView
import me.lkl.dalvikus.ui.util.DefaultCard

val packagingViewModel = PackagingViewModel()

@Composable
internal fun RightPanelContent() {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedContent(targetState = selectedNavItem, label = "NavItem Animation") { targetTab ->
            when (targetTab) {
                "Editor", "Decompiler" -> DefaultCard { TabView() }
                "Settings" -> SettingsView()
                "Packaging" -> PackagingView(packagingViewModel)
                else -> throw IllegalArgumentException(
                    "Unsupported selectedNavItem: $selectedNavItem. "
                )
            }
        }
    }
}

