package com.shenghaiyang.loopviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2016/2/14.
 */
public class LoopViewPager extends ViewPager {

    private LoopPagerWrapper mWrapper;
    private LoopOnPageChangeListener mListener;

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
            if (position != 0 && position != mWrapper.getCount() - 1) {
                int originalPosition = mWrapper.toOriginalPosition(position);
                mListener.onPageScrolled(originalPosition, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mListener == null) {
                return;
            }
            if (position != 0 && position != mWrapper.getCount() - 1) {
                int originalPosition = mWrapper.toOriginalPosition(position);
                mListener.onPageSelected(originalPosition);
            }
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
}
