package me.lkl.dalvikus.ui.tree

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.tree_field_count
import dalvikus.composeapp.generated.resources.tree_method_count
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.lkl.dalvikus.LocalSnackbarManager
import me.lkl.dalvikus.errorreport.crtExHandler
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.tree.Node
import me.lkl.dalvikus.tree.Metadata
import me.lkl.dalvikus.tree.root.HiddenRoot
import me.lkl.dalvikus.util.formatFileDate
import me.lkl.dalvikus.util.formatFileSize
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TreeView(
    root: HiddenRoot,
    modifier: Modifier = Modifier,
    onFileSelected: ((Node) -> Unit)? = null,
    selectedElement: Node? = null,
    scrollAndExpandSelection: MutableState<Boolean> = mutableStateOf(false)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    val expandedState = remember { mutableStateMapOf<Node, Boolean>() }

    val rootChildren by root.childrenFlow.collectAsState()
    val visibleNodes = mutableListOf<Pair<Node, Int>>()

    @Composable
    fun visit(node: Node, indent: Int) {
        visibleNodes.add(node to indent)

        val isExpanded = expandedState[node] == true
        if (node is ContainerNode && isExpanded) {
            val children = node.childrenFlow.collectAsState()
            children.value.forEach { child ->
                visit(child, indent + if (node.isRoot) 0 else 1)
            }
        }
    }

    @Composable
    fun recomputeVisibleNodes() {
        visibleNodes.clear()
        rootChildren.forEach { visit(it, 0) }
    }

    if (scrollAndExpandSelection.value) {
        selectedElement?.let { selected ->
            var parent: Node? = selected
            while (parent != null) {
                expandedState[parent] = true
                parent = parent.parent
            }
            recomputeVisibleNodes()
            val index = visibleNodes.indexOfFirst { it.first == selected }
            if (index >= 0) {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(index)
                }
            }
        }
        scrollAndExpandSelection.value = false
    }

    recomputeVisibleNodes()

    val snackbarManager = LocalSnackbarManager.current

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

                        if (node is ContainerNode) {
                            if (shouldExpand) {
                                try {
                                    node.loadChildren()
                                } catch (ex: Exception) {
                                    Logger.e("Failed to load file", ex)
                                    snackbarManager.showError(ex)
                                }
                            }
                            expandedState[node] = shouldExpand
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
/*
@Composable
private fun TreeRow(
    node: Node,
    indent: Int,
    isExpanded: Boolean,
    onToggleExpand: suspend (Boolean) -> Unit,
    onClick: () -> Unit,
    selected: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val isContainer = node is ContainerNode

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
                    coroutineScope.launch(Dispatchers.IO + crtExHandler) {
                        onToggleExpand(!isExpanded)
                    }
                }
            }
            .padding(start = (4 + indent * 16).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
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


*/

@Composable
private fun TreeRow(
    node: Node,
    indent: Int,
    isExpanded: Boolean,
    onToggleExpand: suspend (Boolean) -> Unit,
    onClick: () -> Unit,
    selected: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val isContainer = node is ContainerNode

    val backgroundModifier = if (selected) {
        Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
    } else {
        Modifier
    }

    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = backgroundModifier
            .fillMaxWidth()
            .clickable {
                onClick()
                if (isContainer) {
                    coroutineScope.launch(Dispatchers.IO + crtExHandler) {
                        onToggleExpand(!isExpanded)
                    }
                }
            }
            .padding(start = (4 + indent * 16).dp),
        leadingContent = {
            if (isContainer) {
                Icon(
                    imageVector = if (isExpanded) Icons.Outlined.ExpandMore else Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
        },
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = node.icon,
                    contentDescription = node.name,
                    tint = node.color ?: MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = node.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    color = node.color ?: MaterialTheme.colorScheme.onSurface
                )
            }
        },
        supportingContent = {
            val metadataStrings = node.getMetadata().map { (label, value) ->
                when (label) {
                    Metadata.FILE_SIZE -> formatFileSize(value as Long)
                    Metadata.LAST_EDITED -> formatFileDate(value as Long)
                    Metadata.METHOD_COUNT -> pluralStringResource(Res.plurals.tree_method_count, value as Int, value.toString())
                    Metadata.FIELD_COUNT -> pluralStringResource(Res.plurals.tree_field_count, value as Int, value.toString())
                }
            }

            val metadataText = metadataStrings.joinToString(separator = ", ")

            if (metadataText.isNotEmpty()) {
                Text(
                    text = metadataText,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    )
}
