package me.lkl.dalvikus.tabs.contentprovider

class NopContentProvider : ContentProvider() {
    override val editableContent: Boolean = false

    override suspend fun loadContent() {}
    override fun getFileType(): String = "txt"

    override fun getSourcePath(): String? = null
}