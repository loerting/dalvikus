package me.lkl.dalvikus.ui.packaging

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.InstallMobile
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.android.apksig.ApkVerifier
import dalvikus.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.snackbarHostStateDelegate
import me.lkl.dalvikus.tree.archive.ArchiveTreeNode
import me.lkl.dalvikus.ui.uiTreeRoot
import me.lkl.dalvikus.ui.util.CardTitleWithDivider
import me.lkl.dalvikus.ui.util.DefaultCard
import org.jetbrains.compose.resources.stringResource
import java.io.File

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PackagingView() {
    val keystoreFile = dalvikusSettings["keystore_file"] as File
    val keyAlias = dalvikusSettings["key_alias"] as String

    val keystorePassword = remember { mutableStateOf("") }
    val keyPassword = remember { mutableStateOf("") }

    val packagingViewModel = remember { PackagingViewModel() }
    Column(Modifier.fillMaxSize()) {
        DefaultCard {
            Column {
                CardTitleWithDivider(
                    title = stringResource(Res.string.signature_settings_title),
                    icon = Icons.Outlined.Key
                )
                Column(Modifier.padding(16.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stringResource(Res.string.signature_settings_info),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        stringResource(Res.string.signature_keystore_password, keystoreFile.name),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    OutlinedTextField(
                        value = keystorePassword.value,
                        onValueChange = { keystorePassword.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
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
                        visualTransformation = PasswordVisualTransformation()
                    )
                }

            }
        }

        SignInfoCards(keystoreFile, keystorePassword, keyAlias, keyPassword, packagingViewModel)
    }
}

@Composable
private fun SignInfoCards(
    keystoreFile: File,
    keystorePassword: MutableState<String>,
    keyAlias: String,
    keyPassword: MutableState<String>,
    packagingViewModel: PackagingViewModel
) {
    val apks = remember(uiTreeRoot.children) {
        uiTreeRoot.children.filter {
            it is ArchiveTreeNode && it.file.extension.equals(
                "apk",
                ignoreCase = true
            )
        }.map { it as ArchiveTreeNode }
    }
    val currentlySigning = remember { mutableStateOf<ArchiveTreeNode?>(null) }
    val dismiss = stringResource(Res.string.dismiss)
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentlySigning.value) {
        val apk = currentlySigning.value ?: return@LaunchedEffect
        packagingViewModel.signApk(
            keystoreFile = keystoreFile,
            keystorePassword = keystorePassword.value.toCharArray(),
            keyAlias = keyAlias,
            keyPassword = keyPassword.value.toCharArray(),
            apk = apk.file,
            outputApk = apk.file, // overwrite; make sure that's intended
            onError = { throwable ->
                currentlySigning.value = null
                scope.launch {
                    snackbarHostStateDelegate?.showSnackbar(
                        message = throwable.message ?: "Error signing APK",
                        actionLabel = dismiss,
                        duration = SnackbarDuration.Short
                    )
                }
            },
            onSuccess = {
                currentlySigning.value = null
            }
        )
    }

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
                                SignatureStatus(packagingViewModel, apk, currentlySigning)
                            }
                            if (currentlySigning.value == apk) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                IconButton({
                                    currentlySigning.value = apk

                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Draw,
                                        contentDescription = stringResource(Res.string.sign)
                                    )
                                }
                                IconButton({
                                    currentlySigning.value = apk
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.InstallMobile,
                                        contentDescription = stringResource(Res.string.sign_and_deploy)
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
    apk: ArchiveTreeNode,
    apkToSign: MutableState<ArchiveTreeNode?>
) {
    var signatureState by remember(apk) { mutableStateOf<ApkVerifier.Result?>(null) }

    LaunchedEffect(apk, apkToSign) {
        if (apkToSign.value != null) return@LaunchedEffect // some apk is being signed.
        signatureState = null
        signatureState = packagingViewModel.checkSignature(apk.file)
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
