package me.lkl.dalvikus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.ui.tabs.TabManager

@Composable
internal fun RightPanelContent(tabState: MutableState<List<TabElement>>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val tabList = tabState.value

        TabManager(
            tabs = tabList,
            onCloseTab = { tabToRemove ->
                tabState.value = tabList.filter { it != tabToRemove }
            }
        )
    }
}