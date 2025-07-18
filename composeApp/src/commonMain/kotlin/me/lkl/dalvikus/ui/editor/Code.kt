package me.lkl.dalvikus.ui.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.lkl.dalvikus.io.IOChannel

class Code(
    private val ioChannel: IOChannel<String>,
    val codeType: String,
) {
    var code: String by mutableStateOf("")
        private set

    val isEditable: Boolean = ioChannel.writingSupported
    fun isLoaded(): Boolean = code.isNotEmpty()

    suspend fun loadCode() {
        val initialText = ioChannel.read()
        code = initialText
    }

    fun updateCode(newText: String) {
        code = newText
    }

    suspend fun saveCode() {
        if (isEditable) {
            ioChannel.write(code)
        } else {
            throw UnsupportedOperationException("This Code instance is not editable.")
        }
    }
}
