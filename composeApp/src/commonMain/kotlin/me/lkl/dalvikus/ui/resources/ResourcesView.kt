package me.lkl.dalvikus.ui.resources

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.all_elements
import dalvikus.composeapp.generated.resources.resources_incomplete_hint
import dalvikus.composeapp.generated.resources.resources_search_placeholder
import io.github.composegears.valkyrie.MatchCase
import io.github.composegears.valkyrie.RegularExpression
import me.lkl.dalvikus.tree.archive.ApkNode
import me.lkl.dalvikus.ui.uiTreeRoot
import me.lkl.dalvikus.util.CollapseCard
import me.lkl.dalvikus.util.CollapseCardMaxWidth
import me.lkl.dalvikus.util.SearchOptions
import me.lkl.dalvikus.util.createSearchMatcher
import me.lkl.dalvikus.util.to0xHex
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesView() {
    val scope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()
    val searchFieldState = rememberTextFieldState()
    var searchOptions by remember { mutableStateOf(SearchOptions()) }

    val searchField =
        @Composable {
            SearchBarDefaults.InputField(
                searchBarState = searchBarState,
                textFieldState = searchFieldState,
                onSearch = {},
                placeholder = {
                    Text(
                        stringResource(Res.string.resources_search_placeholder),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.FilterAlt, contentDescription = null)
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
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                scrollBehavior = null
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            ApkResourceCards(searchFieldState, searchOptions)
        }
    }
}

val resourceTypesIcons = mapOf(
    // ordered by importance
    "string" to Icons.Outlined.TextFields,
    "plurals" to Icons.Outlined.FormatListNumbered,
    "layout" to Icons.Outlined.ViewAgenda,
    "id" to Icons.Outlined.Badge,
    "raw" to Icons.AutoMirrored.Outlined.InsertDriveFile,
    "drawable" to Icons.Outlined.Image,
    "color" to Icons.Outlined.Palette,
    "bool" to Icons.Outlined.CheckBox,
    "integer" to Icons.Outlined.Dialpad,
    "array" to Icons.AutoMirrored.Outlined.ViewList,

   /* "anim" to Icons.Outlined.PlayArrow,
    "animator" to Icons.Outlined.Movie,
    "attr" to Icons.Outlined.Tune,
    "dimen" to Icons.Outlined.FormatListNumbered,
    "menu" to Icons.Outlined.Menu,
    "mipmap" to Icons.Outlined.Apps,

    "style" to Icons.Outlined.Style,
    "xml" to Icons.Outlined.Code,
    "font" to Icons.Outlined.FontDownload, */
)



@Composable
private fun ApkResourceCards(searchFieldState: TextFieldState, searchOptions: SearchOptions) {
    val treeRootChildren by uiTreeRoot.childrenFlow.collectAsState()
    val apks = treeRootChildren.filterIsInstance<ApkNode>()

    val gridState = rememberLazyGridState()
    var selectedResType by remember { mutableStateOf("all") }

    val searchMatcher by remember(searchFieldState.text, searchOptions) {
        derivedStateOf { createSearchMatcher(searchFieldState.text.toString().replace(" ", ""), searchOptions) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            FilterChip(
                // TODO remove elevation = null when https://youtrack.jetbrains.com/issue/CMP-2868 is fixed.
                elevation = null,
                selected = selectedResType == "all",
                onClick = { selectedResType = "all" },
                label = { Text(stringResource(Res.string.all_elements)) }
            )
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
            Text(
                text = stringResource(Res.string.resources_incomplete_hint),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = CollapseCardMaxWidth),
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
                                        && (searchMatcher(resourceSpec.name) || searchMatcher(resourceSpec.id.toLong().toString()) || searchMatcher(resourceSpec.id.toLong().to0xHex()))
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
                                        val resIdPkg = String.format("%02x", resId.packageId)
                                        val resIdTypeId = String.format("%02x", resId.type)
                                        val resIdEntryNumber = String.format("%04x", resId.entry)

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
