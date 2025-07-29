package me.lkl.dalvikus.ui.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.ui.editor.highlight.CodeHighlightColors
import me.lkl.dalvikus.ui.editor.highlight.highlightCode
import me.lkl.dalvikus.ui.editor.suggestions.AssistPopupState

class EditorViewModel(private val tab: TabElement, val highlightColors: CodeHighlightColors) {
    var isLoaded by mutableStateOf(false)
        private set

    var highlightedText by mutableStateOf(AnnotatedString(tab.contentProvider.contentFlow.value.decodeToString()))
        private set

    val fontSize = (dalvikusSettings["font_size"] as Int).sp

    var internalContent by mutableStateOf(TextFieldValue())
        private set

    val assistPopupState = mutableStateOf(AssistPopupState())

    val popupKeyEvents = Modifier.onPreviewKeyEvent { event ->
        val apState = assistPopupState.value

        if (event.type != KeyEventType.KeyDown || !isEditable()) return@onPreviewKeyEvent false

        if(apState.popupDismissed) {
            if((event.isCtrlPressed || event.isMetaPressed) && event.key == Key.Spacebar) {
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

            isLoaded = true
            refreshHighlight()
        }
    }

    suspend fun refreshHighlight() {
        highlightedText = withContext(Dispatchers.Default) {
                highlightCode(internalContent.text, tab.contentProvider.getFileType(), highlightColors)
            }
    }

    fun changeContent(newTextFieldValue: TextFieldValue, coroutineScope: CoroutineScope) {
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
        if (isNewlineInserted) {
            // TODO implement new line indentation
        }

        mimicOldHighlight(newText, diffIndex, newSuffixStart, oldSuffixStart, insertedText)

        if(newText.length > oldText.length) {
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
            if(newText.length != oldText.length)
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
        if(!isLoaded) throw IllegalArgumentException("code not initialized.")
        coroutineScope.launch {
            tab.contentProvider.updateContent(internalContent.text.toByteArray())
            tab.hasUnsavedChanges.value = false
        }
    }

    fun hasUnsavedChanges(): Boolean {
        return isLoaded && tab.hasUnsavedChanges.value
    }

    fun isEditable() = tab.contentProvider.editableContent

    fun insertAtCaret(text: String) {
        if (!isLoaded) return

        val currentText = internalContent.text
        val selectionStart = internalContent.selection.start
        val selectionEnd = internalContent.selection.end

        val newText = currentText.substring(0, selectionStart) + text + currentText.substring(selectionEnd)

        internalContent = internalContent.copy(text = newText)
    }

    fun getFileType(): String = tab.contentProvider.getFileType()
    fun replaceTextAndMoveCaret(startIndex: Int, stopIndex: Int, text: String, coroutineScope: CoroutineScope) {
        if (!isLoaded) return

        val currentText = internalContent.text
        val newText = currentText.substring(0, startIndex) + text + currentText.substring(stopIndex)

        val newTextFieldValue = internalContent.copy(text = newText, selection = TextRange(startIndex + text.length, startIndex + text.length))
        changeContent(newTextFieldValue, coroutineScope)
    }
}
