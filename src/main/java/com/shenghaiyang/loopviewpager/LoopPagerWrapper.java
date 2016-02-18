package com.shenghaiyang.loopviewpager;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/2/14.
 */
class LoopPagerWrapper extends PagerAdapter {

    private PagerAdapter originalAdapter;

    LoopPagerWrapper(PagerAdapter originalAdapter) {
        this.originalAdapter = originalAdapter;
    }

    int toOriginalPosition(int position) {
        int originalCount = getOriginalCount();
        if (originalCount == 0) {
            return 0;
        }
        int originalPosition = (position - 1) % originalCount;
        if (originalPosition < 0) {
            originalPosition += originalCount;
        }
        return originalPosition;
    }

    @Override
    public void startUpdate(ViewGroup container) {
        originalAdapter.startUpdate(container);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return originalAdapter.instantiateItem(container, toOriginalPosition(position));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        originalAdapter.destroyItem(container, toOriginalPosition(position), object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        originalAdapter.setPrimaryItem(container, toOriginalPosition(position), object);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        originalAdapter.finishUpdate(container);
    }

    @Override
    public Parcelable saveState() {
        return originalAdapter.saveState();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        originalAdapter.restoreState(state, loader);
    }

    @Override
    public int getItemPosition(Object object) {
        return originalAdapter.getItemPosition(object);
    }

    @Override
    public void notifyDataSetChanged() {
        originalAdapter.notifyDataSetChanged();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        originalAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        originalAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return originalAdapter.getPageTitle(toOriginalPosition(position));
    }

    @Override
    public float getPageWidth(int position) {
        return originalAdapter.getPageWidth(toOriginalPosition(position));
    }

    @Override
    public int getCount() {
        return getOriginalCount() + 2;
    }

    public int getOriginalCount() {
        return originalAdapter.getCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return originalAdapter.isViewFromObject(view, object);
    }

    public PagerAdapter getOriginalAdapter() {
        return originalAdapter;
    }
}
