package me.lkl.dalvikus.tree.archive

import brut.androlib.res.decoder.AXmlResourceParser
import brut.androlib.res.decoder.AndroidManifestPullStreamDecoder
import brut.androlib.res.decoder.AndroidManifestResourceParser
import brut.androlib.res.decoder.ResStreamDecoder
import brut.androlib.res.decoder.ResXmlPullStreamDecoder
import me.lkl.dalvikus.tabs.CodeTab
import me.lkl.dalvikus.tabs.TabElement
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.util.guessIfEditableTextually
import me.lkl.dalvikus.util.newXmlSerializer
import java.io.ByteArrayOutputStream
import kotlin.properties.Delegates


class ZipEntryImageNode(
    override val name: String,
    private val fullPath: String,
    private val zipRoot: ZipNode,
    override val parent: ContainerNode?
) : ZipEntryFileNode(name, fullPath, zipRoot, parent) {

    override fun isDisplayable(): Boolean = true
    override fun isEditable(): Boolean = false

    override suspend fun createTab(): TabElement {
        return CodeTab(
            tabName = name,
            tabId = fullPath,
            tabIcon = icon,
            contentProvider = this
        )
    }
}
