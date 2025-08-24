package me.lkl.dalvikus.ui.tabs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DisabledByDefault
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.*
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tabs.contentprovider.DualContentProvider
import org.jetbrains.compose.resources.stringResource

@Composable
fun TabContextMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    offset: DpOffset,
    tab: TabElement,
    tabIndex: Int,
    tabManager: TabManager,
    onCloseTab: (TabElement) -> Unit,
    onCloseTabs: (List<TabElement>) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = offset
    ) {
        // Close tab
        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.tab_close))
                }
            },
            onClick = {
                onCloseTab(tab)
                onDismissRequest()
            }
        )

        // Close other tabs
        if (tabManager.tabs.size > 1) {
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DisabledByDefault,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.tab_close_others))
                    }
                },
                onClick = {
                    val otherTabs = tabManager.tabs.filter { it != tab }
                    onCloseTabs(otherTabs)
                    onDismissRequest()
                }
            )
        }

        // Close tabs to the left
        if (tabIndex > 0) {
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.tab_close_left))
                    }
                },
                onClick = {
                    val tabsToLeft = tabManager.tabs.take(tabIndex)
                    onCloseTabs(tabsToLeft)
                    onDismissRequest()
                }
            )
        }

        // Close tabs to the right
        if (tabIndex < tabManager.tabs.size - 1) {
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.tab_close_right))
                    }
                },
                onClick = {
                    val tabsToRight = tabManager.tabs.drop(tabIndex + 1)
                    onCloseTabs(tabsToRight)
                    onDismissRequest()
                }
            )
        }

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        // Show in files
        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.tab_show_in_files))
                }
            },
            onClick = {
                showInFiles(tab)
                onDismissRequest()
            }
        )
    }
}

private fun showInFiles(tab: TabElement) {
    val contentProvider = tab.contentProvider
    if (contentProvider is DualContentProvider) {
        tryOpenContentProviderInTree(contentProvider.contentProvider)
        tryOpenContentProviderInTree(contentProvider.contentProvider2)
    } else {
        tryOpenContentProviderInTree(contentProvider)
    }
}