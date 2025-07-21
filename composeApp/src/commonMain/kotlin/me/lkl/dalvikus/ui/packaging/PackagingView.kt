package me.lkl.dalvikus.ui.packaging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.blend
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.app_dalvik_button
import dalvikus.composeapp.generated.resources.app_github_button
import dalvikus.composeapp.generated.resources.app_gpl_button
import dalvikus.composeapp.generated.resources.app_name
import dalvikus.composeapp.generated.resources.no_app_selected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lkl.dalvikus.settings.DalvikusSettings
import me.lkl.dalvikus.theme.AndroidGreen
import me.lkl.dalvikus.ui.lastAndroidArchive
import org.jetbrains.compose.resources.stringResource
import java.awt.Desktop
import java.net.URI

@Composable
fun PackagingView() {
    val packagingViewModel = remember(lastAndroidArchive) { PackagingViewModel(lastAndroidArchive) }
    val signatureResult by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(lastAndroidArchive) {
        if (lastAndroidArchive != null) {
            withContext(Dispatchers.Default) {
                packagingViewModel.checkSignature()
            }
        }
    }

    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.Android,
                tint = AndroidGreen,
                contentDescription = stringResource(Res.string.app_name),
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                lastAndroidArchive?.name ?: stringResource(Res.string.no_app_selected),
                style = MaterialTheme.typography.bodyLarge,
                color = AndroidGreen,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))
            Column {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Text("Signature Status",
                        modifier = Modifier.weight(1f),)
                    val sigRes = packagingViewModel.signatureResult
                    if (sigRes != null) {
                        VerificationChip("V1", sigRes.isVerifiedUsingV1Scheme)
                        VerificationChip("V2", sigRes.isVerifiedUsingV2Scheme)
                        VerificationChip("V3", sigRes.isVerifiedUsingV3Scheme)
                        VerificationChip("V3.1", sigRes.isVerifiedUsingV31Scheme)
                        VerificationChip("V4", sigRes.isVerifiedUsingV4Scheme)
                    } else {
                        if(lastAndroidArchive == null) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                                contentDescription = null
                            )
                        } else
                            CircularProgressIndicator()
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 16.dp))

            }


            Spacer(Modifier.height(48.dp))
            Row {
                TextButton(onClick = {
                    Desktop.getDesktop().browse(DalvikusSettings.getRepoURI())
                }) {
                    Text(stringResource(Res.string.app_github_button),
                        softWrap = false)
                }
                TextButton(onClick = {
                    Desktop.getDesktop().browse(URI("https://source.android.com/docs/core/runtime/dalvik-bytecode"))
                }) {
                    Text(stringResource(Res.string.app_dalvik_button),
                        softWrap = false)
                }
                TextButton(onClick = {
                    Desktop.getDesktop().browse(URI("https://www.gnu.org/licenses/gpl-3.0.html"))
                }) {
                    Text(stringResource(Res.string.app_gpl_button),
                        softWrap = false)
                }
            }
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