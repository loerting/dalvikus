package me.lkl.dalvikus.tabs.contentprovider

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class ContentProvider {
    abstract val editableContent: Boolean
    protected val _contentFlow = MutableStateFlow<ByteArray>(ByteArray(0))
    val contentFlow: StateFlow<ByteArray> = _contentFlow.asStateFlow()

    abstract suspend fun loadContent()

    open suspend fun updateContent(newContent: ByteArray) {
        if(!editableContent) throw IllegalArgumentException("ContentProvider is not editable.")
        _contentFlow.value = newContent
    }

    abstract fun getFileType(): String

    abstract fun getSourcePath(): String?
}