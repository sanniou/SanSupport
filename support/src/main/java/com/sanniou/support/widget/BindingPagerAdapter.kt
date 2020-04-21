package com.sanniou.support.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewpager.widget.PagerAdapter
import com.sanniou.multiitem.AdapterViewHolder
import com.sanniou.multiitem.DataItem
import com.sanniou.multiitem.getAdapterHolder

/**
 * 用于 databinding 的 PagerAdapter
 */
class BindingPagerAdapter(
    private var items: List<DataItem>,
    private var titles: List<String>? = null
) :
    PagerAdapter() {

    override fun getPageTitle(position: Int): CharSequence? {
        return if (titles == null) {
            super.getPageTitle(position)
        } else titles!![position]
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val item = items[position]
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(view.context),
            item.getItemType(),
            view,
            false
        )
        return binding.root.also {
            val holder = AdapterViewHolder(it)
            holder.onBind(item)

            view.addView(it)
            holder.onAttached()
        }
    }

    override fun setPrimaryItem(
        container: ViewGroup, position: Int,
        `object`: Any
    ) {
        super.setPrimaryItem(container, position, `object`)
        if (container is WrappingViewPager) {
            container.onPageChanged(`object` as View)
        }
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        with(`object` as View) {
            this.getAdapterHolder().onDetached()
            this.getAdapterHolder().onRecycled()
            container.removeView(this)
        }
    }

    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean {
        return view === `object`
    }
}