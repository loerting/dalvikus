package me.lkl.dalvikus.tree.archive

import brut.androlib.res.decoder.AXmlResourceParser
import brut.androlib.res.decoder.AndroidManifestPullStreamDecoder
import brut.androlib.res.decoder.AndroidManifestResourceParser
import brut.androlib.res.decoder.ResStreamDecoder
import brut.androlib.res.decoder.ResXmlPullStreamDecoder
import com.android.apksig.internal.apk.AndroidBinXmlParser
import me.lkl.dalvikus.tree.ContainerNode
import me.lkl.dalvikus.util.guessIfEditableTextually
import me.lkl.dalvikus.util.newXmlSerializer
import org.xmlpull.v1.XmlSerializer
import java.io.ByteArrayOutputStream
import kotlin.properties.Delegates


class ApkEntryXmlNode(
    override val name: String,
    private val fullPath: String,
    private val apkRoot: ApkNode,
    override val parent: ContainerNode?
) : ZipEntryFileNode(name, fullPath, apkRoot, parent) {

    private var isManifest by Delegates.notNull<Boolean>()
    private var isPlaintext by Delegates.notNull<Boolean>()

    init {
        require(name.endsWith(".xml", ignoreCase = true)) { "ZipEntryXmlNode must be initialized with a .xml file" }
        isManifest = fullPath == "AndroidManifest.xml"
        isPlaintext = !isManifest && guessIfEditableTextually(apkRoot.readEntry(fullPath).inputStream())
    }

    override suspend fun getContent(): ByteArray {
        val readEntry = apkRoot.readEntry(fullPath)
        if (isPlaintext) return readEntry

        val parser = if (isManifest) {
            AndroidManifestResourceParser(apkRoot.resTable)
        } else {
            AXmlResourceParser(apkRoot.resTable)
        }

        val decoder: ResStreamDecoder = if (isManifest) {
            AndroidManifestPullStreamDecoder(parser, newXmlSerializer())
        } else {
            ResXmlPullStreamDecoder(parser, newXmlSerializer())
        }

        return ByteArrayOutputStream().use { output ->
            decoder.decode(readEntry.inputStream(), output)
            output.toByteArray()
        }
    }

    override suspend fun writeContent(newContent: ByteArray) {
        if(!isPlaintext) {
            throw IllegalStateException("Cannot write content to non-plaintext XML entry: $fullPath")
        }
        // TODO currently I haven't found a good way to write XML back to binary format.
        apkRoot.updateEntry(fullPath, newContent)
        parent?.notifyChanged()
    }

    override fun isDisplayable(): Boolean = true
    override fun isEditable(): Boolean = isPlaintext
}
