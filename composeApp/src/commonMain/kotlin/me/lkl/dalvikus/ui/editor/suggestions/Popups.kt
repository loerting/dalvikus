package me.lkl.dalvikus.ui.editor.suggestions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.ArrowCircleRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import dalvikus.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import me.lkl.dalvikus.tabs.SmaliTab
import me.lkl.dalvikus.ui.editor.EditorViewModel
import me.lkl.dalvikus.ui.editor.LayoutSnapshot
import me.lkl.dalvikus.ui.selectFileTreeNode
import me.lkl.dalvikus.util.getTextWidth
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

    EditorAnnotationPopup(lastLayoutSnapshot, annotation, viewModel, {
        ModernPopupContainer(
            color = MaterialTheme.colorScheme.error,
            icon = Icons.Default.ErrorOutline,
            text = annotation.item,
            textStyle = textStyle,
            action = stringResource(Res.string.copy),
            actionIcon = Icons.Default.ContentCopy,
        ) {
            scope.launch {
                clipboard.setClipEntry(ClipEntry(StringSelection(annotation.item)))
            }
        }
    }, getTextWidth(annotation.item, textStyle))
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

    val browse = fullPath.startsWith("android") && !fullPath.contains('.')
    val actionText =
        when {
            hasEntry -> stringResource(Res.string.open)
            browse -> stringResource(Res.string.browse_web)
            else -> null
        }
    val actionIcon =
        when {
            hasEntry -> Icons.Outlined.ArrowCircleRight
            browse -> Icons.Outlined.Search
            else -> null
        }

    EditorAnnotationPopup(lastLayoutSnapshot, annotation, viewModel, {
        ModernPopupContainer(
            color = MaterialTheme.colorScheme.primary,
            icon = Icons.Outlined.Info,
            text = lookupText,
            textStyle = textStyle,
            action = actionText,
            actionIcon = actionIcon,
        ) {

            scope.launch {
                val resolved = smaliTab.dexEntryClassNode.root.resolveChildrenPath(fullPath)
                resolved?.let { selectFileTreeNode(it) }
            }

            if (hasEntry) {
                scope.launch {
                    val resolved = smaliTab.dexEntryClassNode.root.resolveChildrenPath(fullPath)
                    resolved?.let { selectFileTreeNode(it) }
                }
            } else if (browse) {
                Desktop.getDesktop().browse(URI("https://developer.android.com/reference/${fullPath}"))
            }
        }
    }, getTextWidth(lookupText, textStyle))
}


@Composable
fun HexPopup(
    annotation: AnnotatedString.Range<String>,
    textStyle: TextStyle,
    lastLayoutSnapshot: LayoutSnapshot?,
    viewModel: EditorViewModel
) {
    val rawHex = annotation.item.trim().lowercase()
    val isNegative = rawHex.startsWith("-")
    val cleanedHex = rawHex.removePrefix("-").removePrefix("0x")

    val infoText = try {
        val unsignedValue = cleanedHex.toBigInteger(16)
        val signedValue = if (isNegative) -unsignedValue else unsignedValue

        when (cleanedHex.length) {
            8 -> {
                if(!isNegative && (cleanedHex.startsWith("7e") || cleanedHex.startsWith("7f"))) {
                    "$cleanedHex (resource ID) = ${viewModel.tryResolveResIdText(unsignedValue)} (resolved) "
                } else {
                    val floatValue = Float.fromBits(signedValue.toInt())
                    "$signedValue (dec) = $floatValue (float)"
                }
            }

            16 -> {
                val doubleValue = Double.fromBits(signedValue.toLong())
                "$signedValue (dec) = $doubleValue (double)"
            }

            else -> "$rawHex (hex) = $signedValue (dec)"
        }
    } catch (e: Exception) {
        "Invalid hex: $rawHex"
    }

    EditorAnnotationPopup(lastLayoutSnapshot, annotation, viewModel, {
        ModernPopupContainer(
            color = MaterialTheme.colorScheme.secondary,
            text = infoText,
            textStyle = textStyle,
            icon = Icons.Outlined.Info,
        )
    }, getTextWidth(infoText, textStyle))
}

