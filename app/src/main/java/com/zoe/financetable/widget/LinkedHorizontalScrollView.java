package com.zoe.financetable.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.HashSet;

public class LinkedHorizontalScrollView extends LinearLayout implements Handler.Callback {
    public LinkedHorizontalScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LinkedHorizontalScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public LinkedHorizontalScrollView(Context context) {
        super(context);
        init(context);
    }
    private int move, width;//总偏移量
    private boolean stop = true;
    private GestureDetector mGestureDetector;
    private Handler mHandler;
    private boolean click_end = false;
    public static int title_move = 0;
    private void init(Context context) {
        mHandler = new Handler(Looper.getMainLooper(), this);
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                click_end = move == width;
                stop = true;
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if(mClickListener != null) mClickListener.onClick(LinkedHorizontalScrollView.this);
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())) {
                    if (move((int) distanceX)) {
                        if (click_end) getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX, float velocityY) {
                stop = false;
                Message message = mHandler.obtainMessage();
                message.what = MSG_FLING;
                message.arg1 = (int) velocityX / 50;//速度太大, 变小20倍
                mHandler.sendMessage(message);
                return true;
            }

        });
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        move = x;
    }

    public void stopFling(){
        stop = true;
    }

    /**
     * @param distanceX 手指滑动的距离
     * @return true: 限制当前方向滑动(滑动到两端时)
     */
    private boolean move(int distanceX) {
        if (distanceX > 1000) distanceX = 1000;
        if (distanceX < -1000) distanceX = -1000;
        if (move == 0 && distanceX < 0) return true;
        if (move == width && distanceX > 0) return true;
        int tmp = move + distanceX;
        boolean stop = false;
        if (tmp < 0) {
            distanceX = -move;
            stop = true;
        }
        if (tmp > width) {
            distanceX = width - move;
            stop = true;
        }
        move += distanceX;
        scrollBy(distanceX, 0);

        if (mListener != null) {
            mListener.onScroll(move);
        }
        return stop;
    }

    /**
     * 停止滑动
     */
    public void up() {
        if (mListener != null) {
            mListener.onScroll(move);
        }
    }

    public int getMove() {
        return move;
    }

    private static final int MSG_FLING = 100;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MSG_FLING) {
            int arg1 = msg.arg1;
            if (Math.abs(arg1) < 1 || stop) {
                up();
                return false;
            }
            if (move(-arg1)) {
                return false;
            }
            Message message = mHandler.obtainMessage();
            message.what = MSG_FLING;
            message.arg1 = (int) (arg1 / 1.05);//速度每次减小1.1倍
            mHandler.sendMessageDelayed(message, 10);
        }
        return false;
    }
    public HashSet<LinkedHorizontalScrollView> observerList;
    public void setObserverList(HashSet<LinkedHorizontalScrollView> observerList){
        this.observerList = observerList;
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(observerList != null){
            observerList.remove(this);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        scrollTo(title_move, 0);
        if(observerList != null){
            observerList.add(this);
        }
    }

    public void setWidth(int w) {
        width = w;
    }

    private OnScrollListener mListener;
    private OnClickListener mClickListener;

    public void setOnClickListener(OnClickListener l){
        this.mClickListener = l;
    }

    public void setOnScrollListener(OnScrollListener l) {
        mListener = l;
    }

    public interface OnScrollListener {
        void onScroll(int x);
    }
    public interface OnClickListener {
        void onClick(View v);
    }

}
