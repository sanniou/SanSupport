package com.sanniou.support.widget

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * 通过路由创建 fragment ，需要初始化这个变量才可以使用 RouterFragmentAdapter
 */
lateinit var defaultFragmentFactory: (route: String) -> Fragment

/**
 * 使用路由快速创建 多Fragment的ViewPager
 * 需要首先设置 [defaultFragmentFactory] ,建议在初始化的时候设置
 */
class RouterFragmentAdapter(
    fm: FragmentManager,
    private val fragments: List<String>,
    private val titles: List<String>?
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getPageTitle(position: Int): CharSequence? {
        return titles?.get(position)
    }

    override fun setPrimaryItem(
        container: ViewGroup, position: Int,
        `object`: Any
    ) {
        super.setPrimaryItem(container, position, `object`)
        if (container is WrappingViewPager) {
            container.onPageChanged((`object` as Fragment).view)
        }
    }

    override fun getItem(position: Int) = defaultFragmentFactory(fragments[position])

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}