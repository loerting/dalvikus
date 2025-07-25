package me.lkl.dalvikus.ui.packaging

import SettingRow
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.InstallMobile
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.apksig.ApkVerifier
import dalvikus.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.snackbarHostStateDelegate
import me.lkl.dalvikus.tree.archive.ZipNode
import me.lkl.dalvikus.ui.uiTreeRoot
import me.lkl.dalvikus.ui.util.CollapseCard
import me.lkl.dalvikus.ui.util.DefaultCard
import me.lkl.dalvikus.ui.util.PasswordField
import me.lkl.dalvikus.ui.util.toOneLiner
import org.jetbrains.compose.resources.stringResource
import settingPadHor
import settingPadVer

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PackagingView(packagingViewModel: PackagingViewModel) {
    val keystorePassword by packagingViewModel.keystorePassword.collectAsState()
    val keyPassword by packagingViewModel.keyPassword.collectAsState()

    val keystoreInfo = packagingViewModel.getKeystoreInfo()

    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            if (keystoreInfo.isValid()) return@Scaffold
            ExtendedFloatingActionButton(
                onClick = {
                    packagingViewModel.openConsoleCreateKeystore(
                        scope,
                        keystorePassword,
                        keyPassword
                    )
                },
                icon = {
                    Icon(Icons.Default.Key, contentDescription = stringResource(Res.string.fab_create_keystore))
                },
                text = {
                    Text(stringResource(Res.string.fab_create_keystore))
                },

                )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            CollapseCard(
                title = stringResource(Res.string.signature_settings_title),
                icon = Icons.Outlined.Key,
                defaultState = true
            ) {
                Column {
                    SettingRow(dalvikusSettings.getSetting("keystore_file"))
                    Column(Modifier.padding(horizontal = settingPadHor)) {
                        Text(
                            stringResource(Res.string.signature_keystore_password, keystoreInfo.keystoreFile.name),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        PasswordField(
                            password = keystorePassword,
                            onPasswordChange = packagingViewModel::updateKeystorePassword,
                            isError = keystorePassword.length < 6,
                            errorMessage = stringResource(Res.string.error_password_min_length)
                        )
                        Spacer(modifier = Modifier.height(settingPadVer))
                    }

                    SettingRow(dalvikusSettings.getSetting("key_alias"))
                    Column(Modifier.padding(horizontal = settingPadHor)) {
                        Text(
                            stringResource(Res.string.signature_key_password, keystoreInfo.keyAlias),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        PasswordField(
                            password = keyPassword,
                            onPasswordChange = packagingViewModel::updateKeyPassword,
                            isError = keyPassword.length < 6,
                            errorMessage = stringResource(Res.string.error_password_min_length)
                        )
                        Spacer(modifier = Modifier.height(settingPadVer))
                    }
                }
            }
            SignInfoCards(packagingViewModel)
        }
    }
}


@Composable
private fun SignInfoCards(
    packagingViewModel: PackagingViewModel
) {
    val keystoreInfo = packagingViewModel.getKeystoreInfo()

    val treeRootChildren by uiTreeRoot.childrenFlow.collectAsState()
    val apks =
        treeRootChildren
            .filter { it is ZipNode && it.name.endsWith("apk", ignoreCase = true) }
            .map { it as ZipNode }

    val loadingApk = remember { mutableStateOf<ZipNode?>(null) }
    val failureMessage = stringResource(Res.string.snack_failed)
    val successMessage = stringResource(Res.string.snack_success)
    val dismiss = stringResource(Res.string.dismiss)
    val scope = rememberCoroutineScope()


    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = listState
        ) {
            apks.forEach { apk ->

                item {
                    DefaultCard(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Android,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = apk.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                SignatureStatus(packagingViewModel, apk, loadingApk)
                            }
                            if (loadingApk.value == apk) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    strokeWidth = 3.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                IconButton({
                                    loadingApk.value = apk
                                    scope.launch {
                                        packagingViewModel.signApk(
                                            keystoreInfo = keystoreInfo,
                                            apk = apk.zipFile,
                                            outputApk = apk.zipFile,
                                            onError = { throwable ->
                                                loadingApk.value = null
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
                                                loadingApk.value = null
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
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Draw,
                                        contentDescription = stringResource(Res.string.sign)
                                    )
                                }
                                IconButton({
                                    loadingApk.value = apk
                                    scope.launch {
                                        packagingViewModel.deployApk(
                                            apk = apk.zipFile,
                                            onError = { throwable ->
                                                loadingApk.value = null
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
                                                loadingApk.value = null
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
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.InstallMobile,
                                        contentDescription = stringResource(Res.string.deploy)
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState = listState),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(vertical = 8.dp, horizontal = 8.dp)
        )
    }
}

@Composable
fun SignatureStatus(
    packagingViewModel: PackagingViewModel,
    apk: ZipNode,
    loadingApk: MutableState<ZipNode?>
) {
    var signatureState by remember(apk) { mutableStateOf<ApkVerifier.Result?>(null) }

    LaunchedEffect(apk, loadingApk) {
        if (loadingApk.value != null) return@LaunchedEffect // some apk is being signed.
        signatureState = null
        signatureState = packagingViewModel.checkSignature(apk.zipFile)
    }

    if (signatureState != null) {
        val levels = listOf(
            signatureState!!.isVerifiedUsingV1Scheme,
            signatureState!!.isVerifiedUsingV2Scheme,
            signatureState!!.isVerifiedUsingV3Scheme,
            signatureState!!.isVerifiedUsingV4Scheme
        )
        val texts = listOf("V1", "V2", "V3", "V4")
        MultiChoiceSegmentedButtonRow {
            texts.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = texts.size),
                    onCheckedChange = {},
                    checked = levels[index],
                ) {
                    Text(label)
                }
            }
        }
    } else {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
