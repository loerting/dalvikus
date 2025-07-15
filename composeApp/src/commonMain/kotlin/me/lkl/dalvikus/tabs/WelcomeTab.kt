package me.lkl.dalvikus.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.welcome_tab_name
import org.jetbrains.compose.resources.stringResource

data class WelcomeTab(
    override val tabId: String = "welcome",
    override val tabIcon: ImageVector = Icons.Default.EmojiPeople
) : TabElement {
    @Composable
    override fun tabName(): String = stringResource(Res.string.welcome_tab_name)
}