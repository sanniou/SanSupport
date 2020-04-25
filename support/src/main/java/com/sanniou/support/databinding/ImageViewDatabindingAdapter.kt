package com.sanniou.support.databinding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.blankj.utilcode.util.ObjectUtils
import com.sanniou.support.helper.ImageLoader

@BindingAdapter("android:srcCompat")
fun bindingImage(view: ImageView, resource: Drawable?) {
    view.setImageDrawable(resource)
}

@BindingAdapter(value = ["res", "error", "placeholder"], requireAll = false)
fun bindingImageView(
    view: ImageView,
    img: Any,
    error: Int,
    placeholder: Int
) {
    bindingImage(
        view,
        img,
        error,
        placeholder,
        center = false,
        circle = false,
        original = false,
        noDiskCache = false,
        noMemoryCache = false
    )
}

@BindingAdapter(
    value = ["res", "error", "placeholder", "center", "circle", "original", "noDiskCache", "noMemoryCache"],
    requireAll = false
)
fun bindingImage(
    view: ImageView,
    res: Any?,
    error: Int,
    placeholder: Int,
    center: Boolean,
    circle: Boolean,
    original: Boolean,
    noDiskCache: Boolean,
    noMemoryCache: Boolean
) {
    if (ObjectUtils.isEmpty(res)) {
        ImageLoader.clear(view)
        return
    }
    res!!
    val option: ImageLoader.Option = ImageLoader.getAOption()
    if (error != 0) {
        option.error(error)
    }
    if (placeholder != 0) {
        option.placeholder(placeholder)
    }
    option.diskCache(!noDiskCache)
    option.memoryCache(!noMemoryCache)
    option.centerCrop(center)
    option.circle(circle)
    option.original(original)
    ImageLoader.load(view, res, option)
}
