package me.lkl.dalvikus.ui.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TabView(
    tabManager: TabManager
) {
    Column {
        ScrollableTabRow(
            selectedTabIndex = tabManager.selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp,
        ) {
            tabManager.tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = tabManager.selectedTabIndex == index,
                    onClick = { tabManager.selectTab(index) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(imageVector = tab.tabIcon, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(tab.tabName(), maxLines = 1)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        if (tabManager.tabs.size > 1) {
                                            tabManager.closeTab(tab)
                                        }
                                    }
                            )
                        }
                    }
                )
            }
        }

        if (tabManager.tabs.isNotEmpty()) {
            TabContentRenderer(tab = tabManager.currentTab)
        }
    }
}