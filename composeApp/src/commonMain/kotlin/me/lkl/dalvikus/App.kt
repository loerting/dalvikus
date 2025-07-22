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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.lighten
import dalvikus.composeapp.generated.resources.*
import me.lkl.dalvikus.settings.DalvikusSettings
import me.lkl.dalvikus.tabs.WelcomeTab
import me.lkl.dalvikus.theme.AndroidGreen
import me.lkl.dalvikus.theme.AppTheme
import me.lkl.dalvikus.theme.LocalThemeIsDark
import me.lkl.dalvikus.tree.TreeElement
import me.lkl.dalvikus.tree.archive.ArchiveTreeNode
import me.lkl.dalvikus.ui.LeftPanelContent
import me.lkl.dalvikus.ui.RightPanelContent
import me.lkl.dalvikus.ui.nav.NavItem
import me.lkl.dalvikus.ui.tabs.TabManager
import me.lkl.dalvikus.ui.uiTreeRoot
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun App(
    showExitDialog: MutableState<Boolean>,
    onExitConfirmed: () -> Unit
) = AppTheme {
    Scaffold(
        topBar = { TopBar() }
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
    DalvikusSettings(
        Object()
    )
}
internal val tabManager: TabManager by lazy {
    TabManager(
        initialTabs = listOf(WelcomeTab())
    )
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
internal fun Content() {
    val splitPaneState = rememberSplitPaneState(0.25f)


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

            second(minSize = 200.dp) {
                RightPanelContent(selectedNavItem)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopBar() {
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
            DeployButton { node ->
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
fun DeployButton(deploy: (TreeElement) -> Unit) {
    val apks =
        uiTreeRoot.children.filter { it is ArchiveTreeNode && it.file.extension.equals("apk", ignoreCase = true) }
    var checked by remember { mutableStateOf(false) }
    SplitButtonLayout(
        leadingButton = {
            SplitButtonDefaults.LeadingButton(
                onClick = {
                    deploy(apks.last())
                },
                enabled = apks.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = AndroidGreen.lighten(1.2f))
            ) {
                Icon(
                    Icons.Filled.InstallMobile,
                    contentDescription = null,
                    modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(Res.string.deploy))
            }
        },
        trailingButton = {
            SplitButtonDefaults.TrailingButton(
                checked = checked,
                onCheckedChange = { checked = it },
                colors = ButtonDefaults.buttonColors(containerColor = AndroidGreen.lighten(1.2f)),
                enabled = apks.isNotEmpty()
            ) {
                val rotation: Float by
                animateFloatAsState(
                    targetValue = if (checked) 180f else 0f,
                    label = "Trailing Icon Rotation",
                )
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    modifier =
                        Modifier.size(SplitButtonDefaults.TrailingIconSize).graphicsLayer {
                            this.rotationZ = rotation
                        },
                    contentDescription = null,
                )
            }
        },
        modifier = Modifier.padding(horizontal = 8.dp),
    )
    DropdownMenu(expanded = checked, onDismissRequest = { checked = false }) {
        DropdownMenuItem(
            text = { Text(stringResource(Res.string.deploy_last_selected)) },
            onClick = {
                checked = false
                deploy(apks.last())
            },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
            trailingIcon = { Text("F1", textAlign = TextAlign.Center) },
        )
        HorizontalDivider()
        apks.forEach { apk ->
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.deploy_specific, apk.name)) },
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
