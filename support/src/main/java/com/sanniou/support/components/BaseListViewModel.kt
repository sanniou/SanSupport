package com.sanniou.support.components

import com.sanniou.multiitem.DataItem
import com.sanniou.multiitem.MultiItemArrayList
import com.sanniou.support.extensions.deleteLast

open class BaseListViewModel : BaseViewModel() {

    val list = MultiItemArrayList<DataItem>()

    fun add(item: DataItem) = list.add(item)

    fun set(index: Int, item: DataItem) = list.set(index, item)

    fun add(position: Int, dataItem: DataItem) = list.add(position, dataItem)

    fun remove(item: DataItem) = list.remove(item)

    fun removeAt(position: Int) = list.removeAt(position)

    fun removeAt(item: DataItem) = list.removeAt(item)

    fun removeLast() = list.deleteLast()

    fun clear() = list.clear()

    protected fun List<DataItem>.addTo() = list.addAll(this)
}
