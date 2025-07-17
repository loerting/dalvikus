package me.lkl.dalvikus.ui.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Code(
    private val initialCodeProvider: suspend () -> String,
    val codeType: String,
    val isEditable: Boolean = true,
    val onCodeChange: suspend (String) -> Unit = { _ -> }
) {
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
