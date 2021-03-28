package com.example.atthatcustomerwithcal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Add_event_nonFromCal extends Activity_Base implements View.OnClickListener {

    //화면 요소
    TextView dateTv, timeTv;
    EditText memoEt;
    Button OkBtn;

    //이전 화면에서 넘어오는 요소
    String datePrevAct = "", monthPrevAct = "", dayPrevAct = "", yearPrevAct = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_non);

        //화면 요소와 연결
        dateTv = findViewById(R.id.dateTv);
        timeTv = findViewById(R.id.timeTv);
        timeTv.setOnClickListener(this);

        memoEt = findViewById(R.id.shortmemoEt);
        OkBtn = findViewById(R.id.OkBtn);
        OkBtn.setOnClickListener(this);

        getDataFromPrevActivity();
        if (!dayPrevAct.equals("")) {
            dateTv.setText(monthPrevAct + " " + dayPrevAct);
        }

    }

    //날짜 선택 달력 띄우는 메소드
    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment_addEventNon();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timeTv:
                Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minuts = calendar.get(Calendar.MINUTE);

                CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(Add_event_nonFromCal.this
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
                        ArrayList<Events> tempArrayList = collectEventByDate(datePrevAct);

                        for (int i = 0; i < tempArrayList.size(); i++) {
                            if (timeTv.getText().toString().equals(tempArrayList.get(i).TIME) && datePrevAct.equals(tempArrayList.get(i).DATE)) {

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


                            saveEvent("미등록", timeTv.getText().toString(), datePrevAct, monthPrevAct, yearPrevAct
                                    , "", 0, false, memoEt.getText().toString(), "미등록", false, ""
                                    , "", "", "", formatDate);

                            setResult(RESULT_OK);
                            finish();


                        } else {
                            showToast("동일한 시간에 저장된 예약이 있습니다.");
                        }
                    }

                }
                break;
        }
    }

    private void getDataFromPrevActivity() {
        Intent intent = getIntent();
        datePrevAct = intent.getStringExtra("date");
        monthPrevAct = intent.getStringExtra("month");
        dayPrevAct = intent.getStringExtra("day");
        yearPrevAct = intent.getStringExtra("year");
    }
}