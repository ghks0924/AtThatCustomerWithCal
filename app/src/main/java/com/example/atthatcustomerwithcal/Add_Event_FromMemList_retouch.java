package com.example.atthatcustomerwithcal;

import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Add_Event_FromMemList_retouch extends Activity_Base {

    TextView dateTv, timeTv, nameTv, numberTv, priceTv;
    EditText shortmemoEt;
    RadioGroup radioGroup;
    RadioButton priceRb, freeRb;
    EditText priceEt;


    String customer_name;
    String customer_number;
    String retouch;

    String month_string;
    String monthForSave;
    String day_string;
    String year_string;
    String date;

    List<Events> listviewEvents = new ArrayList<>(); //listview

    private DatePickerDialog.OnDateSetListener callbackMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__event__from_memlist_re);

        //이전 액티비티에서 정보 받아오기 고객이름, 번호
        getDataFromPrevActivity();


        //화면요소
        dateTv = findViewById(R.id.dateTv);
        dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
//                Intent intent = new Intent(Add_Event_FromMemList.this, PopupCalendarActivity.class);
//                startActivity(intent);
//                DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, 2019, 5, 24);
//                dialog.show();
            }
        });

        timeTv = findViewById(R.id.timeTv);
        timeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minuts = calendar.get(Calendar.MINUTE);

                CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(Add_Event_FromMemList_retouch.this
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
                }, hours, minuts, false);

                timePickerDialog.show();
            }
        });
        nameTv = findViewById(R.id.nameTv);
        numberTv = findViewById(R.id.numberTv);

        nameTv.setText(customer_name);
        numberTv.setText(customer_number);
        shortmemoEt = findViewById(R.id.shortmemoEt);

        radioGroup = findViewById(R.id.radioGroup);
        priceRb = findViewById(R.id.priceRb);
        freeRb = findViewById(R.id.freeRb);
        priceEt = findViewById(R.id.priceEt);
        priceTv = findViewById(R.id.priceTv);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.priceRb:
                        priceEt.setVisibility(View.VISIBLE);
                        priceTv.setVisibility(View.VISIBLE);
                        break;

                    case R.id.freeRb:
                        priceEt.setVisibility(View.GONE);
                        priceTv.setVisibility(View.GONE);
                        break;
                }
            }
        });

        findViewById(R.id.OkBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!priceRb.isChecked() && !freeRb.isChecked()) {
                    showToast("리터치 종류를 선택해주세요");
                } else {
                    if (priceRb.isChecked()) {//유료 리터치인경우
                        if (dateTv.getText().toString().startsWith("예약")) {
                            showToast("예약 날짜를 선택해주세요");
                        } else {
                            if (priceEt.getText().toString().isEmpty()) {
                                showToast("리터치 가격을 입력해주세요.");
                            } else {
                                if (timeTv.getText().toString().startsWith("예약")) {
                                    showToast("예약 시간을 선택해주세요");
                                } else {
                                    //예약 중복 막기 date&&time으로
                                    boolean isExistedAlreadyForDateTime = false;

                                    for (int i = 0; i < listviewEvents.size(); i++) {
                                        if (timeTv.getText().toString().equals(listviewEvents.get(i).TIME) && date.equals(listviewEvents.get(i).DATE)) {

                                            isExistedAlreadyForDateTime = true;
                                            break;
                                        }
                                    }

                                    if (!isExistedAlreadyForDateTime) {
                                        // 현재시간을 msec 으로 구한다.
                                        long now = System.currentTimeMillis();
                                        // 현재시간을 date 변수에 저장한다.
                                        Date date123123 = new Date(now);
                                        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
                                        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
                                        // nowDate 변수에 값을 저장한다.
                                        String formatDate = sdfNow.format(date123123);
                                        saveEvent(nameTv.getText().toString(), timeTv.getText().toString(), date, monthForSave + "월"
                                                , year_string, retouch, Integer.parseInt(priceEt.getText().toString()), false
                                                , shortmemoEt.getText().toString(),customer_number, false, "유료리터치", "", "",
                                                "", formatDate);
                                        listviewEvents.clear();
                                        listviewEvents = collectEventByDate(date);
                                        Collections.sort(listviewEvents, comparator_events);
                                        finish();
                                    } else {
                                        showToast("동일한 시간에 저장된 예약이 있습니다.");

                                    }
                                }

                            }

                        }
                    } else { //무료 리터치인경우
                        if (dateTv.getText().toString().startsWith("예약")) {
                            showToast("예약 날짜를 선택해주세요");
                        } else {
                            if (timeTv.getText().toString().startsWith("예약")) {
                                showToast("예약 시간을 선택해주세요");
                            } else {
                                //예약 중복 막기 date&&time으로
                                boolean isExistedAlreadyForDateTime = false;

                                for (int i = 0; i < listviewEvents.size(); i++) {
                                    if (timeTv.getText().toString().equals(listviewEvents.get(i).TIME) && date.equals(listviewEvents.get(i).DATE)) {

                                        isExistedAlreadyForDateTime = true;
                                        break;
                                    }
                                }

                                if (!isExistedAlreadyForDateTime) {
                                    // 현재시간을 msec 으로 구한다.
                                    long now = System.currentTimeMillis();
                                    // 현재시간을 date 변수에 저장한다.
                                    Date date123123 = new Date(now);
                                    // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
                                    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
                                    // nowDate 변수에 값을 저장한다.
                                    String formatDate = sdfNow.format(date123123);
                                    saveEvent(nameTv.getText().toString(), timeTv.getText().toString(), date, monthForSave + "월"
                                            , year_string, retouch, 0, false, shortmemoEt.getText().toString(),customer_number
                                            , false, "무료리터치", "", "", "", formatDate);
                                    listviewEvents.clear();
                                    listviewEvents = collectEventByDate(date);
                                    Collections.sort(listviewEvents, comparator_events);
                                    finish();
                                } else {
                                    showToast("동일한 시간에 저장된 예약이 있습니다.");

                                }
                            }


                        }
                    }

                }


            }
        });



    }

    //이전 액티비티에서 받아오는 정보
    private void getDataFromPrevActivity() {
        Intent intent = getIntent();
        customer_name = intent.getStringExtra("customer_name");
        customer_number = intent.getStringExtra("customer_number");
        retouch = intent.getStringExtra("retouch");
    }

    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
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

        loadEventsList();
    }

    //ReadEventslist
    private void loadEventsList() {
        listviewEvents.clear();
        listviewEvents = collectEventByDate(date);
        Collections.sort(listviewEvents, comparator_events);
    }

}