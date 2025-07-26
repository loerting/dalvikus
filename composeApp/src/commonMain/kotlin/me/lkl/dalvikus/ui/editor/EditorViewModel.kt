package me.lkl.dalvikus.ui.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.ui.editor.highlight.CodeHighlightColors
import me.lkl.dalvikus.ui.editor.highlight.highlightCode

class EditorViewModel(private val tab: TabElement, val highlightColors: CodeHighlightColors) {
    var isLoaded by mutableStateOf(false)
        private set

    var highlightedText by mutableStateOf(AnnotatedString(tab.contentProvider.contentFlow.value.decodeToString()))
        private set

    val fontSize = (dalvikusSettings["font_size"] as Int).sp

    var internalContent by mutableStateOf("")

    suspend fun loadCode() {
        withContext(Dispatchers.IO) {
            tab.contentProvider.loadContent()
            internalContent = tab.contentProvider.contentFlow.value.decodeToString()

            isLoaded = true
            refreshHighlight()
        }
    }

    suspend fun refreshHighlight() {
        highlightedText = withContext(Dispatchers.Default) {
                highlightCode(internalContent, tab.contentProvider.getFileType(), highlightColors)
            }
    }

    fun onCodeChanged(oldText: String, newText: String, coroutineScope: CoroutineScope) {
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
        internalContent = newText

        if (dalvikusSettings["save_automatically"]) {
            saveCode(coroutineScope)
        } else {
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
            tab.contentProvider.updateContent(internalContent.toByteArray())
            tab.hasUnsavedChanges.value = false
        }
    }

    fun isEditable() = tab.contentProvider.editableContent
}
