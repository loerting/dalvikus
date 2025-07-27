package me.lkl.dalvikus.ui.tree

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.lkl.dalvikus.theme.*
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.Node
import me.lkl.dalvikus.tree.root.HiddenRoot
import me.lkl.dalvikus.ui.editableFiles

@Composable
fun TreeView(
    root: HiddenRoot,
    modifier: Modifier = Modifier,
    onFileSelected: ((Node) -> Unit)? = null,
    selectedElement: Node? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    val expandedState = remember { mutableStateMapOf<Node, Boolean>() }
    val rootChildren by root.childrenFlow.collectAsState()

    val visibleNodes by remember(rootChildren, expandedState) {
        derivedStateOf {
            val result = mutableListOf<Pair<Node, Int>>()

            fun visit(node: Node, indent: Int) {
                result.add(node to indent)

                val isExpanded = expandedState[node] == true
                if (node is ContainerNode && isExpanded) {
                    val children = node.childrenFlow.value
                    children.forEach { child ->
                        visit(child, indent + if (node.isRoot) 0 else 1)
                    }
                }
            }

            rootChildren.forEach { visit(it, 0) }

            result
        }
    }


    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(visibleNodes) { (node, indent) ->
                TreeRow(
                    node = node,
                    indent = indent,
                    isExpanded = expandedState[node] == true,
                    onToggleExpand = { shouldExpand ->
                        coroutineScope.launch {
                            if (node is ContainerNode) {
                                if (shouldExpand) {
                                    try {
                                        node.loadChildren()
                                    } catch(ex: Exception) {
                                        Logger.e("Failed to load file", ex)
                                    }
                                }
                                expandedState[node] = shouldExpand
                            }
                        }
                    },
                    onClick = { onFileSelected?.invoke(node) },
                    selected = (node == selectedElement)
                )
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.align(Alignment.CenterEnd).padding(8.dp),
            style = LocalScrollbarStyle.current
        )
    }
}

@Composable
private fun TreeRow(
    node: Node,
    indent: Int,
    isExpanded: Boolean,
    onToggleExpand: (Boolean) -> Unit,
    onClick: () -> Unit,
    selected: Boolean
) {
    val isContainer = node is ContainerNode
    var loading by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .requiredHeight(48.dp)
            .fillMaxWidth()
            .background(
                if (selected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                else
                    Color.Transparent
            )
            .clickable {
                onClick()
                if (isContainer) {
                    loading = true
                    onToggleExpand(!isExpanded)
                    loading = false
                }
            }
            .padding(start = (4 + indent * 16).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            loading -> CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.primary
            )

            isContainer -> Icon(
                imageVector = if (isExpanded) Icons.Outlined.ExpandMore else Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )

            else -> Spacer(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = node.icon,
            contentDescription = node.name,
            tint = node.color ?: MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = node.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge,
            color = node.color ?: MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}


fun IconForFileExtension(fileName: String): ImageVector {
    val extension = fileName.substringAfterLast('.', "").lowercase()

    return when (extension) {
        "txt", "md", "log" -> Icons.Outlined.Description
        "jpg", "jpeg", "png", "gif", "bmp", "webp" -> Icons.Outlined.Image
        "mp3", "wav", "ogg", "flac" -> Icons.Outlined.MusicNote
        "mp4", "avi", "mov", "mkv", "webm" -> Icons.Outlined.Movie
        "pdf" -> Icons.Outlined.PictureAsPdf
        "zip", "jar" -> Icons.Filled.FolderZip
        "doc", "docx" -> Icons.AutoMirrored.Outlined.Article
        "xls", "xlsx" -> Icons.Outlined.TableChart
        "ppt", "pptx" -> Icons.Outlined.Slideshow
        "html", "xml", "json", "yaml", "yml" -> Icons.Outlined.Code
        "apk", "apks", "aab", "xapk", "dex", "odex" -> Icons.Filled.Android
        else -> Icons.Outlined.Description
    }
}

fun ColorForFileExtension(fileName: String): Color? {
    val extension = fileName.substringAfterLast('.', "").lowercase()

    return when (extension) {
        "apk", "dex" -> AndroidGreen
        "zip", "rar", "7z", "tar", "gz" -> ArchiveGray
        "html", "xml", "json", "yaml", "yml" -> CodeBlue
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg" -> ImagePurple
        "mp3", "wav", "flac", "aac", "ogg" -> AudioTeal
        "mp4", "avi", "mkv", "mov", "webm" -> VideoRed
        "pdf" -> PdfRed
        "doc", "docx" -> WordBlue
        "xls", "xlsx" -> ExcelGreen
        "ppt", "pptx" -> PowerPointOrange
        else -> null
    }
}