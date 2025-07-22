package me.lkl.dalvikus.ui.packaging

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.blend
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.signature_key_password
import dalvikus.composeapp.generated.resources.signature_keystore_password
import dalvikus.composeapp.generated.resources.signature_settings_title
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.theme.AndroidGreen
import me.lkl.dalvikus.tree.TreeElement
import me.lkl.dalvikus.tree.archive.ArchiveTreeNode
import me.lkl.dalvikus.ui.OnCard
import me.lkl.dalvikus.ui.uiTreeRoot
import org.jetbrains.compose.resources.stringResource
import java.io.File

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PackagingView() {
    val keystoreFile = dalvikusSettings["keystore_file"] as File
    val keyAlias = dalvikusSettings["key_alias"] as String
    val packagingViewModel = remember { PackagingViewModel() }
    Column(Modifier.fillMaxSize()) {
        OnCard {
            Column {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Key,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(Res.string.signature_settings_title),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                val keystorePassword = remember { mutableStateOf("") }
                val keyPassword = remember { mutableStateOf("") }

                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(Res.string.signature_keystore_password, keystoreFile.name), style = MaterialTheme.typography.bodyLarge)
                    OutlinedTextField(
                        value = keystorePassword.value,
                        onValueChange = { keystorePassword.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(stringResource(Res.string.signature_key_password, keyAlias), style = MaterialTheme.typography.bodyLarge)
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

        OnCard {
            val apks = uiTreeRoot.children.filter { it is ArchiveTreeNode && it.file.extension.equals("apk", ignoreCase = true) }
            val expandedStates = remember(apks) {
                mutableStateMapOf<String, Boolean>().apply {
                    apks.forEach { put(it.name, true) }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                apks.forEach { apk ->
                    val key = apk.name
                    val expanded = expandedStates[key] ?: true

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedStates[key] = !(expandedStates[key] ?: true)
                                }
                                .padding(vertical = 8.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Android,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = apk.name,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (expanded) "Collapse" else "Expand"
                            )
                        }
                        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                    }

                    item {
                        AnimatedVisibility(
                            visible = expanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column {
                                APKDetails(packagingViewModel, apk)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun APKDetails(packagingViewModel: PackagingViewModel, apk: TreeElement) {
    Row (verticalAlignment = Alignment.CenterVertically) {
        Text("Signature Status",
            modifier = Modifier.weight(1f),)
        val sigRes = remember(apk) {
            packagingViewModel.checkSignature((apk as ArchiveTreeNode).file)
        }
        if (sigRes != null) {
            VerificationChip("V1", sigRes.isVerifiedUsingV1Scheme)
            VerificationChip("V2", sigRes.isVerifiedUsingV2Scheme)
            VerificationChip("V3", sigRes.isVerifiedUsingV3Scheme)
            VerificationChip("V4", sigRes.isVerifiedUsingV4Scheme)
        } else {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun VerificationChip(name: String, isVerified: Boolean) {
    ElevatedSuggestionChip(modifier = Modifier.padding(4.dp), onClick = {

    }, label = {
        Text(name)
    }, icon = {
        if (isVerified) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = AndroidGreen
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }, colors = if (isVerified) {
        SuggestionChipDefaults.suggestionChipColors(
            containerColor = AndroidGreen.blend(Color.White, 0.75f),
            labelColor = AndroidGreen
        )
    } else {
        SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            labelColor = MaterialTheme.colorScheme.onErrorContainer
        )
    })
}