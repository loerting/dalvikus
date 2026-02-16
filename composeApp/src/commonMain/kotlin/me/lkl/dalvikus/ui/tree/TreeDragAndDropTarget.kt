package me.lkl.dalvikus.ui.tree

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import me.lkl.dalvikus.ui.addFileToTree
import me.lkl.dalvikus.ui.snackbar.SnackbarManager
import java.io.File
import java.io.Reader
import java.net.URI

class TreeDragAndDropTarget(val snackbarManager: SnackbarManager, val unsupportedFileText: String) : DragAndDropTarget {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onDrop(event: DragAndDropEvent): Boolean {
        val flavors = event.awtTransferable.transferDataFlavors
        for (flavor in flavors) {
            if (flavor.isMimeTypeEqual("application/x-java-file-list")) {
                val files = event.awtTransferable.getTransferData(flavor) as? List<*>
                files?.filterIsInstance<File>()?.forEach { file ->
                    addFileToTree(file, snackbarManager, unsupportedFileText)
                }
                if (files?.isNotEmpty() == true) return true
            }

            if (flavor.isMimeTypeEqual("text/uri-list") || flavor.isMimeTypeEqual("text/plain")) {
                val dataStr: String = when (val rawData = event.awtTransferable.getTransferData(flavor)) {
                    is String -> rawData
                    is Reader -> rawData.readText()
                    else -> continue
                }
                // Split into lines, ignore comments
                val uris = dataStr.lines()
                    .mapNotNull { line -> line.trim().takeIf { it.isNotBlank() && !it.startsWith("#") } }
                    .mapNotNull { uri ->
                        resolveUriToFile(uri)
                    }
                uris.forEach { file ->
                    if (file.exists()) {
                        addFileToTree(file, snackbarManager, unsupportedFileText)
                    }
                }
                if (uris.isNotEmpty()) return true
            }
        }

        return false
    }
    private fun resolveUriToFile(uriString: String): File? {
        try {
            val uri = URI(uriString)

            if (uri.scheme == "file") {
                return File(uri)
            }

            // Case B: MTP/GVFS Fallback (Linux Specific)
            // MTP URIs look like: mtp://[usb:001,002]/Interner...
            // These are often mounted at: /run/user/$UID/gvfs/mtp:host=.../Interner...
            if (uri.scheme == "mtp" || uri.scheme == "gphoto2") {
                // 1. Try to guess the runtime dir
                val runtimeDir = System.getenv("XDG_RUNTIME_DIR")
                    ?: "/run/user/${System.getProperty("user.uid") ?: "1000"}"

                val gvfsDir = File(runtimeDir, "gvfs")

                if (gvfsDir.exists() && gvfsDir.isDirectory) {
                    // We search the gvfs mounts for a folder that contains our file path
                    // The URI path usually starts with slash, e.g. "/Internal Storage/foo.apk"
                    val relativePath = uri.path.trimStart('/')

                    val foundFile = gvfsDir.listFiles()?.firstNotNullOfOrNull { mountPoint ->
                        val candidate = File(mountPoint, relativePath)
                        if (candidate.exists()) candidate else null
                    }

                    if (foundFile != null) return foundFile
                }
            }
        } catch (e: Exception) {
            println("Failed to resolve URI '$uriString': ${e.message}")
        }
        return null
    }
}