package com.sanniou.support.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.sanniou.support.helper.ImageLoader

fun View.getActivity() = findActivity(context)

private fun findActivity(context: Context): Activity? =
    when (context) {
        is Activity -> {
            context
        }
        is ContextWrapper -> {
            findActivity(context.baseContext)
        }
        else -> {
            null
        }
    }

fun View.getString(@StringRes res: Int) = this.resources.getString(res)

fun View.getColor(@ColorRes res: Int) = ContextCompat.getColor(context, res)

fun View.getdimension(@DimenRes res: Int) = this.resources.getDimension(res)

fun ImageView.load(any: Any) = ImageLoader.load(this, any)