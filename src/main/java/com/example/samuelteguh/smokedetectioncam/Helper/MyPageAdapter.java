package com.example.samuelteguh.smokedetectioncam.Helper;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MyPageAdapter extends FragmentStatePagerAdapter {
    public MyPageAdapter(FragmentManager fm){
        super(fm);
    }
    public Fragment getItem(int position){
        return  null;
    }
    public int getCount(){
        return 10;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}
