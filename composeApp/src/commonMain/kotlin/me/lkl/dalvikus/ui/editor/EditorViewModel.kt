package me.lkl.dalvikus.ui.editor

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import brut.androlib.res.data.ResResSpec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tree.archive.ApkNode
import me.lkl.dalvikus.ui.editor.highlight.CodeHighlightColors
import me.lkl.dalvikus.ui.editor.highlight.highlightCode
import me.lkl.dalvikus.ui.editor.suggestions.AssistPopupState
import me.lkl.dalvikus.ui.uiTreeRoot
import java.math.BigInteger

const val maxEditorFileSize = 128 * 1024 // 128 KiB
const val maxEditorLines = 10000 // 10,000 lines

class EditorViewModel(private val tab: TabElement) {
    var isLoaded by mutableStateOf(false)
        private set

    lateinit var highlightColors: CodeHighlightColors

    var highlightedText by mutableStateOf(AnnotatedString(tab.contentProvider.contentFlow.value.decodeToString()))
        private set

    val fontSize = (dalvikusSettings["font_size"] as Int).sp

    var internalContent by mutableStateOf(TextFieldValue())
        private set

    val assistPopupState = mutableStateOf(AssistPopupState())

    val editable by mutableStateOf(tab.contentProvider.isEditable())

    var openable by
    mutableStateOf(tab.contentProvider.getSizeEstimate() < maxEditorFileSize && tab.contentProvider.isDisplayable())
        private set

    val popupKeyEvents = Modifier.onPreviewKeyEvent { event ->
        val apState = assistPopupState.value

        if (event.type != KeyEventType.KeyDown || !editable) return@onPreviewKeyEvent false

        if (apState.popupDismissed) {
            if ((event.isCtrlPressed || event.isMetaPressed) && event.key == Key.Spacebar) {
                assistPopupState.value = assistPopupState.value.copy(popupDismissed = false)
                return@onPreviewKeyEvent true
            }
            return@onPreviewKeyEvent false
        }

        return@onPreviewKeyEvent when (event.key) {
            Key.DirectionDown -> {
                assistPopupState.value = apState.copy(selectedIndex = apState.selectedIndex + 1)
                true
            }

            Key.DirectionUp -> {
                assistPopupState.value = apState.copy(selectedIndex = apState.selectedIndex - 1)
                true
            }

            Key.Enter -> {
                assistPopupState.value = apState.copy(enterRequest = true)
                true
            }

            Key.Escape -> {
                assistPopupState.value = AssistPopupState()
                true
            }

            else -> false
        }
    }

    suspend fun loadCode() {
        withContext(Dispatchers.IO) {
            tab.contentProvider.loadContent()
            internalContent = internalContent.copy(text = tab.contentProvider.contentFlow.value.decodeToString())

            if (internalContent.text.lines().size > maxEditorLines) {
                openable = false
                return@withContext
            }

            isLoaded = true
            refreshHighlight()
        }
    }

    suspend fun refreshHighlight() {
        highlightedText = withContext(Dispatchers.Default) {
            highlightCode(internalContent.text, tab.contentProvider.getFileType(), highlightColors)
        }
    }

    fun changeContent(newTextFieldValue: TextFieldValue, coroutineScope: CoroutineScope, checkNewline: Boolean = true) {
        val oldText = internalContent.text
        val newText = newTextFieldValue.text

        val diffIndex = newText.commonPrefixWith(oldText).length

        val newTextSuffix = newText.substring(diffIndex)
        val oldTextSuffix = oldText.substring(diffIndex)
        val commonSuffixLength = newTextSuffix.commonSuffixWith(oldTextSuffix).length

        val oldSuffixStart = oldText.length - commonSuffixLength
        val newSuffixStart = newText.length - commonSuffixLength

        val safeNewSuffixStart = maxOf(newSuffixStart, diffIndex)
        val insertedText = newText.substring(diffIndex, safeNewSuffixStart)

        val isNewlineInserted = insertedText == "\n"

        if (checkNewline && isNewlineInserted) {
            val caretPosition = newTextFieldValue.selection.start
            if (caretPosition > 0) {
                val beforeNewline = newText.substring(0, caretPosition - 1)
                val currentLineStart = beforeNewline.lastIndexOf('\n') + 1
                val currentLine = beforeNewline.substring(currentLineStart)
                val leadingIndent = currentLine.takeWhile { it == ' ' || it == '\t' }

                val updatedText = newText.substring(0, caretPosition) + leadingIndent + newText.substring(caretPosition)
                val updatedSelection = TextRange(caretPosition + leadingIndent.length)

                changeContent(
                    newTextFieldValue.copy(text = updatedText, selection = updatedSelection),
                    coroutineScope,
                    checkNewline = false
                )
                return
            }
        }

        mimicOldHighlight(newText, diffIndex, newSuffixStart, oldSuffixStart, insertedText)

        if (newText.length > oldText.length) {
            assistPopupState.value = assistPopupState.value.copy(
                popupDismissed = false,
                selectedIndex = 0
            )
        }
        // Update the internal content with the new text
        internalContent = newTextFieldValue.copy(text = newText)


        if (dalvikusSettings["save_automatically"]) {
            saveCode(coroutineScope)
        } else {
            // TODO this does not take into account the case where the text is by selection replacement, but i don't want a O(n) equals check here
            if (newText.length != oldText.length)
                tab.hasUnsavedChanges.value = true
        }
    }

    /**
     * Inserts the changes into the old highlight and uses it temporarily as the current one.
     */
    private fun mimicOldHighlight(
        newText: String,
        diffIndex: Int,
        newSuffixStart: Int,
        oldSuffixStart: Int,
        insertedText: String
    ) {
        val offsetAfterChange = newSuffixStart - oldSuffixStart

        val builder = AnnotatedString.Builder()

        builder.append(newText.substring(0, diffIndex))
        highlightedText.spanStyles.forEach { span ->
            if (span.end <= diffIndex) {
                builder.addStyle(span.item, span.start, span.end)
            }
        }

        builder.append(insertedText)

        builder.append(newText.substring(newSuffixStart))
        highlightedText.spanStyles.forEach { span ->
            if (span.start >= oldSuffixStart) {
                builder.addStyle(span.item, span.start + offsetAfterChange, span.end + offsetAfterChange)
            }
        }

        highlightedText = builder.toAnnotatedString()
    }

    fun saveCode(coroutineScope: CoroutineScope) {
        if (!isLoaded) throw IllegalArgumentException("code not initialized.")
        coroutineScope.launch {
            tab.contentProvider.updateContent(internalContent.text.toByteArray())
            tab.hasUnsavedChanges.value = false
        }
    }

    fun hasUnsavedChanges(): Boolean {
        return isLoaded && tab.hasUnsavedChanges.value
    }


    fun getFileType(): String = tab.contentProvider.getFileType()
    fun replaceTextAndMoveCaret(startIndex: Int, stopIndex: Int, text: String, coroutineScope: CoroutineScope) {
        if (!isLoaded) return

        val currentText = internalContent.text
        val newText = currentText.substring(0, startIndex) + text + currentText.substring(stopIndex)

        val newTextFieldValue = internalContent.copy(
            text = newText,
            selection = TextRange(startIndex + text.length, startIndex + text.length)
        )
        changeContent(newTextFieldValue, coroutineScope)
    }

    fun tryResolveResIdText(unsignedValue: BigInteger): String {
        val apks = uiTreeRoot.childrenFlow.value.filterIsInstance<ApkNode>()
        val resIds = mutableListOf<ResResSpec>()
        for (apk in apks) {
            val resResSpec = apk.getResourceById(unsignedValue.toInt())
            if (resResSpec != null) {
                resIds.add(resResSpec)
            }
        }
        return if (resIds.isEmpty()) "Resource not found"
        else resIds.joinToString(", ") { "R.${it.type.name}.${it.name}" }
    }
}
