package com.example.atthatcustomerwithcal;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarViewPagerAdapter extends FragmentStateAdapter {


    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy.MM", Locale.KOREA);
    Calendar calendar = Calendar.getInstance(Locale.KOREA);


    int START_POSITION = Integer.MAX_VALUE/2;

    public CalendarViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public CalendarViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public CalendarViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) { return new Fragment_EachCalendar(); }

    @Override
    public int getItemCount() {
            return 51;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
