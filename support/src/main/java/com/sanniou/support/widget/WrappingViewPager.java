package com.sanniou.support.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.ViewPager;

/**
 * ViewPager with dynamic height support. For basic usage, replace {@link ViewPager} with {@link
 * WrappingViewPager} in your layout file, and set its height property to {@code wrap_content}.
 * <p>
 * You also have to make your adapter inform the {@link WrappingViewPager} of every page change: the
 * easiest way to achieve this is to override {@link androidx.core.view.PagerAdapter#setPrimaryItem(ViewGroup,
 * int, Object)} and call {@link WrappingViewPager#onPageChanged(View)}. To avoid unnecessary calls,
 * only do this when the page is changed, instead of the old one being reselected. For a basic
 * example of this, see how it is implemented in the library's own .
 *
 * @author Santeri Elo
 * @author Abhishek V (http://stackoverflow.com/a/32410274)
 * @author Vihaan Verma (http://stackoverflow.com/a/32488566)
 */
public class WrappingViewPager extends ViewPager {

    private View mCurrentView;

    public WrappingViewPager(Context context) {
        super(context);
    }

    public WrappingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.AT_MOST) {
            if (mCurrentView != null) {
                int height;
                mCurrentView.measure(widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                height = mCurrentView.getMeasuredHeight();

                if (height < getMinimumHeight()) {
                    height = getMinimumHeight();
                }

                int newHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
                heightMeasureSpec = newHeight;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * This method should be called when the ViewPager changes to another page. For best results call
     * this method in the adapter's setPrimary
     *
     * @param currentView PagerAdapter item view
     */
    public void onPageChanged(View currentView) {
        mCurrentView = currentView;
        requestLayout();
    }
}