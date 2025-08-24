package me.lkl.dalvikus.ui.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.*
import me.lkl.dalvikus.tabManager
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tabs.WelcomeTab
import me.lkl.dalvikus.tabs.contentprovider.ContentProvider
import me.lkl.dalvikus.tree.Node
import me.lkl.dalvikus.ui.currentSelection
import me.lkl.dalvikus.ui.scrollAndExpandSelection
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabView() {
    val pendingCloseTab = remember { mutableStateOf<TabElement?>(null) }
    val showCloseDialog = remember { mutableStateOf(false) }
    val pendingCloseTabs = remember { mutableStateOf<List<TabElement>>(emptyList()) }

    // Context menu state
    val showContextMenu = remember { mutableStateOf(false) }
    val contextMenuTab = remember { mutableStateOf<TabElement?>(null) }
    val contextMenuIndex = remember { mutableStateOf(-1) }

    if (showCloseDialog.value && (pendingCloseTab.value != null || pendingCloseTabs.value.isNotEmpty())) {
        UnsavedChangesDialog(
            tabManager,
            showCloseDialog,
            pendingCloseTab,
            pendingCloseTabs
        )
    }

    Column {
        SecondaryScrollableTabRow(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            selectedTabIndex = tabManager.selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp,
        ) {
            if (tabManager.tabs.isEmpty()) {
                Tab(
                    selected = true,
                    onClick = { /* no-op */ },
                    text = {
                        TabViewContent(
                            tab = WelcomeTab(),
                            onClose = null
                        )
                    },
                )
            }
            tabManager.tabs.forEachIndexed { index, tab ->
                Box {
                    Tab(
                        selected = tabManager.selectedTabIndex == index,
                        onClick = { tabManager.selectTab(index) },
                        text = {
                            TabViewContent(
                                tab = tab,
                                onClose = {
                                    handleTabClose(
                                        tab = tab,
                                        pendingCloseTab = pendingCloseTab,
                                        showCloseDialog = showCloseDialog
                                    )
                                },
                            )
                        },
                        modifier = Modifier
                            .onClick(matcher = PointerMatcher.mouse(PointerButton.Secondary)) {
                                contextMenuTab.value = tab
                                contextMenuIndex.value = index
                                showContextMenu.value = true
                            }
                    )

                    if (showContextMenu.value && contextMenuTab.value == tab) {
                        TabContextMenu(
                            expanded = showContextMenu.value,
                            onDismissRequest = { showContextMenu.value = false },
                            tab = tab,
                            tabIndex = index,
                            tabManager = tabManager,
                            onCloseTab = { tabToClose ->
                                handleTabClose(
                                    tab = tabToClose,
                                    pendingCloseTab = pendingCloseTab,
                                    showCloseDialog = showCloseDialog
                                )
                            },
                            onCloseTabs = { tabsToClose ->
                                handleTabsClose(
                                    tabs = tabsToClose,
                                    pendingCloseTabs = pendingCloseTabs,
                                    showCloseDialog = showCloseDialog
                                )
                            }
                        )
                    }
                }
            }
        }

        TabContentRenderer(tab = tabManager.currentTab)
    }
}

private fun handleTabClose(
    tab: TabElement,
    pendingCloseTab: MutableState<TabElement?>,
    showCloseDialog: MutableState<Boolean>
) {
    if (tab.hasUnsavedChanges.value) {
        pendingCloseTab.value = tab
        showCloseDialog.value = true
    } else {
        tabManager.closeTab(tab)
    }
}

private fun handleTabsClose(
    tabs: List<TabElement>,
    pendingCloseTabs: MutableState<List<TabElement>>,
    showCloseDialog: MutableState<Boolean>
) {
    val tabsWithUnsavedChanges = tabs.filter { it.hasUnsavedChanges.value }
    if (tabsWithUnsavedChanges.isNotEmpty()) {
        pendingCloseTabs.value = tabs
        showCloseDialog.value = true
    } else {
        tabManager.closeTabs(tabs)
    }
}

@Composable
fun TabViewContent(tab: TabElement, onClose: (() -> Unit)?) {
    var unsaved by tab.hasUnsavedChanges

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = tab.tabIcon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            if (unsaved) "${tab.tabName()}*" else tab.tabName(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(4.dp))
        if (onClose != null) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
                    .clickable {
                        onClose()
                    }
            )
        }
    }
}

fun tryOpenContentProviderInTree(
    provider1: ContentProvider
) {
    if (provider1 is Node) {
        currentSelection = provider1 as Node
        scrollAndExpandSelection.value = true
    }
}

@Composable
fun UnsavedChangesDialog(
    tabManager: TabManager,
    showCloseDialog: MutableState<Boolean>,
    pendingCloseTab: MutableState<TabElement?>,
    pendingCloseTabs: MutableState<List<TabElement>>
) {
    val abort = {
        showCloseDialog.value = false
        pendingCloseTab.value = null
        pendingCloseTabs.value = emptyList()
    }

    val tabsToClose = pendingCloseTabs.value
    val singleTab = pendingCloseTab.value

    val message = when {
        singleTab != null -> stringResource(
            Res.string.unsaved_changes_message,
            singleTab.tabName()
        )

        tabsToClose.size == 1 -> stringResource(
            Res.string.unsaved_changes_message,
            tabsToClose.first().tabName()
        )

        else -> stringResource(Res.string.unsaved_changes_multiple_message)
    }

    AlertDialog(
        onDismissRequest = abort,
        title = {
            Text(stringResource(Res.string.unsaved_changes_title))
        },
        text = {
            Text(message)
        },
        confirmButton = {
            // TODO maybe add a save button in the future
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = {
                        when {
                            singleTab != null -> tabManager.closeTab(singleTab)
                            tabsToClose.isNotEmpty() -> tabManager.closeTabs(tabsToClose)
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