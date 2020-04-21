/**
 * Copyright (c) 2020 Mercedes-Benz. All rights reserved.
 */
package com.sanniou.support.components

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanniou.support.extensions.takeIfFalse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

data class UiEvent(var event: Int, var any: Any? = null)

/**
 *  Send UiEvent for LifecycleOwner subscription instead of view interface
 */
open class BaseViewModel : ViewModel(), LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry {
        return lifecycleRegistry
    }

    private val mUiEvent = MutableLiveData<UiEvent>()

    private val mLockedEvent = hashSetOf<Int>()

    init {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun sendEvent(event: Int, any: Any? = null): Unit =
        mLockedEvent.contains(event).takeIfFalse {
            mUiEvent.setValue(UiEvent(event, any))
        }.let {
            Unit
        }

    fun lockEvent(event: Int) {
        mLockedEvent.add(event)
    }

    fun unlockEvent(event: Int) {
        mLockedEvent.remove(event)
    }

    /**
     * observe ui event with LifecycleOwner,now we can auto remove observer when view destroy
     */
    fun observeEvent(owner: LifecycleOwner, observer: Observer<UiEvent>) {
        mUiEvent.observe(owner, observer)
    }

    fun observeEventInt(owner: LifecycleOwner, observer: Observer<Int>) {
        mUiEvent.observe(owner, Observer {
            observer.onChanged(it.event)
        })
    }

    fun observeEventIgnoreStatus(owner: LifecycleOwner, observer: Observer<UiEvent>) {
        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }
        owner.lifecycle
            .addObserver(LiveDataForeverObserver(observer, mUiEvent))
    }

    fun observeEvent(observer: Observer<UiEvent>) {
        mUiEvent.observe(this, observer)
    }

    fun removeObserver(observer: Observer<UiEvent>) {
        mUiEvent.removeObserver(observer)
    }

    override fun onCleared() {
        super.onCleared()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        mUiEvent.removeObservers(this)
    }

    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) =
        viewModelScope.launch(context, start, block)
}

/**
 * LiveData does not respond by default after LifecycleOwner onStop ,
 * so implement an Observer that is independent of stop,
 * auto release when LifecycleOwner state is destroy
 */
class LiveDataForeverObserver<T>(
    private var mObserver: Observer<T>?,
    private var mLiveData: LiveData<T>?
) : LifecycleEventObserver {

    init {
        mObserver?.let {
            mLiveData?.observeForever(it)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (source.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            release(source)
        }
    }

    private fun release(source: LifecycleOwner) {
        mObserver?.let {
            mLiveData?.removeObserver(it)
        }
        source.lifecycle.removeObserver(this)
        mObserver = null
        mLiveData = null
    }
}
