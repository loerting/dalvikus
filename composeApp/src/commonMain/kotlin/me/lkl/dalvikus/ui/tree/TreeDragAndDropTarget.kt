package me.lkl.dalvikus.ui.tree

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import me.lkl.dalvikus.ui.addFileToTree
import java.io.File
import java.net.URI

class TreeDragAndDropTarget(val unsupportedFileText: String) : DragAndDropTarget {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onDrop(event: DragAndDropEvent): Boolean {
        val flavors = event.awtTransferable.transferDataFlavors

        for (flavor in flavors) {
            if (flavor.isMimeTypeEqual("application/x-java-file-list")) {
                val files = event.awtTransferable.getTransferData(flavor) as? List<*>
                files?.filterIsInstance<File>()?.forEach { file ->
                    addFileToTree(file, unsupportedFileText)
                }
                return true
            }

            if (flavor.isMimeTypeEqual("text/uri-list") || flavor.isMimeTypeEqual("text/plain")) {
                val data = event.awtTransferable.getTransferData(flavor) as? String ?: continue

                // Split into lines, ignore comments
                val uris = data.lines()
                    .mapNotNull { line -> line.trim().takeIf { it.isNotBlank() && !it.startsWith("#") } }
                    .mapNotNull { uri ->
                        try {
                            File(URI(uri))
                        } catch (e: Exception) {
                            null
                        }
                    }

                uris.forEach { file ->
                    if (file.exists()) {
                        addFileToTree(file, unsupportedFileText)
                    }
                }

                return uris.isNotEmpty()
            }
        }

        return false
    }
}