package com.jaewon.project1;

/**
 * Created by won on 2015-08-20.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by SeungHyo on 2015-08-11.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"Seoul", "Incheon", "Busan", "New York"};

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        return SuperAwesomeCardFragment.newInstance(position);
    }

    public String returnString(int i) {
        return TITLES[i];
    }
}
