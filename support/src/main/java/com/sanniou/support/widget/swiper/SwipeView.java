package com.sanniou.support.widget.swiper;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface SwipeView {

    /**
     * 刷新中
     */
    int STATE_LOADING = 0;
    /**
     * 普通状态
     */
    int STATE_NORMAL = 1;
    /**
     * 提示刷新
     */
    int STATE_PULLING = 2;
    /**
     * 刷新完成
     */
    int STATE_COMPLETE = 3;
    /**
     * 刷新失败
     */
    int STATE_FAILED = 4;

    /**
     * @return View 的背景颜色 用于 RefreshLayout 绘制同色背景
     */
    default int getBackColor() {
        return Color.TRANSPARENT;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_LOADING, STATE_NORMAL, STATE_PULLING, STATE_COMPLETE, STATE_FAILED})
    @interface STATE {

    }

    void setState(@STATE int state);


    View getView();

    /**
     * 计算状态
     *
     * @param totalHeight SwipeRefreshLayout统计的head高度
     * @param offset      需要处理的滑动距离
     */
    float scrollOffset(float totalHeight, int offset);

    void setOnRequestListener(OnRequestListener listener);

    boolean isLoading();


    /**
     * RefreshLayout 借此触发事件拦截
     *
     * @return 是否可滑动，
     */
    default boolean scrollable() {
        return !isLoading();
    }

    /**
     * 结束滚动时被调用
     */
    void endScroll(float totalHeight);

    void setEnable(boolean enable);

    /**
     * @return RefreshLayout 布局时所需要的高度，默认是 View Height
     */
    default int getViewEdge() {
        return -getViewHeight();
    }

    /**
     * @return RefreshLayout 测量时所需要的高度，默认是 View Height
     */
    int getViewHeight();

    /**
     * @return 已滑动的距离
     */
    float getSwipeHeight();

    @STATE
    int getState();
}

