package me.lkl.dalvikus.ui.editor

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import brut.androlib.res.data.ResResSpec
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.lkl.dalvikus.dalvikusSettings
import me.lkl.dalvikus.errorreport.crtExHandler
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tree.archive.ApkNode
import me.lkl.dalvikus.tree.dex.AssemblyException
import me.lkl.dalvikus.theme.CodeHighlightColors
import me.lkl.dalvikus.ui.editor.highlight.highlightCode
import me.lkl.dalvikus.ui.editor.suggestions.AssistPopupState
import me.lkl.dalvikus.ui.editor.suggestions.MaximumAssistSuggestions
import me.lkl.dalvikus.ui.snackbar.SnackbarManager
import me.lkl.dalvikus.ui.uiTreeRoot
import me.lkl.dalvikus.util.SearchOptions
import me.lkl.dalvikus.util.createSearchMatcher
import java.math.BigInteger

class EditorViewModel(private val tab: TabElement) {
    var isLoaded by mutableStateOf(false)
        private set

    lateinit var highlightColors: CodeHighlightColors

    var highlightedText by mutableStateOf(AnnotatedString(tab.contentProvider.contentFlow.value.decodeToString()))
        private set

    val fontSize
        get() = (dalvikusSettings["font_size"] as Int).sp

    var internalContent by mutableStateOf(TextFieldValue())
        private set

    val assistPopupState = mutableStateOf(AssistPopupState())

    val editable by mutableStateOf(tab.contentProvider.isEditable())

    var isSaving by mutableStateOf(false)
        private set

    var openable by
    mutableStateOf(tab.contentProvider.getSizeEstimate() < getMaxFileBytes() && tab.contentProvider.isDisplayable())
        private set

    private fun getMaxFileBytes(): Int = ((dalvikusSettings["max_file_size"] as Int) * 1024)

    var isSearchActive by mutableStateOf(false)
    var searchOptions by mutableStateOf(SearchOptions())

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
        val max = MaximumAssistSuggestions // total suggestions count

        return@onPreviewKeyEvent when (event.key) {
            Key.DirectionDown -> {
                val newIndex = (apState.selectedIndex + 1) % max
                assistPopupState.value = apState.copy(selectedIndex = newIndex)
                true
            }

            Key.DirectionUp -> {
                val newIndex = (apState.selectedIndex - 1 + max) % max
                assistPopupState.value = apState.copy(selectedIndex = newIndex)
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
        Logger.i("Loading code for tab: ${tab.tabId}")
        withContext(Dispatchers.Default) {
            tab.contentProvider.loadContent()

            internalContent = internalContent.copy(text = tab.contentProvider.contentFlow.value.decodeToString())

            val charCount = internalContent.text.length

            Logger.i("Loaded ${tab.contentProvider.getFileType()} with $charCount characters.")

            if (charCount * 2 > getMaxFileBytes()) {
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

    fun changeContent(
        newTextFieldValue: TextFieldValue,
        coroutineScope: CoroutineScope,
        editorAdditions: Boolean = true
    ) {
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

        if (editorAdditions) {
            val caretPosition = newTextFieldValue.selection.start
            if (caretPosition > 0) {
                fun insertTextAtCaret(suffix: String, moveCaretBack: Boolean = true) {
                    val updatedText =
                        newText.substring(0, caretPosition) + suffix + newText.substring(caretPosition)
                    val updatedSelection = TextRange(
                        caretPosition - if (moveCaretBack) 0 else -suffix.length
                    )

                    changeContent(
                        newTextFieldValue.copy(text = updatedText, selection = updatedSelection),
                        coroutineScope,
                        editorAdditions = false
                    )
                }

                when (insertedText) {
                    "\n" -> {
                        val beforeNewline = newText.substring(0, caretPosition - 1)
                        val currentLineStart = beforeNewline.lastIndexOf('\n') + 1
                        val currentLine = beforeNewline.substring(currentLineStart)
                        val leadingIndent = currentLine.takeWhile { it == ' ' || it == '\t' }

                        insertTextAtCaret(leadingIndent, moveCaretBack = false)
                        return
                    }

                    "{" -> {
                        insertTextAtCaret("}")
                        return
                    }

                    "(" -> {
                        insertTextAtCaret(")")
                        return
                    }

                    "L" -> {
                        insertTextAtCaret(";")
                        return
                    }

                    "\"" -> {
                        insertTextAtCaret("\"")
                        return
                    }
                }
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

        if (!tab.hasUnsavedChanges.value && newText != oldText)
            tab.hasUnsavedChanges.value = true
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

    fun saveCode(coroutineScope: CoroutineScope, snackbarManager: SnackbarManager) {
        if (!isLoaded) throw IllegalArgumentException("code not initialized.")
        isSaving = true
        coroutineScope.launch(Dispatchers.Default + crtExHandler) {
            try {
                tab.contentProvider.updateContent(internalContent.text.toByteArray())
                tab.hasUnsavedChanges.value = false
            } catch(e: AssemblyException) {
                snackbarManager.showAssembleError(e.errorLines)
            } catch (e: Exception) {
                Logger.e("Failed to save code for tab: ${tab.tabId}", e)
                snackbarManager.showError(e)
            }
            withContext(Dispatchers.Main) {
                isSaving = false
            }
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

    fun getClippedHighlightedText(startLine: Int, endLine: Int): AnnotatedString {
        val text = highlightedText.text
        val length = text.length

        val lineStartIndices = mutableListOf(0) // start index of each line; first line starts at 0

        // Collect indices where lines start
        for (i in text.indices) {
            if (text[i] == '\n' && i + 1 < length) {
                lineStartIndices.add(i + 1)
            }
        }

        val totalLines = lineStartIndices.size

        // Clamp startLine and endLine within valid range
        val safeStartLine = startLine.coerceAtLeast(0).coerceAtMost(totalLines - 1)
        val safeEndLine = endLine.coerceAtLeast(safeStartLine).coerceAtMost(totalLines - 1)

        // Calculate start and end character indices for substring
        val startIndex = lineStartIndices[safeStartLine]
        val endIndex = if (safeEndLine + 1 < totalLines) {
            lineStartIndices[safeEndLine + 1] - 1 // position of newline at end of line
        } else {
            length // end of text (last line)
        }

        // Number of lines before and after the clipped block
        val linesBefore = safeStartLine
        val linesAfter = totalLines - safeEndLine - 1

        return buildAnnotatedString {
            // Append empty lines before (with newlines to keep line count)
            append("\n".repeat(linesBefore))
            // Append the clipped visible lines, preserving formatting and spans
            append(highlightedText, startIndex, endIndex)
            // Append empty lines afte
            append("\n".repeat(linesAfter))
        }
    }

    fun searchAndSelect(
        searchText: String,
        direction: SearchDirection,
        lineHeightPx: Float,
        verticalScrollState: ScrollState,
        coroutine: CoroutineScope
    ): Int {
        if (!isLoaded || searchText.isEmpty()) return 0

        val matcher = createSearchMatcher(searchText, this.searchOptions)
        val text = internalContent.text
        val caretPosition = internalContent.selection.start

        val matches = mutableListOf<IntRange>()
        var index = 0
        while (index < text.length) {
            val start = text.indexOf(searchText, index, ignoreCase = !searchOptions.caseSensitive)
            if (start == -1) break

            val candidate = text.substring(start, start + searchText.length)
            if (matcher(candidate)) {
                matches.add(start until (start + searchText.length))
            }
            index = start + 1
        }

        if (matches.isEmpty()) {
            return 0
        }

        val matchRange = when (direction) {
            SearchDirection.NEXT -> matches.firstOrNull { it.first > caretPosition } ?: matches.first()
            SearchDirection.PREVIOUS -> matches.lastOrNull { it.last < caretPosition } ?: matches.last()
        }

        internalContent = internalContent.copy(
            selection = TextRange(matchRange.first, matchRange.last + 1)
        )

        coroutine.launch(Dispatchers.Main) {
            val textUpToMatch = internalContent.text.substring(0, matchRange.first)
            val lineIndex = textUpToMatch.count { it == '\n' }
            verticalScrollState.animateScrollTo(((lineIndex - 10) * lineHeightPx).toInt())
        }

        return matches.size
    }

}