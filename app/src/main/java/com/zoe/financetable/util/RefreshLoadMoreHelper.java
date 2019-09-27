package com.zoe.financetable.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zoe.financetable.R;

/**
 * Created by heningkai on 16/10/25.
 */

public class RefreshLoadMoreHelper {
    private TextView mTvLoadingMore;
    private ImageView mIvProgress;
    private TextView mTvLoadError;
    private View mLoadMoreLayout;
    private boolean hasMore = true;
    public static final int STATE_PULL2LOAD = 0;
    public static final int STATE_RELEASE2LOAD = 1;
    public static final int STATE_LOADING = 2;
    public static final int STATE_NO_MORE = 3;
    private int state = STATE_PULL2LOAD;

    public RefreshLoadMoreHelper(Context context) {
        this.mLoadMoreLayout = LayoutInflater.from(context).inflate(R.layout.loading_more, null);
        this.mTvLoadingMore = (TextView) this.mLoadMoreLayout.findViewById(R.id.tv_loading_more);
        this.mIvProgress = (ImageView) this.mLoadMoreLayout.findViewById(R.id.iv_progress);
        this.mTvLoadError = (TextView) this.mLoadMoreLayout.findViewById(R.id.tv_load_error);

        Glide.with(context).load(R.drawable.loading_bottom).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (resource instanceof GifDrawable) {
                    //加载一次
                    ((GifDrawable)resource).setLoopCount(Integer.MAX_VALUE);
                }
                return false;
            }
        }).into(mIvProgress);
    }

    public void setOnErrorClickListener(View.OnClickListener onErrorClickListener) {
        this.mTvLoadError.setOnClickListener(onErrorClickListener);
    }

    public void setBackgroundColor(int color) {
        this.mLoadMoreLayout.setBackgroundColor(color);
    }

    public int getState(){
        return state;
    }
    // 添加底部加载视图
    public View getFooterView() {
        mTvLoadingMore.setText("上拉加载更多");
        state = STATE_PULL2LOAD;
        showTextView();
        hasMore = true;
        mLoadMoreLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        return mLoadMoreLayout;
    }

    // 设置开始加载
    public void setFooterViewLoading() {
        mTvLoadingMore.setText("正在加载");
        state = STATE_LOADING;
        showProgressView();
    }
    public void setFooterViewPull2Load() {
        mTvLoadingMore.setText("上拉加载更多");
        state = STATE_PULL2LOAD;
        showTextView();
        hasMore = true;
    }
    public void setFooterViewRelease2Load() {
        mTvLoadingMore.setText("松开加载更多");
        state = STATE_RELEASE2LOAD;
        showTextView();
    }
    public void setFooterViewHasNoMoreData() {
        mTvLoadingMore.setText("没有更多了");
        state = STATE_NO_MORE;
        showTextView();
        hasMore = false;
    }
    private void showTextView() {
        mTvLoadingMore.setVisibility(View.VISIBLE);
        mIvProgress.setVisibility(View.GONE);
        mTvLoadError.setVisibility(View.GONE);
    }
    private void showProgressView() {
        mTvLoadingMore.setVisibility(View.VISIBLE);
        mIvProgress.setVisibility(View.VISIBLE);
        mTvLoadError.setVisibility(View.GONE);
    }

    public void showErrorView() {
        mTvLoadingMore.setVisibility(View.GONE);
        mIvProgress.setVisibility(View.GONE);
        mTvLoadError.setVisibility(View.VISIBLE);
    }

    public boolean hasMore(){
        return hasMore;
    }

}
