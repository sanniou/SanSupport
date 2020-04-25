package com.sanniou.support.databinding

import androidx.databinding.BindingAdapter
import com.sanniou.support.widget.swiper.LSwipeRefreshLayout
import com.sanniou.support.widget.swiper.OnRequestListener

@BindingAdapter("onRefresh")
fun setOnRefreshListener(v: LSwipeRefreshLayout, listener: OnRequestListener?) {
    v.setOnRefreshListener(listener)
}

@BindingAdapter("refreshStatus")
fun setRefreshStatus(v: LSwipeRefreshLayout, state: Boolean) {
    v.stopRefresh(state)
}

@BindingAdapter("loadStatus")
fun setLoadStatus(v: LSwipeRefreshLayout, state: Boolean) {
    v.stopLoadMore(state)
}

@BindingAdapter("state")
fun setRefreshLayoutStatus(v: LSwipeRefreshLayout, state: Int) {
    when (state) {
        1 -> v.stopRefresh(true)
        2 -> v.stopRefresh(false)
        3 -> {
            v.stopLoadMore(true)
            v.stopLoadMore(false)
        }
        4 -> v.stopLoadMore(false)
        else -> {
        }
    }
}
