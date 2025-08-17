package me.lkl.dalvikus.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.getSelectedText
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.editor_search_no_results
import dalvikus.composeapp.generated.resources.editor_search_placeholder
import dalvikus.composeapp.generated.resources.editor_search_results_found
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import io.github.composegears.valkyrie.MatchCase
import io.github.composegears.valkyrie.RegularExpression
import me.lkl.dalvikus.util.defaultHazeStyle
import org.jetbrains.compose.resources.stringResource

enum class SearchDirection {
    NEXT,
    PREVIOUS
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun EditorSearchOverlay(
    hazeState: HazeState,
    viewModel: EditorViewModel,
    onSearch: (CharSequence, SearchDirection) -> Int
) {
    val searchBarState = rememberSearchBarState()
    val searchFieldState = rememberTextFieldState()

    var resultsFound by mutableStateOf(0)

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(viewModel.isSearchActive) {
        searchFieldState.setTextAndPlaceCursorAtEnd(
            viewModel.internalContent.getSelectedText().text
        )

        focusRequester.requestFocus()
    }

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .hazeEffect(
                state = hazeState,
                style = defaultHazeStyle(MaterialTheme.colorScheme.surfaceContainerHigh)
            )
            .wrapContentHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp)
        ) {
            // Search field
            SearchBarDefaults.InputField(
                colors = SearchBarDefaults.inputFieldColors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                ),
                modifier = Modifier
                    .weight(0.9f)
                    .focusRequester(focusRequester),
                searchBarState = searchBarState,
                textFieldState = searchFieldState,
                onSearch = {
                    resultsFound = onSearch(searchFieldState.text, SearchDirection.NEXT)
                },
                placeholder = {
                    Text(
                        stringResource(Res.string.editor_search_placeholder),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    Row {
                        if (searchFieldState.text.isNotEmpty()) {
                            IconToggleButton(
                                checked = viewModel.searchOptions.useRegex,
                                onCheckedChange = {
                                    viewModel.searchOptions =
                                        viewModel.searchOptions.copy(useRegex = it)
                                }
                            ) {
                                Icon(Icons.Filled.RegularExpression, contentDescription = "Regex")
                            }
                            IconToggleButton(
                                checked = viewModel.searchOptions.caseSensitive,
                                onCheckedChange = {
                                    viewModel.searchOptions =
                                        viewModel.searchOptions.copy(caseSensitive = it)
                                }
                            ) {
                                Icon(Icons.Filled.MatchCase, contentDescription = "Case")
                            }
                            IconButton(onClick = {
                                resultsFound = 0
                                searchFieldState.setTextAndPlaceCursorAtEnd("")
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    }
                }
            )

            // Search direction buttons
            IconButton(
                onClick = {
                    resultsFound = onSearch(searchFieldState.text, SearchDirection.PREVIOUS)
                },
                enabled = searchFieldState.text.isNotEmpty()
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Previous")
            }
            IconButton(
                onClick = {
                    resultsFound = onSearch(searchFieldState.text, SearchDirection.NEXT)
                },
                enabled = searchFieldState.text.isNotEmpty()
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next")
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (resultsFound > 0) {
                Text(
                    stringResource(
                        Res.string.editor_search_results_found,
                        resultsFound
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            } else {
                Text(
                    stringResource(Res.string.editor_search_no_results),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.weight(0.1f))
            IconButton(onClick = {
                viewModel.isSearchActive = false
            }) {
                Icon(Icons.Default.Close, contentDescription = "Close search")
            }
        }
    }
}