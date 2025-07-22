package me.lkl.dalvikus.ui.packaging

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.InstallMobile
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.*
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.tree.FileTreeNode
import me.lkl.dalvikus.tree.TreeElement
import me.lkl.dalvikus.tree.archive.ArchiveTreeNode
import me.lkl.dalvikus.tree.root.TreeRoot
import me.lkl.dalvikus.ui.DefaultCard
import me.lkl.dalvikus.ui.tree.TreeView
import me.lkl.dalvikus.ui.uiTreeRoot
import me.lkl.dalvikus.ui.util.CardTitleWithDivider
import org.jetbrains.compose.resources.stringResource
import java.io.File

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PackagingView() {
    val keystoreFile = dalvikusSettings["keystore_file"] as File
    val keyAlias = dalvikusSettings["key_alias"] as String
    remember { PackagingViewModel() }
    Column(Modifier.fillMaxSize()) {
        DefaultCard {
            Column {
                CardTitleWithDivider(
                    title = stringResource(Res.string.signature_settings_title),
                    icon = Icons.Outlined.Key
                )
                val keystorePassword = remember { mutableStateOf("") }
                val keyPassword = remember { mutableStateOf("") }

                Column(Modifier.padding(16.dp)) {
                    Text(
                        stringResource(Res.string.signature_keystore_password, keystoreFile.name),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    OutlinedTextField(
                        value = keystorePassword.value,
                        onValueChange = { keystorePassword.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        stringResource(Res.string.signature_key_password, keyAlias),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    OutlinedTextField(
                        value = keyPassword.value,
                        onValueChange = { keyPassword.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                }

            }
        }

        DefaultCard { APKSelectionView() }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun APKSelectionView() {
    var currentSelection by remember { mutableStateOf<TreeElement?>(null) }
    var checked by remember { mutableStateOf(false) }
    val size = SplitButtonDefaults.MediumContainerHeight
    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            SplitButtonLayout(
                leadingButton = {
                    SplitButtonDefaults.LeadingButton(
                        onClick = {
                        },
                        modifier = Modifier.heightIn(size),
                        shapes = SplitButtonDefaults.leadingButtonShapesFor(size),
                        contentPadding = SplitButtonDefaults.leadingButtonContentPaddingFor(size),
                    ) {
                        Icon(
                            Icons.Filled.InstallMobile,
                            contentDescription = null,
                            modifier = Modifier.size(SplitButtonDefaults.leadingButtonIconSizeFor(size))
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(Res.string.sign))
                    }
                },
                trailingButton = {
                    SplitButtonDefaults.TrailingButton(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        modifier = Modifier.heightIn(size),
                        shapes = SplitButtonDefaults.trailingButtonShapesFor(size),
                        contentPadding = SplitButtonDefaults.trailingButtonContentPaddingFor(size),
                    ) {
                        val rotation: Float by
                        animateFloatAsState(
                            targetValue = if (checked) 180f else 0f,
                            label = "Trailing Icon Rotation",
                        )
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            modifier =
                                Modifier.size(SplitButtonDefaults.trailingButtonIconSizeFor(size))
                                    .graphicsLayer { this.rotationZ = rotation },
                            contentDescription = null,
                        )
                    }
                },
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            DropdownMenu(expanded = checked, onDismissRequest = { checked = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.sign)) },
                    onClick = {
                        checked = false
                    },
                    leadingIcon = { Icon(Icons.Outlined.Draw, contentDescription = null) },
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.sign_and_deploy)) },
                    onClick = {
                        checked = false
                    },
                    leadingIcon = { Icon(Icons.Outlined.InstallMobile, contentDescription = null) },
                )
            }
        }
    ) {

        val apks = remember(uiTreeRoot.children) {
            uiTreeRoot.children.filter {
                it is ArchiveTreeNode && it.file.extension.equals(
                    "apk",
                    ignoreCase = true
                )
            }
        }
        val treeRoot = remember(apks) {
            TreeRoot().also { treeRoot ->
                treeRoot.children.addAll(apks.map { FileTreeNode((it as ArchiveTreeNode).file, treeRoot) })
            }
        }
        Column {
            CardTitleWithDivider(
                title = stringResource(Res.string.signature_settings_title),
                icon = Icons.Outlined.Android
            )
            TreeView(
                treeRoot,
                selectedElement = currentSelection,
                onFileSelected = { currentSelection = it },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

