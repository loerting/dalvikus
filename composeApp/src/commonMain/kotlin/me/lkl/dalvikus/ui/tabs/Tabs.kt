package me.lkl.dalvikus.ui.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.lkl.dalvikus.tabs.TabElement

@Composable
fun TabManager(
    tabs: List<TabElement>,
    onCloseTab: (TabElement) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
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
                                        if (tabs.size > 1) {
                                            onCloseTab(tab)
                                            if (selectedTabIndex >= tabs.size - 1) {
                                                selectedTabIndex = maxOf(0, selectedTabIndex - 1)
                                            }
                                        }
                                    }
                            )
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        if (tabs.isNotEmpty()) {
            TabContentRenderer(tab = tabs[selectedTabIndex])
        }
    }
}