package com.sanniou.support.widget.swiper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewCompat.NestedScrollType;
import androidx.core.view.ViewCompat.ScrollAxis;

import com.blankj.utilcode.util.LogUtils;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static androidx.core.view.ViewCompat.TYPE_TOUCH;

/**
 * @author sanniou
 * <p>
 * 下拉刷新控件 可在layout之前使用{@link #setHeader}添加自定义头部， 若未设置，添加默认头部{@link
 * #createHeader}
 */

public class LSwipeRefreshLayout extends ViewGroup
        implements NestedScrollingParent2, NestedScrollingChild2, RefreshParent {

    protected static RefreshHeaderCreator sHeaderCreator;
    protected static RefreshHeaderCreator sFooterCreator;
    /**
     * 能否上拉加载
     */
    protected static boolean sLoadMoreEnable = false;
    /**
     * 能否刷新
     */
    protected static boolean sRefreshEnable = true;
    /**
     * 响应 nested scroll 的惯性滑动
     */
    protected static boolean sFlingReact = false;

    /**
     * 全局 header 构造
     */
    public static void setDefaultHeaderCreator(RefreshHeaderCreator headerCreator) {
        sHeaderCreator = headerCreator;
    }

    /**
     * 全局 footer 构造
     */
    public static void setDefaultFooterCreator(RefreshHeaderCreator footerCreator) {
        sFooterCreator = footerCreator;
    }

    public static void setsFlingReact(boolean flingReact) {
        sFlingReact = flingReact;
    }

    /**
     * 用于获取 xml 中的 enable 属性
     */
    protected static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.enabled
    };

    //<editor-fold desc="flag">

    protected static final int STATE_LOAD_MORE = 861;
    protected static final int STATE_REFRESH = 219;
    protected static final int STATE_NORMAL = 718;

    private static final int INVALID_POINTER = -1;
    //</editor-fold>

    protected int mTargetViewIndex = -1;
    /**
     * 表示 移动控件的类型
     */
    protected int mState = STATE_NORMAL;
    /**
     * 主控件
     */
    protected View mTarget;
    /**
     * 顶部下拉控件
     */
    protected SwipeView mRefreshHeader;
    /**
     * 底部上拉控件
     */
    protected SwipeView mRefreshFooter;
    /**
     * 计算空间高度
     */
    protected float mTotalUnconsumed;

    // nested scrolling 帮助类

    protected final NestedScrollingParentHelper mNestedScrollingParentHelper;
    protected final NestedScrollingChildHelper mNestedScrollingChildHelper;
    protected final int[] mParentScrollConsumed = new int[2];
    protected final int[] mParentOffsetInWindow = new int[2];
    protected boolean mNestedScrollInProgress;

    // using for normal scrolling
    /**
     * 在被判定为滚动之前用户手指可以移动的最大值。
     */
    protected int mTouchSlop;
    /**
     * 初始Y点
     */
    protected float mInitialDownY;
    /**
     * 滑动状态
     */
    protected boolean mIsBeingDragged;
    /**
     * 位移焦点
     */
    protected int mActivePointerId = INVALID_POINTER;
    /**
     * 用于计算多点触控Y轴的偏移量
     */
    protected SparseArrayCompat<Float> lastY = new SparseArrayCompat<>();

    /**
     * 用于自定义下拉判断条件的回调方法
     */
    protected OnChildScrollUpCallback mChildScrollUpCallback;
    protected OnChildScrollDownCallback mChildScrollDownCallback;


    protected OnRequestListener mRefreshListener;
    protected OnRequestListener mLoadMoreListener;

    private Paint mPaint = new Paint();

    public LSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public LSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        // ViewGroup 默认不会绘制，设置 false 因为 需要绘制 SwipeView 的背景
        setWillNotDraw(false);
        //配合 #getChildDrawingOrder 改变 child 的绘制顺序
        setChildrenDrawingOrderEnabled(true);

        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
        if (mRefreshHeader == null) {
            createHeader();
        }
        if (mRefreshFooter == null) {
            createFooter();
        }
        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();
    }

    public void setOnRefreshListener(OnRequestListener listener) {
        mRefreshListener = listener;
        if (mRefreshHeader != null) {
            mRefreshHeader.setOnRequestListener(listener);
        }
    }

    public void setOnLoadMoreListener(OnRequestListener listener) {
        mLoadMoreListener = listener;
        if (mRefreshFooter != null) {
            mRefreshFooter.setOnRequestListener(listener);
        }
    }

    private void createHeader() {
        if (sHeaderCreator != null) {
            setHeader(sHeaderCreator.createHead(getContext()));
            return;
        }
        setHeader(new NormalHeader(getContext()));
    }

    private void createFooter() {
        if (sFooterCreator != null) {
            setFooter(sFooterCreator.createHead(getContext()));
            return;
        }
        setFooter(new ParallaxFooter(getContext()));
    }


    @Override
    public void removeView(View view) {
        super.removeView(view);
        if (view == mTarget) {
            mTarget = null;
            mTargetViewIndex = -1;
        }
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        if (mTargetViewIndex == index) {
            mTarget = null;
            mTargetViewIndex = -1;
        }
    }

    @Override
    public void setHeader(@NonNull SwipeView header) {
        swapSwipeView(header, mRefreshHeader, mRefreshListener);
        header.setEnable(sRefreshEnable);
        mRefreshHeader = header;
    }

    @Override
    public void setFooter(@NonNull SwipeView footer) {
        swapSwipeView(footer, mRefreshFooter, mLoadMoreListener);
        footer.setEnable(sLoadMoreEnable);
        mRefreshFooter = footer;
    }

    private void swapSwipeView(@NonNull SwipeView newSV, SwipeView oldSV,
                               OnRequestListener listener) {
        if (newSV == oldSV) {
            return;
        }
        if (oldSV != null) {
            oldSV.setOnRequestListener(null);
            removeView(oldSV.getView());
        }
        newSV.setOnRequestListener(listener);
        addView(newSV.getView());
    }

    @Override
    public SwipeView getHeader() {
        return mRefreshHeader;
    }

    @Override
    public SwipeView getFooter() {
        return mRefreshFooter;
    }

    /**
     * 开始刷新
     */
    @Override
    public void startRefresh() {
        if (!isRefreshing()) {
            mRefreshHeader.setState(SwipeView.STATE_LOADING);
        }
    }

    /**
     * 停止刷新
     */
    @Override
    public void stopRefresh(boolean success) {
        if (isRefreshing()) {
            if (success) {
                mRefreshHeader.setState(SwipeView.STATE_COMPLETE);
            } else {
                mRefreshHeader.setState(SwipeView.STATE_FAILED);
            }
        }
    }

    /**
     * 开始加载
     */
    @Override
    public void startLoadMore() {
        if (!isLoading()) {
            mRefreshFooter.setState(SwipeView.STATE_LOADING);
        }
    }

    /**
     * 停止加载
     */
    @Override
    public void stopLoadMore(boolean success) {
        if (isLoading()) {
            if (success) {
                mRefreshFooter.setState(SwipeView.STATE_COMPLETE);
            } else {
                mRefreshFooter.setState(SwipeView.STATE_FAILED);
            }
        }
    }

    @Override
    public boolean isRefreshing() {
        return mRefreshHeader != null && mRefreshHeader.isLoading();
    }

    @Override
    public boolean isLoading() {
        return mRefreshFooter != null && mRefreshFooter.isLoading();
    }

    protected void ensureTarget() {
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mRefreshHeader.getView()) && !child.equals(mRefreshFooter.getView())) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mTargetViewIndex < 0) {
            return i;
        } else if (i == 0) {
            return mTargetViewIndex;
        } else if (i == mTargetViewIndex) {
            return 0;
        } else {
            return i;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }

        final View thisView = this;
        final int paddingLeft = thisView.getPaddingLeft();
        final int paddingTop = thisView.getPaddingTop();

        for (int i = 0, len = super.getChildCount(); i < len; i++) {
            View child = super.getChildAt(i);
            if (mRefreshHeader != null && mRefreshHeader.getView() == child) {
                final View headerView = mRefreshHeader.getView();
                int left = 0;
                int top = mRefreshHeader.getViewEdge();
                int right = left + headerView.getMeasuredWidth();
                int bottom = top + headerView.getMeasuredHeight();
                headerView.layout(left, top, right, bottom);
            }
            if (mRefreshFooter != null && mRefreshFooter.getView() == child) {
                final View footerView = mRefreshFooter.getView();
                int left = 0;
                int right = left + footerView.getMeasuredWidth();
                int bottom = thisView.getMeasuredHeight() - mRefreshFooter.getViewEdge();
                int top = bottom - footerView.getMeasuredHeight();
                footerView.layout(left, top, right, bottom);
            }
            if (mTarget != null && mTarget == child) {
                final View contentView = mTarget;
                int left = paddingLeft;
                int top = paddingTop;
                int right = left + contentView.getMeasuredWidth();
                int bottom = top + contentView.getMeasuredHeight();
                contentView.layout(left, top, right, bottom);
            }
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }

        int minimumHeight = 0;
        final View thisView = this;

        mTargetViewIndex = -1;
        for (int i = 0, len = super.getChildCount(); i < len; i++) {
            View child = super.getChildAt(i);
            if (mRefreshHeader != null && mRefreshHeader.getView() == child) {
                final View headerView = mRefreshHeader.getView();
                final LayoutParams lp = headerView.getLayoutParams();
                final int widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, 0, lp.width);
                int height = mRefreshHeader.getViewHeight();
                headerView.measure(widthSpec, makeMeasureSpec(Math.max(height, 0), EXACTLY));
            }
            if (mRefreshFooter != null && mRefreshFooter.getView() == child) {
                final View footerView = mRefreshFooter.getView();
                final LayoutParams lp = footerView.getLayoutParams();
                final int widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, 0, lp.width);
                int height = mRefreshFooter.getViewHeight();
                footerView.measure(widthSpec, makeMeasureSpec(Math.max(height, 0), EXACTLY));
            }
            if (mTarget != null && mTarget == child) {
                mTargetViewIndex = i;
                final View contentView = mTarget;
                final LayoutParams lp = contentView.getLayoutParams();
                final int widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                        thisView.getPaddingLeft() + thisView.getPaddingRight(), lp.width);
                final int heightSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec,
                        thisView.getPaddingTop() + thisView.getPaddingBottom(), lp.height);
                contentView.measure(widthSpec, heightSpec);
                minimumHeight += contentView.getMeasuredHeight();
            }
        }

        super.setMeasuredDimension(
                View.resolveSize(super.getSuggestedMinimumWidth(), widthMeasureSpec),
                View.resolveSize(minimumHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float swipeHeight = mRefreshHeader.getSwipeHeight();
        int color = mRefreshHeader.getBackColor();
        mPaint.setColor(color);
        canvas.drawRect(0, 0, getWidth(), swipeHeight, mPaint);
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            reset();
        }
        if (mRefreshHeader != null) {
            mRefreshHeader.setEnable(enabled);
        }
        if (mRefreshFooter != null) {
            mRefreshFooter.setEnable(enabled);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    /**
     * 重置状态
     */
    protected void reset() {
        mRefreshHeader.setState(SwipeView.STATE_COMPLETE);
        mRefreshFooter.setState(SwipeView.STATE_COMPLETE);
    }

    protected boolean canChildScrollUp() {
        if (mChildScrollUpCallback != null) {
            return mChildScrollUpCallback.canChildScrollUp(this, mTarget);
        }
        return mTarget != null && mTarget.canScrollVertically(-1);
    }

    protected boolean canChildScrollDown() {
        if (mChildScrollDownCallback != null) {
            return mChildScrollDownCallback.canChildScrollDown(this, mTarget);
        }
        return mTarget != null && mTarget.canScrollVertically(1);
    }

    /**
     * 设置后在 {@link #canChildScrollUp() }中优先使用该方法判断能否下拉
     */
    public void setOnChildScrollUpCallback(@Nullable OnChildScrollUpCallback callback) {
        mChildScrollUpCallback = callback;
    }

    public void setOnChildScrollDownCallback(@Nullable OnChildScrollDownCallback callback) {
        mChildScrollDownCallback = callback;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

        final int action = ev.getActionMasked();
        int pointerIndex;

        if (!isEnabled() || isRefreshing() || isLoading() || mNestedScrollInProgress) {

            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    LogUtils.e("Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                startDragging(y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            default:
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        final int action = ev.getActionMasked();
        int pointerIndex;

        if (!isEnabled() || isRefreshing() || isLoading() || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    break;
                }
                mInitialDownY = ev.getY(pointerIndex);
                //必须加上滚动判定的偏移量
                lastY.put(mActivePointerId, mInitialDownY + mTouchSlop);
                mTotalUnconsumed = 0;
                break;

            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    LogUtils.e("Got ACTION_MOVE event but have an invalid active pointer id.");
                    break;
                }
                final float y = ev.getY(pointerIndex);
                startDragging(y);

                Float aFloat = lastY.get(mActivePointerId);
                if (aFloat == null) {
                    lastY.put(mActivePointerId, y);
                    LogUtils.e("code active point null " + mActivePointerId);
                    break;
                }
                float dy = y - aFloat;
                lastY.put(mActivePointerId, y);

                if (mIsBeingDragged) {
                    if ((mTotalUnconsumed > 0 || (mTotalUnconsumed == 0 && dy > 0))
                            && !canChildScrollUp()) {
                        moveHead(mTotalUnconsumed, (int) dy);
                    } else if ((mTotalUnconsumed < 0 || (mTotalUnconsumed == 0 && dy < 0))
                            && !canChildScrollDown()) {
                        moveFoot(mTotalUnconsumed, (int) dy);
                    } else {
                        return false;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    LogUtils.e("Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                lastY.put(mActivePointerId, ev.getY(pointerIndex));
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                lastY.remove(mActivePointerId);
                if (pointerIndex < 0) {
                    LogUtils.e("Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }
                if (mIsBeingDragged) {
                    scrollToNormal();
                    mIsBeingDragged = false;
                }
                mActivePointerId = INVALID_POINTER;
                break;
            case MotionEvent.ACTION_CANCEL:
                return false;
            default:
        }
        return true;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // 如果控件是一个 List 且 Android 版本低于 L ，或者是一个不支持 nested
        // scrolling 的控件，忽略这个请求
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView) || (mTarget
                != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    // <editor-fold desc="NestedScrollingParent">

    /**
     * @param child  此ViewParent包含目标的直接子项
     * @param target 启动嵌套滚动的视图  (在这里如果不涉及多层嵌套的话,child和target是相同的
     * @param axes   由{@link ViewCompat ＃SCROLL_AXIS_HORIZONTAL} {@link ViewCompat ＃SCROLL_AXIS_VERTICAL}组成的标志或两者兼而有之
     * @return 如果此ViewParent接受嵌套滚动操作，则返回true
     */
    @Override
    public boolean onStartNestedScroll(View child, View target, int axes) {
        return onStartNestedScroll(child, target, axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target,
                                       @ScrollAxis int axes,
                                       @NestedScrollType int type) {
        if (type == ViewCompat.TYPE_NON_TOUCH && !sFlingReact) {
            return false;
        }
        boolean isVertical = (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        return isEnabled()
                && mRefreshHeader.scrollable()
                && mRefreshFooter.scrollable()
                && isVertical;
    }

    /**
     * 对成功声明嵌套滚动操作做出反应。
     *
     * <p>此方法将在之
     * {@link #onStartNestedScroll(View, View, int) }返回true后调用。 它提供 View 及其超类可以执行用于嵌套滚动的初始配置的机会
     *
     * @param child  此ViewParent包含目标的直接子项
     * @param target 启动嵌套滚动的视图
     * @param axes   由{@link ViewCompat ＃SCROLL_AXIS_HORIZONTAL} {@link ViewCompat ＃SCROLL_AXIS_VERTICAL}组成的标志，或两者兼而有之
     * @see #onStartNestedScroll(View, View, int)
     * @see #onStopNestedScroll(View)
     */
    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target,
                                       @ScrollAxis int axes,
                                       @NestedScrollType int type) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type);
        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;

        float headerHeight = mRefreshHeader.getSwipeHeight();
        if (headerHeight > 0) {
            mTotalUnconsumed = headerHeight;
            mState = STATE_REFRESH;
        } else {
            float footerHeight = mRefreshFooter.getSwipeHeight();
            if (footerHeight < 0) {
                mTotalUnconsumed = footerHeight;
                mState = STATE_LOAD_MORE;
            }
        }
    }

    /**
     * 在目标视图占用滚动的一部分之前，对正在进行的嵌套滚动作出反应。
     * <p>
     * 当嵌套滚动 child 调用 {@link View#dispatchNestedPreScroll(int, int, int[], int[])} 时 onNestedPreScroll
     * 被调用
     * <p>
     * 方法的实现应该通过 consumed 数组报告dx，dy 的像素消耗数量 consumed[0]对应于dx，consumed[1]对应于dy。
     * 此参数永远不会为null。consumed[0]和consumed[1]的初始为0
     *
     * @param target   启动嵌套滚动的视图
     * @param dx       水平滚动距离（以像素为单位）
     * @param dy       垂直滚动距离（以像素为单位）
     * @param consumed 输出。此父级消耗的水平和垂直滚动距离
     */

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH);
    }


    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed,
                                  @NestedScrollType int type) {
        //如果mHeadView或者mFooterView未隐藏
        if (mTotalUnconsumed != 0) {
            //如果是上滑且是移动head状态，消耗掉dy
            if (dy > 0 && mState == STATE_REFRESH) {
                consumed[1] = dy + (int) moveHead(mTotalUnconsumed, -dy);
                //如果是下滑且是移动foot状态
            } else if (dy < 0 && mState == STATE_LOAD_MORE) {
                consumed[1] = dy - (int) moveFoot(-mTotalUnconsumed, -dy);
            }
        }
        // 消耗掉多余的距离
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    /**
     * 当 ViewParent 的当前nested scrolling child 分发一个 nested scroll 事件时调用
     * <p>
     * 要接收对此方法的调用，ViewParent 之前必须在 onStartNestedScroll 返回 true
     * <p>
     * 滚动距离的消耗和未消耗部分都报告给 ViewParent 。
     *
     * @param target       控制嵌套滚动的后代视图
     * @param dxConsumed   目标已消耗的水平滚动距离（以像素为单位）
     * @param dyConsumed   目标已消耗的垂直滚动距离（以像素为单位）
     * @param dxUnconsumed 目标未消耗的水平滚动距离（以像素为单位）
     * @param dyUnconsumed 目标未消耗的垂直滚动距离（以像素为单位）
     */
    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                ViewCompat.TYPE_TOUCH);
    }


    /**
     * @param target       控制嵌套滚动的后代视图
     * @param dxConsumed   目标已消耗的水平滚动距离（以像素为单位）
     * @param dyConsumed   目标已消耗的垂直滚动距离（以像素为单位）
     * @param dxUnconsumed 水平滚动距离（以像素为单位不消耗）
     * @param dyUnconsumed 垂直滚动距离（以像素为单位不消耗）
     * @param type         导致此滚动事件的输入类型 ViewCompat.TYPE_TOUCH
     */
    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, @NestedScrollType int type) {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);

        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy == 0) {
            return;
        }
        if (dy < 0 && !canChildScrollUp()) {
            // 下滑且 child不能被下拉
            moveHead(mTotalUnconsumed, -dy);
        } else if (dy > 0 && !canChildScrollDown()) {
            // 上滑且child不能被上拉
            moveFoot(mTotalUnconsumed, -dy);
        }
    }

    /**
     * @param target    启动了嵌套滚动的 View
     * @param velocityX 水平速度，以每秒像素为单位
     * @param velocityY 垂直速度，以每秒像素为单位
     * @return true if this parent consumed the fling ahead of the target view
     */
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (mTotalUnconsumed > 0) {
            return true;
        }
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    /**
     * @param target    启动了嵌套滚动的 View
     * @param velocityX 水平速度，以每秒像素为单位
     * @param velocityY 垂直速度，以每秒像素为单位
     * @param consumed  如果 child 消耗了这个，则为true，否则为false
     * @return 如果此 parent 消费或以其他方式对 fling 作出反应，则为true
     */
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }


    @Override
    public void onStopNestedScroll(View target) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, @NestedScrollType int type) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        mNestedScrollInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed != 0) {
            scrollToNormal();
            mTotalUnconsumed = 0;
        }
        // Dispatch up our nested parent
        stopNestedScroll();
    }

    /**
     * 返回此NestedScrollingParent的嵌套滚动的当前方向。
     *
     * @return Flags indicating the current axes of nested scrolling
     * @see ViewCompat#SCROLL_AXIS_HORIZONTAL
     * @see ViewCompat#SCROLL_AXIS_VERTICAL
     * @see ViewCompat#SCROLL_AXIS_NONE
     */
    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    // </editor-fold >
    // <editor-fold  desc="NestedScrollingChild">

    /**
     * 启用或禁用此 View 的嵌套滚动。
     *
     * @param enabled true表示启用嵌套滚动，false表示禁用
     */
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    /**
     * @return 如果 View 启用了嵌套滚动，则返回true。
     */
    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    /**
     * 沿给定轴开始嵌套滚动操作。
     *
     * @param axes 表示滚动的方向如 ViewCompat.SCROLL_AXIS_VERTICAL(垂直方向滚动) ViewCompat.SCROLL_AXIS_HORIZONTAL(水平方向滚动)
     * @return 如果找到了合作父级并且已为当前手势启用了嵌套滚动，则为true。
     */

    @Override
    public boolean startNestedScroll(int axes) {
        return startNestedScroll(axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean startNestedScroll(@ScrollAxis int axes, @NestedScrollType int type) {
        return mNestedScrollingChildHelper.startNestedScroll(axes, type);

    }

    /**
     * 停止正在进行的嵌套滚动。
     */
    @Override
    public void stopNestedScroll() {
        stopNestedScroll(ViewCompat.TYPE_TOUCH);

    }

    @Override
    public void stopNestedScroll(@NestedScrollType int type) {
        mNestedScrollingChildHelper.stopNestedScroll(type);
    }

    /**
     * @return 如果此视图具有嵌套滚动父级，则返回true。
     */
    @Override
    public boolean hasNestedScrollingParent() {
        return hasNestedScrollingParent(TYPE_TOUCH);
    }

    @Override
    public boolean hasNestedScrollingParent(@NestedScrollType int type) {
        return mNestedScrollingChildHelper.hasNestedScrollingParent(type);

    }

    /**
     * 发送正在进行嵌套滚动的一步。
     *
     * @param dxConsumed     此滚动步骤中此视图消耗的水平距离（以像素为单位）
     * @param dyConsumed     此滚动步骤中此视图消耗的垂直距离（以像素为单位）
     * @param dxUnconsumed   此视图未消耗的水平滚动距离（以像素为单位）
     * @param dyUnconsumed   此视图未消耗的水平滚动距离（以像素为单位）
     * @param offsetInWindow 可选的。如果不为null，则返回时将包含此视图在此操作之前到完成之后的本地坐标中的偏移量。 View实现可以使用它来调整预期的输入坐标跟踪。
     *                       表示剩下的距离dxUnconsumed和dyUnconsumed使得view在父布局中的位置偏移了多少
     * @return 如果调度了事件，则为true;如果无法调度，则为false。
     * @see #dispatchNestedPreScroll(int, int, int[], int[])
     */
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow,
                TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, @Nullable int[] offsetInWindow,
                                        @NestedScrollType int type) {
        return mNestedScrollingChildHelper
                .dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow,
                        type);
    }

    /**
     * 在此 View 消耗其任何部分之前，发送嵌套滚动的一步。
     *
     * @param dx             水平滚动距离（像素）
     * @param dy             垂直滚动距离（像素）
     * @param consumed       Output. 如果不为null， consumed[0] 代表 dx 的消耗部分，consumed[1] 表示 dy 消耗.
     * @param offsetInWindow 可选的。如果不为null，则返回时将包含本地偏移量，查看此操作之前到完成之后此视图的坐标。视图实现可以使用它来调整预期的输入坐标跟踪。
     *                       表示剩下的距离dxUnconsumed和dyUnconsumed使得view在父布局中的位置偏移了多少
     * @return 如果父级使用了部分或全部滚动增量，则返回true
     * @see #dispatchNestedScroll(int, int, int, int, int[])
     */
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed,
                                           @Nullable int[] offsetInWindow, @NestedScrollType int type) {
        return mNestedScrollingChildHelper
                .dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    /**
     * 将fling发送到嵌套的滚动父级。
     *
     * @param velocityX 水平投掷速度，以每秒像素为单位
     * @param velocityY 垂直投掷速度，以每秒像素为单位
     * @param consumed  如果消磨了这个，则为true，否则为false
     * @return 如果嵌套的滚动父级消耗或以其他方式响应fling，则返回true
     */
    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        if (velocityY < 0 && !canChildScrollUp()) {
            return true;
        }
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    /**
     * 在此视图处理之前，将 fling 分发到嵌套的滚动父级。
     *
     * @param velocityX 水平投掷速度，以每秒像素为单位
     * @param velocityY 垂直投掷速度，以每秒像素为单位
     * @return 如果嵌套滚动父级使用了fling，则返回true
     */
    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        if (velocityY < 0 && !canChildScrollUp()) {
            return true;
        }
        if (velocityY > 0 && !canChildScrollDown()) {
            return true;
        }
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
    //</editor-fold>

    /**
     *
     */
    protected float moveHead(float overScrollTop, int dy) {
        mState = STATE_REFRESH;
        //将累计高度限制在返回值
        float scrollOffset = mRefreshHeader.scrollOffset(overScrollTop, dy);
        mTotalUnconsumed = mRefreshHeader.getSwipeHeight();
        return scrollOffset;
    }

    protected float moveFoot(float overScrollTop, int dy) {
        mState = STATE_LOAD_MORE;
        float scrollOffset = mRefreshFooter.scrollOffset(overScrollTop, dy);
        mTotalUnconsumed = mRefreshFooter.getSwipeHeight();
        return scrollOffset;
    }

    /**
     * 判断是否开始下拉(或者上拉)
     *
     * @param y 移动量
     */
    protected void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (yDiff > mTouchSlop && !canChildScrollUp()) {
            mIsBeingDragged = true;
        } else if (-yDiff > mTouchSlop && !canChildScrollDown()) {
            mIsBeingDragged = true;
        }
    }

    public View getTarget() {
        return mTarget;
    }

    /**
     * 多点触控，切换激活的point
     */
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    /**
     * 手势结束时被调用
     */
    protected void scrollToNormal() {
        switch (mState) {
            case STATE_REFRESH:
                mRefreshHeader.endScroll(mTotalUnconsumed);
                break;
            case STATE_LOAD_MORE:
                mRefreshFooter.endScroll(mTotalUnconsumed);
                break;
            default:
        }
        mState = STATE_NORMAL;
    }

    /**
     * 开放给Swipe控件，用于改变主控件高度
     *
     * @param y 滚动的距离
     */
    @Override
    public void changeTargetOffset(float y) {
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.setTranslationY(y);
        invalidate();
    }

    @Override
    public float getTargetOffset() {
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return 0;
        }
        return mTarget.getTranslationY();
    }

    @Override
    public void setLoadMoreEnable(boolean loadMoreEnable) {
        sLoadMoreEnable = loadMoreEnable;
        if (mRefreshFooter != null) {
            mRefreshFooter.setEnable(loadMoreEnable);
        }
    }

    @Override
    public void setRefreshEnable(boolean refreshEnable) {
        sRefreshEnable = refreshEnable;
        if (mRefreshHeader != null) {
            mRefreshHeader.setEnable(refreshEnable);
        }
    }

    public interface OnChildScrollUpCallback {

        boolean canChildScrollUp(LSwipeRefreshLayout parent, @Nullable View child);
    }

    public interface OnChildScrollDownCallback {

        boolean canChildScrollDown(LSwipeRefreshLayout parent, @Nullable View child);
    }


    public interface RefreshHeaderCreator {

        @NonNull
        SwipeView createHead(Context context);
    }

}
