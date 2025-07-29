package me.lkl.dalvikus.decompiler

import co.touchlab.kermit.Logger
import me.lkl.dalvikus.dalvikusSettings
import org.benf.cfr.reader.api.CfrDriver
import org.benf.cfr.reader.api.ClassFileSource
import org.benf.cfr.reader.api.OutputSinkFactory
import org.benf.cfr.reader.api.SinkReturns
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

class CFRDecompiler : JavaDecompiler {
    override suspend fun decompileJava(internalName: String, bytes: ByteArray): String {
        try {
            val verbose = dalvikusSettings["decompiler_verbose"] as Boolean
            // 4) CFR bridge: feed the bytes into CFR to get Java source
            val options = mutableMapOf<String, String>().apply {
                put("showversion", verbose.toString())
                put("silent", (!verbose).toString())
                put("dumpclasspath", verbose.toString())

                put("hidelongstrings", "true")
                put("hideutf", "false")
                put("innerclasses", "false")

                put("forcetopsort", "true")
                put("forcetopsortaggress", "true")
            }

            var result: String? = null

            val sinkFactory = object : OutputSinkFactory {
                override fun <T : Any?> getSink(
                    sinkType: OutputSinkFactory.SinkType,
                    sinkClass: OutputSinkFactory.SinkClass
                ): OutputSinkFactory.Sink<T> {
                    return when (sinkType) {
                        OutputSinkFactory.SinkType.JAVA if sinkClass == OutputSinkFactory.SinkClass.DECOMPILED -> OutputSinkFactory.Sink<T> { x ->
                            val dr = x as SinkReturns.Decompiled
                            result = dr.java
                        }
                        OutputSinkFactory.SinkType.EXCEPTION if sinkClass == OutputSinkFactory.SinkClass.EXCEPTION_MESSAGE -> OutputSinkFactory.Sink<T> { x ->
                            val dr = x as SinkReturns.ExceptionMessage
                            val sw = StringWriter().also { dr.thrownException.printStackTrace(PrintWriter(it)) }
                            result = "CFR Exception: ${dr.message}\n$sw"
                        }
                        else -> OutputSinkFactory.Sink<T> { _ ->
                            //throw UnsupportedOperationException("Unsupported sink type or class: $sinkType, $sinkClass")
                        }
                    }
                }

                override fun getSupportedSinks(
                    sinkType: OutputSinkFactory.SinkType,
                    available: Collection<OutputSinkFactory.SinkClass>
                ) = when (sinkType) {
                        OutputSinkFactory.SinkType.JAVA -> listOf(OutputSinkFactory.SinkClass.DECOMPILED)
                        OutputSinkFactory.SinkType.EXCEPTION -> listOf(OutputSinkFactory.SinkClass.EXCEPTION_MESSAGE)
                        else -> emptyList()
                    }
            }

            val classSource = object : ClassFileSource {
                override fun informAnalysisRelativePathDetail(path: String?, detail: String?) = Unit
                override fun getPossiblyRenamedPath(path: String?) = path
                override fun getClassFileContent(path: String): Pair<ByteArray, String> {
                    val pathInternalName = path.substringBeforeLast(".class")
                    return if (pathInternalName == internalName) {
                        Pair.make(bytes, internalName)
                    } else {
                        // fallback to standard resource loading
                        val url = this::class.java.classLoader.getResource("$path")
                            ?: throw IllegalArgumentException("Class file not found: $path")
                        val data = url.openStream().use { it.readBytes() }
                        Pair.make(data, path.substringBeforeLast(".class"))
                    }
                }

                override fun addJar(path: String) = throw UnsupportedOperationException()
            }

            val driver = CfrDriver.Builder()
                .withClassFileSource(classSource)
                .withOutputSink(sinkFactory)
                .withOptions(options)

                .build()
            driver.analyse(Collections.singletonList(internalName))

            return result?.takeIf { it.isNotBlank() } ?: "No CFR output received"
        } catch (e: Throwable) {
            Logger.e("CFR decompilation error", e)
            val sw = StringWriter().also { e.printStackTrace(PrintWriter(it)) }
            return "Decompilation error: ${e.message}\n$sw"
        }
    }
}
