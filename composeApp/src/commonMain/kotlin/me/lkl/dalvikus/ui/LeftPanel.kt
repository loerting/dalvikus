package me.lkl.dalvikus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import me.lkl.dalvikus.tabManager
import me.lkl.dalvikus.tree.FileNode
import me.lkl.dalvikus.tree.Node
import me.lkl.dalvikus.tree.archive.ZipNode
import me.lkl.dalvikus.tree.backing.FileBacking
import me.lkl.dalvikus.tree.dex.DexFileNode
import me.lkl.dalvikus.tree.filesystem.FileSystemFileNode
import me.lkl.dalvikus.tree.root.HiddenRoot
import me.lkl.dalvikus.ui.tree.FileSelectorDialog
import me.lkl.dalvikus.ui.tree.TreeView
import org.jetbrains.compose.resources.stringResource

val editableFiles = listOf("apk", "apks", "aab", "jar", "zip", "xapk", "dex", "odex")

var showTreeAddFileDialog by mutableStateOf(false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LeftPanelContent() {
    val scope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()
    val searchQuery = remember { mutableStateOf("") }

    if (showTreeAddFileDialog) {
        FileSelectorDialog(
            title = stringResource(Res.string.dialog_select_android_archive_title),
            message = stringResource(Res.string.dialog_select_android_archive_message),
            filePredicate = { it is FileSystemFileNode && !it.file.isDirectory && it.file.extension in editableFiles },
            onDismissRequest = {
                showTreeAddFileDialog = false
            }) { node ->
            if (node !is FileSystemFileNode) return@FileSelectorDialog
            when (node.file.extension.lowercase()) {
                "apk", "apks", "aab", "jar", "zip", "xapk" -> uiTreeRoot.addChild(
                    ZipNode(
                        node.name,
                        node.file,
                        uiTreeRoot
                    )
                )

                "dex", "odex" -> uiTreeRoot.addChild(DexFileNode(
                    node.name,
                    FileBacking(node.file),
                    uiTreeRoot
                ))
                else -> throw AssertionError("Unsupported file type: ${node.file.extension} not in $editableFiles")
            }
            showTreeAddFileDialog = false
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopSearchBar(
                state = searchBarState,
                inputField = {
                    TextField(
                        value = searchQuery.value,
                        onValueChange = { searchQuery.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                stringResource(Res.string.tree_search_placeholder),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        trailingIcon = {
                            if (searchQuery.value.isNotEmpty()) {
                                IconButton(onClick = { searchQuery.value = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors()
                    )
                },
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 4.dp,
                shadowElevation = 0.dp,
                windowInsets = SearchBarDefaults.windowInsets,
                modifier = Modifier.fillMaxWidth(),
                scrollBehavior = null
            )
        },

        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showTreeAddFileDialog = true },
                icon = {
                    Icon(Icons.Default.FolderOpen, contentDescription = stringResource(Res.string.fab_load_file))
                },
                text = {
                    Text(stringResource(Res.string.fab_load_file))
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TreeView(
                uiTreeRoot,
                onFileSelected = { node ->
                    currentSelection = node

                    if (node is FileNode) {
                        scope.launch {
                            val newTab = node.createTab()
                            tabManager.addOrSelectTab(newTab)
                        }
                    }
                }, selectedElement = currentSelection
            )
        }
    }
}

internal val uiTreeRoot: HiddenRoot = HiddenRoot()
internal var currentSelection by mutableStateOf<Node?>(null)