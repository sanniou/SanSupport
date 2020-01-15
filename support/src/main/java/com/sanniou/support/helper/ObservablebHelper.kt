package com.sanniou.support.helper

import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 比起官方更易使用的 callback
 */
fun <T : BaseObservable> T.notifyChange(block: (sender: T, propertyId: Int) -> Unit) {
    addOnPropertyChangedCallback(object : OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable, propertyId: Int) {
            block(sender as T, propertyId)
        }
    })
}

/**
 * 可以绑定 生命周期
 */
fun <T : BaseObservable> notifyChange(
    lifecycleOwner: LifecycleOwner,
    observable: T,
    callback: (sender: T, propertyId: Int) -> Unit
) {
    lifecycleOwner.lifecycle.addObserver(LiveObservableCallback(observable, callback))
}

internal class LiveObservableCallback<T : BaseObservable>(
    private var observable: T,
    private var mChangedCallback: (sender: T, propertyId: Int) -> Unit
) : OnPropertyChangedCallback(), LifecycleEventObserver {


    init {
        observable.addOnPropertyChangedCallback(this)
    }

    override fun onPropertyChanged(
        sender: Observable,
        propertyId: Int
    ) {
        mChangedCallback(sender as T, propertyId)
    }

    override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event
    ) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            release(source)
        }
    }

    private fun release(source: LifecycleOwner) {
        observable.removeOnPropertyChangedCallback(this)
        source.lifecycle.removeObserver(this)
    }
}


