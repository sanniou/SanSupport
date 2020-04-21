package com.sanniou.support.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(
    factory: ViewModelProvider.Factory? = null
) = factory?.let {
    ViewModelProvider(this, it).get(T::class.java)
} ?: run {
    ViewModelProvider(this).get(T::class.java)
}

/**
 * not do null check
 */
inline fun <reified T : ViewModel> Fragment.getActivityViewModel() =
    ViewModelProvider(activity!!).get(T::class.java)