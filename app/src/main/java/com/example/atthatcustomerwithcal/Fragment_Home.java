package com.example.atthatcustomerwithcal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Fragment_Home extends Fragment {

    HomeActivity homeActivity;

    //생성자
    public Fragment_Home() {

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        homeActivity = (HomeActivity)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeActivity = null;
    }


    Calendar calendar = Calendar.getInstance(Locale.KOREA);
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy.MM", Locale.KOREA);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_home,
                container, false);


        final String currentDateStr = dateFormat2.format(calendar.getTime());

        //화면요소
        FloatingActionButton floatingActionButton;
        TextView menuIv;
        TextView currentDateTv;
        CustomCalendarView_event customCalendarView_event;
        MaterialCardView materialCardView_date;
        MaterialCardView prevBtn, nextBtn;

        //initLayout
        menuIv = rootview.findViewById(R.id.menuIv);
        menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.onGoToMenuActivity();
            }
        });

        currentDateTv = rootview.findViewById(R.id.current_date_tv);
        currentDateTv.setText(currentDateStr);

        prevBtn = rootview.findViewById(R.id.previous_cardview);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        nextBtn = rootview.findViewById(R.id.next_cardview);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        materialCardView_date = rootview.findViewById(R.id.current_date_cardview);
        materialCardView_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("currentDateIsClicked?", "I dunno");
                homeActivity.onPopUpDatePickerDialog();
            }
        });

        floatingActionButton = rootview.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.onGoToAddEvent();
            }
        });

        customCalendarView_event = rootview.findViewById(R.id.customcalendar);
        customCalendarView_event.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity().getApplicationContext(), position+"", Toast.LENGTH_SHORT).show();
            }
        });

//        customCalendarView_event = rootview.findViewById(R.id.customca)

//        ViewPager2 viewPager;
//
//        FragmentStateAdapter pagerAdapter;
//
//        viewPager = rootview.findViewById(R.id.viewpager2);
////        viewPager.setPageTransformer(new ZoomOutPageTransformer());
//
//        pagerAdapter = new CalendarViewPagerAdapter(getChildFragmentManager(), getLifecycle());
//        viewPager.setAdapter(pagerAdapter);
//        viewPager.setCurrentItem(2);



        return rootview;

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("LifeCycle", "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("LifeCycle", "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LifeCycle", "onDestroy");
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("LifeCycle", "onDestroyView");
    }

}
