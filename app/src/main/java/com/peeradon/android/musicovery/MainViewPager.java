package com.peeradon.android.musicovery;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * MainViewPager.java
 * A custom class that extends from ViewPager
 *
 * This custom class exists to make a custom ViewPager,
 * where the second "item" (page) has its swiping functionality disabled,
 * since the second "item" is the page where OSMDroid map is shown, and the swiping
 * functionality of the ViewPager will interfere with OSMDroid map's
 * swiping functionality if not disabled.
 */

public class MainViewPager extends ViewPager{

    public MainViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch(getCurrentItem()){
            case 1: // second page
                return false;
            default:
                return super.onInterceptTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(getCurrentItem()){
            case 1: // second page
                return false;
            default:
                return super.onTouchEvent(event);
        }
    }
}
