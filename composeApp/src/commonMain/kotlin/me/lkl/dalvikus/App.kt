package me.lkl.dalvikus

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import me.lkl.dalvikus.settings.DalvikusSettings
import me.lkl.dalvikus.tabs.WelcomeTab
import me.lkl.dalvikus.theme.AppTheme
import me.lkl.dalvikus.theme.LocalThemeIsDark
import me.lkl.dalvikus.tree.archive.ZipNode
import me.lkl.dalvikus.ui.LeftPanelContent
import me.lkl.dalvikus.ui.RightPanelContent
import me.lkl.dalvikus.ui.nav.NavItem
import me.lkl.dalvikus.ui.packagingViewModel
import me.lkl.dalvikus.ui.showTreeAddFileDialog
import me.lkl.dalvikus.ui.tabs.TabManager
import me.lkl.dalvikus.ui.uiTreeRoot
import me.lkl.dalvikus.settings.shortcutToggleEditorDecompiler
import me.lkl.dalvikus.settings.shortcutTreeAdd
import me.lkl.dalvikus.ui.util.toOneLiner
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import org.jetbrains.compose.ui.tooling.preview.Preview



internal var snackbarHostStateDelegate: SnackbarHostState? = null
    private set

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun App(
    showExitDialog: MutableState<Boolean>,
    onExitConfirmed: () -> Unit
) = AppTheme {
    val snackbarHostState = remember { SnackbarHostState() }
    snackbarHostStateDelegate = snackbarHostState
    Scaffold(
        topBar = { TopBar() },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.exit))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showExitDialog.value = false
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }
}

internal val dalvikusSettings: DalvikusSettings by lazy {
    DalvikusSettings()
}
internal val tabManager: TabManager by lazy {
    TabManager(
        initialTabs = listOf(WelcomeTab())
    )
}

var selectedNavItem by mutableStateOf("Editor")

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
internal fun Content() {
    val splitPaneState = rememberSplitPaneState(0.25f)

    val navItems = listOf(
        NavItem("Editor", Icons.Default.Edit, Res.string.nav_editor),
        NavItem("Decompiler", Icons.Default.Code, Res.string.nav_decompiler),
        NavItem("Packaging", Icons.Default.Draw, Res.string.nav_signing),

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
                    onClick = { showTree = !showTree },
                    icon = {
                        Icon(
                            if (!showTree) Icons.Outlined.Park else Icons.Filled.Park,
                            contentDescription = stringResource(Res.string.nav_file_tree)
                        )
                    },
                    label = { Text(stringResource(Res.string.nav_file_tree)) }
                )
            }
        }

        val animatedSplit by animateFloatAsState(targetValue = if (showTree) 0.25f else 0f)
        LaunchedEffect(animatedSplit) { splitPaneState.positionPercentage = animatedSplit }

        HorizontalSplitPane(
            modifier = Modifier.fillMaxSize(),
            splitPaneState = splitPaneState,
        ) {

            first(minSize = if (showTree) 200.dp else 0.dp) {
                AnimatedVisibility(
                    visible = showTree,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                    ) {
                        LeftPanelContent()
                    }
                }
            }

            second(minSize = 400.dp) {
                RightPanelContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopBar() {
    val scope = rememberCoroutineScope()
    rememberTooltipState(isPersistent = true)
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
                        text = stringResource(Res.string.app_name),
                        fontWeight = FontWeight.SemiBold
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
            val failureMessage = stringResource(Res.string.snack_failed)
            val successMessage = stringResource(Res.string.snack_success)
            val dismiss = stringResource(Res.string.dismiss)
            DeployButton { node ->
                scope.launch {
                    packagingViewModel.signApk(
                        keystoreInfo = packagingViewModel.getKeystoreInfo(),
                        apk = node.zipFile,
                        outputApk = node.zipFile,
                        onError = { throwable ->
                            scope.launch {
                                snackbarHostStateDelegate?.showSnackbar(
                                    // TODO find a better way of formatting stringResource later.
                                    message = failureMessage + " " + throwable.toOneLiner(),
                                    actionLabel = dismiss,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        onSuccess = {
                            scope.launch {
                                packagingViewModel.deployApk(
                                    apk = node.zipFile,
                                    onError = { throwable ->
                                        scope.launch {
                                            snackbarHostStateDelegate?.showSnackbar(
                                                // TODO find a better way of formatting stringResource later.
                                                message = failureMessage + " " + throwable.toOneLiner(),
                                                actionLabel = dismiss,
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    },
                                    onSuccess = {
                                        scope.launch {
                                            snackbarHostStateDelegate?.showSnackbar(
                                                message = successMessage,
                                                actionLabel = dismiss,
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            scrolledContainerColor = Color.Unspecified,
            navigationIconContentColor = Color.Unspecified,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            actionIconContentColor = Color.Unspecified
        ),
    )
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
fun DeployButton(deploy: (ZipNode) -> Unit) {
    val treeRootChildren by uiTreeRoot.childrenFlow.collectAsState()
    val apks =
        treeRootChildren
            .filter { it is ZipNode && it.name.endsWith("apk", ignoreCase = true) }
            .map { it as ZipNode }
    var checked by remember { mutableStateOf(false) }
    IconButton(
        onClick = { checked = !checked },
    ) {
        Icon(Icons.Outlined.PlayCircle, contentDescription = stringResource(Res.string.sign_and_deploy))
    }
    DropdownMenu(expanded = checked, onDismissRequest = { checked = false }) {
        apks.forEach { apk ->
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.sign_deploy_app, apk.name)) },
                onClick = {
                    checked = false
                    deploy(apk)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Android,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

val keyActionMap: Map<Key, () -> Unit> = mapOf(
    shortcutToggleEditorDecompiler to {
        selectedNavItem = when(selectedNavItem) {
            "Editor" -> "Decompiler"
            "Decompiler" -> "Editor"
            else -> selectedNavItem
        }
    },
    shortcutTreeAdd to { showTreeAddFileDialog = true }
)