package com.sanniou.support.lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * hide null check when getValue,
 */

open class NonNullLiveData<T>(defaultValue: T) : MutableLiveData<T>() {
    init {
        value = defaultValue;
    }

    override fun setValue(value: T?) {
        if (value == null) {
            return
        }
        super.setValue(value)
    }

    override fun postValue(value: T?) {
        if (value == null) {
            return
        }
        super.postValue(value)
    }

    override fun getValue(): T {
        return super.getValue()!!
    }
}

class NonNullMediatorLiveData<T> : MediatorLiveData<T>()

fun <T> LiveData<T>.nonNull(): NonNullMediatorLiveData<T> {
    val mediator: NonNullMediatorLiveData<T> =
        NonNullMediatorLiveData()
    mediator.addSource(this) { it?.let { mediator.value = it } }
    return mediator
}

fun <T> NonNullMediatorLiveData<T>.observe(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner, Observer {
        it?.let(observer)
    })
}