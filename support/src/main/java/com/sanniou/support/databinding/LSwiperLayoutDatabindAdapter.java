package com.sanniou.support.databinding;

import androidx.databinding.BindingAdapter;

import com.sanniou.support.widget.swiper.LSwipeRefreshLayout;
import com.sanniou.support.widget.swiper.OnRequestListener;


public class LSwiperLayoutDatabindAdapter {

    @BindingAdapter("onRefresh")
    public static void setOnRefreshListener(LSwipeRefreshLayout v, OnRequestListener listener) {
        v.setOnRefreshListener(listener);
    }

    @BindingAdapter("refreshStatus")
    public static void setRefreshStatus(LSwipeRefreshLayout v, boolean state) {
        v.stopRefresh(state);
    }

    @BindingAdapter("loadStatus")
    public static void setLoadStatus(LSwipeRefreshLayout v, boolean state) {
        v.stopLoadMore(state);
    }

    @BindingAdapter("state")
    public static void setRefreshLayoutStatus(LSwipeRefreshLayout v, int state) {
        switch (state) {
            case 1:
                v.stopRefresh(true);
                break;
            case 2:
                v.stopRefresh(false);
                break;
            case 3:
                v.stopLoadMore(true);
            case 4:
                v.stopLoadMore(false);
                break;
            default:
        }
    }
}
