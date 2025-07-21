package me.lkl.dalvikus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.tree_search_placeholder
import me.lkl.dalvikus.tabManager
import me.lkl.dalvikus.tree.FileTreeNode
import me.lkl.dalvikus.tree.TreeElement
import me.lkl.dalvikus.tree.archive.ArchiveTreeNode
import me.lkl.dalvikus.ui.tree.TreeView
import org.jetbrains.compose.resources.stringResource
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LeftPanelContent() {
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(
            inputField = {
                TextField(
                    value = query,
                    onValueChange = {
                        query = it
                    },
                    placeholder = {
                        Text(
                            stringResource(Res.string.tree_search_placeholder),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(Res.string.tree_search_placeholder)
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    query = ""
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1
                )
            },
            expanded = false,
            onExpandedChange = { },
            shape = RoundedCornerShape(16.dp),
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            tonalElevation = 4.dp,
            shadowElevation = 0.dp,
            windowInsets = SearchBarDefaults.windowInsets,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            content = {}
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TreeView(treeRoot,
                onFileSelected = {
                    node ->
                    if (!node.isClickable) return@TreeView
                    currentSelection = node
                    if (node is ArchiveTreeNode && node.isZipRoot() && node.file.extension.equals("apk", ignoreCase = true))
                        lastAndroidArchive = node

                    if(!node.isContainer) {
                        val newTab = node.createTab()
                        tabManager.addOrSelectTab(newTab)
                    }
                }, selectedElement = currentSelection)
        }
    }
}

internal val treeRoot: FileTreeNode = FileTreeNode(File(System.getProperty("user.home")), null)
internal var currentSelection by mutableStateOf<TreeElement?>(null)
internal var lastAndroidArchive by mutableStateOf<ArchiveTreeNode?>(null)