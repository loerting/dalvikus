package me.lkl.dalvikus

import SettingsScreen
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.Settings
import dalvikus.composeapp.generated.resources.*
import me.lkl.dalvikus.settings.DalvikusSettings
import me.lkl.dalvikus.tabs.WelcomeTab
import me.lkl.dalvikus.theme.AppTheme
import me.lkl.dalvikus.theme.LocalThemeIsDark
import me.lkl.dalvikus.ui.LeftPanelContent
import me.lkl.dalvikus.ui.RightPanelContent
import me.lkl.dalvikus.ui.nav.NavItem
import me.lkl.dalvikus.ui.tabs.TabManager
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.awt.Desktop
import java.net.URI
import java.util.Objects


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun App(
    showExitDialog: MutableState<Boolean>,
    onExitConfirmed: () -> Unit
) = AppTheme {
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

    if (showExitDialog.value) {
        AlertDialog(
            onDismissRequest = { showExitDialog.value = false },
            title = { Text(stringResource(Res.string.dialog_exit_title)) },
            text = { Text(stringResource(Res.string.dialog_exit_message)) },
            confirmButton = {
                TextButton(onClick = {
                    onExitConfirmed()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                        contentDescription = null
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showExitDialog.value = false
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null
                    )
                }
            }
        )
    }
}

internal val dalvikusSettings: DalvikusSettings by lazy {
    DalvikusSettings(
        Object()
    )
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
internal fun Content() {
    val splitPaneState = rememberSplitPaneState(0.25f)
    val tabManager = remember {
        TabManager(
            initialTabs = listOf(WelcomeTab())
        )
    }


    var selectedNavItem by remember { mutableStateOf("Editor") }

    val navItems = listOf(
        NavItem("Editor", Icons.Default.Edit, Res.string.nav_editor),
        NavItem("Decompiler", Icons.Default.Code, Res.string.nav_decompiler),
        NavItem("Packaging", Icons.Default.Android, Res.string.nav_packaging),

        NavItem("Settings", Icons.Outlined.Settings, Res.string.nav_settings),
    )
    var showTree by remember { mutableStateOf(true) }

    Row {
        Column(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            NavigationRail(
                modifier = Modifier.padding(start = 8.dp, top = 16.dp)
            ) {
                navItems.forEach { item ->
                    NavigationRailItem(
                        selected = selectedNavItem == item.key,
                        onClick = { selectedNavItem = item.key },
                        icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                        label = { Text(stringResource(item.labelRes)) }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                NavigationRailItem(
                    selected = false,
                    onClick = {
                        showTree = !showTree
                        if (!showTree) splitPaneState.positionPercentage = 0.0f
                    },
                    icon = {
                        Icon(
                            if (showTree) Icons.Outlined.FolderOff else Icons.Outlined.FolderOpen,
                            contentDescription = stringResource(Res.string.nav_file_tree)
                        )
                    },
                    label = { Text(stringResource(Res.string.nav_file_tree)) }
                )
            }
        }

        HorizontalSplitPane(
            modifier = Modifier.fillMaxSize(),
            splitPaneState = splitPaneState,
        ) {

            first(minSize = if (showTree) 200.dp else 0.dp) {
                if (showTree) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                    ) {
                        LeftPanelContent(tabManager)
                    }
                }
            }

            second(minSize = 200.dp) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                ) {
                    if(selectedNavItem == "Settings") {
                        SettingsScreen()
                    } else {
                        RightPanelContent(tabManager, selectedNavItem)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    var clickedStar by remember { mutableStateOf(false) }

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
                    imageVector = if (isDarkState.value) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = {
                clickedStar = true
                Desktop.getDesktop().browse(DalvikusSettings.getRepoURI()) }) {
                Icon(
                    imageVector = if (clickedStar) Icons.Filled.Star else Icons.Outlined.StarRate,
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
    )

}