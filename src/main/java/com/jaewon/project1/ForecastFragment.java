package com.jaewon.project1;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

/**
 * Created by won on 2015-07-10.
 */

public class ForecastFragment extends Fragment {

    private  PagerSlidingTabStrip tabs;
    private  ViewPager pager;
    private MyPagerAdapter adapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //Util.getInstance().printLog(DEBUG, LOG, "OnCreate!");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        pager = (ViewPager) rootView.findViewById(R.id.myPager);
        adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4,getResources()
        .getDisplayMetrics());

        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);

        return rootView;
    }
}