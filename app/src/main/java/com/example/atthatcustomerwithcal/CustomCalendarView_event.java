package com.example.atthatcustomerwithcal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CustomCalendarView_event extends LinearLayout {
//    ImageButton nextButton, previousButton;

//    RelativeLayout dateRl;
//    Button addBtn;
//    TextView CurrentDate;
    GridView gridView;

    private static final int MAX_CALENDAR_DAYS = 42;
    Calendar calendar = Calendar.getInstance(Locale.KOREA);
    Calendar calendarPrev = Calendar.getInstance(Locale.KOREA);
    Calendar monthCalendar;

    Context context;
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy.MM", Locale.KOREA);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.KOREA);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.KOREA);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
    SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);


    MyGridAdapter myGridAdapter;
    MyListAdapter_events myListAdapterEvents;
    MyListAdapter_customers myListAdapter_customers;

    //이벤트 생성을 위한 요소들
    AlertDialog alertDialog_events;
    AlertDialog alertDialog_addEvents;
    List<Date> dates = new ArrayList<>();
    List<Date> selectedGroupBy = new ArrayList<>();
    List<Events> eventsList = new ArrayList<>(); //gridview
    List<Events> eventsList_prev = new ArrayList<>();
    List<Events> listviewEvents = new ArrayList<>(); //listview

    ArrayList<MenuList> menuArraylist = new ArrayList<>();//메뉴위한
    ArrayList<Customers> customersArrayList = new ArrayList<>(); //고객을 위한 어레이 리스트뷰
    ArrayList<Customers> customersArrayList_copy = new ArrayList<>(); //검색을 위해서


    int itemPosition; //고객선택 대화상자에서 선택받은 포지션 저장하기 위해서
    int tempPrice; //메뉴판에서 고른 가격 값을 임시로 저장하기 위한 변수
    String tempDate;
    boolean isChecked; //이벤트 생성시 리터치 체크됐는지 안됐는지
    String isCheckedStr; //이벤트 생성시 리터치 여부 텍스트로 반환값


    DBOpenHelper dbOpenHelper;
    DBOpenHelper_Menu dbOpenHelper_menu;
    DBOpenHelper_Customer dbOpenHelper_customer;

    //onItemClickListener
    public boolean isSelectedAtFirst;
    public boolean isSelected[] = new boolean[42]; // 각 셀의 클릭이력 여부
    public int GridprevPosition;
    public int GridtempPosition;

    public int ListTempPosition;

    public CustomCalendarView_event(Context context) {
        super(context);
    }

    public CustomCalendarView_event(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        calendarPrev.add(calendarPrev.MONTH, -1); //전달의 객체를 구해야하기 위해 따로 생성해줌

        InitializeLayout();
        SetUpCalendar();


//        previousButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) { // 전달로 이동
//                calendar.add(Calendar.MONTH, -1);
//                calendarPrev.add(Calendar.MONTH, -1); // 전 달의 매출 계산하기 위해서
//                SetUpCalendar();
//            }
//        });

//        nextButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) { // 다음달로 이동
//                calendar.add(Calendar.MONTH, 1);
//                calendarPrev.add(Calendar.MONTH, 1);
//                SetUpCalendar();
//
//            }
//        });


//        addBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (!isSelectedAtFirst) {
//                    //맨처음 화면을 켰을때 addBtn을 누르면 아무일도 일어나지 않음
//                } else {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setCancelable(true);
//                    final View addView = LayoutInflater.from(getContext()).inflate(R.layout.add_newevent_layout_test, null);
//
//                    final TextView title = addView.findViewById(R.id.title);
//                    final TextView EventName = addView.findViewById(R.id.eventname);
//                    EventName.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //고객 검색 및 선택 다이얼로그 띄우기
//                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                            builder.setCancelable(true);
//
//                            customersArrayList.clear();
//                            customersArrayList = loadCustomersData();
//                            Collections.sort(customersArrayList, comparator_customers);
//
//                            final View addView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_customers, null);
//
//                            final TextView title = addView.findViewById(R.id.title);
//                            final EditText searchEt = addView.findViewById(R.id.searchEt);
//                            searchEt.addTextChangedListener(new TextWatcher() {
//                                @Override
//                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                                }
//
//                                @Override
//                                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                                }
//
//                                @Override
//                                public void afterTextChanged(Editable s) {
//                                    String searchText = searchEt.getText().toString().toLowerCase();
//                                    search(searchText);
//                                }
//                            });
////                            final Button searchBtn = addView.findViewById(R.id.searchBtn);
//
//                            final ListView listView2 = addView.findViewById(R.id.listview_picking);
//                            myListAdapter_customers = new MyListAdapter_customers((Activity) context);
//                            listView2.setAdapter(myListAdapter_customers);
//
//                            listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                    EventName.setText(customersArrayList.get(position).CUSTOMER_NAME + "_" + customersArrayList.get(position).GRADE);
//                                    alertDialog_events.dismiss();
//                                }
//                            });
//
//                            builder.setView(addView);
//                            alertDialog_events = builder.create();
//                            alertDialog_events.show();
//                        }
//                    });
//                    final CheckBox retouch_chBox = addView.findViewById(R.id.retouch_chBox);
//
//                    final TextView eventTime = addView.findViewById(R.id.eventtime);
//                    eventTime.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Calendar calendar = Calendar.getInstance();
//                            int hours = calendar.get(Calendar.HOUR_OF_DAY);
//                            int minuts = calendar.get(Calendar.MINUTE);
//
//                            CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(addView.getContext()
//                                    , new CustomTimePickerDialog.OnTimeSetListener() {
//                                @Override
//                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                                    Calendar c = Calendar.getInstance();
//                                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                                    c.set(Calendar.MINUTE, minute);
//                                    c.setTimeZone(TimeZone.getDefault());
//                                    SimpleDateFormat hformat = new SimpleDateFormat("a K:mm", Locale.KOREA);
//                                    String event_Time = hformat.format(c.getTime());
//                                    eventTime.setText(event_Time);
//                                    eventTime.setTextColor(Color.BLACK);
//                                }
//                            }, hours, minuts, false);
//
//                            timePickerDialog.show();
//                        }
//                    });
//                    final String date = eventDateFormat.format(dates.get(GridtempPosition));
//                    final String month = monthFormat.format(dates.get(GridtempPosition));
//                    final String year = yearFormat.format(dates.get(GridtempPosition));
//
//
//                    final Button addBtn = addView.findViewById(R.id.addevent);
//                    addBtn.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            isChecked = retouch_chBox.isChecked();
//                            if (isChecked) {
//                                isCheckedStr = "리터치";
//                            } else {
//                                isCheckedStr = "";
//                            }
//                            SaveEvent(EventName.getText().toString(), eventTime.getText().toString(), date, month, year, isCheckedStr, 0, false, "");
//                            SetUpCalendar();
//
//                            alertDialog_addEvents.dismiss();
//
//                        }
//                    });
//
//                    builder.setView(addView);
//                    alertDialog_addEvents = builder.create();
//                    alertDialog_addEvents.show();
//
//                }
//
//
//            }
//        });

        //그리드뷰 아이템 클릭 리스너
////        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Log.d("position", "position = " + position);
////                Log.d("position", "tempPosition = " + GridtempPosition);
////                Log.d("position", "isSelected = " + isSelected);
////                Log.d("position", "prevPosition = " + GridprevPosition);
////
////
//////                myGridAdapter.makeAllUnselect(position);
//////                myGridAdapter.notifyDataSetChanged();
////
////                ListTempPosition = position;
////
////                if (!isSelectedAtFirst) {//앱이 실행됐을때 최초 셀 클린인지?
////                    GridprevPosition = position;
////                    GridtempPosition = position;
////
////
////
////                    isSelectedAtFirst = true;
////                    isSelected[position] = true;
////                } else {//최초클릭이 아니라면 이하 라인부터
////                    GridtempPosition = position;
////
////
////                    if (GridprevPosition == position && isSelected[position] == true) { //최초 클릭된애가 또 눌리는 경우
////                        //AlertDialog 띄우기
////                        final String date = eventDateFormat.format(dates.get(position)); //날짜 받아오기
////                        tempDate = date;
////                        Log.d("date222", date);
////
////                        //일단 클리어 해주기
////                        listviewEvents.clear();
////                        listviewEvents = CollectEventByDate(date);
////                        Collections.sort(listviewEvents, comparator_events);
////
////
////                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
////                        builder.setCancelable(true);
////                        final View addView = LayoutInflater.from(getContext()).inflate(R.layout.test_recyclerview, null);
////                        final TextView monthTv = addView.findViewById(R.id.monthTv);
////                        final TextView dayTv = addView.findViewById(R.id.dayTv);
////
////
////
////                        monthTv.setText(monthFormat.format(dates.get(position)));
////                        dayTv.setText(" " + date.charAt(8) + "" + date.charAt(9) + "일");
////
////                        final ListView listView = addView.findViewById(R.id.listview);
////                        myListAdapterEvents = new MyListAdapter_events((Activity) context);
////                        listView.setAdapter(myListAdapterEvents);
////
////                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////                            @Override
////                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                                ListTempPosition = position;
////
////                                ShowAlertDialogWithListview_menu();
////
////                                Log.d("price", "이게먼저 뜨나? " + listviewEvents.get(position).PRICE);
////
//////                                Event(listviewEvents.get(position).EVENT, listviewEvents.get(position).DATE, listviewEvents.get(position).TIME, tempPrice);
//////                              Toast.makeText(context,"이거 되나?", Toast.LENGTH_SHORT).show();
////
////                                listView.setAdapter(myListAdapterEvents);
////
////
////                            }
////                        });
////
////                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
////                            @Override
////                            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
////                                ListTempPosition = position;
////                                androidx.appcompat.app.AlertDialog.Builder ab = new androidx.appcompat.app.AlertDialog.Builder(context);
////                                ab.setTitle("예약 수정/삭제");
////                                ab.setIcon(R.mipmap.ic_launcher);
////                                ab.setMessage(listviewEvents.get(ListTempPosition).EVENT + " " + listviewEvents.get(ListTempPosition).TIME + "\n예약을 수정 또는 삭제하시겠습니까? ");
////                                ab.setPositiveButton("예", new DialogInterface.OnClickListener() {
////                                    @Override
////                                    public void onClick(DialogInterface dialog, int which) {
////                                        deleteEvents();
////
////                                        listviewEvents.clear();
////                                        listviewEvents = CollectEventByDate(date);
////                                        Collections.sort(listviewEvents, comparator_events);
////                                        listView.setAdapter(myListAdapterEvents);
////                                        SetUpCalendar();
////                                        Toast.makeText(context, "예약이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
////                                    }
////                                });
////                                ab.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
////                                    @Override
////                                    public void onClick(DialogInterface dialog, int which) {
////
////                                    }
////                                });
////
////                                ab.setNeutralButton("수정", new DialogInterface.OnClickListener() {
////                                    @Override
////                                    public void onClick(DialogInterface dialog, int which) {
////                                        if (listviewEvents.get(ListTempPosition).PRICE != 0){
////                                            Toast.makeText(context, "작업이 끝난 예약은 수정할 수 없습니다", Toast.LENGTH_SHORT).show();
////                                        } else {
////                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
////                                            builder.setCancelable(true);
////                                            final View addView = LayoutInflater.from(getContext()).inflate(R.layout.add_newevent_layout_test, null);
////
////                                            final TextView title = addView.findViewById(R.id.title);
////                                            title.setText("예약 수정");
////                                            final TextView EventName = addView.findViewById(R.id.eventname);
////                                            EventName.setText(listviewEvents.get(ListTempPosition).EVENT); //예약 수정시에는 시간, 리터치 여부만 바꾸게 해줄거임
////
////                                            final CheckBox retouch_chBox = addView.findViewById(R.id.retouch_chBox);
////                                            if(!listviewEvents.get(ListTempPosition).RETOUCH.isEmpty()){
////                                                retouch_chBox.setChecked(true);
////                                            } else{
////                                                retouch_chBox.setChecked(false);
////                                            }
////
////                                            final TextView eventTime = addView.findViewById(R.id.eventtime);
////                                            eventTime.setText(listviewEvents.get(ListTempPosition).TIME);
////                                            Log.d("updateEventTest","before" + listviewEvents.get(ListTempPosition).TIME);
////                                            eventTime.setOnClickListener(new OnClickListener() {
////                                                @Override
////                                                public void onClick(View v) {
////                                                    Calendar calendar = Calendar.getInstance();
////                                                    int hours = calendar.get(Calendar.HOUR_OF_DAY);
////                                                    int minuts = calendar.get(Calendar.MINUTE);
////
////                                                    CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(addView.getContext()
////                                                            , new CustomTimePickerDialog.OnTimeSetListener() {
////                                                        @Override
////                                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
////                                                            Calendar c = Calendar.getInstance();
////                                                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
////                                                            c.set(Calendar.MINUTE, minute);
////                                                            c.setTimeZone(TimeZone.getDefault());
////                                                            SimpleDateFormat hformat = new SimpleDateFormat("a K:mm", Locale.KOREA);
////                                                            String event_Time = hformat.format(c.getTime());
////                                                            eventTime.setText(event_Time);
////                                                            eventTime.setTextColor(Color.BLACK);
////                                                        }
////                                                    }, hours, minuts, false);
////
////                                                    timePickerDialog.show();
////                                                }
////                                            });
////                                            final String date = eventDateFormat.format(dates.get(GridtempPosition));
////                                            final String month = monthFormat.format(dates.get(GridtempPosition));
////                                            final String year = yearFormat.format(dates.get(GridtempPosition));
////
////                                            final Button updateBtn = addView.findViewById(R.id.addevent);
////                                            updateBtn.setOnClickListener(new OnClickListener() {
////                                                @Override
////                                                public void onClick(View v) {
////
////                                                    isChecked = retouch_chBox.isChecked();
////                                                    if (isChecked) {
////                                                        isCheckedStr = "리터치";
////                                                    } else {
////                                                        isCheckedStr = "";
////                                                    }
////                                                    reviseEvent(listviewEvents.get(ListTempPosition).EVENT,
////                                                            listviewEvents.get(ListTempPosition).DATE,
////                                                            listviewEvents.get(position).TIME,
////                                                            eventTime.getText().toString(),
////                                                            isCheckedStr);
////                                                    Log.d("updateEventTest","after" + listviewEvents.get(ListTempPosition).TIME);
////                                                    Log.d("updateEventTest","after 2" + eventTime.getText().toString());
////
////                                                    listviewEvents.clear();
////                                                    listviewEvents = CollectEventByDate(date);
////                                                    Collections.sort(listviewEvents, comparator_events);
////                                                    listView.setAdapter(myListAdapterEvents);
////                                                    SetUpCalendar();
////
////                                                    alertDialog_addEvents.dismiss();
////                                                }
////                                            });
////
////                                            SetUpCalendar();
////                                            builder.setView(addView);
////                                            alertDialog_addEvents = builder.create();
////                                            alertDialog_addEvents.show();
////                                        }
////
////
////                                    }
////                                });
////                                ab.setCancelable(true);
////                                ab.show();
////                                return true;
////
////                            }
////                        });
////
////
////
////                        final Button addBtn = addView.findViewById(R.id.addBtn);
////                        addBtn.setOnClickListener(new OnClickListener() {
////                            @Override
////                            public void onClick(View v) {
////                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
////                                builder.setCancelable(true);
////                                final View addView = LayoutInflater.from(getContext()).inflate(R.layout.add_newevent_layout_test, null);
////
////                                final TextView title = addView.findViewById(R.id.title);
////                                final TextView EventName = addView.findViewById(R.id.eventname);
////                                EventName.setOnClickListener(new OnClickListener() {
////                                    @Override
////                                    public void onClick(View v) {
////
////                                        //고객 검색 및 선택 다이얼로그 띄우기
////                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
////                                        builder.setCancelable(true);
////
////                                        customersArrayList.clear();
////                                        customersArrayList = loadCustomersData();
////                                        Collections.sort(customersArrayList,comparator_customers);
////
////                                        final View addView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_customers, null);
////
////                                        final TextView title = addView.findViewById(R.id.title);
////                                        final EditText searchEt = addView.findViewById(R.id.searchEt);
////                                        searchEt.addTextChangedListener(new TextWatcher() {
////                                            @Override
////                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
////
////                                            }
////
////                                            @Override
////                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
////
////                                            }
////
////                                            @Override
////                                            public void afterTextChanged(Editable s) {
////                                                String searchText = searchEt.getText().toString().toLowerCase();
////                                                search(searchText);
////                                            }
////                                        });
////                                        final Button searchBtn = addView.findViewById(R.id.searchBtn);
////
////                                        final ListView listView2 = addView.findViewById(R.id.listview_picking);
////                                        myListAdapter_customers = new MyListAdapter_customers((Activity) context);
////                                        listView2.setAdapter(myListAdapter_customers);
////
////                                        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////                                            @Override
////                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                                                EventName.setText(customersArrayList.get(position).CUSTOMER_NAME+"_"+customersArrayList.get(position).GRADE);
////                                                alertDialog_events.dismiss();
////                                            }
////                                        });
////
////                                        builder.setView(addView);
////                                        alertDialog_events = builder.create();
////                                        alertDialog_events.show();
////                                    }
////                                });
////                                final CheckBox retouch_chBox = addView.findViewById(R.id.retouch_chBox);
////
////                                final TextView eventTime = addView.findViewById(R.id.eventtime);
////                                eventTime.setOnClickListener(new OnClickListener() {
////                                    @Override
////                                    public void onClick(View v) {
////                                        Calendar calendar = Calendar.getInstance();
////                                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
////                                        int minuts = calendar.get(Calendar.MINUTE);
////
////                                        CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(addView.getContext()
////                                                , new CustomTimePickerDialog.OnTimeSetListener() {
////                                            @Override
////                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
////                                                Calendar c = Calendar.getInstance();
////                                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
////                                                c.set(Calendar.MINUTE, minute);
////                                                c.setTimeZone(TimeZone.getDefault());
////                                                SimpleDateFormat hformat = new SimpleDateFormat("a K:mm", Locale.KOREA);
////                                                String event_Time = hformat.format(c.getTime());
////                                                eventTime.setText(event_Time);
////                                                eventTime.setTextColor(Color.BLACK);
////                                            }
////                                        }, hours, minuts, false);
////
////                                        timePickerDialog.show();
////                                    }
////                                });
////                                final String date = eventDateFormat.format(dates.get(GridtempPosition));
////                                final String month = monthFormat.format(dates.get(GridtempPosition));
////                                final String year = yearFormat.format(dates.get(GridtempPosition));
////
////
////                                final Button addBtn = addView.findViewById(R.id.addevent);
////                                addBtn.setOnClickListener(new OnClickListener() {
////                                    @Override
////                                    public void onClick(View v) {
////                                        isChecked = retouch_chBox.isChecked();
////                                        if (isChecked) {
////                                            isCheckedStr = "리터치";
////                                        } else {
////                                            isCheckedStr = "";
////                                        }
////                                        SaveEvent(EventName.getText().toString(), eventTime.getText().toString(), date, month, year, isCheckedStr, 0);
////                                        SetUpCalendar();
////
////
////
////                                        listviewEvents.clear();
////                                        listviewEvents = CollectEventByDate(date);
////                                        Collections.sort(listviewEvents, comparator_events);
////                                        listView.setAdapter(myListAdapterEvents);
////                                        SetUpCalendar();
////
////                                        alertDialog_addEvents.dismiss();
////
////                                    }
////                                });
////
////                                SetUpCalendar();
////                                builder.setView(addView);
////                                alertDialog_addEvents = builder.create();
////                                alertDialog_addEvents.show();
////
////
////                            }
////                        });
////
////
////                        builder.setView(addView);
////                        alertDialog_events = builder.create();
////                        alertDialog_events.show();
////
////                        SetUpCalendar();
////
////
////                    } else if (GridprevPosition != position) {
////                        isSelected[GridprevPosition] = false;
////                        isSelected[position] = true;
////                        GridprevPosition = position;
////                    }
////
////
////                }
////            }
////
////
//////                tempPosition = position;
//////                //달력 밑에 선택된 날짜의 예약을 리스트 뷰로 뿌려주는 메소드를 많들기
//////
//////                String date = eventDateFormat.format((dates.get(position)));
//////                listviewEvents = CollectEventByDate(date);
////////                for (int i = 0; i < listviewEvents.size(); i++) {
////////                    String tempTime = listviewEvents.get(i).TIME;
////////                    String tempEvent = listviewEvents.get(i).EVENT;
////////                    String tempRetouch = listviewEvents.get(i).RETOUCH;
////////                    Log.d("ReservList", date +"/" + tempTime + "/" + tempEvent + "/" + tempRetouch);
////////                }
////
////
//////                myListAdapter.notifyDataSetChanged();
//////
////
////        });
//
//
//        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                String date = eventDateFormat.format(dates.get(position));
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setCancelable(true);
//                View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout, null);
//                RecyclerView recyclerView = showView.findViewById(R.id.EventsRV);
//                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());
//                recyclerView.setLayoutManager(layoutManager);
//                recyclerView.setHasFixedSize(true);
//                EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(showView.getContext(),
//                        CollectEventByDate(date));
//                recyclerView.setAdapter(eventRecyclerAdapter);
//                eventRecyclerAdapter.notifyDataSetChanged();
//
//                builder.setView(showView);
//                alertDialog_events = builder.create();
//                alertDialog_events.show();
//                ;
//                alertDialog_events.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//                        SetUpCalendar();
//                    }
//                });
//                return true;
//            }
//        });
    }

    public ArrayList<Events> CollectEventByDate(String date) {
        ArrayList<Events> arrayList = new ArrayList<>();
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEvents(date, database);
        while (cursor.moveToNext()) {
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            String Date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            String retouch = cursor.getString(cursor.getColumnIndex(DBStructure.RETOUCH));
            int price = cursor.getInt(cursor.getColumnIndex(DBStructure.PRICE));
            String complete = cursor.getString(cursor.getColumnIndex(DBStructure.COMPLETE));
            String shortmemo = cursor.getString(cursor.getColumnIndex(DBStructure.SHORTMEMO));
            String number = cursor.getString(cursor.getColumnIndex(DBStructure.NUMBER));
            String noshow = cursor.getString(cursor.getColumnIndex(DBStructure.NOSHOW));
            String menu = cursor.getString(cursor.getColumnIndex(DBStructure.MENU));
            String cardcash = cursor.getString(cursor.getColumnIndex(DBStructure.CARDCASH));
            String materialmemo = cursor.getString(cursor.getColumnIndex(DBStructure.MATERIALMEMO));
            String contentmemo = cursor.getString(cursor.getColumnIndex(DBStructure.CONTENTMEMO));
            String crtdate = cursor.getString(cursor.getColumnIndex(DBStructure.CRTDATE));
            Events events = new Events(event, time, Date, month, Year, retouch, price
                    , Boolean.parseBoolean(complete), shortmemo, number, Boolean.parseBoolean(noshow), menu,
                    cardcash, materialmemo, contentmemo, crtdate);

        }
        cursor.close();
        dbOpenHelper.close();

        return arrayList;
    }

    public CustomCalendarView_event(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void SaveEvent(String event, String time, String date, String month, String year, String retouch, int price, boolean complete
            , String shortmemo, String number, boolean noshow, String menu, String cardcash, String materialmemo, String contentmemo, String crtdate) {
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.SaveEvent(event, time, date, month, year, retouch, price, complete, shortmemo, number, noshow, menu, cardcash, materialmemo,
                contentmemo, crtdate, database);
        dbOpenHelper.close();
        Toast.makeText(context, "예약이 저장되었습니다.", Toast.LENGTH_SHORT).show();

    }

    private void UpdateEventForPrice(String event, String date, String time, int price) {
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.updateEventForPrice(event, date, time, price, database);
        dbOpenHelper.close();
    }

    private void reviseEvent(String event, String date, String beforeTime, String updateTime, String retouch) {
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.reviseEvent(event, date, beforeTime, updateTime, retouch, database);
        dbOpenHelper.close();
    }

    private void InitializeLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
//        nextButton = view.findViewById(R.id.nextBtn);
//        previousButton = view.findViewById(R.id.previousBtn);
//        dateRl = view.findViewById(R.id.dateRl);
//        CurrentDate = view.findViewById(R.id.current_Date);
        gridView = view.findViewById(R.id.gridview);
        gridView.setDrawSelectorOnTop(true);


        //추가내용
//        addBtn = view.findViewById(R.id.addBtn);
    }

    public void SetUpCalendar() {
        String currentDate = dateFormat.format(calendar.getTime());
        String currentDate2 = dateFormat2.format(calendar.getTime());
        Log.d("CalendarRenovation", "currentDate : " + currentDate);
        Log.d("CalendarRenovation", "currentDate2 : " + currentDate2);

//        CurrentDate.setText(currentDate2);
        dates.clear();
        monthCalendar = (Calendar) calendar.clone();

        Log.d("CalrendarResnovation", "monthCalendar : "+monthCalendar.getTime().toString());

        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int FirstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayOfMonth);

        CollectEventsPerMonth(monthFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));
        Log.d("collect", calendarPrev.getTime() + "");
        //이전달 프라이스 모으기 위해
        CollectPricePerPrevMonth(monthFormat.format(calendarPrev.getTime()), yearFormat.format(calendar.getTime()));

        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);

        }
        myGridAdapter = new MyGridAdapter(context, dates, calendar, eventsList);
        myGridAdapter.isAppOpen = true;
        gridView.setAdapter(myGridAdapter);


        if (!isSelectedAtFirst) {
            myGridAdapter.tempPosition = GridtempPosition;
        } else {
            Log.d("posi1", "atFirst tempPosition of adapter " + myGridAdapter.tempPosition);

            myGridAdapter.tempPosition = GridtempPosition;
            Log.d("posi1", "gridtemp" + myGridAdapter.tempPosition);
        }


        myListAdapterEvents = new MyListAdapter_events((Activity) context);

    }

    public void setUpCalendarAtFirst() {
        String currentDate = dateFormat.format(calendar.getTime());
//        CurrentDate.setText(currentDate);
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int FirstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayOfMonth);

        CollectEventsPerMonth(monthFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));
        Log.d("collect", calendarPrev.getTime() + "");
        //이전달 프라이스 모으기 위해
        CollectPricePerPrevMonth(monthFormat.format(calendarPrev.getTime()), yearFormat.format(calendar.getTime()));

        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);

        }
        myGridAdapter = new MyGridAdapter(context, dates, calendar, eventsList);
        gridView.setAdapter(myGridAdapter);


        if (!isSelectedAtFirst) {
            myGridAdapter.tempPosition = GridtempPosition;
        } else {
            Log.d("posi1", "atFirst tempPosition of adapter " + myGridAdapter.tempPosition);

            myGridAdapter.tempPosition = GridtempPosition;
            Log.d("posi1", "gridtemp" + myGridAdapter.tempPosition);
        }


        myListAdapterEvents = new MyListAdapter_events((Activity) context);

    }

    private void CollectEventsPerMonth(String Month, String year) {
        eventsList.clear();
        int sumPricePerMonth = 0;
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEventsperMonth(Month, year, database);
        while (cursor.moveToNext()) {
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            String date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            String retouch = cursor.getString(cursor.getColumnIndex(DBStructure.RETOUCH));
            int price = cursor.getInt(cursor.getColumnIndex(DBStructure.PRICE));
            String complete = cursor.getString(cursor.getColumnIndex(DBStructure.COMPLETE));
            String shortmemo = cursor.getString(cursor.getColumnIndex(DBStructure.SHORTMEMO));
            String number = cursor.getString(cursor.getColumnIndex(DBStructure.NUMBER));
            String noshow = cursor.getString(cursor.getColumnIndex(DBStructure.NOSHOW));
            String menu = cursor.getString(cursor.getColumnIndex(DBStructure.MENU));
            String cardcash = cursor.getString(cursor.getColumnIndex(DBStructure.CARDCASH));
            String materialmemo = cursor.getString(cursor.getColumnIndex(DBStructure.MATERIALMEMO));
            String contentmemo = cursor.getString(cursor.getColumnIndex(DBStructure.CONTENTMEMO));
            String crtdate = cursor.getString(cursor.getColumnIndex(DBStructure.CRTDATE));
            Events events = new Events(event, time, date, month, Year, retouch, price
                    , Boolean.parseBoolean(complete), shortmemo, number, Boolean.parseBoolean(noshow), menu,
                    cardcash, materialmemo, contentmemo, crtdate);
            eventsList.add(events);

            if (Boolean.parseBoolean(complete)){
                sumPricePerMonth += price;
            }

        }
        Log.d("collectPricePerMonth", "contemp : " + eventsList.size() + ":" + sumPricePerMonth);
        cursor.close();
        dbOpenHelper.close();
    }

    private void CollectPricePerPrevMonth(String Month, String year) {
        eventsList_prev.clear();
        int sumPricePerPrevMonth = 0;
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEventsperMonth(Month, year, database);
        while (cursor.moveToNext()) {
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            String date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            String retouch = cursor.getString(cursor.getColumnIndex(DBStructure.RETOUCH));
            int price = cursor.getInt(cursor.getColumnIndex(DBStructure.PRICE));
            String complete = cursor.getString(cursor.getColumnIndex(DBStructure.COMPLETE));
            String shortmemo = cursor.getString(cursor.getColumnIndex(DBStructure.SHORTMEMO));
            String number = cursor.getString(cursor.getColumnIndex(DBStructure.NUMBER));
            String noshow = cursor.getString(cursor.getColumnIndex(DBStructure.NOSHOW));
            String menu = cursor.getString(cursor.getColumnIndex(DBStructure.MENU));
            String cardcash = cursor.getString(cursor.getColumnIndex(DBStructure.CARDCASH));
            String materialmemo = cursor.getString(cursor.getColumnIndex(DBStructure.MATERIALMEMO));
            String contentmemo = cursor.getString(cursor.getColumnIndex(DBStructure.CONTENTMEMO));
            String crtdate = cursor.getString(cursor.getColumnIndex(DBStructure.CONTENTMEMO));
            Events events = new Events(event, time, date, month, Year, retouch, price
                    , Boolean.parseBoolean(complete), shortmemo, number, Boolean.parseBoolean(noshow), menu,
                    cardcash, materialmemo, contentmemo, crtdate);
            sumPricePerPrevMonth += price;
        }
        Log.d("collectPricePrevMonth", "Prev : " + eventsList_prev.size() + ":" + sumPricePerPrevMonth);
        cursor.close();
        dbOpenHelper.close();
    }

    //예약된 이벤트를 위한 ListAdapter
    class RowHolder_events {

        TextView profileTvHolder;
        TextView timeTvHolder;

    }

    class MyListAdapter_events extends ArrayAdapter {
        LayoutInflater lnf;

        public MyListAdapter_events(Activity context) {
            super(context, R.layout.custom_listview_reserv_row, listviewEvents);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listviewEvents.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return listviewEvents.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            RowHolder_events viewHolder;
            if (convertView == null) {
                convertView = lnf.inflate(R.layout.custom_listview_reserv_row, parent, false);
                viewHolder = new RowHolder_events();

                viewHolder.profileTvHolder = convertView.findViewById(R.id.profile_tv);
                viewHolder.timeTvHolder = convertView.findViewById(R.id.time_tv);
//                viewHolder.nameTvHolder = convertView.findViewById(R.id.name_tv);
//                viewHolder.gradeTvHolder = convertView.findViewById(R.id.grade_tv);
//                viewHolder.retouchTvHolder = convertView.findViewById(R.id.retouch_tv);
//                viewHolder.priceTvHolder = convertView.findViewById(R.id.price_tv);


                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RowHolder_events) convertView.getTag();
            }

            if (listviewEvents.get(position).PRICE == 0) {
                viewHolder.timeTvHolder.setText(listviewEvents.get(position).TIME + "  " +
                        listviewEvents.get(position).EVENT + "  " +
                        listviewEvents.get(position).RETOUCH + "  ");

            } else {
                viewHolder.timeTvHolder.setText(listviewEvents.get(position).TIME + "  " +
                        listviewEvents.get(position).EVENT + "  " +
                        listviewEvents.get(position).RETOUCH + "  " +
                        listviewEvents.get(position).PRICE);

            }


            viewHolder.timeTvHolder.setTextColor(Color.BLACK);
            viewHolder.timeTvHolder.setTypeface(null, Typeface.BOLD);

            if (listviewEvents.get(position).PRICE != 0) {
                viewHolder.timeTvHolder.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.timeTvHolder.setTypeface(null, Typeface.ITALIC);
                viewHolder.timeTvHolder.setTextColor(Color.BLACK);
            }
//            viewHolder.nameTvHolder.setText(listviewEvents.get(position).EVENT);
//            viewHolder.retouchTvHolder.setText("리터치");
//            viewHolder.priceTvHolder.setText(listviewEvents.get(position).PRICE);


            return convertView;
        }

    }

    class RowHolder_customers {
        TextView nameTvHolder;
        TextView gradeTvHolder;
        TextView numberTvHolder;
    }

    class MyListAdapter_customers extends ArrayAdapter {
        LayoutInflater lnf;

        public MyListAdapter_customers(Activity context) {
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

            viewHolder.nameTvHolder.setText(customersArrayList.get(position).CUSTOMER_NAME);


            return convertView;
        }
    }

    //예약리스트에서 메뉴판 띄우기 위한 메서드
    public void ShowAlertDialogWithListview_menu() {
        final ArrayList<MenuList> tempArrayList = loadMenusData();

        List<String> mMenus = new ArrayList<>();

        for (int i = 0; i < tempArrayList.size(); i++) {
            mMenus.add(tempArrayList.get(i).ITEM);
        }

        //Create sequence of items
        final CharSequence[] Animals = mMenus.toArray(new String[mMenus.size()]);
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
        dialogBuilder.setTitle("종류를 선택해주세요.");
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = Animals[item].toString();  //Selected item in listview
//                Toast.makeText(context, tempArrayList.get(item).PRICE, Toast.LENGTH_SHORT).show();

                tempPrice = tempArrayList.get(item).PRICE;

                UpdateEventForPrice(listviewEvents.get(ListTempPosition).EVENT, listviewEvents.get(ListTempPosition).DATE, listviewEvents.get(ListTempPosition).TIME, tempPrice);
                Log.d("price", "tempPrice :" + tempPrice);
                Log.d("price", "event : " + listviewEvents.get(ListTempPosition).EVENT);
                Log.d("price", "date : " + listviewEvents.get(ListTempPosition).DATE);
                Log.d("price", "time : " + listviewEvents.get(ListTempPosition).TIME);
                Toast.makeText(context, listviewEvents.get(ListTempPosition).PRICE + "", Toast.LENGTH_SHORT);

                Log.d("price", "price : " + listviewEvents.get(ListTempPosition).PRICE);
                Log.d("price", listviewEvents.get(ListTempPosition).PRICE + "");
//                Log.d("price", tempPrice);

                listviewEvents.clear();
                listviewEvents = CollectEventByDate(tempDate);
                Collections.sort(listviewEvents, comparator_events);

                myListAdapterEvents.notifyDataSetChanged();

            }
        });
        //Create alert dialog object via builder
        androidx.appcompat.app.AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();

        loadMenusData();
        myListAdapterEvents.notifyDataSetChanged();

    }

    public void ShowAlertDialogWithListview_customer() {
        final ArrayList<Customers> tempArrayList = loadCustomersData();

        List<String> mMenus = new ArrayList<>();

        for (int i = 0; i < tempArrayList.size(); i++) {
            mMenus.add(tempArrayList.get(i).CUSTOMER_NAME);
        }

        //Create sequence of items
        final CharSequence[] Animals = mMenus.toArray(new String[mMenus.size()]);
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
        dialogBuilder.setTitle("손님을 선택해주세요.");
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                itemPosition = item;
                String selectedText = Animals[item].toString();  //Selected item in listview
                Toast.makeText(context, tempArrayList.get(item).CUSTOMER_NAME, Toast.LENGTH_SHORT).show();

            }
        });
        //Create alert dialog object via builder
        androidx.appcompat.app.AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();

        loadMenusData();
        myListAdapterEvents.notifyDataSetChanged();
    }

    String ownername = "12341234";

    //메뉴정보 불러오는 메서드
    public ArrayList<MenuList> loadMenusData() {
        menuArraylist.clear();

        ArrayList<MenuList> arrayList = new ArrayList<>();
        dbOpenHelper_menu = new DBOpenHelper_Menu(context);
        SQLiteDatabase database = dbOpenHelper_menu.getReadableDatabase();
        Cursor cursor = dbOpenHelper_menu.ReadMenus(ownername, database);
        while (cursor.moveToNext()) {
            String ownername2 = cursor.getString(cursor.getColumnIndex(DBStructure_Menu.OWNER_NAME));
            String menuname = cursor.getString(cursor.getColumnIndex(DBStructure_Menu.MENUNAME));
            int menuprice = cursor.getInt(cursor.getColumnIndex(DBStructure_Menu.MENUPRICE));

            MenuList menuList = new MenuList(ownername2, menuname, menuprice);
            arrayList.add(menuList);

        }
        cursor.close();

        menuArraylist.addAll(arrayList);


        return menuArraylist;

    }

    //회원정보 불러오는 메서드
    public ArrayList<Customers> loadCustomersData() {
        customersArrayList.clear();
        customersArrayList_copy.clear();

        Log.d("tag", ownername);

        ArrayList<Customers> arrayList = new ArrayList<>();
        dbOpenHelper_customer = new DBOpenHelper_Customer(context);
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

        Collections.sort(customersArrayList, comparator_customers);
        Collections.sort(customersArrayList_copy, comparator_customers);
        return customersArrayList;

    }


    //events 정렬을 위한 메소드
    public static Comparator<Events> comparator_events = new Comparator<Events>() {

        @Override
        public int compare(Events o1, Events o2) {
            return o1.TIME.compareTo(o2.TIME);
        }

    };

    //customers 정렬을 위한 메소드
    public static Comparator<Customers> comparator_customers = new Comparator<Customers>() {

        @Override
        public int compare(Customers o1, Customers o2) {
            return o1.CUSTOMER_NAME.compareTo(o2.CUSTOMER_NAME);
        }


    };

    //예약삭제 메소드
    public void deleteEvents() {
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.deleteEvent(listviewEvents.get(ListTempPosition).EVENT, listviewEvents.get(ListTempPosition).DATE, listviewEvents.get(ListTempPosition).TIME, database);
        dbOpenHelper.close();

        listviewEvents.clear();

    }

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

}
