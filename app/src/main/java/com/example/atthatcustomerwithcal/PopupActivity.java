//package com.example.atthatcustomerwithcal;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.TimePicker;
//import android.widget.Toast;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//import java.util.TimeZone;
//
//import static android.widget.Toast.LENGTH_SHORT;
//import static android.widget.Toast.makeText;
//import static com.example.atthatcustomerwithcal.BaseActivity.comparator_customers;
//import static com.example.atthatcustomerwithcal.BaseActivity.comparator_events;
//
//public class PopupActivity extends Activity {
//
//    //DBOpenHelper
//    DBOpenHelper dbOpenHelper;
//    DBOpenHelper_Customer dbOpenHelper_customer;
//    DBOpenHelper_Menu dbOpenHelper_menu;
//
//    CustomCalendarView calendarView;
//
//    List<Date> dates = new ArrayList<>();
//    ArrayList<Customers> customersArrayList = new ArrayList<>(); //고객을 위한 어레이 리스트뷰
//    ArrayList<Customers> customersArrayList_copy = new ArrayList<>(); //검색을 위해서
//    ArrayList<MenuList> menuArraylist = new ArrayList<>();//메뉴위한
//
//    List<Events> listviewEvents = new ArrayList<>(); //listview
//
//    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.KOREA);
//    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.KOREA);
//    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
//    SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
//
//
//    ListView listView;
//    MyListAdapter_events myListAdapter_events;
//    MyListAdapter_customers myListAdapter_customers;
//
//    //AlertDialog
//    AlertDialog alertDialog_events;
//    AlertDialog alertDialog_addEvents;
//
//    //논리 or 기능
//    boolean isChecked; //addEvent때 리터치 여부
//    String isCheckedStr;
//    int tempPrice;
//    boolean tempComplete;
//    //화면요소
//    TextView titleTv;
//    LinearLayout frameLl;
//    Button btn;
//
//    //이전 액티비티에서 받아오는 요소
//    String date;
//    String month;
//    String day;
//    int GridTempPosition;
//    int ListTempPosition;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_popup);
//
//        //화면요소
//        frameLl = findViewById(R.id.frameLl);
//        titleTv = findViewById(R.id.titleTv);
//        btn = findViewById(R.id.btn);
//        listView = findViewById(R.id.listview);
//
//
//        getDataFromPrevActivity();
//        //ReadEventslist
//        loadEventsList();
//        //title 세팅
//        titleTv.setText(month + " " + day);
//        //리스트뷰 어댑터 연결
//        myListAdapter_events = new MyListAdapter_events(this);
//        listView.setAdapter(myListAdapter_events);
//
//        //시숧완료 컨트롤을 위한 ItemClickListener
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ListTempPosition = position;
//                Toast.makeText(PopupActivity.this, listviewEvents.get(position).EVENT, LENGTH_SHORT).show();
//                ShowAlertDialogWithListview_menu();
//            }
//        });
//
//        //예약 수정 및 삭제를 위한 ItemLongClinkListener
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                ListTempPosition = position;
//                androidx.appcompat.app.AlertDialog.Builder ab = new androidx.appcompat.app.AlertDialog.Builder(PopupActivity.this);
//                ab.setTitle("예약 수정/삭제");
//                ab.setIcon(R.mipmap.ic_launcher);
//                ab.setMessage(listviewEvents.get(ListTempPosition).EVENT + " " + listviewEvents.get(ListTempPosition).TIME + "\n예약을 수정 또는 삭제하시겠습니까? ");
//                ab.setPositiveButton("예", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        deleteEvents();
//
//                        listviewEvents.clear();
//                        listviewEvents = CollectEventByDate(date);
//                        Collections.sort(listviewEvents, comparator_events);
//                        listView.setAdapter(myListAdapter_events);
//                        Toast.makeText(PopupActivity.this, "예약이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                ab.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//
//                ab.setNeutralButton("수정", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (listviewEvents.get(ListTempPosition).PRICE != 0) {
//                            Toast.makeText(PopupActivity.this, "작업이 끝난 예약은 수정할 수 없습니다", Toast.LENGTH_SHORT).show();
//                        } else {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(PopupActivity.this);
//                            builder.setCancelable(true);
//                            final View addView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_newevent_layout_test, null);
//
//                            final TextView title = addView.findViewById(R.id.title);
//                            title.setText("예약 수정");
//                            final TextView EventName = addView.findViewById(R.id.eventname);
//                            EventName.setText(listviewEvents.get(ListTempPosition).EVENT); //예약 수정시에는 시간, 리터치 여부만 바꾸게 해줄거임
//
//                            final CheckBox retouch_chBox = addView.findViewById(R.id.retouch_chBox);
//                            if (!listviewEvents.get(ListTempPosition).RETOUCH.isEmpty()) {
//                                retouch_chBox.setChecked(true);
//                            } else {
//                                retouch_chBox.setChecked(false);
//                            }
//
//                            final TextView eventTime = addView.findViewById(R.id.eventtime);
//                            eventTime.setText(listviewEvents.get(ListTempPosition).TIME);
//                            Log.d("updateEventTest", "before" + listviewEvents.get(ListTempPosition).TIME);
//                            eventTime.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Calendar calendar = Calendar.getInstance();
//                                    int hours = calendar.get(Calendar.HOUR_OF_DAY);
//                                    int minuts = calendar.get(Calendar.MINUTE);
//
//                                    CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(addView.getContext()
//                                            , new CustomTimePickerDialog.OnTimeSetListener() {
//                                        @Override
//                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                                            Calendar c = Calendar.getInstance();
//                                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                                            c.set(Calendar.MINUTE, minute);
//                                            c.setTimeZone(TimeZone.getDefault());
//                                            SimpleDateFormat hformat = new SimpleDateFormat("a K:mm", Locale.KOREA);
//                                            String event_Time = hformat.format(c.getTime());
//                                            eventTime.setText(event_Time);
//                                            eventTime.setTextColor(Color.BLACK);
//                                        }
//                                    }, hours, minuts, false);
//
//                                    timePickerDialog.show();
//                                }
//                            });
//                            final String date = eventDateFormat.format(dates.get(GridTempPosition));
//                            final String month = monthFormat.format(dates.get(GridTempPosition));
//                            final String year = yearFormat.format(dates.get(GridTempPosition));
//
//                            final Button updateBtn = addView.findViewById(R.id.addevent);
//                            updateBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    isChecked = retouch_chBox.isChecked();
//                                    if (isChecked) {
//                                        isCheckedStr = "리터치";
//                                    } else {
//                                        isCheckedStr = "";
//                                    }
//                                    reviseEvent(listviewEvents.get(ListTempPosition).EVENT,
//                                            listviewEvents.get(ListTempPosition).DATE,
//                                            listviewEvents.get(ListTempPosition).TIME,
//                                            eventTime.getText().toString(),
//                                            isCheckedStr);
//                                    Log.d("updateEventTest", "after" + listviewEvents.get(ListTempPosition).TIME);
//                                    Log.d("updateEventTest", "after 2" + eventTime.getText().toString());
//
//                                    listviewEvents.clear();
//                                    listviewEvents = CollectEventByDate(date);
//                                    Collections.sort(listviewEvents, comparator_events);
//                                    listView.setAdapter(myListAdapter_events);
//
//                                    alertDialog_addEvents.dismiss();
//                                }
//                            });
//
//                            builder.setView(addView);
//                            alertDialog_addEvents = builder.create();
//                            alertDialog_addEvents.show();
//                        }
//
//
//                    }
//                });
//                ab.setCancelable(true);
//                ab.show();
//                return true;
//
//            }
//        });
//
//
//
//
//
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final AlertDialog.Builder builder = new AlertDialog.Builder(PopupActivity.this);
//                builder.setCancelable(true);
//                View addView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_newevent_layout_test, null);
//
//                TextView title = addView.findViewById(R.id.title);
//                final TextView EventName = addView.findViewById(R.id.eventname);
//                EventName.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        final AlertDialog.Builder builder1 = new AlertDialog.Builder(PopupActivity.this);
//                        builder1.setCancelable(true);
//
//                        customersArrayList.clear();
//                        customersArrayList = loadCustomersData();
//                        Collections.sort(customersArrayList, comparator_customers);
//
//                        View addView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_dialog_customers, null);
//
//                        TextView title = addView.findViewById(R.id.title);
//                        final EditText searchEt = addView.findViewById(R.id.searchEt);
//                        searchEt.addTextChangedListener(new TextWatcher() {
//                            @Override
//                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                            }
//
//                            @Override
//                            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                            }
//
//                            @Override
//                            public void afterTextChanged(Editable s) {
//                                String searchText = searchEt.getText().toString().toLowerCase();
//                                search(searchText);
//                            }
//                        });
////                        final Button searchBtn = addView.findViewById(R.id.searchBtn);
//////
//                        final ListView listView2 = addView.findViewById(R.id.listview_picking);
//                        myListAdapter_customers = new MyListAdapter_customers(PopupActivity.this);
//                        listView2.setAdapter(myListAdapter_customers);
//
//                        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                EventName.setText(customersArrayList.get(position).CUSTOMER_NAME + "_" + customersArrayList.get(position).GRADE);
//                                alertDialog_events.dismiss();
//                            }
//                        });
//
//                        builder1.setView(addView);
//                        alertDialog_events = builder1.create();
//                        alertDialog_events.show();
//                    }
//                });
//
////                final CheckBox retouch_chBox = addView.findViewById(R.id.retouch_chBox);
//                final TextView eventTime = addView.findViewById(R.id.eventtime);
//                eventTime.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Calendar calendar = Calendar.getInstance();
//                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
//                        int minuts = calendar.get(Calendar.MINUTE);
//
//                        CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(PopupActivity.this
//                                , new CustomTimePickerDialog.OnTimeSetListener() {
//                            @Override
//                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                                Calendar c = Calendar.getInstance();
//                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                                c.set(Calendar.MINUTE, minute);
//                                c.setTimeZone(TimeZone.getDefault());
//                                SimpleDateFormat hformat = new SimpleDateFormat("a K:mm", Locale.KOREA);
//                                String event_Time = hformat.format(c.getTime());
//                                eventTime.setText(event_Time);
//                                eventTime.setTextColor(Color.BLACK);
//                            }
//                        }, hours, minuts, false);
//
//                        timePickerDialog.show();
//                    }
//                });
//
//                final String date2 = eventDateFormat.format(dates.get(GridTempPosition));
//                final String month2 = monthFormat.format(dates.get(GridTempPosition));
//                final String year2 = yearFormat.format(dates.get(GridTempPosition));
//
//
//                final Button addBtn = addView.findViewById(R.id.addevent);
//                addBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        isChecked = retouch_chBox.isChecked();
//                        if (isChecked) {
//                            isCheckedStr = "리터치";
//                        } else {
//                            isCheckedStr = "";
//                        }
//                        SaveEvent(EventName.getText().toString(), eventTime.getText().toString(), date2, month2, year2, isCheckedStr, 0, false);
////                        SetUpCalendar();
//
//                        listviewEvents.clear();
//                        listviewEvents = CollectEventByDate(date);
//                        Collections.sort(listviewEvents, comparator_events);
//                        listView.setAdapter(myListAdapter_events);
////                        SetUpCalendar();
//
//                        alertDialog_addEvents.dismiss();
//
//                    }
//                });
//
//                builder.setView(addView);
//                alertDialog_addEvents = builder.create();
//                alertDialog_addEvents.show();
//            }
//        });
//
//
//        frameLl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//    }
//
//    //이전 액티비티에서 받아오는 정보
//    private void getDataFromPrevActivity() {
//        Intent intent = getIntent();
//        date = intent.getStringExtra("date");
//        month = intent.getStringExtra("month");
//        day = intent.getStringExtra("day");
//        dates = (List<Date>) intent.getSerializableExtra("dates");
//
//        GridTempPosition = intent.getIntExtra("gridPosition", 0);
//    }
//
//    //ReadEventslist
//    private void loadEventsList() {
//        listviewEvents.clear();
//        listviewEvents = CollectEventByDate(date);
//        Collections.sort(listviewEvents, comparator_events);
//    }
//
//    //회원 검색 기능 메소드 생성
//    public void search(String charText) {
//        //문자 입력시마다 리스트를 지우고 새로 뿌려준다.
//        customersArrayList.clear(); // 원본 비워! 왜냐면 Adapter가 원본이랑 연결되어 있거든
//        //문자입력이 없을때는 모든 데이터를 보여준다.
//        if (charText.length() == 0) {
//            customersArrayList.addAll(customersArrayList_copy);
//        } else { //문자입력시
//            for (int i = 0; i < customersArrayList_copy.size(); i++) { //리소스의 모든 데이터를 검색한다.
//                if (customersArrayList_copy.get(i).CUSTOMER_NAME.toLowerCase().contains(charText)) {
//                    //검색된 데이터를 리스트에 추가한다.
//                    //list.add(arraylist.get(i));
//                    customersArrayList.add(customersArrayList_copy.get(i));
//                }
//            }
//        }
//        //리스트 데이터가 변경되었으므로 어댑터 갱신
//        myListAdapter_customers.notifyDataSetChanged();
//    }
//
//    //다이얼로그 바깥클릭시 닫히도록
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //바깥레이어 클릭시 안닫히게
//        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//
//            return true;
//        } else {
//            finish();
//            return false;
//        }
//
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        //안드로이드 백버튼 막기
//        finish();
//        return;
//    }
//
//    //회원정보 불러오는 메서드
//    public ArrayList<Customers> loadCustomersData() {
//        customersArrayList.clear();
//        customersArrayList_copy.clear();
//
//        Log.d("tag", ownername);
//
//        ArrayList<Customers> arrayList = new ArrayList<>();
//        dbOpenHelper_customer = new DBOpenHelper_Customer(getApplicationContext());
//        SQLiteDatabase database = dbOpenHelper_customer.getReadableDatabase();
//        Cursor cursor = dbOpenHelper_customer.ReadCustomers(ownername, database);
//        while (cursor.moveToNext()) {
//            String ownername2 = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.OWNER_NAME));
//            String customername = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.CUSTOMER_NAME));
//            String number = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.NUMBER));
//            String grade = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.GRADE));
//            String recommend = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.RECOMMEND));
//            String point = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.POINT));
//            String visit = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.VISIT));
//            String memo = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.MEMO));
//            Customers customers = new Customers(ownername2, customername, number, grade, recommend, point, visit, memo);
//            arrayList.add(customers);
//
//        }
//        cursor.close();
//
//        customersArrayList.addAll(arrayList);
//        customersArrayList_copy.addAll(arrayList);
//
//        Collections.sort(customersArrayList, comparator_customers);
//        Collections.sort(customersArrayList_copy, comparator_customers);
//        return customersArrayList;
//
//    }
//
//    //예약된 이벤트를 위한 ListAdapter
//    class RowHolder_events {
//        TextView profileTvHolder;
//        TextView timeTvHolder;
//    }
//
//    class MyListAdapter_events extends ArrayAdapter {
//        LayoutInflater lnf;
//
//        public MyListAdapter_events(Activity context) {
//            super(context, R.layout.custom_listview_reserv_row, listviewEvents);
//            lnf = (LayoutInflater) context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//
//        @Override
//        public int getCount() {
//            // TODO Auto-generated method stub
//            return listviewEvents.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            // TODO Auto-generated method stub
//            return listviewEvents.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            // TODO Auto-generated method stub
//            return position;
//        }
//
//        public View getView(int position, View convertView, ViewGroup parent) {
//            RowHolder_events viewHolder;
//            if (convertView == null) {
//                convertView = lnf.inflate(R.layout.custom_listview_reserv_row, parent, false);
//                viewHolder = new RowHolder_events();
//
//                viewHolder.profileTvHolder = convertView.findViewById(R.id.profile_tv);
//                viewHolder.timeTvHolder = convertView.findViewById(R.id.time_tv);
////                viewHolder.nameTvHolder = convertView.findViewById(R.id.name_tv);
////                viewHolder.gradeTvHolder = convertView.findViewById(R.id.grade_tv);
////                viewHolder.retouchTvHolder = convertView.findViewById(R.id.retouch_tv);
////                viewHolder.priceTvHolder = convertView.findViewById(R.id.price_tv);
//
//
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (RowHolder_events) convertView.getTag();
//            }
//
//            if (listviewEvents.get(position).RETOUCH.equals("리터치")){
//                viewHolder.timeTvHolder.setText(listviewEvents.get(position).TIME + "  " +
//                        listviewEvents.get(position).EVENT + "  " +
//                        listviewEvents.get(position).RETOUCH + "  ");
//            } else {
//                if (listviewEvents.get(position).PRICE == 0){
//                    viewHolder.timeTvHolder.setText(listviewEvents.get(position).TIME + "  " +
//                            listviewEvents.get(position).EVENT + "  " +
//                            listviewEvents.get(position).RETOUCH);
//                } else {
//                    viewHolder.timeTvHolder.setText(listviewEvents.get(position).TIME + "  " +
//                            listviewEvents.get(position).EVENT + "  " +
//                            listviewEvents.get(position).RETOUCH + "  " +
//                            listviewEvents.get(position).PRICE);
//                }
//
//            }
//
//
//            viewHolder.timeTvHolder.setTextColor(Color.BLACK);
//            viewHolder.timeTvHolder.setTypeface(null, Typeface.BOLD);
//
//            if (listviewEvents.get(position).COMPLETE == true) {
//                viewHolder.timeTvHolder.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//                viewHolder.timeTvHolder.setTypeface(null, Typeface.ITALIC);
//                viewHolder.timeTvHolder.setTextColor(Color.BLACK);
//            }
////            viewHolder.nameTvHolder.setText(listviewEvents.get(position).EVENT);
////            viewHolder.retouchTvHolder.setText("리터치");
////            viewHolder.priceTvHolder.setText(listviewEvents.get(position).PRICE);
//
//
//            return convertView;
//        }
//
//    }
//
//    //저장된 회원리스트 위한 ListAdapter
//
//    class RowHolder_customers {
//        TextView nameTvHolder;
//        TextView gradeTvHolder;
//        TextView visitTvHolder;
//    }
//
//    class MyListAdapter_customers extends ArrayAdapter {
//        LayoutInflater lnf;
//
//        public MyListAdapter_customers(Activity context) {
//            super(context, R.layout.single_row_memberlsit, customersArrayList);
//            lnf = (LayoutInflater) context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//
//        @Override
//        public int getCount() {
//            // TODO Auto-generated method stub
//            return customersArrayList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            // TODO Auto-generated method stub
//            return customersArrayList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            // TODO Auto-generated method stub
//            return position;
//        }
//
//        public View getView(int position, View convertView, ViewGroup parent) {
//            RowHolder_customers viewHolder;
//            if (convertView == null) {
//                convertView = lnf.inflate(R.layout.single_row_memberlsit, parent, false);
//                viewHolder = new RowHolder_customers();
//
//                viewHolder.nameTvHolder = convertView.findViewById(R.id.name_tv);
////                viewHolder.gradeTvHolder = convertView.findViewById(R.id.grade_tv);
////                viewHolder.visitTvHolder = convertView.findViewById(R.id.visit_tv);
//
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (RowHolder_customers) convertView.getTag();
//            }
//
//            viewHolder.nameTvHolder.setText(customersArrayList.get(position).CUSTOMER_NAME);
//            viewHolder.gradeTvHolder.setText(customersArrayList.get(position).GRADE);
//            viewHolder.visitTvHolder.setText(customersArrayList.get(position).VISIT);
//
//            return convertView;
//        }
//    }
//
//    //팝업 액티비티 끌때 어색하지 않게 애니메이션 없애주기
//    @Override
//    protected void onPause() {
//        super.onPause();
//        overridePendingTransition(0, 0);
//    }
//
//    public ArrayList<Events> CollectEventByDate(String date) {
//        ArrayList<Events> arrayList = new ArrayList<>();
//        dbOpenHelper = new DBOpenHelper(this);
//        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
//        Cursor cursor = dbOpenHelper.ReadEvents(date, database);
//        while (cursor.moveToNext()) {
//            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
//            String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
//            String Date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
//            String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
//            String Year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
//            String retouch = cursor.getString(cursor.getColumnIndex(DBStructure.RETOUCH));
//            int price = cursor.getInt(cursor.getColumnIndex(DBStructure.PRICE));
//            String complete = cursor.getString(cursor.getColumnIndex(DBStructure.COMPLETE));
////            Events events = new Events(event, time, Date, month, Year, retouch, price, Boolean.parseBoolean(complete));
//            arrayList.add(events);
//
//        }
//        cursor.close();
//        dbOpenHelper.close();
//
//        return arrayList;
//    }
//
//    private void SaveEvent(String event, String time, String date, String month, String year, String retouch, int price, boolean complete) {
//        dbOpenHelper = new DBOpenHelper(PopupActivity.this);
//        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
//        dbOpenHelper.SaveEvent(event, time, date, month, year, retouch, price, complete, database);
//        dbOpenHelper.close();
//        Toast.makeText(PopupActivity.this, "예약이 저장되었습니다.", LENGTH_SHORT).show();
//
//    }
//
//    //예약리스트에서 메뉴판 띄우기 위한 메서드
//    public void ShowAlertDialogWithListview_menu() {
//        final ArrayList<MenuList> tempArrayList = loadMenusData();
//        Log.d("menuPopup", "여기까지는 뜨나요? 1");
//        List<String> mMenus = new ArrayList<>();
//
//        for (int i = 0; i < tempArrayList.size(); i++) {
//            mMenus.add(tempArrayList.get(i).ITEM);
//        }
//
//        //Create sequence of items
//        final CharSequence[] Animals = mMenus.toArray(new String[mMenus.size()]);
//        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(PopupActivity.this);
//        dialogBuilder.setTitle("종류를 선택해주세요.");
//        dialogBuilder.setItems(Animals,new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int item) {
//                String selectedText = Animals[item].toString();  //Selected item in listview
//
//                tempPrice = tempArrayList.get(item).PRICE;
//
//
//                Log.d("menuPopup", "여기까지는 뜨나요? 2");
//                Log.d("menuPopup", "리터치 " + listviewEvents.get(ListTempPosition).RETOUCH);
//                Log.d("menuPopup", "tempPrcie " + tempPrice);
//                Log.d("menuPopup", "beforeComplete " + listviewEvents.get(ListTempPosition).COMPLETE);
//                UpdateEventForPrice(listviewEvents.get(ListTempPosition).EVENT, listviewEvents.get(ListTempPosition).DATE, listviewEvents.get(ListTempPosition).TIME, tempPrice);
//                UpdateEventForComplete(listviewEvents.get(ListTempPosition).EVENT, listviewEvents.get(ListTempPosition).DATE, listviewEvents.get(ListTempPosition).TIME, "true");
//                Log.d("menuPopup", "가격 " + listviewEvents.get(ListTempPosition).PRICE);
//                Log.d("menuPopup", "afterComplete " + listviewEvents.get(ListTempPosition).COMPLETE);
//
//
//
////                if (!listviewEvents.get(ListTempPosition).RETOUCH.contains("리터치")){ //리터치가 아니면
////                    UpdateEventForPrice(listviewEvents.get(ListTempPosition).EVENT, listviewEvents.get(ListTempPosition).DATE, listviewEvents.get(ListTempPosition).TIME, tempPrice);
////                    UpdateEventForComplete(listviewEvents.get(ListTempPosition).EVENT, listviewEvents.get(ListTempPosition).DATE, listviewEvents.get(ListTempPosition).TIME, true);
////                    Toast.makeText(PopupActivity.this, listviewEvents.get(ListTempPosition).PRICE + "", Toast.LENGTH_SHORT);
////
////                } else{//리터치면
////
////                    UpdateEventForPrice(listviewEvents.get(ListTempPosition).EVENT, listviewEvents.get(ListTempPosition).DATE, listviewEvents.get(ListTempPosition).TIME, 0);
////                    UpdateEventForComplete(listviewEvents.get(ListTempPosition).EVENT, listviewEvents.get(ListTempPosition).DATE, listviewEvents.get(ListTempPosition).TIME, true);
////                    Toast.makeText(PopupActivity.this, listviewEvents.get(ListTempPosition).PRICE + "", Toast.LENGTH_SHORT);
////                }
//
//                Log.d("menuPopup", "여기까지는 뜨나요? 3");
//
//
//                listviewEvents.clear();
//                listviewEvents = CollectEventByDate(date);
//                Collections.sort(listviewEvents, comparator_events);
//
//                myListAdapter_events.notifyDataSetChanged();
//
//            }
//        });
//        //Create alert dialog object via builder
//        androidx.appcompat.app.AlertDialog alertDialogObject = dialogBuilder.create();
//        //Show the dialog
//        alertDialogObject.show();
//
//        loadMenusData();
//        myListAdapter_events.notifyDataSetChanged();
//
//    }
//
//    String ownername = "12341234";
//
//    //메뉴정보 불러오는 메서드
//    public ArrayList<MenuList> loadMenusData() {
//        menuArraylist.clear();
//
//        ArrayList<MenuList> arrayList = new ArrayList<>();
//        dbOpenHelper_menu = new DBOpenHelper_Menu(PopupActivity.this);
//        SQLiteDatabase database = dbOpenHelper_menu.getReadableDatabase();
//        Cursor cursor = dbOpenHelper_menu.ReadMenus(ownername, database);
//        while (cursor.moveToNext()) {
//            String ownername2 = cursor.getString(cursor.getColumnIndex(DBStructure_Menu.OWNER_NAME));
//            String menuname = cursor.getString(cursor.getColumnIndex(DBStructure_Menu.MENUNAME));
//            int menuprice = cursor.getInt(cursor.getColumnIndex(DBStructure_Menu.MENUPRICE));
//
//            MenuList menuList = new MenuList(ownername2, menuname, menuprice);
//            arrayList.add(menuList);
//
//        }
//        cursor.close();
//
//        menuArraylist.addAll(arrayList);
//
//
//        return menuArraylist;
//
//    }
//
//    private void UpdateEventForPrice(String event, String date, String time, int price) {
//        dbOpenHelper = new DBOpenHelper(PopupActivity.this);
//        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
//        dbOpenHelper.updateEventForPrice(event, date, time, price, database);
//        dbOpenHelper.close();
//    }
//
//    private void UpdateEventForComplete(String event, String date, String time, String complete) {
//        dbOpenHelper = new DBOpenHelper(PopupActivity.this);
//        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
//        dbOpenHelper.updateEventForComplete(event, date, time, complete, database);
//        dbOpenHelper.close();
//    }
//
//    //예약삭제 메소드
//    public void deleteEvents() {
//        dbOpenHelper = new DBOpenHelper(PopupActivity.this);
//        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
//        dbOpenHelper.deleteEvent(listviewEvents.get(ListTempPosition).EVENT, listviewEvents.get(ListTempPosition).DATE, listviewEvents.get(ListTempPosition).TIME, database);
//        dbOpenHelper.close();
//
//        listviewEvents.clear();
//    }
//
//    private void reviseEvent(String event, String date, String beforeTime, String updateTime, String retouch) {
//        dbOpenHelper = new DBOpenHelper(PopupActivity.this);
//        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
//        dbOpenHelper.reviseEvent(event, date, beforeTime, updateTime, retouch, database);
//        dbOpenHelper.close();
//    }
//
//}