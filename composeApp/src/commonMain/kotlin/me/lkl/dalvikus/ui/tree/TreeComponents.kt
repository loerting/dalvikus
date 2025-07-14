package me.lkl.dalvikus.ui.tree

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TreeView(
    root: TreeElement,
    modifier: Modifier = Modifier
) {

    val childrenCache = remember { mutableMapOf<TreeElement, List<TreeElement>>() }
    val expandedState = remember { mutableStateMapOf<TreeElement, Boolean>() }
    val coroutineScope = rememberCoroutineScope()
    fun getVisibleNodes(): List<Pair<TreeElement, Int>> {
        val result = mutableListOf<Pair<TreeElement, Int>>()
        fun visit(node: TreeElement, indent: Int) {
            result.add(node to indent)
            if (expandedState[node] == true) {
                childrenCache[node]?.forEach { child ->
                    visit(child, indent + 1)
                }
            }
        }
        visit(root, 0)
        return result
    }

    val scrollState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize()
        ) {
            val visibleNodes = getVisibleNodes()
            itemsIndexed(
                items = visibleNodes,
            ) { _, (node, indent) ->
                TreeRow(node, indent, expandedState, childrenCache, coroutineScope)
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.align(Alignment.CenterEnd).padding(8.dp),
            style = LocalScrollbarStyle.current.copy(
                minimalHeight = 24.dp,
                thickness = 12.dp,
                unhoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
                hoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.50f)
            )
        )
    }
}

@Composable
private fun TreeRow(
    node: TreeElement,
    indent: Int,
    expandedState: SnapshotStateMap<TreeElement, Boolean>,
    childrenCache: MutableMap<TreeElement, List<TreeElement>>,
    coroutineScope: CoroutineScope
) {
    var loading by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .requiredHeight(48.dp)
            .fillMaxWidth()
            .clickable(enabled = !loading && node.isContainer) {
                val currentlyExpanded = expandedState[node] ?: false
                if (!currentlyExpanded) {
                    if (childrenCache[node] == null) {
                        loading = true
                        coroutineScope.launch {
                            val children = withContext(Dispatchers.IO) { node.getChildren() }
                            childrenCache[node] = children
                            expandedState[node] = true
                            loading = false
                        }
                    } else {
                        expandedState[node] = true
                    }
                } else {
                    expandedState[node] = false
                }
            }
            .padding(start = (4 + indent * 16).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            loading -> {
                Spacer(modifier = Modifier.size(4.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            node.isContainer -> {
                Icon(
                    imageVector = if (expandedState[node] == true) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                    contentDescription = if (expandedState[node] == true) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            else -> {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = node.icon,
            contentDescription = node.name,
            tint = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = node.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

fun IconForFileExtension(fileName: String): ImageVector {
    val extension = fileName.substringAfterLast('.', "").lowercase()

    return when (extension) {
        "txt", "md", "log" -> Icons.Default.Description
        "jpg", "jpeg", "png", "gif", "bmp", "webp" -> Icons.Default.Image
        "mp3", "wav", "ogg", "flac" -> Icons.Default.MusicNote
        "mp4", "avi", "mov", "mkv", "webm" -> Icons.Default.Movie
        "pdf" -> Icons.Default.PictureAsPdf
        "zip", "rar", "7z", "tar", "gz" -> Icons.Default.Archive
        "doc", "docx" -> Icons.Default.Article
        "xls", "xlsx" -> Icons.Default.TableChart
        "ppt", "pptx" -> Icons.Default.Slideshow
        "html", "xml", "json", "yaml", "yml" -> Icons.Default.Code
        "apk", "dex" -> Icons.Default.Android
        else -> Icons.Default.Description
    }
}
