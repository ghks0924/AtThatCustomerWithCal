package com.example.atthatcustomerwithcal;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

public class PopupCalendarActivity extends Activity_Base {

    CustomCalendarView_event calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_calendar);

        calendarView = findViewById(R.id.activity_custom_calendar_view);

        calendarView.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showToast(position+"");
            }
        });

    }
}