package me.lkl.dalvikus.ui.tree

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dalvikus.composeapp.generated.resources.Res
import dalvikus.composeapp.generated.resources.cancel
import dalvikus.composeapp.generated.resources.select
import me.lkl.dalvikus.tree.FileTreeNode
import me.lkl.dalvikus.tree.TreeElement
import me.lkl.dalvikus.tree.root.TreeRoot
import org.jetbrains.compose.resources.stringResource
import java.io.File

@Composable
fun FileSelectorDialog(
    title: String,
    filePredicate: (TreeElement) -> Boolean = { it is FileTreeNode && !it.file.isDirectory },
    onDismissRequest: () -> Unit,
    onFileSelected: (TreeElement) -> Unit,
) {
    var selectedFile by remember {
        mutableStateOf<TreeElement?>(null)
    }
    var treeRoot by remember {
        mutableStateOf(TreeRoot(FileTreeNode(File(System.getProperty("user.home")), null)))
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = title)
        },
        text = {
            Box(modifier = Modifier.size(500.dp, 400.dp)) {
                TreeView(
                    root = treeRoot,
                    selectedElement = selectedFile,
                    onFileSelected = { selectedFile = it }
                )
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
