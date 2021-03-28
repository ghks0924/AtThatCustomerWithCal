package com.example.atthatcustomerwithcal;

import androidx.fragment.app.DialogFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Add_event_nonFromHome extends Activity_Base implements View.OnClickListener {

    //화면 요소
    TextView dateTv, timeTv;
    EditText memoEt;
    Button OkBtn;

    //datePicker에서 저장하기 위해 추가 요소
    String month_string;
    String monthForSave;
    String day_string;
    String year_string;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_non);

        //화면 요소와 연결
        dateTv = findViewById(R.id.dateTv);
        dateTv.setOnClickListener(this);
        timeTv = findViewById(R.id.timeTv);
        timeTv.setOnClickListener(this);

        memoEt = findViewById(R.id.shortmemoEt);
        OkBtn = findViewById(R.id.OkBtn);
        OkBtn.setOnClickListener(this);


    }

    //날짜 선택 달력 띄우는 메소드
    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment_addEventNon();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void processDatePickerResult(int year, int month, int day) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.KOREA);

        if (month < 10) {
            month_string = "0" + Integer.toString(month + 1);
        } else {
            month_string = Integer.toString(month + 1);
        }

        monthForSave = Integer.toString(month + 1);

        if (day < 10) {
            day_string = "0" + Integer.toString(day);
        } else {
            day_string = Integer.toString(day);
        }


        year_string = Integer.toString(year);


        date = year_string + "-" + month_string + "-" + day_string;


        dateTv.setText(month_string + "월  " + day_string + "일");
        Log.d("dateCompo", "month_string at save " + month_string);
        Log.d("dateCompo", "day_string at save " + day_string);
        Log.d("dateCompo", "year_string at save " + year_string);
        Log.d("dateCompo", "date at save " + date);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateTv:
                showDatePicker(v);
                dateTv.setTextColor(Color.BLACK);
                break;

            case R.id.timeTv:
                Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minuts = calendar.get(Calendar.MINUTE);

                CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(Add_event_nonFromHome.this
                        , new CustomTimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        c.setTimeZone(TimeZone.getDefault());
                        SimpleDateFormat hformat = new SimpleDateFormat("a K:mm", Locale.KOREA);
                        String event_Time = hformat.format(c.getTime());
                        timeTv.setText(event_Time);
                        timeTv.setTextColor(Color.BLACK);
                    }
                }, hours, minuts, true);

                timePickerDialog.show();
                break;

            case R.id.OkBtn:
                if (dateTv.getText().toString().startsWith("예약")) {
                    showToast("예약 날짜를 선택해주세요");
                } else {
                    if (timeTv.getText().toString().startsWith("예약")) {
                        showToast("예약 시간을 선택해주세요");
                    } else {
                        //예약 중복 방지
                        boolean isExistedAlreadyForDateTime = false;
                        ArrayList<Events> tempArrayList = collectEventByDate(date);

                        for (int i = 0; i < tempArrayList.size(); i++) {
                            if (timeTv.getText().toString().equals(tempArrayList.get(i).TIME) && date.equals(tempArrayList.get(i).DATE)) {

                                isExistedAlreadyForDateTime = true;
                                break;
                            }
                        }
                        if (!isExistedAlreadyForDateTime) {

                            // 현재시간을 msec 으로 구한다.
                            long now = System.currentTimeMillis();
                            // 현재시간을 date 변수에 저장한다.
                            Date date3333 = new Date(now);
                            // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
                            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
                            // nowDate 변수에 값을 저장한다.
                            String formatDate = sdfNow.format(date3333);

                            saveEvent("미등록", timeTv.getText().toString(), date, monthForSave + "월", year_string
                                    , "", 0, false, memoEt.getText().toString(), "미등록",
                                    false, "", "", "", "", formatDate);

                            finish();

                        } else {
                            showToast("동일한 시간에 저장된 예약이 있습니다.");
                        }

                    }

                }
                break;
        }
    }

}