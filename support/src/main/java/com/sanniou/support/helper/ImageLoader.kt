package com.sanniou.support.helper

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.sanniou.support.R

/**
 * Glide 的简单封装
 */
object ImageLoader {

    const val NO_RES = -1

    /**
     * 默认的加载设置
     */
    private var sDefaultOption: Option = getAOption()

    /**
     * 缓存的加载设置，用于每次加载时单独的设置
     */
    private var sCacheOption: Option = getAOption()
    private var sErrorId = R.color.design_default_color_error
    private var sPlaceHolderId = R.color.design_default_color_background

    fun setError(errorId: Int) {
        sErrorId = errorId
    }

    fun setPlaceHolder(placeHolderId: Int) {
        sPlaceHolderId = placeHolderId
    }

    fun getAOption() = Option()
        .error(sErrorId)
        .placeholder(sPlaceHolderId)
        .diskCache(true)
        .memoryCache(true)
        .centerCrop(false)
        .circle(false)

    fun clear(view: ImageView) {
        Glide.with(view).clear(view)
    }

    /**
     * 根据路径加载图片。
     */
    fun load(
        activity: Activity,
        view: ImageView,
        res: Any,
        option: Option = sDefaultOption,
        placeholderId: Int = NO_RES,
        errorId: Int = NO_RES
    ) {
        if (checkActivity(activity)) {
            return
        }
        option.placeholder(placeholderId)
        option.error(errorId)
        load(Glide.with(activity).load(res), res, view, option)
    }

    fun load(
        fragment: Fragment,
        view: ImageView,
        res: Any,
        option: Option = sDefaultOption,
        placeholderId: Int = NO_RES,
        errorId: Int = NO_RES
    ) {
        if (checkFragment(fragment)) {
            return
        }
        option.placeholder(placeholderId)
        option.error(errorId)
        load(Glide.with(fragment).load(res), res, view, option)
    }

    fun load(
        view: ImageView,
        res: Any,
        option: Option = sDefaultOption,
        placeholderId: Int = NO_RES,
        errorId: Int = NO_RES
    ) {
        if (checkViewAttached(view)) {
            return
        }
        option.placeholder(placeholderId)
        option.error(errorId)
        load(Glide.with(view).load(res), res, view, option)
    }

    /**
     * 真正开始加载图片
     */
    private fun load(
        builder: RequestBuilder<Drawable>,
        res: Any,
        view: ImageView,
        option: Option
    ) {
        if (option.placeholder() != NO_RES) {
            builder.placeholder(option.placeholder())
        }
        if (option.error() != NO_RES) {
            builder.error(option.error())
        }
        if (option.original()) {
            builder.override(
                Target.SIZE_ORIGINAL,
                Target.SIZE_ORIGINAL
            )
        }
        if (view.scaleType == ImageView.ScaleType.CENTER_CROP) {
            builder.centerCrop()
        } else {
            builder.fitCenter()
        }
        if (option.centerCrop()) {
            builder.centerCrop()
        }
        if (option.circle()) {
            builder.circleCrop()
        }
        builder.skipMemoryCache(!option.memoryCache())
            .diskCacheStrategy(
                if (checkCache(
                        option,
                        res
                    )
                ) DiskCacheStrategy.DATA else DiskCacheStrategy.NONE
            )
            .into(view)
    }

    private fun checkCache(option: Option?, res: Any): Boolean {
        return (option!!.diskCache()
                && res !is ByteArray
                && res !is Bitmap
                && res !is Int)
    }

    private fun checkFragment(fragment: Fragment?): Boolean {
        return fragment == null || fragment.isDetached
    }

    private fun checkActivity(activity: Activity?): Boolean {
        return activity == null || activity.isFinishing || activity.isDestroyed
    }

    private fun checkViewAttached(view: ImageView?): Boolean {
        if (view == null) {
            return true
        }
        view.context ?: return true
        val activity = ContextHelper.getActivity(view)
        return checkActivity(activity)
    }

    class Option {
        /**
         * 错误图
         */
        private var mError = 0

        /**
         * 占位图
         */
        private var mPlaceholder = 0

        /**
         * 内存缓存
         */
        private var mMemoryCache = true

        /**
         * 磁盘缓存
         */
        private var mDiskCache = true

        /**
         * 圆形
         */
        private var mCircle = false
        private val mHeight = Target.SIZE_ORIGINAL
        private val mWidth = Target.SIZE_ORIGINAL

        /**
         * 裁剪
         */
        private var mCenterCrop = false
        private var mOriginal = false
        fun error(error: Int): Option {
            mError = error
            return this
        }

        fun error(): Int {
            return mError
        }

        fun memoryCache(memoryCache: Boolean): Option {
            mMemoryCache = memoryCache
            return this
        }

        fun memoryCache(): Boolean {
            return mMemoryCache
        }

        fun diskCache(diskCache: Boolean): Option {
            mDiskCache = diskCache
            return this
        }

        fun diskCache(): Boolean {
            return mDiskCache
        }

        fun placeholder(placeholder: Int): Option {
            mPlaceholder = placeholder
            return this
        }

        fun placeholder(): Int {
            return mPlaceholder
        }

        fun circle(circle: Boolean): Option {
            mCircle = circle
            return this
        }

        fun circle(): Boolean {
            return mCircle
        }

        fun centerCrop(b: Boolean): Option {
            mCenterCrop = b
            return this
        }

        fun centerCrop(): Boolean {
            return mCenterCrop
        }

        fun original(original: Boolean) {
            mOriginal = original
        }

        fun original(): Boolean {
            return mOriginal
        }
    }
}