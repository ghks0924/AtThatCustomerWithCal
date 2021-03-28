package com.example.atthatcustomerwithcal;

import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Add_event_existingCustomer extends Activity_Base implements View.OnClickListener {

    //화면 요소
    TextView dateTv, timeTv, nameTv;
    EditText memoEt;
    Button OkBtn;

    //고객 리스트뷰
    ArrayList<Customers> customersArrayList = new ArrayList<>(); //고객을 위한 어레이 리스트뷰
    ArrayList<Customers> customersArrayList_copy = new ArrayList<>(); //검색을 위해서
    //고객 리스트뷰 어댑터
    MyListAdapter_customers myListAdapter_customers;
    public int ListTempPosition;
    //AlertDialog
    android.app.AlertDialog alertDialog_events;

    //datePicker에서 저장하기 위해 추가 요소
    String month_string;
    String monthForSave;
    String day_string;
    String year_string;
    String date;
    String customer_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_existingcustomer);

        //화면 요소와 연결
        dateTv = findViewById(R.id.dateTv);
        dateTv.setOnClickListener(this);
        timeTv = findViewById(R.id.timeTv);
        timeTv.setOnClickListener(this);

        nameTv = findViewById(R.id.nameTv);
        nameTv.setOnClickListener(this);
        memoEt = findViewById(R.id.shortmemoEt);
        OkBtn = findViewById(R.id.OkBtn);
        OkBtn.setOnClickListener(this);

    }

    //===================== 날짜 선택 달력 띄우는 메소드s ============================
    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment_addEventExistingCustomer();
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


    //=========================== 클릭 이벤트 =======================
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

                CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(Add_event_existingCustomer.this
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


            case R.id.nameTv:
                final android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(Add_event_existingCustomer.this);
                builder1.setCancelable(true);

                customersArrayList.clear();
                customersArrayList = loadCustomersData();

                View addView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_dialog_customers, null);

                TextView title = addView.findViewById(R.id.title);
                final EditText searchEt = addView.findViewById(R.id.searchEt);
                searchEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String searchText = searchEt.getText().toString().toLowerCase();
                        search(searchText);
                    }
                });

                final ListView listView2 = addView.findViewById(R.id.listview_picking);
                myListAdapter_customers = new MyListAdapter_customers(this);
                listView2.setAdapter(myListAdapter_customers);

                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ListTempPosition = position;
                        nameTv.setText(customersArrayList.get(position).CUSTOMER_NAME);
                        customer_number = customersArrayList.get(position).NUMBER;
                        alertDialog_events.dismiss();
                    }
                });

                builder1.setView(addView);
                alertDialog_events = builder1.create();
                alertDialog_events.show();

                nameTv.setTextColor(Color.BLACK);


                break;

            case R.id.OkBtn:
                if (dateTv.getText().toString().startsWith("예약")) {
                    showToast("예약 날짜를 선택해주세요");
                } else {
                    if (timeTv.getText().toString().startsWith("예약")) {
                        showToast("예약 시간을 선택해주세요");
                    } else {
                        if (nameTv.getText().toString().startsWith("손님을")) {
                            showToast("손님을 선택해주세요");
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
                                Date date123123 = new Date(now);
                                // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
                                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
                                // nowDate 변수에 값을 저장한다.
                                String formatDate = sdfNow.format(date123123);
                                saveEvent(nameTv.getText().toString(), timeTv.getText().toString(), date, monthForSave + "월", year_string
                                        , "", 0, false, memoEt.getText().toString(), customer_number, false,
                                        "", "", "", "",formatDate);
//                                listviewEvents.clear();
//                                listviewEvents = collectEventByDate(date);
//                                Collections.sort(listviewEvents, comparator_events);
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

    //===================== Customer 관련 메소드 =======================

    //고객데이터 불러오는 메소드
    protected ArrayList<Customers> loadCustomersData() {
        customersArrayList.clear();
        customersArrayList_copy.clear();

        Log.d("tag", ownername);

        ArrayList<Customers> arrayList = new ArrayList<>();
        dbOpenHelper_customer = new DBOpenHelper_Customer(getApplicationContext());
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

        customersArrayList.addAll(arrayList);
        customersArrayList_copy.addAll(arrayList);

        Collections.sort(customersArrayList, comparator_customers_date);
        Collections.sort(customersArrayList_copy, comparator_customers_date);
        return customersArrayList;

    }

    //customers 정렬을 위한 메소드
    public static Comparator<Customers> comparator_customers_date = new Comparator<Customers>() {

        @Override
        public int compare(Customers o1, Customers o2) {
            return o2.SAVEDATE.compareTo(o1.SAVEDATE);
        }

    };

    //회원 검색 기능 메소드 생성
    public void search(String charText) {
        //문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        customersArrayList.clear(); // 원본 비워! 왜냐면 Adapter가 원본이랑 연결되어 있거든
        //문자입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            customersArrayList.addAll(customersArrayList_copy);
        } else { //문자입력시
            for (int i = 0; i < customersArrayList_copy.size(); i++) { //리소스의 모든 데이터를 검색한다.
                if (customersArrayList_copy.get(i).CUSTOMER_NAME.toLowerCase().contains(charText)) {
                    //검색된 데이터를 리스트에 추가한다.
                    //list.add(arraylist.get(i));
                    customersArrayList.add(customersArrayList_copy.get(i));
                }
            }
        }
        //리스트 데이터가 변경되었으므로 어댑터 갱신
        myListAdapter_customers.notifyDataSetChanged();
    }

    // ============================= ListAdapter_customers ========================

    //저장된 회원리스트 위한 ListAdapter_customers
    class RowHolder_customers {
        TextView nameTvHolder;
    }

    class MyListAdapter_customers extends ArrayAdapter {
        LayoutInflater lnf;

        public MyListAdapter_customers(android.app.Activity context) {
            super(context, R.layout.single_row_memberlsit, customersArrayList);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return customersArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return customersArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        //customers getView
        public View getView(int position, View convertView, ViewGroup parent) {
            RowHolder_customers viewHolder;
            if (convertView == null) {
                convertView = lnf.inflate(R.layout.single_row_memberlsit, parent, false);
                viewHolder = new RowHolder_customers();

                viewHolder.nameTvHolder = convertView.findViewById(R.id.name_tv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RowHolder_customers) convertView.getTag();
            }

            String phoneNum = customersArrayList.get(position).NUMBER.substring(0, 3) + "-" + customersArrayList.get(position).NUMBER.substring(3, 7) +
                    "-" + customersArrayList.get(position).NUMBER.substring(7);
            viewHolder.nameTvHolder.setText(customersArrayList.get(position).CUSTOMER_NAME + "  " + customersArrayList.get(position).GRADE
                    + "\n" + phoneNum);

            return convertView;
        }
    }


}