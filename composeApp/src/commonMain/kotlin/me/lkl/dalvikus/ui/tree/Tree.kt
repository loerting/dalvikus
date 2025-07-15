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
import androidx.compose.material.icons.filled.*
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.theme.*
import me.lkl.dalvikus.tree.TreeElement

@Composable
fun TreeView(
    root: TreeElement,
    tabState: MutableState<List<TabElement>>,
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
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
        ) {
            val visibleNodes = getVisibleNodes()
            itemsIndexed(
                items = visibleNodes,
            ) { _, (node, indent) ->
                TreeRow(node, indent, expandedState, childrenCache, coroutineScope, tabState)
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
    coroutineScope: CoroutineScope,
    tabState: MutableState<List<TabElement>>
) {
    var loading by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .requiredHeight(48.dp)
            .fillMaxWidth()
            .clickable(enabled = !loading) {
                if (node.isContainer) {
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
                } else {
                    var newTab = node.createTab()
                    if (newTab !in tabState.value) {
                        tabState.value += newTab
                    }
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
                    contentDescription = null,
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
            tint = node.getColor()
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = node.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = node.getColor()
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
        "doc", "docx" -> Icons.AutoMirrored.Filled.Article
        "xls", "xlsx" -> Icons.Default.TableChart
        "ppt", "pptx" -> Icons.Default.Slideshow
        "html", "xml", "json", "yaml", "yml" -> Icons.Default.Code
        "apk", "dex" -> Icons.Default.Android
        else -> Icons.Default.Description
    }
}

@Composable
fun ColorForFileExtension(fileName: String): Color {
    val extension = fileName.substringAfterLast('.', "").lowercase()

    return when (extension) {
        "apk", "dex" -> AndroidGreen
        "zip", "rar", "7z", "tar", "gz" -> ArchiveOrange
        "html", "xml", "json", "yaml", "yml" -> CodeBlue
        "txt", "md", "log" -> MaterialTheme.colorScheme.onSurface
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg" -> ImagePurple
        "mp3", "wav", "flac", "aac", "ogg" -> AudioTeal
        "mp4", "avi", "mkv", "mov", "webm" -> VideoRed
        "pdf" -> PdfRed
        "doc", "docx" -> WordBlue
        "xls", "xlsx" -> ExcelGreen
        "ppt", "pptx" -> PowerPointOrange
        else -> MaterialTheme.colorScheme.onSurface
    }
}