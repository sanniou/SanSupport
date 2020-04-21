package com.sanniou.support.widget.swiper;

/**
 * @author saniou 下拉控件的基类,用于{@link LSwipeRefreshLayout}
 * <p>
 * layout将滑动状态通过{@link #scrollOffset(float, int)}和{@link #endScroll(float)}交由SwiperView来处理
 */
public abstract class AbsSwipeView implements SwipeView {

    /**
     * 存储state状态
     */
    protected int mState;
    /**
     * 刷新状态
     */
    protected boolean mRefreshing;
    protected OnRequestListener mRefreshListener;

    private boolean mEnable = true;

    public boolean isEnable() {
        return mEnable;
    }

    @Override
    public void setEnable(boolean enable) {
        mEnable = enable;
    }

    @Override
    public void setState(@STATE int state) {
        switch (state) {
            case SwipeView.STATE_NORMAL:
                setNormal();
                break;
            case SwipeView.STATE_PULLING:
                setPulling();
                break;
            case SwipeView.STATE_LOADING:
                setLoading();
                break;
            case SwipeView.STATE_COMPLETE:
                setComplete();
                break;
            case SwipeView.STATE_FAILED:
                setFailed();
                break;
            default:
        }
    }

    private void setLoading() {
        if (mState == STATE_LOADING) {
            return;
        }
        mRefreshing = true;
        refreshAction();
        mState = STATE_LOADING;
        if (mRefreshListener != null) {
            mRefreshListener.onRequest();
        } else {
            setComplete();
        }
    }

    private void setNormal() {
        if (mState == STATE_NORMAL) {
            return;
        }
        mRefreshing = false;
        mState = STATE_NORMAL;
        normalAction();
    }

    private void setPulling() {
        if (mState == STATE_PULLING) {
            return;
        }
        mRefreshing = false;
        mState = STATE_PULLING;
        pullAction();
    }

    private void setFailed() {
        if (mState == STATE_FAILED) {
            return;
        }
        mRefreshing = false;
        mState = STATE_FAILED;
        failedAction();
    }

    private void setComplete() {
        if (mState == STATE_COMPLETE) {
            return;
        }
        mRefreshing = false;
        mState = STATE_COMPLETE;
        completeAction();
    }

    @Override
    public void setOnRequestListener(OnRequestListener refreshListener) {
        mRefreshListener = refreshListener;
    }

    @Override
    public boolean isLoading() {
        return mRefreshing;
    }

    @Override
    public int getState() {
        return mState;
    }

    /**
     * 抽象方法，状态改变时的操作
     */
    protected abstract void refreshAction();

    protected abstract void normalAction();

    protected abstract void pullAction();

    protected abstract void completeAction();

    protected abstract void failedAction();

}
