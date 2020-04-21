package com.sanniou.support.extensions

fun Int.isEven() = this and 1 == 0

fun Int.isOdd() = !isEven()

fun String?.whenEmpty(default: String) =
    if (this == null || this.isEmpty()) {
        default
    } else {
        this
    }

fun String?.orEmpty(vararg strs: String): String =
    this.let {

        if (this.isNullOrEmpty()) {
            for (str in strs) {
                if (str.isNotEmpty()) {
                    return@let str
                }
            }
            throw RuntimeException("all str is empty")

        } else {
            this
        }
    }


inline fun Boolean?.takeIfTrue(block: () -> Unit) {
    if (true == this) block()
}

inline fun Boolean?.takeIfFalse(block: () -> Unit) {
    if (false == this) block()
}

inline fun Boolean?.takeIfNotTrue(block: () -> Unit) {
    if (true != this) block()
}

fun Any?.notNull() = this != null

fun Any?.isNull() = this == null

fun <T> T?.whenNull(default: T) = this ?: default

inline fun <reified T : Any, R> Any?.runType(block: (T) -> R?) = if (this is T) {
    block(this)
} else null

inline fun <reified T : Any> Any?.runType(block: (T) -> Unit) = if (this is T) {
    block(this)
} else null

inline fun <reified T : Any> Any?.checkType(): T? =
    if (this is T) this else null

