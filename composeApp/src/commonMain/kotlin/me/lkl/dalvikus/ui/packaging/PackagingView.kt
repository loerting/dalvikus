package me.lkl.dalvikus.ui.packaging

import SettingRow
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
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
import me.lkl.dalvikus.snackbarManager
import me.lkl.dalvikus.tree.archive.ApkNode
import me.lkl.dalvikus.tree.archive.ZipNode
import me.lkl.dalvikus.ui.uiTreeRoot
import me.lkl.dalvikus.util.CollapseCard
import me.lkl.dalvikus.util.PasswordField
import org.jetbrains.compose.resources.stringResource
import settingPadHor
import settingPadVer
import java.security.MessageDigest
import java.security.cert.X509Certificate

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
            if (!keystoreInfo.passwordsFilled()) return@Scaffold
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
            val keystoreInfo = packagingViewModel.getKeystoreInfo()
            val treeRootChildren by uiTreeRoot.childrenFlow.collectAsState()
            val apks =
                treeRootChildren
                    .filter { it is ApkNode }
                    .map { it as ApkNode }
            val loadingApk = remember<MutableState<ZipNode?>> { mutableStateOf(null) }
            val scope = rememberCoroutineScope()
            val gridState = rememberLazyGridState()
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
                    item {
                        CollapseCard(
                            title = stringResource(Res.string.signature_settings_title),
                            icon = Icons.Outlined.Key,
                            defaultState = false
                        ) {
                            Column {
                                SettingRow(dalvikusSettings.getSetting("keystore_file"))
                                Column(Modifier.padding(horizontal = settingPadHor)) {
                                    Text(
                                        stringResource(
                                            Res.string.signature_keystore_password,
                                            keystoreInfo.keystoreFile.name
                                        ),
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
                    }
                    apks.forEach { apk ->
                        item {
                            CollapseCard(
                                title = apk.name,
                                icon = Icons.Outlined.Android,
                                defaultState = false
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    SignatureStatus(packagingViewModel, apk, loadingApk)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        if (loadingApk.value == apk) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.dp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        } else {
                                            TextButton(onClick = {
                                                loadingApk.value = apk
                                                scope.launch {
                                                    packagingViewModel.signApk(
                                                        keystoreInfo = keystoreInfo,
                                                        apk = apk.zipFile,
                                                        outputApk = apk.zipFile,
                                                        onError = { throwable ->
                                                            loadingApk.value = null
                                                            snackbarManager?.showError(throwable)
                                                        },
                                                        onSuccess = {
                                                            loadingApk.value = null
                                                            snackbarManager?.showSuccess()
                                                        }
                                                    )
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Draw,
                                                    contentDescription = stringResource(Res.string.sign)
                                                )
                                                Spacer(Modifier.width(4.dp))
                                                Text(stringResource(Res.string.sign))
                                            }

                                            TextButton(onClick = {
                                                loadingApk.value = apk
                                                scope.launch {
                                                    packagingViewModel.deployApk(
                                                        apk = apk.zipFile,
                                                        onError = { throwable ->
                                                            loadingApk.value = null
                                                            snackbarManager?.showError(throwable)
                                                        },
                                                        onSuccess = {
                                                            loadingApk.value = null
                                                            snackbarManager?.showSuccess()
                                                        },
                                                        packageName = apk.getAndroidPackageName()
                                                    )
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Outlined.InstallMobile,
                                                    contentDescription = stringResource(Res.string.deploy)
                                                )
                                                Spacer(Modifier.width(4.dp))
                                                Text(stringResource(Res.string.deploy))
                                            }
                                        }
                                    }
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
}


@Composable
fun SignatureStatus(
    packagingViewModel: PackagingViewModel,
    apk: ZipNode,
    loadingApk: MutableState<ZipNode?>
) {
    var signatureState by remember(apk) { mutableStateOf<ApkVerifier.Result?>(null) }

    LaunchedEffect(apk, loadingApk) {
        if (loadingApk.value != null) return@LaunchedEffect
        signatureState = null
        signatureState = packagingViewModel.checkSignature(apk.zipFile)
    }

    if (signatureState != null) {
        val result = signatureState!!
        val levels = listOf(
            result.isVerifiedUsingV1Scheme,
            result.isVerifiedUsingV2Scheme,
            result.isVerifiedUsingV3Scheme || result.isVerifiedUsingV31Scheme,
            result.isVerifiedUsingV4Scheme
        )
        val texts = listOf("V1", "V2", "V3/3.1", "V4")

        Column {
            Text(stringResource(Res.string.signature_validity), style = MaterialTheme.typography.titleSmall)

            MultiChoiceSegmentedButtonRow(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
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

            Spacer(modifier = Modifier.height(12.dp))

            SignatureSchemeSection("V1", result.v1SchemeSigners.mapNotNull { it.certificate })
            SignatureSchemeSection("V2", result.v2SchemeSigners.mapNotNull { it.certificate })
            SignatureSchemeSection("V3", result.v3SchemeSigners.mapNotNull { it.certificate })
            SignatureSchemeSection("V3.1", result.v31SchemeSigners.mapNotNull { it.certificate })
            SignatureSchemeSection("V4", result.v4SchemeSigners.mapNotNull { it.certificate })

            result.sourceStampInfo?.certificate?.let {
                SignatureSchemeSection("SourceStamp", listOf(it))
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

@Composable
fun SignatureSchemeSection(title: String, certs: List<X509Certificate>) {
    if (certs.isEmpty()) return

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = stringResource(Res.string.signer_title, title),
            style = MaterialTheme.typography.titleSmall
        )

        certs.forEachIndexed { index, cert ->
            val subject = cert.subjectX500Principal.name
            val issuer = cert.issuerX500Principal.name
            val sha256 = cert.encoded.sha256Fingerprint()

            SelectionContainer {
                Column(modifier = Modifier.padding(start = 8.dp, top = 4.dp)) {
                    Text(
                        text = stringResource(Res.string.signer_index, index + 1),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(Res.string.signer_subject, subject),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = stringResource(Res.string.signer_issuer, issuer),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = stringResource(Res.string.signer_sha256, sha256),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}


fun ByteArray.sha256Fingerprint(): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(this)
    return digest.joinToString(":") { "%02X".format(it) }
}
