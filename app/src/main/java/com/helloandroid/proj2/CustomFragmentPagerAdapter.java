package com.helloandroid.proj2;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] VIEW_MAPNTOP_TITLES = {"NUMBERS", "IMAGE", "SEARCH"};
    private ArrayList<Fragment> fList;

    public CustomFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fList){
        super(fm);
        this.fList = fList;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return VIEW_MAPNTOP_TITLES[position];
    }

    @Override
    public Fragment getItem(int positin) {
        return this.fList.get(positin);
    }

    @Override
    public int getCount() {
        return fList.size();
    }
}
