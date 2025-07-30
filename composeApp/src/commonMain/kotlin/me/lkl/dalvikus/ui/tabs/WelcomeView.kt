package me.lkl.dalvikus.ui.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.app_dalvik_button
import dalvikus.composeapp.generated.resources.app_github_button
import dalvikus.composeapp.generated.resources.app_gpl_button
import dalvikus.composeapp.generated.resources.app_introduction
import dalvikus.composeapp.generated.resources.app_name
import me.lkl.dalvikus.settings.DalvikusSettings
import org.jetbrains.compose.resources.stringResource
import java.awt.Desktop
import java.net.URI

@Composable
fun WelcomeView() {
    Box(Modifier.Companion.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Companion.Center) {
        Column(horizontalAlignment = Alignment.Companion.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.AutoFixHigh,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(Res.string.app_name),
                modifier = Modifier.Companion.size(48.dp)
            )
            Spacer(Modifier.Companion.height(8.dp))
            Text(
                stringResource(Res.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Companion.SemiBold,
                textAlign = TextAlign.Companion.Center
            )

            Spacer(Modifier.Companion.height(48.dp))
            Text(
                stringResource(Res.string.app_introduction),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Companion.Justify
            )

            Spacer(Modifier.Companion.height(48.dp))
            Row {
                TextButton(onClick = {
                    Desktop.getDesktop().browse(DalvikusSettings.Companion.getRepoURI())
                }) {
                    Text(
                        stringResource(Res.string.app_github_button),
                        softWrap = false
                    )
                }
                TextButton(onClick = {
                    Desktop.getDesktop().browse(URI("https://source.android.com/docs/core/runtime/dalvik-bytecode"))
                }) {
                    Text(
                        stringResource(Res.string.app_dalvik_button),
                        softWrap = false
                    )
                }
                TextButton(onClick = {
                    Desktop.getDesktop().browse(URI("https://www.gnu.org/licenses/gpl-3.0.html"))
                }) {
                    Text(
                        stringResource(Res.string.app_gpl_button),
                        softWrap = false
                    )
                }
            }
        }
    }
}