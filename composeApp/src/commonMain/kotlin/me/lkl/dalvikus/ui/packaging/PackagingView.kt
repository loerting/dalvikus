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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.apksig.ApkVerifier
import dalvikus.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import me.lkl.dalvikus.LocalSnackbarManager
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.errorreport.crtExHandler
import me.lkl.dalvikus.tools.sha256Fingerprint
import me.lkl.dalvikus.tree.archive.ApkNode
import me.lkl.dalvikus.tree.archive.ZipNode
import me.lkl.dalvikus.ui.uiTreeRoot
import me.lkl.dalvikus.util.CollapseCard
import me.lkl.dalvikus.util.CollapseCardMaxWidth
import me.lkl.dalvikus.util.PasswordField
import org.jetbrains.compose.resources.stringResource
import settingPadHor
import settingPadVer
import java.security.cert.X509Certificate

@Composable
fun PackagingView() {
    val snackbarManager = LocalSnackbarManager.current

    val packagingViewModel = remember { PackagingViewModel(snackbarManager) }

    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        val keystoreInfo = getKeystoreInfo()
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
                columns = GridCells.Adaptive(minSize = CollapseCardMaxWidth),
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
                            SettingRow(dalvikusSettings.getSetting("key_alias"))
                            Column(Modifier.padding(horizontal = settingPadHor)) {
                                Text(
                                    stringResource(
                                        Res.string.signature_keystore_password
                                    ),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                PasswordField(
                                    password = keystorePasswordField.concatToString(),
                                    onPasswordChange = { keystorePasswordField = it.toCharArray() },
                                    isError = keystorePasswordField.size < 6,
                                    errorMessage = stringResource(Res.string.error_password_min_length)
                                )
                                Spacer(modifier = Modifier.height(settingPadVer))
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if(!keystoreInfo.seemsValid()) {
                                    TextButton(
                                        onClick = {
                                            packagingViewModel.keytool.createKeystore(keystorePasswordField)
                                        },
                                        enabled = keystoreInfo.isPasswordFilled()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Key,
                                            contentDescription = stringResource(Res.string.btn_create_keystore)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(stringResource(Res.string.btn_create_keystore))
                                    }
                                }
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
                                            scope.launch(crtExHandler) {
                                                packagingViewModel.apkSigner.signApk(
                                                    keystoreInfo = keystoreInfo,
                                                    apk = apk.zipFile,
                                                    outputApk = apk.zipFile,
                                                ) {
                                                    if (it) snackbarManager.showSuccess()
                                                    loadingApk.value = null
                                                }
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
                                            scope.launch(crtExHandler) {
                                                packagingViewModel.adbDeployer.deployApk(
                                                    apk = apk.zipFile,
                                                    packageName = apk.getAndroidPackageName()
                                                ) {
                                                    if (it) snackbarManager.showSuccess()
                                                    loadingApk.value = null
                                                }
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


@Composable
fun SignatureStatus(
    packagingViewModel: PackagingViewModel,
    apk: ZipNode,
    loadingApk: MutableState<ZipNode?>
) {
    var signatureState by remember(apk) { mutableStateOf<ApkVerifier.Result?>(null) }

    LaunchedEffect(apk, loadingApk.value) {
        if (loadingApk.value != null) return@LaunchedEffect
        signatureState = null
        signatureState = packagingViewModel.apkSigner.checkSignature(apk.zipFile)
    }

    if (signatureState != null) {
        val result = signatureState!!
        val levels = listOf(
            result.isVerifiedUsingV1Scheme,
            result.isVerifiedUsingV2Scheme,
            result.isVerifiedUsingV3Scheme,
            result.isVerifiedUsingV31Scheme,
            result.isVerifiedUsingV4Scheme,
            result.isSourceStampVerified
        )
        val texts = listOf("V1", "V2", "V3", "V3.1", "V4", "SourceStamp")

        Column {
            Text(stringResource(Res.string.signature_validity), style = MaterialTheme.typography.titleSmall)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                texts.forEachIndexed { index, label ->
                    FilterChip(
                        // TODO remove elevation = null when https://youtrack.jetbrains.com/issue/CMP-2868 is fixed.
                        elevation = null,
                        selected = levels[index],
                        onClick = {},
                        leadingIcon = {
                            Icon(
                                imageVector = if (levels[index]) Icons.Outlined.Key else Icons.Outlined.Close,
                                contentDescription = null
                            )
                        },
                        label = { Text(label) }
                    )
                }
            }


            Spacer(modifier = Modifier.height(12.dp))
            if(result.errors.isNotEmpty()) {
                result.errors.forEach { error ->
                    SelectionContainer {
                        Text(
                            text = error.toString(),
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(Res.string.signature_no_errors),
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                )
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