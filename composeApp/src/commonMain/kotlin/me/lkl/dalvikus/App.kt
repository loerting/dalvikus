package me.lkl.dalvikus

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.app_description
import dalvikus.composeapp.generated.resources.app_name
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tabs.WelcomeTab
import me.lkl.dalvikus.theme.AppTheme
import me.lkl.dalvikus.theme.LocalThemeIsDark
import me.lkl.dalvikus.ui.LeftPanelContent
import me.lkl.dalvikus.ui.RightPanelContent
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun App() = AppTheme {
    Scaffold(
        topBar = {
            TopBar()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Ensures content doesn't go under the app bar
        ) {
            Content()
        }
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
internal fun Content() {
    val splitPaneState = rememberSplitPaneState(0.25f)
    val tabState = remember {
        mutableStateOf(listOf<TabElement>(WelcomeTab()))
    }

    HorizontalSplitPane(
        modifier = Modifier.fillMaxSize(),
        splitPaneState = splitPaneState,
    ) {
        first(minSize = 200.dp) {
            OutlinedCard(
                modifier = Modifier.fillMaxSize().padding(8.dp),
            ) {
                LeftPanelContent(tabState)
            }
        }
        second(minSize = 200.dp) {
            OutlinedCard(
                modifier = Modifier.fillMaxSize().padding(8.dp),
            ) {
                RightPanelContent(tabState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        title = {
            val infiniteTransition = rememberInfiniteTransition()
            val offsetY by infiniteTransition.animateFloat(
                initialValue = -1f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AutoFixHigh,
                        contentDescription = stringResource(Res.string.app_name),
                        modifier = Modifier.graphicsLayer {
                            translationY = offsetY
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.app_name)
                    )
                }
                Text(
                    text = stringResource(Res.string.app_description),
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        navigationIcon = {
            val isDarkState = LocalThemeIsDark.current
            IconButton(onClick = { isDarkState.value = !isDarkState.value }) {
                Icon(
                    imageVector = if (isDarkState.value) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: Handle favorite click */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Favorite"
                )
            }
            IconButton(onClick = { /* TODO: Handle share click */ }) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
    )

}
