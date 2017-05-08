package com.peeradon.android.musicovery;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * MainPageAdapter.java
 * A custom class that extends from FragmentPagerAdapter
 *
 * The class takes 3 fragments as input parameters
 * these input fragments are used by FragmentPagerAdapter to populate the ViewPager
 */

public class MainPageAdapter extends FragmentPagerAdapter {
    final private int PAGE_COUNT = 3;
    private Fragment frag1;
    private Fragment frag2;
    private Fragment frag3;
    private String[] tab_names;

    public MainPageAdapter(FragmentManager fm, Fragment frag1, Fragment frag2, Fragment frag3, Context context) {
        super(fm);
        this.frag1 = frag1;
        this.frag2 = frag2;
        this.frag3 = frag3;
        this.tab_names = context.getResources().getStringArray(R.array.tab_names);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 1:
                return tab_names[1];
            case 2:
                return tab_names[2];
            case 3:
                return tab_names[3];
            default:
                return tab_names[0];
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return frag1;
            case 1:
                return frag2;
            case 2:
                return frag3;
            default:
                return null;
        }
    }
}
