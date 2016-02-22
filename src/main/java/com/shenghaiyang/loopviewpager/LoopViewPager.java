package com.shenghaiyang.loopviewpager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/2/14.
 */
public class LoopViewPager extends ViewPager {

    private LoopPagerWrapper mWrapper;
    private LoopOnPageChangeListener mListener;

    private Thread mLoopThread;
    private Handler mHandler;
    private int mLoopTime;
    private int mPauseTime;
    private int i;

    public LoopViewPager(Context context) {
        super(context, null);
        init();
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mListener = new LoopOnPageChangeListener();
        super.addOnPageChangeListener(mListener);
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                setCurrentItem(msg.what);
            }
        };
        mLoopTime = 2000;
        mPauseTime = 3000;
        i = 1;
        mLoopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.currentThread().sleep(mLoopTime);
                        Message msg = mHandler.obtainMessage();
                        msg.what = i % mWrapper.getOriginalCount();
                        mHandler.sendMessage(msg);
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public void start() {
        mLoopThread.start();
    }

    private void pause() {
        if (mLoopThread.isAlive()) {
            throw new IllegalStateException("Loop thread has not start.");
        }
        try {
            mLoopThread.sleep(mPauseTime);
            i = getCurrentItem();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        mWrapper = new LoopPagerWrapper(adapter);
        super.setAdapter(mWrapper);
        super.setCurrentItem(1);
    }

    @Override
    public PagerAdapter getAdapter() {
        return mWrapper.getOriginalAdapter();
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        LoopOnPageChangeListener loopListener = new LoopOnPageChangeListener(listener);
        super.addOnPageChangeListener(loopListener);
    }

    @Override
    public void clearOnPageChangeListeners() {
        super.clearOnPageChangeListeners();
        addOnPageChangeListener(mListener);
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        setRealCurrentItem(item + 1, smoothScroll);
    }

    private void setRealCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public int getCurrentItem() {
        int item = super.getCurrentItem();
        return mWrapper.toOriginalPosition(item);
    }

    private int getRealCurrentItem() {
        return super.getCurrentItem();
    }

    private class LoopOnPageChangeListener implements OnPageChangeListener {

        private OnPageChangeListener mListener;

        private LoopOnPageChangeListener() {
        }

        private LoopOnPageChangeListener(OnPageChangeListener listener) {
            this.mListener = listener;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mListener == null) {
                return;
            }
            int originalPosition = mWrapper.toOriginalPosition(position);
            mListener.onPageScrolled(originalPosition, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            if (mListener == null) {
                return;
            }
            int originalPosition = mWrapper.toOriginalPosition(position);
            mListener.onPageSelected(originalPosition);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            int position = getRealCurrentItem();
            if (mListener == null) {
                if (ViewPager.SCROLL_STATE_IDLE == state
                        && (position == 0 || position == mWrapper.getCount() - 1)) {
                    int originalPosition = mWrapper.toOriginalPosition(position);
                    setRealCurrentItem(originalPosition + 1, false);
                }
            } else {
                mListener.onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE);
                if (position != 0 && position != mWrapper.getCount() - 1) {
                    mListener.onPageScrollStateChanged(state);
                }
            }
        }
    }

    public int getLoopTime() {
        return mLoopTime;
    }

    public void setLoopTime(int loopTime) {
        this.mLoopTime = loopTime;
    }

    public int getPauseTime() {
        return mPauseTime;
    }

    public void setPauseTime(int pauseTime) {
        this.mPauseTime = pauseTime;
    }
}
