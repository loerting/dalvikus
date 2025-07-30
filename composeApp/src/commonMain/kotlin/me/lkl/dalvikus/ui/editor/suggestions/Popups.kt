package me.lkl.dalvikus.ui.editor.suggestions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.outlined.ArrowCircleRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.lookup_popup_current_class
import dalvikus.composeapp.generated.resources.lookup_popup_not_found
import dalvikus.composeapp.generated.resources.lookup_popup_open_definition
import dalvikus.composeapp.generated.resources.lookup_popup_runtime
import kotlinx.coroutines.launch
import me.lkl.dalvikus.tabs.SmaliTab
import me.lkl.dalvikus.ui.editor.EditorViewModel
import me.lkl.dalvikus.ui.editor.LayoutSnapshot
import me.lkl.dalvikus.ui.selectFileTreeNode
import org.jetbrains.compose.resources.stringResource
import java.awt.Desktop
import java.awt.datatransfer.StringSelection
import java.net.URI

@Composable
fun ErrorPopup(
    lastLayoutSnapshot: LayoutSnapshot?,
    annotation: AnnotatedString.Range<String>,
    viewModel: EditorViewModel,
    textStyle: TextStyle
) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    EditorAnnotationPopup(lastLayoutSnapshot, annotation, viewModel, textStyle) {
        Surface(
            modifier = Modifier.Companion
                .wrapContentSize()
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(4.dp)),
            shadowElevation = 4.dp,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = annotation.item,
                    style = textStyle.copy(color = MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(8.dp)
                )
                IconButton(onClick = {
                    scope.launch {
                        clipboard.setClipEntry(ClipEntry(StringSelection(annotation.item)))
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy to clipboard",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LookupPopup(
    smaliTab: SmaliTab,
    lastLayoutSnapshot: LayoutSnapshot?,
    annotation: AnnotatedString.Range<String>,
    viewModel: EditorViewModel,
    textStyle: TextStyle
) {
    val fullPath = annotation.item
    val hasEntry = smaliTab.dexEntryClassNode.root.hasClass(fullPath)

    val lookupText = when {
        fullPath == smaliTab.dexEntryClassNode.fullPath -> stringResource(Res.string.lookup_popup_current_class)
        !hasEntry && (fullPath.startsWith("java") || fullPath.startsWith("android")) -> stringResource(Res.string.lookup_popup_runtime)
        !hasEntry -> stringResource(Res.string.lookup_popup_not_found)
        else -> stringResource(Res.string.lookup_popup_open_definition, fullPath)
    }

    val scope = rememberCoroutineScope()

    EditorAnnotationPopup(lastLayoutSnapshot, annotation, viewModel, textStyle) {
        Surface(
            modifier = Modifier.Companion
                .wrapContentSize()
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)),
            shadowElevation = 4.dp,
            shape = RoundedCornerShape(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lookupText,
                    style = textStyle.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(8.dp)
                )
                if (hasEntry) {
                    IconButton(onClick = {
                        scope.launch {
                            val resolved = smaliTab.dexEntryClassNode.root.resolveChildrenPath(fullPath)
                            resolved?.let { selectFileTreeNode(it) }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowCircleRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                } else if(fullPath.startsWith("android") && !fullPath.contains('.')) {
                    IconButton(onClick = {
                        Desktop.getDesktop().browse(URI("https://developer.android.com/reference/${fullPath}"))
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}