package com.sanniou.support.databinding

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.sanniou.multiitem.DataItem
import com.sanniou.support.helper.ContextHelper.getActivity
import com.sanniou.support.widget.BindingPagerAdapter
import com.sanniou.support.widget.RouterFragmentAdapter

@BindingAdapter(
    value = ["fragments", "items", "titles", "currentItem", "smoothScroll"],
    requireAll = false
)
fun bindingFragmentViewPager(
    viewPager: ViewPager,
    fragments: List<String>?,
    items: List<DataItem>?,
    titles: List<String>?,
    index: Int,
    smoothScroll: Boolean
) { //fragments 参数不为空，则设置 FragmentAdapter
    if (fragments != null) {
        val adapter: PagerAdapter = RouterFragmentAdapter( // 这里有个强转，可能需要注意下
            (getActivity(viewPager) as AppCompatActivity).supportFragmentManager,
            fragments, titles
        )
        viewPager.adapter = adapter
        //items 参数不为空，则设置 BindingPagerAdapter
    } else if (items != null) {
        val adapter: PagerAdapter =
            BindingPagerAdapter(items, titles)
        viewPager.adapter = adapter
    }
    if (index == 0) {
        return
    }
    viewPager.setCurrentItem(index, smoothScroll)
}

@BindingAdapter(value = ["currentItem", "smoothScroll"], requireAll = false)
fun setViewPagerCurrentItem(
    viewPager: ViewPager,
    index: Int,
    smoothScroll: Boolean
) {
    if (viewPager.currentItem == index) {
        return
    }
    viewPager.setCurrentItem(index, smoothScroll)
}

@BindingAdapter("adapter")
fun bindingViewPager(viewpager: ViewPager, adapter: PagerAdapter?) {
    viewpager.adapter = adapter
}

@BindingAdapter("limit")
fun bindingViewPager(viewpager: ViewPager, limit: Int) {
    viewpager.offscreenPageLimit = limit
}