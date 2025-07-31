package me.lkl.dalvikus.ui.resources

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.lkl.dalvikus.tree.archive.ApkNode
import me.lkl.dalvikus.ui.uiTreeRoot
import me.lkl.dalvikus.util.CollapseCard

@Composable
fun ResourcesView() {
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            ApkResourceCards()
        }
    }
}

val resourceTypesIcons = mapOf(
    "all" to Icons.Outlined.Android,

    "anim" to Icons.Outlined.PlayArrow,
    "animator" to Icons.Outlined.Movie,
    "attr" to Icons.Outlined.Tune,
    "array" to Icons.Outlined.ViewList,
    "bool" to Icons.Outlined.CheckBox,
    "color" to Icons.Outlined.Palette,
    "dimen" to Icons.Outlined.FormatListNumbered,
    "drawable" to Icons.Outlined.Image,
    "integer" to Icons.Outlined.Dialpad,
    "layout" to Icons.Outlined.ViewAgenda,
    "menu" to Icons.Outlined.Menu,
    "mipmap" to Icons.Outlined.Apps,
    "plurals" to Icons.Outlined.FormatListNumbered,
    "raw" to Icons.Outlined.InsertDriveFile,
    "string" to Icons.Outlined.TextFields,
    "style" to Icons.Outlined.Style,
    "xml" to Icons.Outlined.Code,
    "font" to Icons.Outlined.FontDownload,
    "id" to Icons.Outlined.Badge,
)

@Composable
private fun ApkResourceCards() {
    // TODO add search bar for resource ids in base 16, base 10, and resource name.
    val treeRootChildren by uiTreeRoot.childrenFlow.collectAsState()
    val apks = treeRootChildren.filterIsInstance<ApkNode>()

    val gridState = rememberLazyGridState()
    var selectedResType by remember { mutableStateOf("all") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Filter chips row
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            resourceTypesIcons.forEach { (type, icon) ->
                FilterChip(
                    // TODO remove elevation = null when https://youtrack.jetbrains.com/issue/CMP-2868 is fixed.
                    elevation = null,
                    selected = selectedResType == type,
                    onClick = { selectedResType = type },
                    leadingIcon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = type
                        )
                    },
                    label = { Text(type) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 800.dp),
                state = gridState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 12.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                apks.forEach { apk ->
                    item {
                        CollapseCard(
                            title = apk.name,
                            icon = Icons.Outlined.Android,
                            defaultState = false
                        ) {
                            val resSpecList = apk.getResourceSpecs()
                            if (resSpecList == null) return@CollapseCard
                            val resourceSpecs = resSpecList.filter { resourceSpec ->
                                resourceSpec != null && (selectedResType == "all" || resourceSpec.type.name == selectedResType)
                            }

                            val innerListState = rememberLazyListState()

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 400.dp)
                            ) {
                                LazyColumn(
                                    state = innerListState,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(resourceSpecs.size) { index ->
                                        val resourceSpec = resourceSpecs[index]
                                        val resId = resourceSpec!!.id
                                        val resIdPkg = String.format("%02X", resId.packageId)
                                        val resIdTypeId = String.format("%02X", resId.type)
                                        val resIdEntryNumber = String.format("%04X", resId.entry)

                                        ListItem(
                                            headlineContent = {
                                                SelectionContainer {
                                                    Text(
                                                        resourceSpec.name,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            },
                                            overlineContent = {
                                                SelectionContainer {
                                                    Text(
                                                        "${resIdPkg} ${resIdTypeId} ${resIdEntryNumber}",
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            },
                                            trailingContent = {
                                                Text(resourceSpec.type.name)
                                            },
                                            leadingContent = {
                                                Icon(
                                                    resourceTypesIcons[resourceSpec.type.name]
                                                        ?: Icons.Default.QuestionMark, contentDescription = null
                                                )
                                            },
                                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 4.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                VerticalScrollbar(
                                    adapter = rememberScrollbarAdapter(innerListState),
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .fillMaxHeight()
                                        .padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState = gridState),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp, horizontal = 8.dp)
            )
        }
    }
}
