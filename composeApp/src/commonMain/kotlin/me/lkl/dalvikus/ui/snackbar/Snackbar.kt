package me.lkl.dalvikus.ui.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.lkl.dalvikus.ui.util.toOneLiner
import java.awt.datatransfer.StringSelection
import java.io.PrintWriter
import java.io.StringWriter

class SnackbarManager(
    val snackbarHostState: SnackbarHostState,
    private val clipboardManager: Clipboard,
    private val coroutineScope: CoroutineScope,
    private val snackbarResources: SnackbarResources
) {

    fun showError(throwable: Throwable) {
        coroutineScope.launch {
            val message = snackbarResources.snackFailed.format(throwable.toOneLiner())
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = snackbarResources.copy,
                duration = SnackbarDuration.Long,
                withDismissAction = true
            )
            if (result == SnackbarResult.ActionPerformed) {
                val fullStackTrace = StringWriter().also { throwable.printStackTrace(PrintWriter(it)) }.toString()
                clipboardManager.setClipEntry(ClipEntry(StringSelection(fullStackTrace)))
            }
        }
    }

    fun showMessage(message: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
        }
    }

    fun showSuccess() = showMessage(snackbarResources.snackSuccess)
    fun showAssembleError(lexerErrors: Int, parserErrors: Int) {
        coroutineScope.launch {
            val message = snackbarResources.snackAssembleError.format(lexerErrors, parserErrors)
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
        }
    }
}

data class SnackbarResources(
    val copy: String,
    val snackFailed: String,
    val snackSuccess: String,
    val snackAssembleError: String,
)