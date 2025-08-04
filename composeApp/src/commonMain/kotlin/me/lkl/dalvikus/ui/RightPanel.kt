package me.lkl.dalvikus.ui

import SettingsView
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.lkl.dalvikus.selectedNavItem
import me.lkl.dalvikus.ui.packaging.PackagingView
import me.lkl.dalvikus.ui.packaging.PackagingViewModel
import me.lkl.dalvikus.ui.resources.ResourcesView
import me.lkl.dalvikus.ui.tabs.TabView
import me.lkl.dalvikus.util.DefaultCard

val packagingViewModel = PackagingViewModel()

@Composable
internal fun RightPanelContent() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedContent(targetState = selectedNavItem, label = "NavItem Animation") { targetTab ->
            when (targetTab) {
                "Editor", "Decompiler" -> DefaultCard { TabView() }
                "Resources" -> ResourcesView()
                "Packaging" -> PackagingView(packagingViewModel)
                "Settings" -> SettingsView()
                else -> throw IllegalArgumentException(
                    "Unsupported selectedNavItem: $selectedNavItem. "
                )
            }
        }
    }
}

