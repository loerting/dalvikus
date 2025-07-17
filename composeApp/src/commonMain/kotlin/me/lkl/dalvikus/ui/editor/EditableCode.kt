package me.lkl.dalvikus.ui.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class EditableCode(
    private val initialCodeProvider: suspend () -> String,
    val codeType: String,
    val onCodeChange: suspend (String) -> Unit
) {
    // State-backed TextFieldValue property, starting empty
    var code: String by mutableStateOf("")
        private set

    fun isLoaded(): Boolean = code.isNotEmpty()

    suspend fun loadCode() {
        val initialText = initialCodeProvider()
        code = initialText
    }

    fun updateCode(newText: String) {
        code = newText
    }
}
