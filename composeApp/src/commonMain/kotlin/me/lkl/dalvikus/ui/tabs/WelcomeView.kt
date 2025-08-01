package me.lkl.dalvikus.ui.tabs

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import dalvikus.composeapp.generated.resources.*
import me.lkl.dalvikus.settings.DalvikusSettings
import org.jetbrains.compose.resources.stringResource
import java.awt.Desktop
import java.net.URI
import kotlinx.coroutines.delay
import me.lkl.dalvikus.dalvikusSettings

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WelcomeView() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300) // subtle delay for animation
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(800)) + slideInVertically(initialOffsetY = { 40 }),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(32.dp)
                        .widthIn(max = 640.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoFixHigh,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )

                    Text(
                        stringResource(Res.string.app_name),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        stringResource(Res.string.app_introduction, getUserName()),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        val buttonShape = RoundedCornerShape(16.dp)
                        val buttonColors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )

                        LinkButton(
                            text = stringResource(Res.string.app_github_button),
                            uri = DalvikusSettings.getRepoURI(),
                            icon = Icons.Outlined.Star,
                            shape = buttonShape,
                            colors = buttonColors
                        )

                        LinkButton(
                            text = stringResource(Res.string.app_dalvik_button),
                            uri = URI("https://source.android.com/docs/core/runtime/dalvik-bytecode"),
                            icon = Icons.Outlined.Android,
                            shape = buttonShape,
                            colors = buttonColors
                        )

                        LinkButton(
                            text = stringResource(Res.string.app_gpl_button),
                            uri = URI("https://www.gnu.org/licenses/gpl-3.0.html"),
                            icon = Icons.Outlined.Gavel,
                            shape = buttonShape,
                            colors = buttonColors
                        )
                    }
                }
            }
        }

}

private fun getUserName(): String {
    return System.getProperty("user.name") ?: "dalvikus user"
}

@Composable
fun LinkButton(
    text: String,
    uri: URI,
    icon: ImageVector,
    shape: RoundedCornerShape,
    colors: ButtonColors
) {
    TextButton(
        onClick = { Desktop.getDesktop().browse(uri) },
        shape = shape,
        colors = colors
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text, softWrap = false)
    }
}
