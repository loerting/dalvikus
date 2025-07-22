package me.lkl.dalvikus.ui.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.*
import me.lkl.dalvikus.tabManager
import me.lkl.dalvikus.tabs.TabElement
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabView(
    selectedNavItem: String
) {
    val pendingCloseTab = remember { mutableStateOf<TabElement?>(null) }
    val showCloseDialog = remember { mutableStateOf(false) }
    if (showCloseDialog.value && pendingCloseTab.value != null) {
        UnsavedChangesDialog(tabManager, showCloseDialog, pendingCloseTab)
    }
    Column {
        SecondaryScrollableTabRow(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            selectedTabIndex = tabManager.selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp,
        ) {
            tabManager.tabs.forEachIndexed { index, tab ->
                val tooltipState = rememberTooltipState(isPersistent = true)
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
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(8.dp),
                                tooltip = {
                                    RichTooltip(
                                        title = { Text(stringResource(Res.string.tooltip_tab_title)) }
                                    ) {
                                        Text(
                                            tab.tabSource?.getSourceDescription()
                                                ?: stringResource(Res.string.unknown_source)
                                        )
                                    }
                                },
                                state = tooltipState
                            ) {
                                Text(
                                    if (unsaved) "${tab.tabName()}*" else tab.tabName(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

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
                TextButton(
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
                TextButton(onClick = abort) {
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