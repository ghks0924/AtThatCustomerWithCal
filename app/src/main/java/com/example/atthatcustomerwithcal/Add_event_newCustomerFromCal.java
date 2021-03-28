package com.example.atthatcustomerwithcal;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Add_event_newCustomerFromCal extends Activity_Base implements View.OnClickListener {

    //화면요소
    TextView dateTv, timeTv, gradeTv;
    EditText nameEt, numberEt, recommendEt, shortmemoEt;
    Button OkBtn;

    //현재 고객리스트 가져오기
    ArrayList<Customers> savedCustomersList;

    //이전 액티비티에서 받아오는 데이터 변수
    String date;
    String month;
    String day;
    String year;

    MainActivity mainActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_newcustomerfromcal);

        // toolbar
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //뒤로가기 버튼, 디폴트값임
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //앱 제목 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //화면요소
        dateTv = findViewById(R.id.dateTv);
        timeTv = findViewById(R.id.timeTv);
        timeTv.setOnClickListener(this);
//        gradeTv = findViewById(R.id.gradeTv);
//        gradeTv.setOnClickListener(this);
        nameEt = findViewById(R.id.nameEt);
        numberEt = findViewById(R.id.numberEt);
        numberEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        recommendEt = findViewById(R.id.recommendEt);
        shortmemoEt = findViewById(R.id.shortmemoEt);
        OkBtn = findViewById(R.id.OkBtn);
        OkBtn.setOnClickListener(this);

        getDataFromPrevActivity();
        dateTv.setText(month + " " + day);

        loadCustomersData(); //현재 회원 정보 받아오기

    }//End of onCreate

    //뒤로가기 버튼에 기능 추가
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timeTv:
                Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minuts = calendar.get(Calendar.MINUTE);

                CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(Add_event_newCustomerFromCal.this
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
                break;
//            case R.id.gradeTv:
//                ShowAlertDialogWithListview();
//                break;
            case R.id.OkBtn:
                if (timeTv.getText().toString().startsWith("예약")) {
                    showToast("예약 시간을 선택해주세요.");
                } else {
                    if (nameEt.getText().toString().isEmpty() || numberEt.getText().toString().isEmpty() || !isValidCellPhoneNumber(numberEt.getText().toString())) {
                        showToast("이름 또는 전화번호를 확인해주세요.");
                    } else {


                        boolean isExistedAlready = false;
                        //핸드폰 번호로 고객 중복 확인
                        for (int i = 0; i < savedCustomersList.size(); i++) {
                            if (numberEt.getText().toString().equals(savedCustomersList.get(i).NUMBER)) {
                                isExistedAlready = true;
                                break;
                            }
                        }
                        if (isExistedAlready) {
                            showToast("이미 동일한 연락처로 저장된 고객이 있습니다.");
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
                                String formatDate = sdfNow.format(date3333); //고객저장

                                //하이픈 빼고 전화번호 저장하기
                                String phoneNum = numberEt.getText().toString().replace("-", "");
                                Log.d("numberhipen", phoneNum);



                                saveEvent(nameEt.getText().toString(), timeTv.getText().toString(), date, month, year, ""
                                        , 0, false, shortmemoEt.getText().toString(), phoneNum
                                        , false, "", "", "", "", formatDate);
                                saveCustomer(ownername, nameEt.getText().toString(), phoneNum, "신규",
                                        recommendEt.getText().toString(), "", "", shortmemoEt.getText().toString(), formatDate, 0);
                                showToast("신규 손님 및 예약이 저장되었습니다");

                                setResult(RESULT_OK);
                                finish();


                            } else {
                                showToast("동일한 시간에 저장된 예약이 있습니다.");
                            }

                        }
                    }
                }
                break;
        }
    }

    private void getDataFromPrevActivity() {
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        month = intent.getStringExtra("month");
        day = intent.getStringExtra("day");
        year = intent.getStringExtra("year");
    }

//    public void ShowAlertDialogWithListview() {
//        List<String> mAnimals = new ArrayList<String>();
//        mAnimals.add("VIP");
//        mAnimals.add("GOLD");
//        mAnimals.add("재방문");
//        mAnimals.add("신규");
//        mAnimals.add("Black");
//
////Create sequence of items
//        final CharSequence[] Animals = mAnimals.toArray(new String[mAnimals.size()]);
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
////        dialogBuilder.setTitle("고객 등급");
//        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int item) {
//                String selectedText = Animals[item].toString();  //Selected item in listview
//                gradeTv.setText(selectedText);
//            }
//        });
//        //Create alert dialog object via builder
//        AlertDialog alertDialogObject = dialogBuilder.create();
//        //Show the dialog
//        alertDialogObject.show();
//    }


    //회원정보 불러오는 메서드
    public ArrayList<Customers> loadCustomersData() {
        savedCustomersList = new ArrayList<>();
        savedCustomersList.clear();

        Log.d("tag", ownername);

        ArrayList<Customers> arrayList = new ArrayList<>();
        dbOpenHelper_customer = new DBOpenHelper_Customer(this);
        SQLiteDatabase database = dbOpenHelper_customer.getReadableDatabase();
        Cursor cursor = dbOpenHelper_customer.ReadCustomers(ownername, database);
        while (cursor.moveToNext()) {
            String ownername2 = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.OWNER_NAME));
            String customername = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.CUSTOMER_NAME));
            String number = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.NUMBER));
            String grade = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.GRADE));
            String recommend = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.RECOMMEND));
            String point = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.POINT));
            String visit = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.VISIT));
            String memo = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.MEMO));
            String savedate = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.SAVEDATE));
            int noshowcount = cursor.getInt(cursor.getColumnIndex(DBStructure_Customer.NOSHOWCOUNT));
            Customers customers = new Customers(ownername2, customername, number, grade, recommend, point, visit, memo, savedate, noshowcount);
            arrayList.add(customers);

        }
        cursor.close();

        savedCustomersList.addAll(arrayList);

        Collections.sort(savedCustomersList, comparator_customers);


        return savedCustomersList;

    }


}