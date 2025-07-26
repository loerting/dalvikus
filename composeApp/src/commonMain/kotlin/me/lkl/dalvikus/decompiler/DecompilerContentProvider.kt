package me.lkl.dalvikus.decompiler

import me.lkl.dalvikus.tabs.contentprovider.ContentProvider
import me.lkl.dalvikus.tree.dex.DexEntryClassNode

class DecompilerContentProvider(private val dexEntryClassNode: DexEntryClassNode, private val decompilerProvider: () -> Decompiler) : ContentProvider() {
    override val editableContent: Boolean = false

    override suspend fun loadContent() {
        _contentFlow.value = decompilerProvider().decompile(dexEntryClassNode.getClassDef()).toByteArray()
    }

    override fun getFileType(): String = "java"

    override fun getSourcePath(): String? = dexEntryClassNode.getSourcePath()

}
