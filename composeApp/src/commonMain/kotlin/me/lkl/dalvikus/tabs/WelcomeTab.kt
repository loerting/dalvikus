package me.lkl.dalvikus.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.welcome_tab_name
import me.lkl.dalvikus.tabs.contentprovider.ContentProvider
import me.lkl.dalvikus.tabs.contentprovider.NopContentProvider
import me.lkl.dalvikus.ui.editor.EditorViewModel
import org.jetbrains.compose.resources.stringResource

data class WelcomeTab(
    override val tabId: String = "welcome",
    override val tabIcon: ImageVector = Icons.Default.EmojiPeople
) : TabElement {
    override var hasUnsavedChanges: MutableState<Boolean> = mutableStateOf(false)
    override val contentProvider: ContentProvider = NopContentProvider()
    override var editorViewModel: EditorViewModel? = null
        set(value) {
            throw IllegalStateException("WelcomeTab does not support editor view model")
        }
    @Composable
    override fun tabName(): String = stringResource(Res.string.welcome_tab_name)
}