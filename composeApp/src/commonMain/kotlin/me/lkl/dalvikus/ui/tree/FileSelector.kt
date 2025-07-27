package me.lkl.dalvikus.ui.tree

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.cancel
import dalvikus.composeapp.generated.resources.select
import me.lkl.dalvikus.tree.Node
import me.lkl.dalvikus.tree.filesystem.FileSystemFileNode
import me.lkl.dalvikus.tree.filesystem.FileSystemFolderNode
import me.lkl.dalvikus.tree.root.HiddenRoot
import org.jetbrains.compose.resources.stringResource
import java.io.File

@Composable
fun FileSelectorDialog(
    title: String,
    message: String? = null,
    filePredicate: (Node) -> Boolean = { it is FileSystemFileNode },
    onDismissRequest: () -> Unit,
    onFileSelected: (Node) -> Unit,
) {
    var selectedFile by remember {
        mutableStateOf<Node?>(null)
    }
    var treeRoot by remember {
        mutableStateOf(HiddenRoot(FileSystemFolderNode("Home directory", File(System.getProperty("user.home")), null)))
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = title)
        },
        text = {
            Column {
                message?.let {
                    Text(text = it)
                    Spacer(Modifier.size(8.dp))
                }
                Box(modifier = Modifier.size(500.dp, 400.dp)) {
                    TreeView(
                        root = treeRoot,
                        onFileSelected = { selectedFile = it },
                        selectedElement = selectedFile,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = selectedFile != null && filePredicate(selectedFile!!),
                onClick = {
                    onFileSelected(selectedFile!!)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.select))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Icon(
                    imageVector = Icons.Outlined.Cancel,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}
