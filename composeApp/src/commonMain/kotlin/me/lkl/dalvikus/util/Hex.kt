package me.lkl.dalvikus.util

import org.stringtemplate.v4.ST

fun String.is0xHex(): Boolean {
    return this.startsWith("0x") && this.drop(2).all { it.isDigit() || (it in 'a'..'f') || (it in 'A'..'F') }
}

fun Long.to0xHex(): String {
    val sign = if (this < 0) "-" else ""
    return "${sign}0x${this.toString(16).lowercase()}"
}

fun String.base10To0xOrNull(): String? {
    return this.toLongOrNull()?.to0xHex()
}
