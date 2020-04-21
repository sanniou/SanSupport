package com.sanniou.support.widget.swiper;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;

/**
 * 不显示，只用于边缘滚动的Footer
 */
public class ParallaxFooter implements SwipeView {

    private float mHeight;

    private View mView;

    public ParallaxFooter(Context context) {
        mView = new View(context);
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void setState(int state) {

    }

    @Override
    public float scrollOffset(float totalHeight, int offset) {
        double exp = Math.exp(-(mHeight / 400));
        double v = -offset * exp;
        setHeadHeight((int) (Math.max(mHeight + v, 0)));
        return 0;
    }

    @Override
    public void setOnRequestListener(OnRequestListener listener) {

    }

    @Override
    public boolean isLoading() {
        return false;
    }

    @Override
    public void endScroll(float totalUnconsumed) {
        ValueAnimator hideAnimator;
        hideAnimator = ValueAnimator.ofFloat();
        hideAnimator.addUpdateListener(
                animation -> setHeadHeight((float) animation.getAnimatedValue()));
        hideAnimator.addListener(new AnimatorListenerAdapter() {
        });
        hideAnimator.setFloatValues(mHeight, 0F);
        hideAnimator.start();
    }

    @Override
    public void setEnable(boolean canLoadMore) {

    }

    @Override
    public int getViewHeight() {
        return mView.getMeasuredHeight();
    }

    @Override
    public float getSwipeHeight() {
        return -mHeight;
    }

    @Override
    public int getState() {
        return STATE_COMPLETE;
    }

    private void setHeadHeight(float height) {
        mView.setRotationY(-height);
        ((RefreshParent) mView.getParent()).changeTargetOffset(-height);
        mHeight = height;
    }
}
