package com.sanniou.support.databinding

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("onBackToTop")
fun bindingImage(view: View, recycler: RecyclerView) {
    view.setOnClickListener {
        recycler.smoothScrollToPosition(0)
    }
}
