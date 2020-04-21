package com.sanniou.support.multiitem

import androidx.annotation.CallSuper
import com.sanniou.multiitem.AdapterViewHolder
import com.sanniou.multiitem.DataItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 * 处理协程用，从界面移除时自动cancel job
 */
abstract class ScopeItem : DataItem, CoroutineScope by MainScope() {

    @CallSuper
    override fun onDetached(holder: AdapterViewHolder) {
        super.onDetached(holder)
        cancel()
    }
}