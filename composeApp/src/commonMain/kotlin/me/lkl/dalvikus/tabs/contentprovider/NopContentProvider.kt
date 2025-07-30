package me.lkl.dalvikus.tabs.contentprovider

class NopContentProvider : ContentProvider() {
    override suspend fun loadContent() {}
    override fun getFileType(): String = "txt"

    override fun getSourcePath(): String? = null
    override fun getSizeEstimate(): Long = 0L

    override fun isEditableTextually(): Boolean = false
}