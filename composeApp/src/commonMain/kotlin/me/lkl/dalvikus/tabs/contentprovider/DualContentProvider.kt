package me.lkl.dalvikus.tabs.contentprovider

open class DualContentProvider(val contentProvider: ContentProvider, val contentProvider2: ContentProvider) : ContentProvider() {
    override val contentFlow
        get() = if (firstContentProvider) contentProvider.contentFlow else contentProvider2.contentFlow
    open var firstContentProvider: Boolean = true
    override val editableContent: Boolean
        get() = if (firstContentProvider) contentProvider.editableContent else contentProvider2.editableContent

    override suspend fun loadContent() {
        if(firstContentProvider)
            contentProvider.loadContent()
        else
            contentProvider2.loadContent()
    }

    override suspend fun updateContent(newContent: ByteArray) {
        if(firstContentProvider)
            contentProvider.updateContent(newContent)
        else
            contentProvider2.updateContent(newContent)
    }

    override fun getFileType(): String = if(firstContentProvider) contentProvider.getFileType() else contentProvider2.getFileType()

    override fun getSourcePath(): String? = if(firstContentProvider) contentProvider.getSourcePath() else contentProvider2.getSourcePath()

}