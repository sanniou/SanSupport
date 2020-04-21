package com.sanniou.support.coroutines

import android.os.Build
import android.view.View
import android.view.View.OnAttachStateChangeListener
import kotlinx.coroutines.Job

class ViewAutoDisposable(private val view: View, private val job: Job) :
    OnAttachStateChangeListener {

    init {
        if (isViewAttached()) {
            view.addOnAttachStateChangeListener(this)
        } else {
            job.cancel()
        }

        job.invokeOnCompletion {
            view.post {
                view.removeOnAttachStateChangeListener(this)
            }
        }
    }

    override fun onViewAttachedToWindow(v: View?) = Unit

    override fun onViewDetachedFromWindow(v: View?) {
        job.cancel()
        view.removeOnAttachStateChangeListener(this)
    }

    private fun isViewAttached() =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && view.isAttachedToWindow || view.windowToken != null
}

fun Job.autoDispos(view: View) = this.apply {
    ViewAutoDisposable(view, this)
}
