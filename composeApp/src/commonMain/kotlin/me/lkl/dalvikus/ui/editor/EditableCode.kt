package me.lkl.dalvikus.ui.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class EditableCode(
    private val initialCodeProvider: suspend () -> String,
    val codeType: String,
    private val onCodeChange: suspend (String) -> Unit
) {
    // State-backed TextFieldValue property, starting empty
    var textFieldValue by mutableStateOf(TextFieldValue(""))
        private set

    fun isLoaded(): Boolean = textFieldValue.text.isNotEmpty()

    suspend fun loadCode() {
        val initialText = initialCodeProvider()
        textFieldValue = TextFieldValue(
            text = initialText,
            selection = TextRange(0)
        )
    }

    suspend fun updateCode(newTextFieldValue: TextFieldValue) {
        textFieldValue = newTextFieldValue
        onCodeChange(newTextFieldValue.text)
    }
}
