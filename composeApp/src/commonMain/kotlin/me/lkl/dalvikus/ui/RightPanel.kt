package me.lkl.dalvikus.ui

import SettingsView
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.lkl.dalvikus.ui.packaging.PackagingView
import me.lkl.dalvikus.ui.tabs.TabManager
import me.lkl.dalvikus.ui.tabs.TabView

@Composable
internal fun RightPanelContent(tabManager: TabManager, selectedNavItem: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedContent(targetState = selectedNavItem, label = "NavItem Animation") { targetTab ->
            when (targetTab) {
                "Editor", "Decompiler" -> OnCard({ TabView(tabManager, selectedNavItem) })
                "Settings" -> OnCard({ SettingsView() })
                "Packaging" -> OnCard { PackagingView() }
                else -> throw IllegalArgumentException(
                    "Unsupported selectedNavItem: $selectedNavItem. "
                )
            }
        }
    }
}

@Composable
internal fun OnCard(content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxSize().padding(8.dp),
    ) {
        content()
    }
}