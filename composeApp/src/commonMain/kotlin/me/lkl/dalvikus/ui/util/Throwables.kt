package me.lkl.dalvikus.ui.util

fun Throwable.toOneLiner(maxLength: Int = 100): String {
    return this.message?.let { message ->
        if (message.length > maxLength) {
            message.take(maxLength) + "..."
        } else {
            message
        }
    } ?: this.javaClass.simpleName
}