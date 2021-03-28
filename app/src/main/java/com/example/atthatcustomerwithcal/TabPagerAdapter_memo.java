package com.example.atthatcustomerwithcal;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabPagerAdapter_memo extends FragmentStatePagerAdapter {

    //Count number of tabs
    private int tabCount;

    public TabPagerAdapter_memo(@NonNull FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                TabMemoFragment0 fragment1 = new TabMemoFragment0();
                return fragment1;
            case 1:
                TabMemoFragment1 fragment2 = new TabMemoFragment1();
                return fragment2;
            case 2:
                TabMemoFragment2 fragment3 = new TabMemoFragment2();
                return fragment3;
            case 3:
                TabMemoFragment3 fragment4 = new TabMemoFragment3();
                return fragment4;
            case 4:
                TabMemoFragment4 fragment5 = new TabMemoFragment4();
                return fragment5;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabCount;
    }


}
