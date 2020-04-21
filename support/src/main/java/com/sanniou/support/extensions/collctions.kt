package com.sanniou.support.extensions

import java.util.*

fun MutableList<out Any>.deleteLast() = this.removeAt(this.lastIndex)

fun <T> Collection<T>.toString(stringCover: (element: T) -> String = { it.toString() }): String {
    if (isEmpty()) {
        return "[ ]"
    }
    val builder = StringBuilder("[")
    forEach {
        builder.append(stringCover(it)).append("\n")
    }
    return builder.append("]").toString()
}

/**
 * list1, list2的差集（在list1，不在list2中的对象），产生新List.
 *
 * 与List.removeAll()相比，会计算元素出现的次数，如"a"在list1出现两次，而在list2中只出现一次，则差集里会保留一个"a".
 */
fun <T> Collection<T>.difference(others: List<T>): List<T> {
    val result: MutableList<T> = ArrayList(this)
    val iterator = others.iterator()
    while (iterator.hasNext()) {
        result.remove(iterator.next())
    }
    return result
}

