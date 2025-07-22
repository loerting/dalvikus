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
import me.lkl.dalvikus.ui.editor.highlight.CodeHighlightColors
import me.lkl.dalvikus.ui.editor.highlight.highlightCode

class EditorViewModel(private val code: Code, val highlightColors: CodeHighlightColors) {
    var isLoaded by mutableStateOf(false)
        private set

    var highlightedText by mutableStateOf(AnnotatedString(code.code))
        private set

    val fontSize = (dalvikusSettings["font_size"] as Int).sp

    suspend fun loadCode() {
        withContext(Dispatchers.IO) {
            code.loadCode()
        }
        isLoaded = true
        refreshHighlight()
    }

    suspend fun refreshHighlight() {
        highlightedText = withContext(Dispatchers.Default) {
            highlightCode(code, highlightColors)
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
        code.updateCode(newText)

        if (dalvikusSettings["save_automatically"] && code.isEditable) {
            saveCode(coroutineScope)
        } else {
            code.hasUnsavedChanges = true
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
        coroutineScope.launch {
            code.saveCode()
        }
    }

    fun getCode() = code.code
    fun isEditable() = code.isEditable
}
