/**
 * Copyright (c) 2020 Mercedes-Benz. All rights reserved.
 */
package com.sanniou.support.components

import com.sanniou.multiitem.DataItem
import com.sanniou.multiitem.MultiItemArrayList


open class BaseListViewModel : BaseViewModel() {

    val list = MultiItemArrayList<DataItem>()

    fun add(item: DataItem) = list.add(item)

    fun add(position: Int, dataItem: DataItem) = list.add(position, dataItem)

    fun remove(item: DataItem) = list.remove(item)

    fun removeAt(position: Int) = list.removeAt(position)

    fun clear() = list.clear();

}