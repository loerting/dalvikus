package me.lkl.dalvikus.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.*
import io.github.composegears.valkyrie.MatchCase
import io.github.composegears.valkyrie.RegularExpression
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import me.lkl.dalvikus.snackbarManager
import me.lkl.dalvikus.tabManager
import me.lkl.dalvikus.tree.*
import me.lkl.dalvikus.tree.filesystem.FileSystemFileNode
import me.lkl.dalvikus.tree.root.HiddenRoot
import me.lkl.dalvikus.errorreport.crtExHandler
import me.lkl.dalvikus.ui.tree.FileSelectorDialog
import me.lkl.dalvikus.ui.tree.TreeDragAndDropTarget
import me.lkl.dalvikus.ui.tree.TreeView
import me.lkl.dalvikus.util.SearchOptions
import org.jetbrains.compose.resources.stringResource
import java.io.File

val editableFiles = listOf("apk", "apks", "aab", "jar", "zip", "xapk", "dex", "odex")

var showTreeAddFileDialog by mutableStateOf(false)

internal val uiTreeRoot: HiddenRoot = HiddenRoot(
    /*ApkNode(
        "sample.apk",
        File("/home/admin/Downloads/sample.apk"), null
    )*/
)
internal var currentSelection by mutableStateOf<Node?>(null)
internal var scrollAndExpandSelection = mutableStateOf(false)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
internal fun LeftPanelContent() {
    val unsupportedFileText = stringResource(Res.string.tree_unsupported_file_type)
    if (showTreeAddFileDialog) {
        FileSelectorDialog(
            title = stringResource(Res.string.dialog_select_android_archive_title),
            message = stringResource(Res.string.dialog_select_android_archive_message),
            filePredicate = { it is FileSystemFileNode && !it.file.isDirectory && it.file.extension in editableFiles },
            onDismissRequest = {
                showTreeAddFileDialog = false
            }) { node ->
            if (node !is FileSystemFileNode) return@FileSelectorDialog
            addFileToTree(node.file, unsupportedFileText)
            showTreeAddFileDialog = false
        }
    }

    val scope = rememberCoroutineScope()

    val searchBarState = rememberSearchBarState()
    val searchFieldState = rememberTextFieldState()
    var searchOptions by remember { mutableStateOf(SearchOptions()) }

    val searchField =
        @Composable {
            SearchBarDefaults.InputField(
                searchBarState = searchBarState,
                textFieldState = searchFieldState,
                onSearch = {
                    scope.launch { searchBarState.animateToCollapsed() }
                },
                placeholder = {
                    Text(
                        stringResource(Res.string.tree_search_placeholder),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {
                        IconButton(
                            onClick = { scope.launch { searchBarState.animateToCollapsed() } }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back",
                            )
                        }

                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
                trailingIcon = {
                    Row {
                        if (searchFieldState.text.isNotEmpty()) {
                            IconToggleButton(
                                checked = searchOptions.useRegex,
                                onCheckedChange = { searchOptions = searchOptions.copy(useRegex = it) }) {
                                Icon(Icons.Filled.RegularExpression, contentDescription = "Regular expression")
                            }
                            IconToggleButton(
                                checked = searchOptions.caseSensitive,
                                onCheckedChange = { searchOptions = searchOptions.copy(caseSensitive = it) }) {
                                Icon(Icons.Filled.MatchCase, contentDescription = "Case sensitive")
                            }
                            IconButton(onClick = {
                                searchFieldState.setTextAndPlaceCursorAtEnd("")
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    }
                }
            )
        }

    val dragAndDropTarget = remember { TreeDragAndDropTarget(unsupportedFileText) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopSearchBar(
                state = searchBarState,
                inputField = searchField,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 4.dp,
                shadowElevation = 0.dp,
                windowInsets = SearchBarDefaults.windowInsets,
                modifier = Modifier.fillMaxWidth(),
                scrollBehavior = null
            )
            ExpandedFullScreenSearchBar(state = searchBarState, inputField = searchField) {
                SearchResults(
                    searchFieldState,
                    searchOptions,
                    onResultClick = { result ->
                        currentSelection = result
                        scrollAndExpandSelection.value = true
                        searchFieldState.setTextAndPlaceCursorAtEnd("")
                        scope.launch { searchBarState.animateToCollapsed() }
                    }
                )
            }
        },

        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showTreeAddFileDialog = true },
                icon = {
                    Icon(Icons.Default.FolderOpen, contentDescription = stringResource(Res.string.fab_load_file))
                },
                text = {
                    Text(stringResource(Res.string.fab_load_file), maxLines = 1)
                }
            )
        }
    ) { innerPadding ->
        val rootKids = uiTreeRoot.childrenFlow.collectAsState()
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { true },
                    target = dragAndDropTarget
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (rootKids.value.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(Res.string.tree_drop_placeholder),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
                return@Scaffold
            }
            TreeView(
                uiTreeRoot,
                onFileSelected = { node ->
                    currentSelection = node

                    if (node is FileNode) {
                        scope.launch(crtExHandler) {
                            val newTab = node.createTab()
                            tabManager.addOrSelectTab(newTab)
                        }
                    }
                },
                selectedElement = currentSelection,
                scrollAndExpandSelection = scrollAndExpandSelection
            )
        }
    }
}

fun addFileToTree(file: File, unsupportedFileText: String) {
    val node = NodeFactory.createNode(file, uiTreeRoot)
    if (node !is FileSystemFileNode) {
        uiTreeRoot.addChild(node)
    } else {
        snackbarManager?.showMessage(unsupportedFileText)
    }
}

@Composable
private fun SearchResults(
    searchFieldState: TextFieldState,
    searchOptions: SearchOptions,
    onResultClick: (Node) -> Unit,
    modifier: Modifier = Modifier
) {
    val query = searchFieldState.text
    var results by remember { mutableStateOf<List<TreeSearchResult>>(emptyList()) }

    val allTypes = TreeSearchResultType.entries.toSet()
    var selectedTypes by remember { mutableStateOf(allTypes) }

    // Trigger search when query changes
    LaunchedEffect(query, searchOptions, selectedTypes) {
        results = emptyList()
        if (query.isNotBlank()) {
            searchTreeBFS(uiTreeRoot, query as String, searchOptions)
                .filter { selectedTypes.contains(it.type) }
                .take(100)
                .collect { match ->
                    results = results + match
                }
        }
    }

    @Composable
    fun getResultTypeName(type: TreeSearchResultType): String = stringResource(
        when (type) {
            TreeSearchResultType.TREE_NODE -> Res.string.tree_search_result_type_node
            TreeSearchResultType.STRING_VALUE -> Res.string.tree_search_result_type_string
            TreeSearchResultType.REFERENCE -> Res.string.tree_search_result_type_reference
            TreeSearchResultType.LITERAL -> Res.string.tree_search_result_type_literal
        }
    )

    Column(modifier.verticalScroll(rememberScrollState())) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allTypes.forEach { type ->
                val label = getResultTypeName(type)
                FilterChip(
                    elevation = null, // TODO remove elevation when https://youtrack.jetbrains.com/issue/CMP-2868 is fixed.
                    selected = selectedTypes.contains(type),
                    onClick = {
                        selectedTypes = if (selectedTypes.contains(type)) {
                            selectedTypes - type
                        } else {
                            selectedTypes + type
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = type.icon,
                            contentDescription = label
                        )
                    },
                    label = { Text(label) }
                )
            }

        }

        for (result in results) {
            val node = result.node
            val type = getResultTypeName(result.type)
            ListItem(
                headlineContent = {
                    Text(
                        result.snippet,
                        color = when (result.type) {
                            TreeSearchResultType.TREE_NODE -> MaterialTheme.colorScheme.onSurface
                            TreeSearchResultType.STRING_VALUE -> MaterialTheme.colorScheme.primary
                            TreeSearchResultType.REFERENCE -> MaterialTheme.colorScheme.secondary
                            TreeSearchResultType.LITERAL -> MaterialTheme.colorScheme.tertiary
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                trailingContent = {
                    Text(
                        type,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                overlineContent = {
                    Text(
                        result.path,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingContent = { Icon(result.icon, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier
                    .clickable { onResultClick(node) }
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        if (results.isEmpty() && query.isNotBlank()) {
            Text(
                stringResource(Res.string.tree_search_no_results, query),
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun selectFileTreeNode(node: Node) {
    currentSelection = node
    scrollAndExpandSelection.value = true
}