package com.example.atthatcustomerwithcal;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.example.atthatcustomerwithcal.R.*;


public class MainActivity extends Activity_Base implements View.OnClickListener {

    final String CalendarRenovation = "CalendarRenovation";

    final public int REQUEST_CONTACTS = 101;
    final public int REQUEST_ALBUM = 102;
    final int GET_GALLERY_IMAGE = 200;
    final int REQUEST_ADD_MEMBERnEVENT = 300;
    final int REQUEST_ADD_EVENT_NONCUS = 301;
    final int REQUEST_POPUP_ACTIVITY = 400;
    final int RESULT_LOAD_IMAGE = 500;


    //=====================================================????????????====================================================

    //Toolbar
    Toolbar toolbar;
    //drawerLayout
    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    CustomCalendarView_event customCalendarViewEvent;

    //?????? ??????
    TextView accountingsTv;
    //calendarView
    String date;
    String month;
    String day;
    String year;

    //?????? Rl
    RelativeLayout yearMonthRl;

    //Footer
    LinearLayout albumLl, memoLl, messageLl, graphLl, customerLl;

    //?????? ?????? ?????? ?????? ?????????
    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat weekdayFormat = new SimpleDateFormat("EE", Locale.getDefault());
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.KOREA);
    SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    String weekDay = weekdayFormat.format(currentTime);
    String yearOfToday = yearFormat.format(currentTime);
    String monthOfToday = monthFormat.format(currentTime);
    String dayOfToday = dayFormat.format(currentTime);

    public boolean isSelectedAtFirst; //?????? ????????? ????????????
    public boolean isSelected[] = new boolean[42]; // ??? ?????? ???????????? ??????

    int gridView_tempPosition;
    int gridView_prevPosition;


    //?????? ????????????
    TextView noeventTv;
    TextView eventsDateTv;
    List<Events> eventsArrayList = new ArrayList<>(); //listview
    ListView listView_Events;
    MyListAdapter_events myListAdapter_events;

    public int ListTempPosition;


    int tempPrice;

    String selectedTextForHour;
    String selectedTextForMin;

    MyListAdapter_customers myListAdapter_customers;

    //AlertDialog
    android.app.AlertDialog alertDialog_events;
    android.app.AlertDialog alertDialog_addEvents;
    AlertDialog alertDialog_reviseEvents;
    android.app.AlertDialog alertDialog_reviseTime;


    //BotLl
    Button btn_accountings, btn_wholeAlbum, btn_memo, btn_message;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        //Firebase ?????????
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initLayout();

        customCalendarViewEvent = findViewById(id.activity_custom_calendar_view);
        final List<Date> dates = customCalendarViewEvent.dates;

//        //================ ?????????(??????) ?????? ??? ?????? ===============
//        customCalendarViewEvent.previousButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                customCalendarView.isSelectedAtFirst = false;
//
//
//                for (int i = 0; i < isSelected.length; i++) {
//                    isSelected[i] = false;
//                }
//
//                customCalendarViewEvent.calendar.add(Calendar.MONTH, -1);
//                customCalendarViewEvent.calendarPrev.add(Calendar.MONTH, -1); // ??? ?????? ?????? ???????????? ?????????
//                customCalendarViewEvent.SetUpCalendar();
//
//
//            }
//        });
//
//        //================ ?????????(??????) ?????? ??? ?????? ===============
//        customCalendarViewEvent.nextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                customCalendarView. isSelectedAtFirst = false;
//
//                for (int i = 0; i < isSelected.length; i++) {
//                    isSelected[i] = false;
//                }
//                customCalendarViewEvent.calendar.add(Calendar.MONTH, 1);
//                customCalendarViewEvent.calendarPrev.add(Calendar.MONTH, 1);
//                customCalendarViewEvent.SetUpCalendar();
//            }
//        });


        //==========================????????? ???????????? ????????????????????????=========================
        customCalendarViewEvent.gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //?????? ??????????????? ?????? ??? ?????????????
                        if (!isSelectedAtFirst) {
                            customCalendarViewEvent.myGridAdapter.isAppOpen = true;
                            gridView_prevPosition = position;
                            gridView_tempPosition = position;

                            getDateData();

                            loadEventsList();
                            if (eventsArrayList.isEmpty()) {
                                noeventTv.setVisibility(View.VISIBLE);
                                listView_Events.setVisibility(View.INVISIBLE);
                            } else {
                                listView_Events.setVisibility(View.VISIBLE);
                                noeventTv.setVisibility(View.INVISIBLE);
                                myListAdapter_events = new MyListAdapter_events(MainActivity.this);
                                listView_Events.setAdapter(myListAdapter_events);
                            }

                            Log.d("posi1", "main gridtemp" + gridView_tempPosition);

                            customCalendarViewEvent.GridtempPosition = gridView_tempPosition;
                            customCalendarViewEvent.myGridAdapter.tempPosition = gridView_tempPosition;

                            Log.d("posi1", "customCalendar temp" + customCalendarViewEvent.GridtempPosition + "?");
                            Log.d("posi1", "adapter gridtemp" + customCalendarViewEvent.myGridAdapter.tempPosition + "?");

                            view.setBackgroundColor(Color.DKGRAY);

                            isSelectedAtFirst = true;
//                    customCalendarView.isSelectedAtFirst = isSelectedAtFirst;
                            isSelected[position] = true;

                        } else {
                            if (gridView_tempPosition == position && isSelected[position] == true) { //?????? ??????????????? ??? ????????? ??????
                                //???????????? ???????????? ????????????

                                getDateData();
                                loadEventsList();

                                if (eventsArrayList.isEmpty()) {

                                    noeventTv.setVisibility(View.VISIBLE);
                                    listView_Events.setVisibility(View.GONE);
                                } else {
                                    listView_Events.setVisibility(View.VISIBLE);
                                    noeventTv.setVisibility(View.GONE);
                                    myListAdapter_events = new MyListAdapter_events(MainActivity.this);
                                    listView_Events.setAdapter(myListAdapter_events);
                                }


                                CustomDialog_HowToAddEvents dialog_howToAddEvents = new CustomDialog_HowToAddEvents(MainActivity.this);
                                dialog_howToAddEvents.setDialogListener(new CustomDialog_HowToAddEvents.CustomDialogListener() {
                                    @Override
                                    public void onPositiveClicked() { //?????? ?????? ????????????
                                        CustomDialog_HowToAddEvents22 customDialog_howToAddEvents22 = new CustomDialog_HowToAddEvents22(MainActivity.this);
                                        customDialog_howToAddEvents22.setDialogListener(new CustomDialog_HowToAddEvents22.CustomDialogListener() {
                                            @Override
                                            public void onPositiveClicked() { //?????? ??????, ?????? ?????? ????????????

                                                //Add_CustomerPlusEvent.class??? ?????????
                                                Intent intent = new Intent(getApplicationContext(), Add_event_newCustomerFromCal.class);
                                                intent.putExtra("date", date);
                                                intent.putExtra("month", month);
                                                intent.putExtra("day", day);
                                                intent.putExtra("year", year);

                                                startActivityForResult(intent, REQUEST_ADD_MEMBERnEVENT);
                                            }

                                            @Override
                                            public void onNegativeClicked() { //?????? ??????, ?????? ?????? ????????????
                                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                                                builder.setCancelable(true);
                                                View addView = LayoutInflater.from(getApplicationContext()).inflate(layout.add_newevent_layout_test, null);

                                                TextView title = addView.findViewById(R.id.title);
                                                final TextView EventName = addView.findViewById(R.id.eventname);
                                                EventName.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        final android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(MainActivity.this);
                                                        builder1.setCancelable(true);

                                                        customersArrayList.clear();
                                                        customersArrayList = loadCustomersData();

                                                        View addView = LayoutInflater.from(getApplicationContext()).inflate(layout.custom_dialog_customers, null);

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
                                                        myListAdapter_customers = new MyListAdapter_customers(MainActivity.this);
                                                        listView2.setAdapter(myListAdapter_customers);

                                                        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                ListTempPosition = position;
                                                                EventName.setText(customersArrayList.get(position).CUSTOMER_NAME);
                                                                alertDialog_events.dismiss();
                                                            }
                                                        });

                                                        builder1.setView(addView);
                                                        alertDialog_events = builder1.create();
                                                        alertDialog_events.show();

                                                    }
                                                });

                                                final TextView eventTime = addView.findViewById(R.id.eventtime);
                                                eventTime.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Calendar calendar = Calendar.getInstance();
                                                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                                                        int minuts = calendar.get(Calendar.MINUTE);

                                                        CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(MainActivity.this
                                                                , new CustomTimePickerDialog.OnTimeSetListener() {
                                                            @Override
                                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                                Calendar c = Calendar.getInstance();
                                                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                                c.set(Calendar.MINUTE, minute);
                                                                c.setTimeZone(TimeZone.getDefault());
                                                                SimpleDateFormat hformat = new SimpleDateFormat("a K:mm", Locale.KOREA);
                                                                String event_Time = hformat.format(c.getTime());
                                                                eventTime.setText(event_Time);
                                                                eventTime.setTextColor(Color.BLACK);
                                                            }
                                                        }, hours, minuts, false);

                                                        timePickerDialog.show();
                                                    }
                                                });

                                                final String date2 = eventDateFormat.format(dates.get(gridView_tempPosition));
                                                Log.d("dateCompo", "datesssssss" + dates.get(gridView_tempPosition));
                                                Log.d("dateCompo", "datesssssss2" + eventDateFormat.format(dates.get(gridView_tempPosition)));
                                                final String month2 = monthFormat.format(dates.get(gridView_tempPosition));
                                                Log.d("dateCompo", "date2" + month2);
                                                final String year2 = yearFormat.format(dates.get(gridView_tempPosition));
                                                Log.d("dateCompo", "date2" + year2);

                                                final EditText memoEt = addView.findViewById(R.id.memoEt);

                                                final Button addBtn = addView.findViewById(R.id.addevent);
                                                addBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        if (EventName.getText().toString().isEmpty()) {
                                                            showToast("????????? ??????????????????");
                                                        } else {

                                                            if (eventTime.getText().toString().isEmpty()) {
                                                                showToast("??????????????? ??????????????????");
                                                            } else {


                                                                //?????? ?????? ?????? date&&time??????
                                                                boolean isExistedAlreadyForDateTime = false;

                                                                for (int i = 0; i < eventsArrayList.size(); i++) {
                                                                    if (eventTime.getText().toString().equals(eventsArrayList.get(i).TIME) && date.equals(eventsArrayList.get(i).DATE)) {

                                                                        isExistedAlreadyForDateTime = true;
                                                                        break;
                                                                    }
                                                                }

                                                                if (!isExistedAlreadyForDateTime) {// ??????????????? msec ?????? ?????????.
                                                                    long now = System.currentTimeMillis();
                                                                    // ??????????????? date ????????? ????????????.
                                                                    Date date3333 = new Date(now);
                                                                    // ????????? ????????? ????????? ????????? ( yyyy/MM/dd ?????? ????????? ?????? ?????? )
                                                                    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
                                                                    // nowDate ????????? ?????? ????????????.
                                                                    String formatDate = sdfNow.format(date3333); //????????????
                                                                    saveEvent(EventName.getText().toString(), eventTime.getText().toString()
                                                                            , date2, month2, year2, "", 0, false
                                                                            , memoEt.getText().toString(), customersArrayList.get(ListTempPosition).NUMBER
                                                                            , false, "", "", "", "", formatDate);
                                                                    eventsArrayList.clear();
                                                                    eventsArrayList = collectEventByDate(date);
                                                                    Collections.sort(eventsArrayList, comparator_events);

                                                                    if (eventsArrayList.size() == 1) {
                                                                        noeventTv.setVisibility(View.GONE);
                                                                        listView_Events.setVisibility(View.VISIBLE);
                                                                    }
                                                                    listView_Events.setAdapter(myListAdapter_events);

                                                                    customCalendarViewEvent.SetUpCalendar();
                                                                    alertDialog_addEvents.dismiss();


                                                                } else {
                                                                    showToast("????????? ????????? ????????? ????????? ????????????.");
                                                                }
                                                            }

                                                        }


                                                    }
                                                });

                                                builder.setView(addView);
                                                alertDialog_addEvents = builder.create();
                                                alertDialog_addEvents.show();


                                            }

                                            @Override
                                            public void onNeutralClicked() {
                                                //Add_CustomerPlusEvent.class??? ?????????
                                                Intent intent = new Intent(MainActivity.this, Add_event_nonFromCal.class);
                                                intent.putExtra("date", date);
                                                intent.putExtra("month", month);
                                                intent.putExtra("day", day);
                                                intent.putExtra("year", year);

                                                startActivityForResult(intent, REQUEST_ADD_EVENT_NONCUS);
                                            }
                                        });
                                        customDialog_howToAddEvents22.show();

                                    }

                                    @Override
                                    public void onNegativeClicked() { //????????? ?????? ????????????
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                                        builder2.setCancelable(true);
                                        final boolean[] isBtnClicked = {false};
                                        View addView = LayoutInflater.from(getApplicationContext()).inflate(layout.add_newevent_layout_test2, null);
                                        final Button newCustomerBtn = addView.findViewById(R.id.newCustomerBtn);

                                        final EditText eventEt = addView.findViewById(R.id.eventEt);
                                        final EditText numberEt = addView.findViewById(R.id.numberEt);
                                        numberEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
                                        //?????? ??????
                                        final TextView eventName = addView.findViewById(R.id.eventname);

                                        newCustomerBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (!isBtnClicked[0]) {
                                                    eventEt.setVisibility(View.VISIBLE);
                                                    numberEt.setVisibility(View.VISIBLE);
                                                    eventName.setVisibility(View.GONE);

                                                    isBtnClicked[0] = true;
                                                    newCustomerBtn.setText("???????????? ?????????");
                                                } else {
                                                    eventEt.setVisibility(View.GONE);
                                                    numberEt.setVisibility(View.GONE);
                                                    eventName.setVisibility(View.VISIBLE);

                                                    isBtnClicked[0] = false;
                                                    newCustomerBtn.setText("???????????? ?????????");
                                                }

                                            }
                                        });


                                        eventName.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(MainActivity.this);
                                                builder1.setCancelable(true);

                                                customersArrayList.clear();
                                                customersArrayList = loadCustomersData();

                                                View addView = LayoutInflater.from(getApplicationContext()).inflate(layout.custom_dialog_customers, null);

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
                                                myListAdapter_customers = new MyListAdapter_customers(MainActivity.this);
                                                listView2.setAdapter(myListAdapter_customers);

                                                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        ListTempPosition = position;
                                                        eventName.setText(customersArrayList.get(position).CUSTOMER_NAME);
                                                        alertDialog_events.dismiss();
                                                    }
                                                });

                                                builder1.setView(addView);
                                                alertDialog_events = builder1.create();
                                                alertDialog_events.show();
                                            }
                                        });

                                        final TextView eventTime = addView.findViewById(R.id.eventTime);
                                        eventTime.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Calendar calendar = Calendar.getInstance();
                                                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                                                int minuts = calendar.get(Calendar.MINUTE);

                                                CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(MainActivity.this
                                                        , new CustomTimePickerDialog.OnTimeSetListener() {
                                                    @Override
                                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                        Calendar c = Calendar.getInstance();
                                                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                        c.set(Calendar.MINUTE, minute);
                                                        c.setTimeZone(TimeZone.getDefault());
                                                        SimpleDateFormat hformat = new SimpleDateFormat("a K:mm", Locale.KOREA);
                                                        String event_Time = hformat.format(c.getTime());
                                                        eventTime.setText(event_Time);
                                                        eventTime.setTextColor(Color.BLACK);
                                                    }
                                                }, hours, minuts, false);

                                                timePickerDialog.show();
                                            }
                                        });

                                        final RadioGroup radioGroup = addView.findViewById(R.id.radioGroup);
                                        final RadioButton priceRb = addView.findViewById(R.id.priceRb);
                                        final RadioButton freeRb = addView.findViewById(R.id.freeRb);

                                        final LinearLayout priceLl = addView.findViewById(R.id.priceLl);
                                        final EditText priceEt = addView.findViewById(R.id.priceEt);
                                        final EditText memoEt = addView.findViewById(R.id.memoEt);

                                        final String date2 = eventDateFormat.format(dates.get(gridView_tempPosition));
                                        final String month2 = monthFormat.format(dates.get(gridView_tempPosition));
                                        final String year2 = yearFormat.format(dates.get(gridView_tempPosition));

                                        final Button addBtn = addView.findViewById(R.id.addevent);
                                        builder2.setView(addView);
                                        final AlertDialog ad23 = builder2.create();
                                        ad23.show();

                                        addBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (!isBtnClicked[0]) { //?????? ?????? ?????????
                                                    if (eventName.getText().toString().isEmpty()) {
                                                        showToast("????????? ??????????????????");
                                                    } else {
                                                        if (eventTime.getText().toString().isEmpty()) {
                                                            showToast("??????????????? ??????????????????");
                                                        } else {
                                                            if (!priceRb.isChecked() && !freeRb.isChecked()) {
                                                                showToast("????????? ????????? ??????????????????");
                                                            } else {
                                                                if (priceRb.isChecked()) { //?????? ???????????? ??????
                                                                    if (priceEt.getText().toString().isEmpty()) {
                                                                        showToast("????????? ????????? ??????????????????.");
                                                                    } else {
                                                                        //?????? ?????? ?????? date&&time??????
                                                                        boolean isExistedAlreadyForDateTime = false;

                                                                        for (int i = 0; i < eventsArrayList.size(); i++) { //????????? ???????????? ????????????
                                                                            if (eventTime.getText().toString().equals(eventsArrayList.get(i).TIME) && date.equals(eventsArrayList.get(i).DATE)) {
                                                                                isExistedAlreadyForDateTime = true;
                                                                                break;
                                                                            }
                                                                        }

                                                                        if (!isExistedAlreadyForDateTime) {
                                                                            // ??????????????? msec ?????? ?????????.
                                                                            long now = System.currentTimeMillis();
                                                                            // ??????????????? date ????????? ????????????.
                                                                            Date date3333 = new Date(now);
                                                                            // ????????? ????????? ????????? ????????? ( yyyy/MM/dd ?????? ????????? ?????? ?????? )
                                                                            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
                                                                            // nowDate ????????? ?????? ????????????.
                                                                            String formatDate = sdfNow.format(date3333); //????????????
                                                                            saveEvent(eventName.getText().toString(), eventTime.getText().toString(), date2, month2, year2,
                                                                                    "?????????"
                                                                                    , Integer.parseInt(priceEt.getText().toString())
                                                                                    , false, memoEt.getText().toString()
                                                                                    , customersArrayList.get(ListTempPosition).NUMBER, false
                                                                                    , "???????????????", "", "", "", formatDate);

                                                                            eventsArrayList.clear();
                                                                            eventsArrayList = collectEventByDate(date);
                                                                            Collections.sort(eventsArrayList, comparator_events);

                                                                            if (eventsArrayList.size() == 1) {
                                                                                noeventTv.setVisibility(View.GONE);
                                                                                listView_Events.setVisibility(View.VISIBLE);
                                                                            }
                                                                            listView_Events.setAdapter(myListAdapter_events);

                                                                            customCalendarViewEvent.SetUpCalendar();
                                                                            ad23.dismiss();


                                                                        } else {
                                                                            showToast("????????? ????????? ????????? ????????? ????????????.");
                                                                        }
                                                                    }

                                                                } else { //?????? ??????????????????
                                                                    //?????? ?????? ?????? date&&time??????
                                                                    boolean isExistedAlreadyForDateTime = false;

                                                                    for (int i = 0; i < eventsArrayList.size(); i++) {
                                                                        if (eventTime.getText().toString().equals(eventsArrayList.get(i).TIME) && date.equals(eventsArrayList.get(i).DATE)) {

                                                                            isExistedAlreadyForDateTime = true;
                                                                            break;
                                                                        }
                                                                    }

                                                                    if (!isExistedAlreadyForDateTime) {
                                                                        // ??????????????? msec ?????? ?????????.
                                                                        long now = System.currentTimeMillis();
                                                                        // ??????????????? date ????????? ????????????.
                                                                        Date date3333 = new Date(now);
                                                                        // ????????? ????????? ????????? ????????? ( yyyy/MM/dd ?????? ????????? ?????? ?????? )
                                                                        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
                                                                        // nowDate ????????? ?????? ????????????.
                                                                        String formatDate = sdfNow.format(date3333); //????????????
                                                                        saveEvent(eventName.getText().toString(), eventTime.getText().toString(), date2, month2, year2
                                                                                , "?????????"
                                                                                , 0, false, memoEt.getText().toString()
                                                                                , customersArrayList.get(ListTempPosition).NUMBER, false
                                                                                , "???????????????", "", "", "", formatDate);

                                                                        eventsArrayList.clear();
                                                                        eventsArrayList = collectEventByDate(date);
                                                                        Collections.sort(eventsArrayList, comparator_events);

                                                                        if (eventsArrayList.size() == 1) {
                                                                            noeventTv.setVisibility(View.GONE);
                                                                            listView_Events.setVisibility(View.VISIBLE);
                                                                        }
                                                                        listView_Events.setAdapter(myListAdapter_events);

                                                                        customCalendarViewEvent.SetUpCalendar();
                                                                        ad23.dismiss();


                                                                    } else {
                                                                        showToast("????????? ????????? ????????? ????????? ????????????.");
                                                                    }
                                                                }

                                                            }

                                                        }

                                                    }
                                                } else { //???????????? ?????????
                                                    //????????? ??????????????? ??????????????????
                                                    if (eventEt.getText().toString().isEmpty() || numberEt.getText().toString().isEmpty()
                                                            || !isValidCellPhoneNumber(numberEt.getText().toString())) {
                                                        showToast("????????? ??????????????? ??????????????????");
                                                    } else {
                                                        if (eventTime.getText().toString().isEmpty()) {
                                                            showToast("??????????????? ??????????????????");
                                                        } else {
                                                            if (!priceRb.isChecked() && !freeRb.isChecked()) {
                                                                showToast("????????? ????????? ??????????????????");
                                                            } else {
                                                                if (priceRb.isChecked()) { //?????? ???????????? ??????
                                                                    if (priceEt.getText().toString().isEmpty()) {
                                                                        showToast("????????? ????????? ??????????????????.");
                                                                    } else {
                                                                        //?????? ?????? ?????? date&&time??????
                                                                        boolean isExistedAlreadyForDateTime = false;
                                                                        for (int i = 0; i < eventsArrayList.size(); i++) { //????????? ???????????? ????????????
                                                                            if (eventTime.getText().toString().equals(eventsArrayList.get(i).TIME) && date.equals(eventsArrayList.get(i).DATE)) {
                                                                                isExistedAlreadyForDateTime = true;
                                                                                break;
                                                                            }
                                                                        }

                                                                        if (!isExistedAlreadyForDateTime) {
                                                                            //????????? ?????? ???????????? ????????????
                                                                            String phoneNum = numberEt.getText().toString().replace("-", "");

                                                                            // ??????????????? msec ?????? ?????????.
                                                                            long now = System.currentTimeMillis();
                                                                            // ??????????????? date ????????? ????????????.
                                                                            Date date123 = new Date(now);
                                                                            // ????????? ????????? ????????? ????????? ( yyyy/MM/dd ?????? ????????? ?????? ?????? )
                                                                            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
                                                                            // nowDate ????????? ?????? ????????????.
                                                                            String formatDate = sdfNow.format(date123);


                                                                            saveEvent(eventEt.getText().toString(), eventTime.getText().toString(), date2, month2, year2,
                                                                                    "?????????"
                                                                                    , Integer.parseInt(priceEt.getText().toString())
                                                                                    , false, memoEt.getText().toString()
                                                                                    , phoneNum, false
                                                                                    , "???????????????", "", "", "", formatDate);


                                                                            saveCustomer(ownername, eventEt.getText().toString(),
                                                                                    phoneNum, "??????", "", "", "", "", formatDate
                                                                                    , 0);

                                                                            eventsArrayList.clear();
                                                                            eventsArrayList = collectEventByDate(date);
                                                                            Collections.sort(eventsArrayList, comparator_events);

                                                                            if (eventsArrayList.size() == 1) {
                                                                                noeventTv.setVisibility(View.GONE);
                                                                                listView_Events.setVisibility(View.VISIBLE);
                                                                            }
                                                                            listView_Events.setAdapter(myListAdapter_events);

                                                                            customCalendarViewEvent.SetUpCalendar();
                                                                            ad23.dismiss();


                                                                        } else {
                                                                            showToast("????????? ????????? ????????? ????????? ????????????.");
                                                                        }
                                                                    }

                                                                } else { //?????? ??????????????????
                                                                    //?????? ?????? ?????? date&&time??????
                                                                    boolean isExistedAlreadyForDateTime = false;

                                                                    for (int i = 0; i < eventsArrayList.size(); i++) {
                                                                        if (eventTime.getText().toString().equals(eventsArrayList.get(i).TIME) && date.equals(eventsArrayList.get(i).DATE)) {

                                                                            isExistedAlreadyForDateTime = true;
                                                                            break;
                                                                        }
                                                                    }

                                                                    if (!isExistedAlreadyForDateTime) {
                                                                        //????????? ?????? ???????????? ????????????
                                                                        String phoneNum = numberEt.getText().toString().replace("-", "");

                                                                        // ??????????????? msec ?????? ?????????.
                                                                        long now = System.currentTimeMillis();
                                                                        // ??????????????? date ????????? ????????????.
                                                                        Date date123 = new Date(now);
                                                                        // ????????? ????????? ????????? ????????? ( yyyy/MM/dd ?????? ????????? ?????? ?????? )
                                                                        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
                                                                        // nowDate ????????? ?????? ????????????.
                                                                        String formatDate = sdfNow.format(date123);

                                                                        saveEvent(eventEt.getText().toString(), eventTime.getText().toString(), date2, month2, year2
                                                                                , "?????????"
                                                                                , 0, true, memoEt.getText().toString()
                                                                                , phoneNum, false
                                                                                , "???????????????", "", "", "", formatDate);


                                                                        saveCustomer(ownername, eventEt.getText().toString(),
                                                                                phoneNum, "??????", "", "", "", "", formatDate
                                                                                , 0);

                                                                        eventsArrayList.clear();
                                                                        eventsArrayList = collectEventByDate(date);
                                                                        Collections.sort(eventsArrayList, comparator_events);

                                                                        if (eventsArrayList.size() == 1) {
                                                                            noeventTv.setVisibility(View.GONE);
                                                                            listView_Events.setVisibility(View.VISIBLE);
                                                                        }
                                                                        listView_Events.setAdapter(myListAdapter_events);

                                                                        customCalendarViewEvent.SetUpCalendar();
                                                                        ad23.dismiss();


                                                                    } else {
                                                                        showToast("????????? ????????? ????????? ????????? ????????????.");
                                                                    }
                                                                }

                                                            }

                                                        }
                                                    }
                                                }

                                            }
                                        });

                                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                                switch (checkedId) {
                                                    case R.id.priceRb:
                                                        priceLl.setVisibility(View.VISIBLE);
                                                        break;

                                                    case R.id.freeRb:
                                                        priceLl.setVisibility(View.GONE);
                                                        break;
                                                }
                                            }
                                        });

                                    }

                                });
                                dialog_howToAddEvents.show();


                            } else if (gridView_tempPosition != position) { //???????????? ?????????
                                gridView_prevPosition = gridView_tempPosition;
                                gridView_tempPosition = position;

                                getDateData();

                                loadEventsList();
                                if (eventsArrayList.isEmpty()) {
                                    noeventTv.setVisibility(View.VISIBLE);
                                    listView_Events.setVisibility(View.INVISIBLE);
                                } else {
                                    listView_Events.setVisibility(View.VISIBLE);
                                    noeventTv.setVisibility(View.INVISIBLE);
                                    myListAdapter_events = new MyListAdapter_events(MainActivity.this);
                                    listView_Events.setAdapter(myListAdapter_events);
                                }

                                view.setBackgroundColor(Color.DKGRAY);
                                customCalendarViewEvent.gridView.getChildAt(gridView_prevPosition).setBackgroundColor(Color.WHITE);

                                isSelected[gridView_prevPosition] = false;
                                isSelected[position] = true;
                            }

                            customCalendarViewEvent.GridtempPosition = gridView_tempPosition;
                            customCalendarViewEvent.myGridAdapter.tempPosition = gridView_tempPosition;
                        }

                    }
                });

        //evnetsListview ??????
        listView_Events.setAdapter(myListAdapter_events); //????????? ??????

        // =====================================?????????????????? ????????? ???????????????=======================================
        listView_Events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListTempPosition = position;

                Log.d("test2222", eventsArrayList.get(position).RETOUCH + "?");
                Log.d("test2222", eventsArrayList.get(position).isCOMPLETE() + "     ??");


                Intent popupIntent = new Intent(MainActivity.this, EachEventActivity.class);
                popupIntent.putExtra("name", eventsArrayList.get(ListTempPosition).EVENT);
                Log.d("name", eventsArrayList.get(ListTempPosition).EVENT);
                popupIntent.putExtra("number", eventsArrayList.get(ListTempPosition).NUMBER);
                Log.d("name", eventsArrayList.get(ListTempPosition).NUMBER);
                popupIntent.putExtra("date", eventsArrayList.get(ListTempPosition).DATE);
                popupIntent.putExtra("time", eventsArrayList.get(ListTempPosition).TIME);
                popupIntent.putExtra("retouch", eventsArrayList.get(ListTempPosition).RETOUCH);
                popupIntent.putExtra("price", eventsArrayList.get(ListTempPosition).PRICE);
                popupIntent.putExtra("memo", eventsArrayList.get(ListTempPosition).SHORTMEMO);
                popupIntent.putExtra("complete", eventsArrayList.get(ListTempPosition).COMPLETE);
                popupIntent.putExtra("noshow", eventsArrayList.get(ListTempPosition).NOSHOW);
                popupIntent.putExtra("menu", eventsArrayList.get(ListTempPosition).MENU);
                popupIntent.putExtra("cardcash", eventsArrayList.get(ListTempPosition).CARDCASH);
                popupIntent.putExtra("materialMemo", eventsArrayList.get(ListTempPosition).MATERIALMEMO);
                popupIntent.putExtra("surgeryMemo", eventsArrayList.get(ListTempPosition).CONTENTMEMO);
                popupIntent.putExtra("crtDate", eventsArrayList.get(ListTempPosition).CRTDATE);
                startActivityForResult(popupIntent, REQUEST_POPUP_ACTIVITY);

                overridePendingTransition(anim.fadein, anim.fadeout);

            }
        });

        listView_Events.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListTempPosition = position;
                final AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                ab.setTitle("?????? ??????/??????");
                ab.setIcon(mipmap.ic_launcher);
                ab.setMessage(eventsArrayList.get(ListTempPosition).EVENT + " " + eventsArrayList.get(ListTempPosition).TIME + "\n????????? ????????????????????????? ");
                ab.setPositiveButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEvents();
                        customCalendarViewEvent.SetUpCalendar();

                        eventsArrayList.clear();
                        eventsArrayList = collectEventByDate(date);
                        Collections.sort(eventsArrayList, comparator_events);
                        listView_Events.setAdapter(myListAdapter_events);
                        Toast.makeText(getApplicationContext(), "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                });
                ab.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                ab.setNeutralButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (eventsArrayList.get(ListTempPosition).COMPLETE == true) {
                            Toast.makeText(MainActivity.this, "????????? ?????? ????????? ????????? ??? ????????????", Toast.LENGTH_SHORT).show();
                        } else {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setCancelable(true);
                            final View addView = LayoutInflater.from(getApplicationContext()).inflate(layout.add_newevent_layout_test, null);

                            final TextView title = addView.findViewById(R.id.title);
                            title.setText("?????? ??????");

                            final TextView EventName = addView.findViewById(R.id.eventname);
                            EventName.setText(eventsArrayList.get(ListTempPosition).EVENT); //?????? ??????????????? ??????, ????????? ????????? ????????? ????????????


                            final TextView eventTime = addView.findViewById(R.id.eventtime);
                            eventTime.setText(eventsArrayList.get(ListTempPosition).TIME);
                            eventTime.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Calendar calendar = Calendar.getInstance();
                                    int hours = calendar.get(Calendar.HOUR_OF_DAY);
                                    int minuts = calendar.get(Calendar.MINUTE);

                                    CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(MainActivity.this
                                            , new CustomTimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            Calendar c = Calendar.getInstance();
                                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            c.set(Calendar.MINUTE, minute);
                                            c.setTimeZone(TimeZone.getDefault());
                                            SimpleDateFormat hformat = new SimpleDateFormat("a K:mm", Locale.KOREA);
                                            String event_Time = hformat.format(c.getTime());
                                            eventTime.setText(event_Time);
                                            eventTime.setTextColor(Color.BLACK);
                                        }
                                    }, hours, minuts, false);

                                    timePickerDialog.show();
                                }
                            });

                            final String date = eventDateFormat.format(dates.get(gridView_tempPosition));
                            final String month = monthFormat.format(dates.get(gridView_tempPosition));
                            final String year = yearFormat.format(dates.get(gridView_tempPosition));

                            final EditText memoEt = addView.findViewById(R.id.memoEt);
                            memoEt.setText(eventsArrayList.get(ListTempPosition).SHORTMEMO);

                            final Button updateBtn = addView.findViewById(R.id.addevent);


                            builder.setView(addView);
                            final AlertDialog ab2 = builder.create();
                            ab2.show();

                            updateBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    reviseEvent(eventsArrayList.get(ListTempPosition).EVENT,
                                            eventsArrayList.get(ListTempPosition).DATE,
                                            eventsArrayList.get(ListTempPosition).TIME,
                                            eventTime.getText().toString(),
                                            memoEt.getText().toString());

                                    loadEventsList();
                                    myListAdapter_events.notifyDataSetChanged();
                                    ab2.dismiss();

                                }

                            });


                        }
                    }
                }); //end of NeutralButton


                ab.show();
                return true;
            }
        });


        //botLl button

    }

    // ?????? ??? ????????? ?????????
    private void initLayout() {
        //Toolbar
        toolbar = findViewById(id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true); //????????? ???????????????


//        //drawerLayout
//        drawerLayout = (DrawerLayout) findViewById(id.drawerLayout);
//        navigationView = findViewById(id.nav);
//
//        //???????????? ???????????? ??????
//        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, string.app_name, string.app_name);
//        actionBarDrawerToggle.syncState(); //???????????? ?????????
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(mipmap.resized_minimap_backarrow);
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//
//        //??????????????? ????????? ??????????????? ?????????
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case id.menu_board:
//                        Intent menuIntent = new Intent(getApplicationContext(), MenuControlActivity.class);
//                        startActivity(menuIntent);
//
//                        //drawerLayout ??????
//                        drawerLayout.closeDrawer(navigationView);
//
//                        break;
//                    case id.menu_sms:
//                        Intent smsIntent = new Intent(getApplicationContext(), SmsListActivity.class);
//                        startActivity(smsIntent);
//
//                        //drawerLayout ??????
//                        drawerLayout.closeDrawer(navigationView);
//                        break;
//                    case id.sign_out:
//                        FirebaseAuth.getInstance().signOut();
//                        //drawerLayout ??????
//                        drawerLayout.closeDrawer(navigationView);
//
//                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);//???????????? ????????????
//                        startActivity(intent);
//                        finish();
//
//                        break;
//                }
//                return false;
//            }
//        });


        //?????? ??????????????????
        yearMonthRl = findViewById(id.dateRl);
        yearMonthRl.setOnClickListener(this);
        eventsDateTv = findViewById(id.eventDateTv);
        eventsDateTv.setText(monthOfToday + "  " + dayOfToday + "??? ?????? ?????????");
        listView_Events = findViewById(id.eventsListview);
        noeventTv = findViewById(id.noeventTv);

        //Footer
        albumLl = findViewById(id.albumLl);
        albumLl.setOnClickListener(this);
        memoLl = findViewById(id.memoLl);
        memoLl.setOnClickListener(this);
        messageLl = findViewById(id.messageLl);
        messageLl.setOnClickListener(this);
        graphLl = findViewById(id.graphLl);
        graphLl.setOnClickListener(this);
        customerLl = findViewById(id.customerLl);
        customerLl.setOnClickListener(this);

        customCalendarViewEvent = findViewById(id.activity_custom_calendar_view);
//        customCalendarViewEvent.dateRl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeDate();
//            }
//        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

//    private void changeDate(){
//        //????????????
//        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
//        int width = (int) (dm.widthPixels * 0.8); // Display ???????????? 90%
//
//        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setCancelable(true);
//        View addView = LayoutInflater.from(getApplicationContext()).inflate(layout.year_month_picker, null);
//
//        final NumberPicker pickedYear = addView.findViewById(id.yearPicker);
//        final NumberPicker pickedMonth = addView.findViewById(id.monthPicker);
//        Button btn = addView.findViewById(id.btn);
//
//        //?????? ????????? ??????
//        pickedYear.setWrapSelectorWheel(false);
//        pickedMonth.setWrapSelectorWheel(false);
//
//        // editText ????????????
//        pickedYear.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//        pickedMonth.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//
//        // ????????? ??????
//        pickedYear.setMinValue(2017);
//        pickedMonth.setMinValue(1);
//
//        // ????????? ??????
//        pickedYear.setMaxValue(2050);
//        pickedMonth.setMaxValue(12);
//
//
//        //????????? ??????
//////        String initDate = customCalendarViewEvent.CurrentDate.getText().toString().trim();
////        Log.d(CalendarRenovation, initDate  );
////        String initYear = initDate.substring(0,4);
////        String initMonth = initDate.substring(5);
//
//        final int initYearNbr = Integer.parseInt(initYear);
//        final int initMonthNbr = Integer.parseInt(initMonth);
//
//        pickedYear.setValue(initYearNbr);
//        pickedMonth.setValue(initMonthNbr);
//
//
//        builder.setView(addView);
//        final AlertDialog ab2 = builder.create();
//        ab2.show();
//
//        //????????????
//        ViewGroup.LayoutParams params = ab2.getWindow().getAttributes();
//        params.width = width;
//        ab2.getWindow().setAttributes((WindowManager.LayoutParams) params);
//
//        final int[] gapYear = new int[1];
//        final int[] gapMonth = new int[1];
//
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                if (pickedMonth.getValue() <10){
////                    customCalendarViewEvent.CurrentDate.setText(pickedYear.getValue()+".0"+pickedMonth.getValue());
////                } else{
////                    customCalendarViewEvent.CurrentDate.setText(pickedYear.getValue()+"."+pickedMonth.getValue());
////                }
//
//                gapYear[0] = pickedYear.getValue() - initYearNbr;
//                gapMonth[0] = pickedMonth.getValue() - initMonthNbr;
//
//                for (int i = 0; i < isSelected.length; i++) {
//                    isSelected[i] = false;
//                }
//
//                customCalendarViewEvent.calendar.add(Calendar.YEAR, gapYear[0]);
//                Log.d(CalendarRenovation, "calendar : " + customCalendarViewEvent.calendar.getTime() + "gapYear : " + gapYear[0]);
//                customCalendarViewEvent.calendar.add(Calendar.MONTH, gapMonth[0]);
//                Log.d(CalendarRenovation, "calendar : " + customCalendarViewEvent.calendar.getTime() + "gapMonth : " + gapMonth[0]);
//                customCalendarViewEvent.SetUpCalendar();
//
//                ab2.dismiss();
//
//            }
//        });
//
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menuformain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case id.action_btn1:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //?????? ???????????????
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //TopLl
//            case id.btn_addNewEvent:
//                ShowAlertDialogWithListview_newEvent();
//                break;

            case id.graphLl:

                Intent intent0 = new Intent(this, Activity_Accounting.class);
                startActivity(intent0);

                break;

            //BotLl
            case id.albumLl:

//
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                startActivityForResult(intent, GET_GALLERY_IMAGE);

//                Intent mIntent = new Intent(Intent.ACTION_VIEW
//                        , Uri.parse("content://media/internal/images/*"));
//                startActivity(mIntent);

                Intent intent2 = new Intent(Intent.ACTION_PICK);
                intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent2, GET_GALLERY_IMAGE);

//                Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/*"));
//                startActivity(mIntent);

                Intent intent3 = new Intent();
                intent3.setAction(Intent.ACTION_GET_CONTENT); // ACTION_PICK??? ???????????? ??????, deprecated + formally
                intent3.setType("image/*");

//                Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
////comma-separated MIME types
//                mediaChooser.setType("video/*, image/*");
//                startActivityForResult(mediaChooser, RESULT_LOAD_IMAGE);

                break;


            case id.memoLl:
                Intent memoIntent = new Intent(this, MemoActivity.class);
                startActivity(memoIntent);

//                            Intent albumIntent = new Intent();
//                albumIntent.setType("image/*");
//                albumIntent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(albumIntent, REQUEST_ALBUM);


                break;

            case id.messageLl:
                Intent intent = new Intent("android.intent.action.MAIN");

                intent.addCategory("android.intent.category.DEFAULT");

                intent.setType("vnd.android-dir/mms-sms");

                startActivity(intent);


                break;

            case id.customerLl:
                Intent intentToMemberList = new Intent(this, MemberList.class);
                intentToMemberList.putExtra("toMemberList", "main_activity");
                startActivity(intentToMemberList);
                break;

        }
    }

    //??????
    String receiveName;
    String receivePhone;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CONTACTS:
                Cursor cursor = getContentResolver().query(data.getData(),
                        new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER},
                        null, null, null);

                cursor.moveToFirst();
                //????????????
                receiveName = cursor.getString(0);
                //???????????? ??????
                receivePhone = cursor.getString(1);
                cursor.close();

                addCustomer(receiveName, receivePhone);

                break;

            case REQUEST_ADD_MEMBERnEVENT:
                loadEventsList();
                listView_Events.setVisibility(View.VISIBLE);
                noeventTv.setVisibility(View.GONE);
                myListAdapter_events = new MyListAdapter_events(MainActivity.this);
                listView_Events.setAdapter(myListAdapter_events);
                customCalendarViewEvent.SetUpCalendar();

                break;

            case REQUEST_ADD_EVENT_NONCUS:

                loadEventsList();
                listView_Events.setVisibility(View.VISIBLE);
                noeventTv.setVisibility(View.GONE);
                myListAdapter_events = new MyListAdapter_events(MainActivity.this);
                listView_Events.setAdapter(myListAdapter_events);
                customCalendarViewEvent.SetUpCalendar();

                break;

            case REQUEST_POPUP_ACTIVITY:
                //????????? ????????????
                boolean complete = data.getBooleanExtra("complete", false);
                boolean noShow = data.getBooleanExtra("noshow", false);
                String cardCash = data.getStringExtra("cardcash");
                String materialMemo = data.getStringExtra("materialMemo");
                String surgeryMemo = data.getStringExtra("surgeryMemo");

                Log.d("noshow got", "complete: " + complete);
                Log.d("noshow got", "noshow: " + noShow);

                //????????? ???
                if (complete && noShow) {
                    //complete??? noshow??? true??? ?????????.
                    loadEventsList();
                    completeEvent(eventsArrayList.get(ListTempPosition).EVENT,
                            eventsArrayList.get(ListTempPosition).DATE,
                            eventsArrayList.get(ListTempPosition).TIME,
                            "true", "true", "", materialMemo, surgeryMemo);
                }

                //?????? ?????? ????????????
                else if (complete && !noShow) {
                    loadEventsList();
                    completeEvent(eventsArrayList.get(ListTempPosition).EVENT,
                            eventsArrayList.get(ListTempPosition).DATE,
                            eventsArrayList.get(ListTempPosition).TIME,
                            "true", "false", cardCash, materialMemo, surgeryMemo);
                }

                loadEventsList();
                myListAdapter_events.notifyDataSetChanged();
                customCalendarViewEvent.SetUpCalendar();

                break;

        }


    }

    public void addCustomer(String receiveName, String receivePhone) {
        Intent intent2 = new Intent(this, Activity_Create_Customer.class);
        intent2.putExtra("name", receiveName);
        intent2.putExtra("phone", receivePhone);
        startActivity(intent2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        customCalendarViewEvent.SetUpCalendar();
        Log.d("resume", "resume");
    }


    //ReadEventslist
    private void loadEventsList() {
        eventsArrayList.clear();
        eventsArrayList = collectEventByDate(date);
        Collections.sort(eventsArrayList, comparator_events);
    }


    //?????? ?????? ?????? ????????? ??????
    public void search(String charText) {
        //?????? ??????????????? ???????????? ????????? ?????? ????????????.
        customersArrayList.clear(); // ?????? ??????! ????????? Adapter??? ???????????? ???????????? ?????????
        //??????????????? ???????????? ?????? ???????????? ????????????.
        if (charText.length() == 0) {
            customersArrayList.addAll(customersArrayList_copy);
        } else { //???????????????
            for (int i = 0; i < customersArrayList_copy.size(); i++) { //???????????? ?????? ???????????? ????????????.
                if (customersArrayList_copy.get(i).CUSTOMER_NAME.toLowerCase().contains(charText)) {
                    //????????? ???????????? ???????????? ????????????.
                    //list.add(arraylist.get(i));
                    customersArrayList.add(customersArrayList_copy.get(i));
                }
            }
        }
        //????????? ???????????? ????????????????????? ????????? ??????
        myListAdapter_customers.notifyDataSetChanged();
    }


    // ============================= ListAdapter ========================

    //????????? ??????????????? ?????? ListAdapter_customers
    class RowHolder_customers {
        TextView nameTvHolder;
        TextView gradeTvHolder;
        TextView numberTvHolder;
    }

    class MyListAdapter_customers extends ArrayAdapter {
        LayoutInflater lnf;

        public MyListAdapter_customers(android.app.Activity context) {
            super(context, layout.single_row_memberlsit, customersArrayList);
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
                convertView = lnf.inflate(layout.single_row_memberlsit, parent, false);
                viewHolder = new RowHolder_customers();

                viewHolder.nameTvHolder = convertView.findViewById(id.name_tv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RowHolder_customers) convertView.getTag();
            }

            customersArrayList.clear();
            customersArrayList = loadCustomersData();
            String phoneNum = customersArrayList.get(position).NUMBER.substring(0, 3) + "-" + customersArrayList.get(position).NUMBER.substring(3, 7) +
                    "-" + customersArrayList.get(position).NUMBER.substring(7);
            viewHolder.nameTvHolder.setText(customersArrayList.get(position).CUSTOMER_NAME + "  "
                    + "\n" + phoneNum);

            return convertView;
        }
    }

    //????????? ???????????? ?????? ListAdapter_events
    class RowHolder_events {
        LinearLayout listviewformemlist;
        TextView profileTvHolder;
        TextView timeTvHolder;
    }

    class MyListAdapter_events extends ArrayAdapter {
        LayoutInflater lnf;

        public MyListAdapter_events(android.app.Activity context) {
            super(context, layout.custom_listview_reserv_row, eventsArrayList);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return eventsArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return eventsArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        //events getView
        @SuppressLint("ResourceAsColor")
        public View getView(int position, View convertView, ViewGroup parent) {
            RowHolder_events viewHolder;
            if (convertView == null) {
                convertView = lnf.inflate(layout.custom_listview_reserv_row, parent, false);
                viewHolder = new RowHolder_events();

                viewHolder.listviewformemlist = convertView.findViewById(id.listviewformemlist);
                viewHolder.profileTvHolder = convertView.findViewById(id.profile_tv);
                viewHolder.timeTvHolder = convertView.findViewById(id.time_tv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RowHolder_events) convertView.getTag();
            }

            //????????? ??? ?????? event
            if (!eventsArrayList.get(position).COMPLETE) {
                viewHolder.timeTvHolder.setTextColor(Color.BLACK);
                viewHolder.timeTvHolder.setTypeface(null, Typeface.BOLD);
                //????????????
                if (eventsArrayList.get(position).RETOUCH.equals("?????????")) {
                    viewHolder.timeTvHolder.setText(eventsArrayList.get(position).TIME + "  " +
                            eventsArrayList.get(position).EVENT + "  " +
                            "?????????" + "  ");
                } else {//???????????? ?????????
                    viewHolder.timeTvHolder.setText(eventsArrayList.get(position).TIME + "  " +
                            eventsArrayList.get(position).EVENT);
                }
            } else {// ????????? ????????? event
                viewHolder.timeTvHolder.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.timeTvHolder.setTypeface(null, Typeface.ITALIC);
                //????????? ?????????
                if (eventsArrayList.get(position).NOSHOW) {
                    viewHolder.profileTvHolder.setBackgroundColor(Color.RED);
                    //????????????
                    if (eventsArrayList.get(position).RETOUCH.equals("?????????")) {
                        viewHolder.timeTvHolder.setText(eventsArrayList.get(position).TIME + "  " +
                                eventsArrayList.get(position).EVENT + "  " +
                                "?????????" + "  ");

                    } else { //???????????? ?????????
                        viewHolder.timeTvHolder.setText(eventsArrayList.get(position).TIME + "  " +
                                eventsArrayList.get(position).EVENT);
                    }

                } else { //????????? ?????? ?????? ??????dfdfdf
                    viewHolder.profileTvHolder.setBackgroundColor(Color.parseColor("#169300"));
                    //????????????
                    if (eventsArrayList.get(position).RETOUCH.equals("?????????")) {
                        //?????? ?????????
                        if (eventsArrayList.get(position).PRICE == 0) {
                            viewHolder.timeTvHolder.setText(eventsArrayList.get(position).TIME + "  " +
                                    eventsArrayList.get(position).EVENT + "  " +
                                    "?????????" + "  ");
                        } else { //?????? ?????????
                            viewHolder.timeTvHolder.setText(eventsArrayList.get(position).TIME + "  " +
                                    eventsArrayList.get(position).EVENT + "  " +
                                    "?????????");
                        }

                    } else { //???????????? ?????????
                        viewHolder.timeTvHolder.setText(eventsArrayList.get(position).TIME + "  " +
                                eventsArrayList.get(position).EVENT);
                    }
                }
            }


            //????????? ????????????
//            viewHolder.timeTvHolder.setPaintFlags(0);


            return convertView;
        }

    }


    private void getDateData() {
        date = customCalendarViewEvent.eventDateFormat.format(customCalendarViewEvent.dates.get(gridView_tempPosition)); //?????? ????????????
        Log.d("dateCompo", "First date : " + date);
        Log.d("dateCompo", "dates : " + customCalendarViewEvent.dates.get(gridView_tempPosition));

        month = customCalendarViewEvent.monthFormat.format(customCalendarViewEvent.dates.get(gridView_tempPosition)); //???
        Log.d("dateCompo", "First month : " + month);
        day = " " + date.charAt(8) + "" + date.charAt(9) + "???"; //day
        Log.d("dateCompo", "First day : " + day);
        year = customCalendarViewEvent.yearFormat.format(customCalendarViewEvent.dates.get(gridView_tempPosition));
        Log.d("dateCompo", "First day : " + year);
        eventsDateTv.setText(month + " " + day + " ?????? ?????????");
    }


    //?????? ?????? ?????????
//    public void ShowAlertDialogWithListview_menu() {
//        final ArrayList<MenuList> tempArrayList = loadMenusData();
//        Log.d("menuPopup", "??????????????? ?????????? 1");
//        List<String> mMenus = new ArrayList<>();
//
//        for (int i = 0; i < tempArrayList.size(); i++) {
//            mMenus.add(tempArrayList.get(i).ITEM);
//        }
//        //Create sequence of items
//        final CharSequence[] Animals = mMenus.toArray(new String[mMenus.size()]);
//        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
//        ab.setTitle("????????? ??????????????????");
//        ab.setItems(Animals, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//                String selectedText = Animals[item].toString();  //Selected item in listview
//
//                tempPrice = tempArrayList.get(item).PRICE;
//
//                updateEventForPrice(eventsArrayList.get(ListTempPosition).EVENT, eventsArrayList.get(ListTempPosition).DATE, eventsArrayList.get(ListTempPosition).TIME, tempPrice);
//                completeEvent(eventsArrayList.get(ListTempPosition).EVENT, eventsArrayList.get(ListTempPosition).DATE, eventsArrayList.get(ListTempPosition).TIME, true, false);
//
//                eventsArrayList.clear();
//                eventsArrayList = collectEventByDate(date);
//                Collections.sort(eventsArrayList, comparator_events);
//
//                myListAdapter_events.notifyDataSetChanged();
//            }
//        });
//
//        ab.setCancelable(true);
//        ab.create();
//        ab.show();
//
//        loadMenusData();
//        myListAdapter_events.notifyDataSetChanged();
//    }

    //?????? ?????? ?????????
//    public void ShowAlertDialogWithListview_menuForRetouch() {
//        Log.d("menuPopup", "??????????????? ?????????? 1");
//        List<String> mMenus = new ArrayList<>();
//
//
//        mMenus.add("??????");
//        mMenus.add("?????????");
//
//        //Create sequence of items
//        final CharSequence[] Animals = mMenus.toArray(new String[mMenus.size()]);
//        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
//        ab.setTitle("????????? ??????????????????");
//        ab.setItems(Animals, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//                String selectedText = Animals[item].toString();  //Selected item in listview
//
//                if (selectedText.startsWith("???")) {
//                    updateEventForPrice(eventsArrayList.get(ListTempPosition).EVENT, eventsArrayList.get(ListTempPosition).DATE, eventsArrayList.get(ListTempPosition).TIME, 0);
//                    updateEventForComplete(eventsArrayList.get(ListTempPosition).EVENT, eventsArrayList.get(ListTempPosition).DATE, eventsArrayList.get(ListTempPosition).TIME, "true");
//
//                } else {
//                    updateEventForPrice(eventsArrayList.get(ListTempPosition).EVENT, eventsArrayList.get(ListTempPosition).DATE, eventsArrayList.get(ListTempPosition).TIME, 0);
//                    updateEventForComplete(eventsArrayList.get(ListTempPosition).EVENT, eventsArrayList.get(ListTempPosition).DATE, eventsArrayList.get(ListTempPosition).TIME, "false");
//                }
//
//
//                eventsArrayList.clear();
//                eventsArrayList = collectEventByDate(date);
//                Collections.sort(eventsArrayList, comparator_events);
//
//                myListAdapter_events.notifyDataSetChanged();
//            }
//        });
//
//        ab.setCancelable(true);
//
//        ab.create();
//        ab.show();
//
////        Dialog dialog = ab.create();
////
////        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
////        lp.copyFrom(dialog.getWindow().getAttributes());
////        lp.width = 800;
////        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
////
////        dialog.show();
////        Window window = dialog.getWindow();
////        window.setAttributes(lp);
//
//        loadMenusData();
//        myListAdapter_events.notifyDataSetChanged();
//    }

    //???????????? ?????????
    public void deleteEvents() {
        dbOpenHelper = new DBOpenHelper(getApplicationContext());
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.deleteEvent(eventsArrayList.get(ListTempPosition).EVENT, eventsArrayList.get(ListTempPosition).DATE, eventsArrayList.get(ListTempPosition).TIME, database);
        dbOpenHelper.close();

        eventsArrayList.clear();
    }


    //?????? ?????? ?????????
    private void reviseEvent(String event, String date, String beforeTime, String updateTime, String memo) {
        dbOpenHelper = new DBOpenHelper(getApplicationContext());
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.reviseEvent(event, date, beforeTime, updateTime, memo, database);
        dbOpenHelper.close();
    }


    private long time = 0;
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "?????? ????????? ?????? ??? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            finish();
        }
    }

    //=================================???????????? ????????? ??????=================================
    public void ShowAlertDialogWithListview_newEvent() {
        List<String> kindOfEvent = new ArrayList<String>();

        kindOfEvent.add("??????");
        kindOfEvent.add("??????");
        kindOfEvent.add("?????????");
        kindOfEvent.add("???????????? ????????????");

        //Create sequence of items
        final CharSequence[] Kinds = kindOfEvent.toArray(new String[kindOfEvent.size()]);
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
        dialogBuilder.setTitle("?????? ??????");
        dialogBuilder.setItems(Kinds, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = Kinds[item].toString(); //Selected item in listview
                if (selectedText.startsWith("??????")) {
                    Intent intent = new Intent(getApplicationContext(), Add_event_existingCustomer.class);
                    startActivity(intent);
                } else if (selectedText.startsWith("??????")) {
                    Intent intent = new Intent(getApplicationContext(), Add_event_newCustomerFromHome.class);
                    startActivity(intent);
                } else if (selectedText.startsWith("?????????")) {
                    Intent intent = new Intent(getApplicationContext(), Add_event_retouch.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), Add_event_nonFromHome.class);
                    startActivity(intent);
                }
            }
        });
        //Create alert dialog object via builder
        android.app.AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }

}



