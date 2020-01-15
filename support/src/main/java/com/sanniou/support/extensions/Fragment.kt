package com.sanniou.support.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

inline fun <reified T : ViewModel> Fragment.getViewModel(
    factory: ViewModelProvider.Factory? = null
) = ViewModelProviders.of(this, factory).get(T::class.java)

/**
 * not do null check
 */
inline fun <reified T : ViewModel> Fragment.getActivityViewModel(
    factory: ViewModelProvider.Factory? = null
) = ViewModelProviders.of(activity!!, factory).get(T::class.java)
