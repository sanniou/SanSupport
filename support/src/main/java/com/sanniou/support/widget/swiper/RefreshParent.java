package com.sanniou.support.widget.swiper;

public interface RefreshParent {

    void changeTargetOffset(float height);

    float getTargetOffset();

    void setHeader(SwipeView swipeView);

    SwipeView getHeader();

    void setFooter(SwipeView swipeView);

    SwipeView getFooter();

    void startRefresh();

    void stopRefresh(boolean success);

    void startLoadMore();

    void stopLoadMore(boolean success);

    boolean isRefreshing();

    boolean isLoading();

    void setLoadMoreEnable(boolean loadMoreEnable);

    void setRefreshEnable(boolean refreshEnable);
}
