package me.lkl.dalvikus.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.tree_search_no_results
import dalvikus.composeapp.generated.resources.tree_search_placeholder
import dalvikus.composeapp.generated.resources.tree_search_chip_class_by_parent
import dalvikus.composeapp.generated.resources.tree_search_chip_literal
import dalvikus.composeapp.generated.resources.tree_search_chip_node
import dalvikus.composeapp.generated.resources.tree_search_chip_reference
import dalvikus.composeapp.generated.resources.tree_search_chip_string
import io.github.composegears.valkyrie.MatchCase
import io.github.composegears.valkyrie.RegularExpression
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import me.lkl.dalvikus.tree.Node
import me.lkl.dalvikus.tree.TreeSearchResult
import me.lkl.dalvikus.tree.TreeSearchResultType
import me.lkl.dalvikus.tree.searchTreeBFS
import me.lkl.dalvikus.ui.currentSelection
import me.lkl.dalvikus.ui.scrollAndExpandSelection
import me.lkl.dalvikus.ui.uiTreeRoot
import me.lkl.dalvikus.util.SearchOptions
import org.jetbrains.compose.resources.stringResource
import kotlin.collections.plus

@Composable
fun SearchResults(
    searchFieldState: TextFieldState,
    searchOptions: SearchOptions,
    onResultClick: (Node) -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    val query = searchFieldState.text
    var results by remember { mutableStateOf<List<TreeSearchResult>>(emptyList()) }

    val allTypes = TreeSearchResultType.entries.toSet()
    var selectedTypes by remember { mutableStateOf(setOf(allTypes.first())) }

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
            TreeSearchResultType.TREE_NODE -> Res.string.tree_search_chip_node
            TreeSearchResultType.STRING_VALUE -> Res.string.tree_search_chip_string
            TreeSearchResultType.REFERENCE -> Res.string.tree_search_chip_reference
            TreeSearchResultType.LITERAL -> Res.string.tree_search_chip_literal
            TreeSearchResultType.CLASS_BY_PARENT -> Res.string.tree_search_chip_class_by_parent
        }
    )

    Column(modifier.verticalScroll(rememberScrollState())) {
        FlowRow(
            modifier = Modifier.Companion
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
                            TreeSearchResultType.CLASS_BY_PARENT -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Companion.Ellipsis
                    )
                },
                trailingContent = {
                    Text(
                        type,
                        maxLines = 1,
                        overflow = TextOverflow.Companion.Ellipsis
                    )
                },
                overlineContent = {
                    Text(
                        result.path,
                        maxLines = 1,
                        overflow = TextOverflow.Companion.Ellipsis
                    )
                },
                leadingContent = { Icon(result.icon, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = Color.Companion.Transparent),
                modifier = Modifier.Companion
                    .clickable { onResultClick(node) }
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        if (results.isEmpty() && query.isNotBlank()) {
            Text(
                stringResource(Res.string.tree_search_no_results, query),
                modifier = Modifier.Companion.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    searchBarState: SearchBarState,
) {
    val scope = rememberCoroutineScope()
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
                                onCheckedChange = { searchOptions = SearchOptions(useRegex = it) }) {
                                Icon(Icons.Filled.RegularExpression, contentDescription = "Regular expression")
                            }
                            IconToggleButton(
                                checked = searchOptions.caseSensitive,
                                onCheckedChange = { searchOptions = SearchOptions(caseSensitive = it) }) {
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
}