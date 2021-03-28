package com.example.atthatcustomerwithcal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class Fragment_EachCalendar extends Fragment {

    HomeActivity homeActivity;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        homeActivity = (HomeActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeActivity = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.fragment_screenslide_page, container, false);

        CustomCalendarView_event customCalendarViewEvent = rootview.findViewById(R.id.calendarView);


        Log.d("calendarControl", "calendar : " + customCalendarViewEvent.calendar.getTime().toString()    );
        Log.d("calendarControl", "monthCalendar : " + customCalendarViewEvent.monthCalendar.getTime().toString()    );


        customCalendarViewEvent.monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        Log.d("calendarControl", "monthCalendar : " + customCalendarViewEvent.monthCalendar.getTime().toString()    );

        customCalendarViewEvent.SetUpCalendar();

        customCalendarViewEvent.gridView = rootview.findViewById(R.id.gridview);
        customCalendarViewEvent.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("calendarInFragment", position + " ");
            }
        });


        return rootview;

    }
}
