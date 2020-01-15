package com.sanniou.support.databinding

import android.graphics.drawable.Drawable
import android.view.ViewStub
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator

@BindingAdapter("android:layout")
fun bindingViewStub(view: ViewStub, resource: Int) {
    view.layoutResource = resource
    view.inflate()
}

@BindingAdapter("android:srcCompat")
fun bindingImage(view: ImageView, resource: Drawable?) {
    view.setImageDrawable(resource)
}

/**
 * ---------------------------------------------------- tablayout ----------------------------------------------------
 */
@BindingAdapter("viewPager")
fun bindingTablayout(tabLayout: TabLayout, viewPager: ViewPager) {
    tabLayout.setupWithViewPager(viewPager)
}

@BindingAdapter(value = ["viewPager", "title"], requireAll = true)
fun bindingTablayout(tabLayout: TabLayout, viewPager: ViewPager2, title: List<String>) {
    TabLayoutMediator(tabLayout, viewPager) { tab, position ->
        tab.text = title[position]
        viewPager.setCurrentItem(tab.position, true)
    }.attach()
}

@BindingAdapter("selected")
fun bindingTablayout(tabLayout: TabLayout, selected: Int) {
    val tab = tabLayout.getTabAt(selected)
    tab?.select()
}

@InverseBindingAdapter(attribute = "selected", event = "selectedAttrChanged")
fun captureYabValue(view: TabLayout): Int {
    return view.selectedTabPosition
}

@BindingAdapter("selectedAttrChanged")
fun bindingTablayout(view: TabLayout, listener: InverseBindingListener) {
    view.addOnTabSelectedListener(object : OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            listener.onChange()
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}
        override fun onTabReselected(tab: TabLayout.Tab) {}
    })
}
