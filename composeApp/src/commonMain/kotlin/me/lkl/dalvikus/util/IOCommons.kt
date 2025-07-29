package me.lkl.dalvikus.util

import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

fun guessIfEditableTextually(
    inputStream: InputStream,
    maxBytesToCheck: Int = 512,
    charset: Charset = StandardCharsets.UTF_8
): Boolean {
    require(maxBytesToCheck >= 0) { "maxBytesToCheck must be non-negative" }

    val bomBytes = ByteArray(4)
    var bomLength = 0

    inputStream.mark(maxBytesToCheck + bomBytes.size)
    try {
        val bytesForBom = inputStream.read(bomBytes, 0, minOf(bomBytes.size, maxBytesToCheck))
        if (bytesForBom > 0) {
            when {
                bytesForBom >= 3 && bomBytes[0].toUByte() == 0xEF.toUByte() && bomBytes[1].toUByte() == 0xBB.toUByte() && bomBytes[2].toUByte() == 0xBF.toUByte() -> {
                    bomLength = 3
                }
                bytesForBom >= 2 && bomBytes[0].toUByte() == 0xFE.toUByte() && bomBytes[1].toUByte() == 0xFF.toUByte() -> {
                    bomLength = 2
                }
                bytesForBom >= 2 && bomBytes[0].toUByte() == 0xFF.toUByte() && bomBytes[1].toUByte() == 0xFE.toUByte() -> {
                    bomLength = 2
                }
                bytesForBom >= 4 && bomBytes[0].toUByte() == 0x00.toUByte() && bomBytes[1].toUByte() == 0x00.toUByte() && bomBytes[2].toUByte() == 0xFE.toUByte() && bomBytes[3].toUByte() == 0xFF.toUByte() -> {
                    bomLength = 4
                }
                bytesForBom >= 4 && bomBytes[0].toUByte() == 0xFF.toUByte() && bomBytes[1].toUByte() == 0xFE.toUByte() && bomBytes[2].toUByte() == 0x00.toUByte() && bomBytes[3].toUByte() == 0x00.toUByte() -> {
                    bomLength = 4
                }
            }
        }
    } finally {
        inputStream.reset()
        if (bomLength > 0) {
            inputStream.skip(bomLength.toLong())
        }
    }

    return inputStream.buffered().use { bufferedStream ->
        val buffer = ByteArray(maxBytesToCheck)
        val bytesRead = bufferedStream.read(buffer)

        if (bytesRead <= 0) {
            return false
        }

        val text = try {
            buffer.copyOf(bytesRead).toString(charset)
        } catch (e: Exception) {
            return false
        }

        val weirdCharCount = text.count { char ->
            val code = char.code
            (code in 0..31 && char !in setOf('\n', '\r', '\t')) || code == 127 || code == 0
        }

        val weirdCharRatio = weirdCharCount.toDouble() / text.length
        val threshold = 0.01

        weirdCharRatio < threshold
    }
}