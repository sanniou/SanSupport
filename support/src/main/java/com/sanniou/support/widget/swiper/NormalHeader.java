package com.sanniou.support.widget.swiper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sanniou.support.R;
import com.sanniou.support.utils.ResourcesUtils;


/**
 * SwipeView默认实现
 */
public class NormalHeader extends AbsSwipeView {

    private final Context mContext;
    /**
     * 判断刷新的偏移量
     */
    private float mFinalOffset = 150;
    private float mFinalHeight = 150;
    private ImageView mIvIcon;
    private TextView mTvTips;
    private ProgressBar mProgressBar;
    private float mHeadHeight;

    protected ViewGroup mContent;
    private ValueAnimator mAnimator;
    private RefreshParent mParent;
    private boolean mRefreshToUp = false;

    public void setFinalHeight(float finalHeight) {
        mFinalHeight = finalHeight;
    }

    public void setFinalOffset(float finalOffset) {
        mFinalOffset = finalOffset;
    }

    @Override
    public boolean scrollable() {
        return true;
    }

    {
        mAnimator = new ValueAnimator();
        mAnimator.addUpdateListener(animation -> setHeadHeight((float) animation.getAnimatedValue()));
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //由于模拟下拉的刷新动画与隐藏动画都用mAnimator，这里判断当收起至顶部时切换状态
                if (mHeadHeight == 0) {
                    setState(STATE_NORMAL);
                }
                if (mRefreshToUp) {
                    mRefreshToUp = false;
                }
            }
        });
    }

    public NormalHeader(Context context) {
        mContext = context;
        init();
    }

    @Override
    public View getView() {
        return mContent;
    }

    @Override
    public float scrollOffset(float totalHeight, int offset) {
        //执行完成动画时不进行移动
        if (mState == STATE_COMPLETE && mHeadHeight >= mFinalHeight) {
            return 0;
        }
        double exp = Math.exp(-(mHeadHeight / mFinalOffset));
        double induce = offset * exp;

        double v = mHeadHeight + induce;
        if (v > 0) {
            setHeadHeight((int) (Math.max(v, 0)));
            return 0;
        } else {
            setHeadHeight(0);
            return (float) v;
        }
    }

    @Override
    public void endScroll(float totalY) {
        //处于"释放刷新"状态
        if (mState == STATE_PULLING) {
            setState(STATE_LOADING);
            return;
        }
        if (mState == STATE_NORMAL) {
            hideAction();
        }
        if (mState == STATE_LOADING) {
            mAnimator.setFloatValues(mHeadHeight, mFinalHeight);
            mAnimator.start();
        }
    }

    @Override
    public int getViewHeight() {
        return (int) mFinalOffset;
    }

    @Override
    public void normalAction() {
        mIvIcon.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        ObjectAnimator.ofFloat(mIvIcon, "rotation", 180, 0).start();
        mTvTips.setText(ResourcesUtils.getString(R.string.normal_tips));
        mState = STATE_NORMAL;
    }

    @Override
    public void pullAction() {
        mIvIcon.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        ObjectAnimator.ofFloat(mIvIcon, "rotation", 0, 180).start();
        mTvTips.setText(ResourcesUtils.getString(R.string.pulling_tips));
    }

    @Override
    public void refreshAction() {
        mIvIcon.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mTvTips.setText(ResourcesUtils.getString(R.string.loading_tips));
        mState = STATE_LOADING;
        mRefreshToUp = mHeadHeight > mFinalHeight;
        mAnimator.setFloatValues(mHeadHeight, mFinalHeight);
        mAnimator.start();
    }

    @Override
    public void completeAction() {
        mIvIcon.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mTvTips.setText(ResourcesUtils.getString(R.string.complete_tips));
        ObjectAnimator.ofFloat(mIvIcon, "rotation", 180, 0).start();
        //显示1000ms后隐藏
        mContent.postDelayed(this::hideAction, 500L);
    }

    @Override
    protected void failedAction() {
        mIvIcon.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mTvTips.setText(ResourcesUtils.getString(R.string.failed_tips));
        ObjectAnimator.ofFloat(mIvIcon, "rotation", 180, 0).start();
        //显示1000ms后隐藏
        mContent.postDelayed(this::hideAction, 500L);
    }

    @Override
    public float getSwipeHeight() {
        return mHeadHeight;
    }

    /**
     * 放手后隐藏HeadView的动画
     */

    private void hideAction() {
        mAnimator.setFloatValues(mHeadHeight, 0F);
        mAnimator.start();
    }

    private void init() {
        LinearLayout content = new LinearLayout(mContext);
        content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mFinalOffset));
        content.setGravity(Gravity.CENTER);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setBackgroundColor(getBackColor());
        mContent = content;
        initContentView();
    }

    private void initContentView() {
        mContent.removeAllViews();
        ImageView imageView = new ImageView(mContext);
        LinearLayout.LayoutParams imageViewParams =
                new LinearLayout.LayoutParams(dp2px(getContext(), 20), dp2px(getContext(), 20));
        imageViewParams.setMargins(0, 0, dp2px(getContext(), 10), 0);
        imageView.setLayoutParams(imageViewParams);
        imageView.setImageResource(R.drawable.icon_pull);
        mContent.addView(imageView);

        ProgressBar progress = new ProgressBar(getContext());
        LinearLayout.LayoutParams progressParams =
                new LinearLayout.LayoutParams(dp2px(getContext(), 20), dp2px(getContext(), 20));
        progressParams.setMargins(0, 0, dp2px(getContext(), 10), 0);
        progress.setLayoutParams(progressParams);
        progress.setVisibility(View.GONE);
        mContent.addView(progress);

        TextView tvTips = new TextView(getContext());
        tvTips.setText(R.string.normal_tips);
        mContent.addView(tvTips);

        mIvIcon = imageView;
        mTvTips = tvTips;
        mProgressBar = progress;

        mState = STATE_NORMAL;
    }

    private Context getContext() {
        return mContext;
    }

    private void ensureParent() {
        if (mParent == null) {
            ViewParent parent = mContent.getParent();
            if (parent instanceof LSwipeRefreshLayout) {
                mParent = (LSwipeRefreshLayout) parent;
            }
        }
    }

    protected void setHeadHeight(float height) {
        setHeadHeight(height, false);
    }

    protected void setHeadHeight(float height, boolean offset) {
        changeContentY(height);
        //过滤请求成功或者失败时的动画值，除非target此时高度不为0
        if (offset || (mState != STATE_COMPLETE && mState != STATE_FAILED)
                || mHeadHeight > 0) {
            ensureParent();
            if (mParent != null) {
                mParent.changeTargetOffset(height);
                mHeadHeight = height;
            }
        }
        mHeadHeight = height;
        updateState();
    }

    protected void changeContentY(float height) {
        if (mState == STATE_LOADING && mHeadHeight <= mFinalOffset) {
            mContent.setTranslationY(mFinalOffset);
            return;
        }
        mContent.setTranslationY(height);
    }

    @Override
    public int getBackColor() {
        return Color.WHITE;
    }

    /**
     * 切换下拉（提示刷新）和普通状态（提示下拉）
     */
    private void updateState() {
        if (mHeadHeight > mFinalOffset && mState == STATE_NORMAL) {
            setState(STATE_PULLING);
        }
        if (mHeadHeight < mFinalOffset && mState == STATE_PULLING) {
            setState(STATE_NORMAL);
        }
    }

    private static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
