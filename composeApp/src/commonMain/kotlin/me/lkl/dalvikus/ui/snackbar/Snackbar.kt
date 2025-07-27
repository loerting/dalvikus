package me.lkl.dalvikus.ui.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.lkl.dalvikus.ui.util.toOneLiner
import org.jetbrains.skiko.ClipboardManager
import java.io.PrintWriter
import java.io.StringWriter

internal val snackbarHostState: SnackbarHostState = SnackbarHostState()

/**
 * Initialized in App.kt
 */
internal lateinit var snackbarCoroutineScope: CoroutineScope
internal lateinit var snackbarCopyText: String
internal lateinit var snackbarOperationFailedText: String
internal lateinit var snackbarOperationSuccessText: String
internal lateinit var snackbarClipboardManager: Clipboard

fun showSnackbarError(throwable: Throwable) {
    snackbarCoroutineScope.launch {
        val result = snackbarHostState.showSnackbar(
            // TODO find a better way of formatting stringResource later.
            message = snackbarOperationFailedText + " " + throwable.toOneLiner(),
            actionLabel = snackbarCopyText,
            duration = SnackbarDuration.Long,
            withDismissAction = true
        )
        if (result == SnackbarResult.ActionPerformed) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)
            val fullStackTrace = sw.toString()
            snackbarClipboardManager.setClipEntry(TextClipboardEntry(fullStackTrace))
        }
    }
}

fun showSnackbarMessage(string: String) {
    snackbarCoroutineScope.launch {
        snackbarHostState.showSnackbar(
            message = string,
            duration = SnackbarDuration.Short,
            withDismissAction = true
        )
    }
}

fun showSnackbarSuccess() {
    showSnackbarMessage(snackbarOperationSuccessText)
}
