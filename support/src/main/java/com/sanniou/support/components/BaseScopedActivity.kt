package com.sanniou.support.components

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseScopedActivity<T : ViewModel> : BaseViewModelActivity<T>(),
    CoroutineScope by MainScope() {

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}