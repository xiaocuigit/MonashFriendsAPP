package com.monash.app.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.monash.app.ui.fragment.FavorUnitsFragment;
import com.monash.app.ui.fragment.LocationFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abner on 2018/4/10.
 *
 */

public class MyViewPagerAdapter extends FragmentPagerAdapter {
    private List<String> titleList = new ArrayList<>();

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
        titleList.add("units");
        titleList.add("location");
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return FavorUnitsFragment.newInstance();
            case 1:
                return LocationFragment.newInstance();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
