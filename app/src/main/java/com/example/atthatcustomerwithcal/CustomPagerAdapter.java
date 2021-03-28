package com.example.atthatcustomerwithcal;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class CustomPagerAdapter extends PagerAdapter {

    LayoutInflater inflater;

    //Count number of tabs
    private int tabCount;


    EditText editText;


    String[] memoStr = {"123", "345", "56723", "1612", "12616"};

    public CustomPagerAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public CustomPagerAdapter(LayoutInflater inflater, int tabCount) {
        this.inflater = inflater;
        this.tabCount = tabCount;

    }



    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = null;
        view = inflater.inflate(R.layout.tab_fragment_memo, null);

        editText = view.findViewById(R.id.fragment_memoEt);
        editText.setText(memoStr[position]);
        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return  5;
        //        return views.length();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}
