package me.lkl.dalvikus.util

import brut.androlib.Config
import brut.androlib.exceptions.AndrolibException
import brut.xmlpull.MXSerializer
import org.xmlpull.v1.XmlSerializer


fun getApkToolConfig(): Config {
    val config = Config()
    config.isKeepBrokenResources = true
    return config
}

@Throws(AndrolibException::class)
fun newXmlSerializer(): XmlSerializer {
    try {
        val serial: XmlSerializer = MXSerializer()
        serial.setFeature(MXSerializer.FEATURE_ATTR_VALUE_NO_ESCAPE, true)
        serial.setProperty(MXSerializer.PROPERTY_DEFAULT_ENCODING, "utf-8")
        serial.setProperty(MXSerializer.PROPERTY_INDENTATION, "    ")
        serial.setProperty(MXSerializer.PROPERTY_LINE_SEPARATOR, System.lineSeparator())
        return serial
    } catch (ex: IllegalArgumentException) {
        throw AndrolibException(ex)
    } catch (ex: IllegalStateException) {
        throw AndrolibException(ex)
    }
}