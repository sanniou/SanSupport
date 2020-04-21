package com.sanniou.support.helper

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View

object ContextHelper {
    fun getActivity(view: View?): Activity? {
        val context = view?.context
        return getActivity(context)
    }

    fun getActivity(context: Context?): Activity? {
        var nextContext = context
        while (nextContext is ContextWrapper) {
            if (nextContext is Activity) {
                return nextContext
            }
            nextContext = nextContext.baseContext
        }
        return null
    }
}