package me.lkl.dalvikus.ui

import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
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
import kotlinx.coroutines.launch
import me.lkl.dalvikus.LocalSnackbarManager
import me.lkl.dalvikus.tabManager
import me.lkl.dalvikus.tree.*
import me.lkl.dalvikus.tree.filesystem.FileSystemFileNode
import me.lkl.dalvikus.tree.root.HiddenRoot
import me.lkl.dalvikus.errorreport.crtExHandler
import me.lkl.dalvikus.selectedNavItem
import me.lkl.dalvikus.ui.search.SearchResults
import me.lkl.dalvikus.ui.snackbar.SnackbarManager
import me.lkl.dalvikus.ui.tree.FileSelectorDialog
import me.lkl.dalvikus.ui.tree.TreeDragAndDropTarget
import me.lkl.dalvikus.ui.tree.TreeView
import me.lkl.dalvikus.util.SearchOptions
import org.jetbrains.compose.resources.stringResource
import java.io.File

val editableFiles = listOf("apk", "dex", "odex", "apks", "aab", "jar", "zip", "xapk")

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
    val snackbarManager = LocalSnackbarManager.current
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
            addFileToTree(node.file, snackbarManager, unsupportedFileText)
            showTreeAddFileDialog = false
        }
    }
    val scope = rememberCoroutineScope()
    val dragAndDropTarget = remember(snackbarManager) { TreeDragAndDropTarget(snackbarManager, unsupportedFileText) }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showTreeAddFileDialog = true },
                icon = {
                    Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.fab_load_file))
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
                        stringResource(Res.string.tree_drop_placeholder, editableFiles.joinToString(", ")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(16.dp)
                    )
                }
                return@Scaffold
            }
            TreeView(
                uiTreeRoot,
                onFileSelected = { node ->
                    currentSelection = node

                    if (node is FileNode) {

                        if(node.name.equals("resources.arsc", true)) {
                            selectedNavItem = "Resources"
                            return@TreeView
                        }

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

fun addFileToTree(file: File, snackbarManager: SnackbarManager, unsupportedFileText: String) {
    val node = NodeFactory.createNode(file, uiTreeRoot)
    if (node !is FileSystemFileNode) {
        uiTreeRoot.addChild(node)
    } else {
        snackbarManager.showMessage(unsupportedFileText)
    }
}

fun selectFileTreeNode(node: Node) {
    currentSelection = node
    scrollAndExpandSelection.value = true
}