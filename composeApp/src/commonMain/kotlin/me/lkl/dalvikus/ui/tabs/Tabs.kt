package me.lkl.dalvikus.ui.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.cancel
import dalvikus.composeapp.generated.resources.discard
import dalvikus.composeapp.generated.resources.save
import dalvikus.composeapp.generated.resources.unsaved_changes_message
import dalvikus.composeapp.generated.resources.unsaved_changes_title
import me.lkl.dalvikus.tabs.TabElement
import org.jetbrains.compose.resources.stringResource

@Composable
fun TabView(
    tabManager: TabManager,
    selectedNavItem: String
) {
    val pendingCloseTab = remember { mutableStateOf<TabElement?>(null) }
    val showCloseDialog = remember { mutableStateOf(false) }
    if (showCloseDialog.value && pendingCloseTab.value != null) {
        UnsavedChangesDialog(tabManager, showCloseDialog, pendingCloseTab)
    }
    Column {
        ScrollableTabRow(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            selectedTabIndex = tabManager.selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp,
        ) {
            tabManager.tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = tabManager.selectedTabIndex == index,
                    onClick = { tabManager.selectTab(index) },
                    text = {
                        var unsaved by tab.hasUnsavedChanges
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(imageVector = tab.tabIcon, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (unsaved) "${tab.tabName()}*" else tab.tabName(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyLarge)

                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        if (tabManager.tabs.size > 1) {
                                            if (tab.hasUnsavedChanges.value) {
                                                pendingCloseTab.value = tab
                                                showCloseDialog.value = true
                                            } else {
                                                tabManager.closeTab(tab)
                                            }
                                        }
                                    }
                            )
                        }
                    }
                )
            }
        }

        if (tabManager.tabs.isNotEmpty()) {
            TabContentRenderer(selectedNavItem, tab = tabManager.currentTab)
        }
    }
}
@Composable
fun UnsavedChangesDialog(
    tabManager: TabManager,
    showCloseDialog: MutableState<Boolean>,
    pendingCloseTab: MutableState<TabElement?>
) {
    val abort = {
        showCloseDialog.value = false
        pendingCloseTab.value = null
    }

    AlertDialog(
        onDismissRequest = abort,
        title = {
            Text(stringResource(Res.string.unsaved_changes_title))
        },
        text = {
            Text(
                stringResource(
                    Res.string.unsaved_changes_message,
                    pendingCloseTab.value!!.tabName()
                )
            )
        },
        confirmButton = {
            // TODO maybe add a save button in the future
        },
        dismissButton = {
            Row {
                androidx.compose.material3.TextButton(
                    onClick = {
                        pendingCloseTab.value?.let {
                            tabManager.closeTab(it)
                        }
                        abort()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.discard))
                }
                androidx.compose.material3.TextButton(onClick = abort) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.cancel))
                }
            }
        }
    )
}