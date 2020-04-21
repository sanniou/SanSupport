package com.sanniou.support.coroutines

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Job

class LifecycleOwnerAutoDisposable(
    private val lifecycleOwner: LifecycleOwner,
    private val job: Job,
    private val cancelEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
) : LifecycleEventObserver {

    init {
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            lifecycleOwner.lifecycle.addObserver(this)
        } else {
            job.cancel()
        }

        job.invokeOnCompletion {
            lifecycleOwner.lifecycle.removeObserver(this)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == cancelEvent && !job.isCancelled) {
            job.cancel()
        }
    }
}

fun Job.autoDispos(lifecycleOwner: LifecycleOwner) = this.apply {
    LifecycleOwnerAutoDisposable(
        lifecycleOwner,
        this
    )
}