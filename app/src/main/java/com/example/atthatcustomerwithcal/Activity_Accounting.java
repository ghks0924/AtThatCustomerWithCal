package com.example.atthatcustomerwithcal;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Activity_Accounting extends Activity_Base {

    final int REQUEST_POPUP_ACTIVITY = 400;

    Calendar calendar = Calendar.getInstance(Locale.KOREA);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.KOREA);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
    List<Date> dates;

    //화면요소
    TextView weeklyTv, displayTvForMonth, displayTvForWeek, monthTv;
    TextView dailyTv, displayTvForDay;

    CustomCalendarView_accounting customCalendarView_accounting;


    //events ArrayList
    ArrayList<Events> eventsArrayList;
    ArrayList<Events> eventsArrayListWeekly;
    //월별 이벤트리스트
    ArrayList<Events> eventsArrayListMonthly = new ArrayList<>();

    //매출등록 안 된 이벤트 ArrayList
    ArrayList<Events> finalFilteredEvents = new ArrayList<>();

    int sumPriceDaily_Card0 = 0;
    int sumPriceDaily_Cash0 = 0;
    int sumPriceDaily = 0;

    int sumPriceWeekly;
    int sumPriceWeekly_Card;
    int sumPriceWeekly_Cash;


    //그리드뷰 관련
    public boolean isSelectedAtFirst = true; //최초 실행시 선택여부
    public boolean isSelected[] = new boolean[42]; // 각 셀의 클릭이력 여부
    int gridView_tempPosition;
    int gridView_prevPosition;

    //calendarView
    String date;
    String month;
    String day;
    String year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounting);

        monthTv = findViewById(R.id.monthTv);
        weeklyTv = findViewById(R.id.weeklyTv);
        dailyTv = findViewById(R.id.dailyTv);
        displayTvForMonth = findViewById(R.id.displayTvForMonth);
        displayTvForWeek = findViewById(R.id.displayTvForWeek);
        displayTvForDay = findViewById(R.id.displayTvForDay);

        // toolbar
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //뒤로가기 버튼, 디폴트값임
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //앱 제목 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        customCalendarView_accounting = findViewById(R.id.activity_custom_calendar_view2);
        dates = customCalendarView_accounting.dates;
        customCalendarView_accounting.SetUpCalendar();

        //월 매출 자동 입력
        DecimalFormat formatter = new DecimalFormat("###,###");
        monthTv.setText(monthFormat.format(customCalendarView_accounting.calendar.getTime()));
        displayTvForMonth.setText(formatter.format(customCalendarView_accounting.monthlyPrice)
                + " (카드 " + formatter.format(customCalendarView_accounting.monthlyPrice_Card) + ")" + " 원");
        //오늘 날짜 구하기
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date nowDate = new Date(now);
        String todayStr = customCalendarView_accounting.eventDateFormat.format(nowDate);
        //일 매출 자동 입력
        collectEventByDateForCardCash(todayStr);
        displayTvForDay.setText(formatter.format(sumPriceDaily) + " (카드 "
                + formatter.format(sumPriceDaily_Card0) + ") 원");
        dailyTv.setText(customCalendarView_accounting.monthFormat.format(nowDate) +
                " " + todayStr.charAt(8) + "" + todayStr.charAt(9) + "일");
        //주 매출 자동 입력

        collectWeeklyEvents(todayStr);
        displayTvForWeek.setText(formatter.format(sumPriceWeekly) + " (카드 "
                + formatter.format(sumPriceWeekly_Card) + ") 원");
        weeklyTv.setText(customCalendarView_accounting.monthFormat.format(nowDate) +
                " " + todayStr.charAt(8) + "" + todayStr.charAt(9) + "일" + " 주매출");

        //월 events불러오기
        CollectEventsPerMonth(monthFormat.format(customCalendarView_accounting.calendar.getTime())
                , yearFormat.format(customCalendarView_accounting.calendar.getTime()));

        //오늘 날짜 && 시간 비교해서 매출 등록 안된 events 있으면 alert 해주는 메소드
        checkEvents();

        //이전달 버튼
//        customCalendarView_accounting.previousButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                customCalendarView.isSelectedAtFirst = false;
//                for (int i = 0; i < isSelected.length; i++) {
//                    isSelected[i] = false;
//                }
//                customCalendarView_accounting.calendar.add(Calendar.MONTH, -1);
//                customCalendarView_accounting.calendarPrev.add(Calendar.MONTH, -1); // 전 달의 매출 계산하기 위해서
//                customCalendarView_accounting.SetUpCalendar();
//
//                DecimalFormat formatter = new DecimalFormat("###,###");
//                monthTv.setText(monthFormat.format(customCalendarView_accounting.calendar.getTime()));
//                displayTvForMonth.setText(formatter.format(customCalendarView_accounting.monthlyPrice)
//                        + " (카드 " + formatter.format(customCalendarView_accounting.monthlyPrice_Card) + ")" + " 원");
//
//                //그 달의 events 불러오기
//                CollectEventsPerMonth(monthFormat.format(customCalendarView_accounting.calendar.getTime())
//                        , yearFormat.format(customCalendarView_accounting.calendar.getTime()));
//
//
//            }
//        });
//
//
//        //다음달 버튼
//        customCalendarView_accounting.nextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                customCalendarView. isSelectedAtFirst = false;
//
//                for (int i = 0; i < isSelected.length; i++) {
//                    isSelected[i] = false;
//                }
//                customCalendarView_accounting.calendar.add(Calendar.MONTH, 1);
//                customCalendarView_accounting.calendarPrev.add(Calendar.MONTH, 1);
//                customCalendarView_accounting.SetUpCalendar();
//
//                DecimalFormat formatter = new DecimalFormat("###,###");
//                monthTv.setText(monthFormat.format(customCalendarView_accounting.calendar.getTime()));
//                displayTvForMonth.setText(formatter.format(customCalendarView_accounting.monthlyPrice)
//                        + " (카드 " + formatter.format(customCalendarView_accounting.monthlyPrice_Card) + ")" + " 원");
//
//                //그 달의 events 불러오기
//                CollectEventsPerMonth(monthFormat.format(customCalendarView_accounting.calendar.getTime())
//                        , yearFormat.format(customCalendarView_accounting.calendar.getTime()));
//            }
//        });

        //======================================그리드뷰 아이템 클릭 리스너====================================

        customCalendarView_accounting.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isSelectedAtFirst) {//최초 클릭시
                    //포지션 저장
                    gridView_tempPosition = position;
                    gridView_prevPosition = position;

                    getDateData(); //날짜 데이터 받아오기

                    //선택된 날짜 표시
                    dailyTv.setText(month + day);

                    //선택된 날짜에 저장된 event 리스트 받아와서 저장
                    eventsArrayList = collectEventByDateForCardCash(date);
                    Collections.sort(eventsArrayList, comparator_events);

                    DecimalFormat formatter = new DecimalFormat("###,###");

                    //선택된 일자의 카드/현금 매출 표시하기
                    displayTvForDay.setText(formatter.format(sumPriceDaily) + " (카드 "
                            + formatter.format(sumPriceDaily_Card0) + ") 원");

                    //==================주매출 관련 =============
                    collectWeeklyEvents(date);
                    displayTvForWeek.setText(formatter.format(sumPriceWeekly) + " (카드 "
                            + formatter.format(sumPriceWeekly_Card) + ") 원");

                    view.setBackgroundColor(Color.DKGRAY);
                    isSelectedAtFirst = false;


                } else {//최초 클릭 X
                    if (position == gridView_tempPosition) {//똑같은애가 눌리면 아무일도 일어나지 않음
                    } else { //다른애가 눌리면
                        gridView_prevPosition = gridView_tempPosition;
                        gridView_tempPosition = position;

                        getDateData(); //날짜 데이터 받아오기

                        //선택된 날짜 표시
                        dailyTv.setText(month + day);

                        //선택된 날짜에 저장된 event 리스트 받아와서 저장
                        eventsArrayList = collectEventByDateForCardCash(date);
                        Collections.sort(eventsArrayList, comparator_events);

                        DecimalFormat formatter = new DecimalFormat("###,###");
                        //=================일매출 관련 =============
                        displayTvForDay.setText(formatter.format(sumPriceDaily) + " (카드 "
                                + formatter.format(sumPriceDaily_Card0) + ") 원");

                        //==================주매출 관련 =============
                        collectWeeklyEvents(date);
                        displayTvForWeek.setText(formatter.format(sumPriceWeekly) + " (카드 "
                                + formatter.format(sumPriceWeekly_Card) + ") 원");

                        view.setBackgroundColor(Color.DKGRAY);
                        customCalendarView_accounting.gridView.getChildAt(gridView_prevPosition).setBackgroundColor(Color.WHITE);

                    }

                }


            }
        });
    }

    //뒤로가기 버튼에 기능 추가
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
            case R.id.action_btn1:
                showToast("하하");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //=======================events 관련 메서드 =========================
    //월별 events 모으는 메서드
    private void CollectEventsPerMonth(String Month, String year) {
        eventsArrayListMonthly.clear();
        dbOpenHelper = new DBOpenHelper(getApplicationContext());
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
            eventsArrayListMonthly.add(events);

            Collections.sort(eventsArrayListMonthly, comparator_events_date);


        }
        Log.d("collectPricePerMonth", "contemp : " + eventsArrayListMonthly.size() + ":");
        cursor.close();
        dbOpenHelper.close();
    }

    //날짜별(Daily) 예약 수집 메소드
    protected ArrayList<Events> collectEventByDateForCardCash(String date) {
        ArrayList<Events> arrayList = new ArrayList<>();
        sumPriceDaily = 0;
        sumPriceDaily_Cash0 = 0;
        sumPriceDaily_Card0 = 0;
        dbOpenHelper = new DBOpenHelper(this);
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
            arrayList.add(events);

            if (Boolean.parseBoolean(complete)) {
                sumPriceDaily += price;
            }

            //월매출 계산 카드 vs 현금
            if (Boolean.parseBoolean(complete) && cardcash.startsWith("카")) {
                //카드
                sumPriceDaily_Card0 += price;
            } else if (Boolean.parseBoolean(complete) && cardcash.startsWith("현")){
                //현금
                sumPriceDaily_Cash0 += price;
            }

        }
        cursor.close();
        dbOpenHelper.close();

        return arrayList;
    }

    //주별(Weekly) 예약 수집 메소드
    protected void collectWeeklyEvents(String date) {
        sumPriceWeekly = 0;
        sumPriceWeekly_Cash = 0;
        sumPriceWeekly_Card = 0;
        Calendar cal = Calendar.getInstance();
        Date tmpDate = null;
        try {
            tmpDate = customCalendarView_accounting.eventDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(tmpDate);

        switch (gridView_tempPosition % 7) {
            case 0:
                weeklyTv.setText(month + day + " 주매출");
                for (int i = 0; i < 7; i++, cal.add(Calendar.DATE, 1)) {
                    String dateWeekly = customCalendarView_accounting.eventDateFormat.format(cal.getTime());
                    dbOpenHelper = new DBOpenHelper(this);
                    SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
                    Cursor cursor = dbOpenHelper.ReadEvents(dateWeekly, database);
                    while (cursor.moveToNext()) {

                        int price = cursor.getInt(cursor.getColumnIndex(DBStructure.PRICE));
                        String cardcash = cursor.getString(cursor.getColumnIndex(DBStructure.CARDCASH));
                        String complete = cursor.getString(cursor.getColumnIndex(DBStructure.COMPLETE));

                        if (Boolean.parseBoolean(complete)){
                            sumPriceWeekly += price;
                        }

                        if (Boolean.parseBoolean(complete) && cardcash.startsWith("카")) {
                            sumPriceWeekly_Card += price;
                        } else if (Boolean.parseBoolean(complete) && cardcash.startsWith("현")){
                            sumPriceWeekly_Cash += price;
                        }

                    }
                    cursor.close();
                    dbOpenHelper.close();
                }
                break;
            case 1:
                weeklyTv.setText(month + day + " 주매출");
                cal.add(Calendar.DATE, -1);
                for (int i = 0; i < 7; i++, cal.add(Calendar.DATE, 1)) {
                    String dateWeekly = customCalendarView_accounting.eventDateFormat.format(cal.getTime());
                    dbOpenHelper = new DBOpenHelper(this);
                    SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
                    Cursor cursor = dbOpenHelper.ReadEvents(dateWeekly, database);
                    while (cursor.moveToNext()) {
                        int price = cursor.getInt(cursor.getColumnIndex(DBStructure.PRICE));
                        String cardcash = cursor.getString(cursor.getColumnIndex(DBStructure.CARDCASH));
                        String complete = cursor.getString(cursor.getColumnIndex(DBStructure.COMPLETE));

                        if (Boolean.parseBoolean(complete)){
                            sumPriceWeekly += price;
                        }

                        if (Boolean.parseBoolean(complete) && cardcash.startsWith("카")) {
                            sumPriceWeekly_Card += price;
                        } else if (Boolean.parseBoolean(complete) && cardcash.startsWith("현")){
                            sumPriceWeekly_Cash += price;
                        }

                    }
                    cursor.close();
                    dbOpenHelper.close();
                }
                break;

            case 2:
                weeklyTv.setText(month + day + " 주매출");
                cal.add(Calendar.DATE, -2);
                for (int i = 0; i < 7; i++, cal.add(Calendar.DATE, 1)) {
                    String dateWeekly = customCalendarView_accounting.eventDateFormat.format(cal.getTime());
                    dbOpenHelper = new DBOpenHelper(this);
                    SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
                    Cursor cursor = dbOpenHelper.ReadEvents(dateWeekly, database);
                    while (cursor.moveToNext()) {
                        int price = cursor.getInt(cursor.getColumnIndex(DBStructure.PRICE));
                        String cardcash = cursor.getString(cursor.getColumnIndex(DBStructure.CARDCASH));
                        String complete = cursor.getString(cursor.getColumnIndex(DBStructure.COMPLETE));

                        if (Boolean.parseBoolean(complete)){
                            sumPriceWeekly += price;
                        }

                        if (Boolean.parseBoolean(complete) && cardcash.startsWith("카")) {
                            sumPriceWeekly_Card += price;
                        } else if (Boolean.parseBoolean(complete) && cardcash.startsWith("현")){
                            sumPriceWeekly_Cash += price;
                        }

                    }
                    cursor.close();
                    dbOpenHelper.close();
                }
                break;
            case 3:
                weeklyTv.setText(month + day + " 주매출");
                cal.add(Calendar.DATE, -3);
                for (int i = 0; i < 7; i++, cal.add(Calendar.DATE, 1)) {
                    String dateWeekly = customCalendarView_accounting.eventDateFormat.format(cal.getTime());
                    dbOpenHelper = new DBOpenHelper(this);
                    SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
                    Cursor cursor = dbOpenHelper.ReadEvents(dateWeekly, database);
                    while (cursor.moveToNext()) {
                        int price = cursor.getInt(cursor.getColumnIndex(DBStructure.PRICE));
                        String cardcash = cursor.getString(cursor.getColumnIndex(DBStructure.CARDCASH));
                        String complete = cursor.getString(cursor.getColumnIndex(DBStructure.COMPLETE));

                        if (Boolean.parseBoolean(complete)){
                            sumPriceWeekly += price;
                        }

                        if (Boolean.parseBoolean(complete) && cardcash.startsWith("카")) {
                            sumPriceWeekly_Card += price;
                        } else if (Boolean.parseBoolean(complete) && cardcash.startsWith("현")){
                            sumPriceWeekly_Cash += price;
                        }

                    }
                    cursor.close();
                    dbOpenHelper.close();
                }
                break;

            case 4:
                weeklyTv.setText(month + day + " 주매출");
                cal.add(Calendar.DATE, -4);
                for (int i = 0; i < 7; i++, cal.add(Calendar.DATE, 1)) {
                    String dateWeekly = customCalendarView_accounting.eventDateFormat.format(cal.getTime());
                    dbOpenHelper = new DBOpenHelper(this);
                    SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
                    Cursor cursor = dbOpenHelper.ReadEvents(dateWeekly, database);
                    while (cursor.moveToNext()) {
                        int price = cursor.getInt(cursor.getColumnIndex(DBStructure.PRICE));
                        String cardcash = cursor.getString(cursor.getColumnIndex(DBStructure.CARDCASH));
                        String complete = cursor.getString(cursor.getColumnIndex(DBStructure.COMPLETE));

                        if (Boolean.parseBoolean(complete)){
                            sumPriceWeekly += price;
                        }

                        if (Boolean.parseBoolean(complete) && cardcash.startsWith("카")) {
                            sumPriceWeekly_Card += price;
                        } else if (Boolean.parseBoolean(complete) && cardcash.startsWith("현")){
                            sumPriceWeekly_Cash += price;
                        }

                    }
                    cursor.close();
                    dbOpenHelper.close();
                }
                break;

            case 5:
                weeklyTv.setText(month + day + " 주매출");
                cal.add(Calendar.DATE, -5);
                for (int i = 0; i < 7; i++, cal.add(Calendar.DATE, 1)) {
                    String dateWeekly = customCalendarView_accounting.eventDateFormat.format(cal.getTime());
                    dbOpenHelper = new DBOpenHelper(this);
                    SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
                    Cursor cursor = dbOpenHelper.ReadEvents(dateWeekly, database);
                    while (cursor.moveToNext()) {
                        int price = cursor.getInt(cursor.getColumnIndex(DBStructure.PRICE));
                        String cardcash = cursor.getString(cursor.getColumnIndex(DBStructure.CARDCASH));
                        String complete = cursor.getString(cursor.getColumnIndex(DBStructure.COMPLETE));

                        if (Boolean.parseBoolean(complete)){
                            sumPriceWeekly += price;
                        }

                        if (Boolean.parseBoolean(complete) && cardcash.startsWith("카")) {
                            sumPriceWeekly_Card += price;
                        } else if (Boolean.parseBoolean(complete) && cardcash.startsWith("현")){
                            sumPriceWeekly_Cash += price;
                        }

                    }
                    cursor.close();
                    dbOpenHelper.close();
                }
                break;

            case 6:
                weeklyTv.setText(month + day + " 주매출");
                cal.add(Calendar.DATE, -6);
                for (int i = 0; i < 7; i++, cal.add(Calendar.DATE, 1)) {
                    String dateWeekly = customCalendarView_accounting.eventDateFormat.format(cal.getTime());
                    dbOpenHelper = new DBOpenHelper(this);
                    SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
                    Cursor cursor = dbOpenHelper.ReadEvents(dateWeekly, database);
                    while (cursor.moveToNext()) {
                        int price = cursor.getInt(cursor.getColumnIndex(DBStructure.PRICE));
                        String cardcash = cursor.getString(cursor.getColumnIndex(DBStructure.CARDCASH));
                        String complete = cursor.getString(cursor.getColumnIndex(DBStructure.COMPLETE));

                        if (Boolean.parseBoolean(complete)){
                            sumPriceWeekly += price;
                        }

                        if (Boolean.parseBoolean(complete) && cardcash.startsWith("카")) {
                            sumPriceWeekly_Card += price;
                        } else if (Boolean.parseBoolean(complete) && cardcash.startsWith("현")){
                            sumPriceWeekly_Cash += price;
                        }

                    }
                    cursor.close();
                    dbOpenHelper.close();
                }
                break;

        }


    }

    //오늘 날짜 && 시간 비교해서 매출 등록 안된 events 있으면 alert 해주는 메소드
    public void checkEvents() {
        ArrayList<Events> unprocessedEvents = new ArrayList<>();
        //1. 먼저 complete가 false인 events를 저장한다.
        for (int i = 0; i < eventsArrayListMonthly.size(); i++) {
            if (eventsArrayListMonthly.get(i).COMPLETE == false) {
                unprocessedEvents.add(eventsArrayListMonthly.get(i));
            }
        }

        if (unprocessedEvents.size() != 0) { //complete가 처리 안된 애들이 없으면 if문 작동X
            //2. 날짜비교해서 오늘 or 오늘보다 이전의 날짜애들만 모음
            finalFilteredEvents.clear();
            for (int i = 0; i < unprocessedEvents.size(); i++) {
                int compare = dateCompare(unprocessedEvents.get(i).DATE);
                if (compare > 0) { //날짜가 오늘부터 전이면 무조건 넣어
                    finalFilteredEvents.add(unprocessedEvents.get(i));
                } else if (compare == 0) { //날짜가 오늘이면 시간 비교 ㄱㄱ
                    if (timeCompare(unprocessedEvents.get(i).TIME) > 0) {
                        finalFilteredEvents.add(unprocessedEvents.get(i));
                    }
                }
            }

            if (finalFilteredEvents.size() != 0) {//complete가 처리 안됐더라도, 날짜가 내일이상이면 if문 작동 X
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Accounting.this);
                builder.setTitle("매출 등록");
                builder.setMessage("매출 등록이 되지 않은 예약이 " + finalFilteredEvents.size() + " 건 있습니다.\n등록하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent eachEventIntent = null;
                        for (int i = 0; i < finalFilteredEvents.size(); i++) {
                            eachEventIntent = new Intent(Activity_Accounting.this, EachEventActivity.class);
                            eachEventIntent.putExtra("name", finalFilteredEvents.get(i).EVENT);
                            Log.d("name", finalFilteredEvents.get(i).EVENT);
                            eachEventIntent.putExtra("number", finalFilteredEvents.get(i).NUMBER);
                            Log.d("name", finalFilteredEvents.get(i).NUMBER);
                            eachEventIntent.putExtra("date", finalFilteredEvents.get(i).DATE);
                            eachEventIntent.putExtra("time", finalFilteredEvents.get(i).TIME);
                            eachEventIntent.putExtra("retouch", finalFilteredEvents.get(i).RETOUCH);
                            eachEventIntent.putExtra("price", finalFilteredEvents.get(i).PRICE);
                            eachEventIntent.putExtra("memo", finalFilteredEvents.get(i).SHORTMEMO);
                            eachEventIntent.putExtra("complete", finalFilteredEvents.get(i).COMPLETE);
                            eachEventIntent.putExtra("noshow", finalFilteredEvents.get(i).NOSHOW);
                            eachEventIntent.putExtra("menu", finalFilteredEvents.get(i).MENU);
                            eachEventIntent.putExtra("cardcash", finalFilteredEvents.get(i).CARDCASH);
                            eachEventIntent.putExtra("materialMemo", finalFilteredEvents.get(i).MATERIALMEMO);
                            eachEventIntent.putExtra("surgeryMemo", finalFilteredEvents.get(i).CONTENTMEMO);

                            startActivityForResult(eachEventIntent, REQUEST_POPUP_ACTIVITY);

                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        }


                    }
                });

                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setCancelable(false);

                builder.show();
            }


        }

    }

    //날짜비교 메서드
    public int dateCompare(String inputDate) {
        //오늘 날짜 구하기
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date nowDate = new Date(now);
        String strNowDate = customCalendarView_accounting.eventDateFormat.format(nowDate);

        //inputData 포맷할 변수
        Date resultDate = null;
        Date resultDateNow = null;

        try {
            resultDate = customCalendarView_accounting.eventDateFormat.parse(inputDate);
            resultDateNow = customCalendarView_accounting.eventDateFormat.parse(strNowDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int compare = resultDateNow.compareTo(resultDate);

        return compare;
    }

    // 시간 비교 메서드
    public int timeCompare(String inputTime) {
        //지금 시간
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat hformat = new SimpleDateFormat("a K:mm", Locale.KOREA);
        String currentTimeStr = hformat.format(c.getTime());

        Date currentTimeDate = null;
        Date inputTimeDate = null;

        try {
            currentTimeDate = hformat.parse(currentTimeStr);
            inputTimeDate = hformat.parse(inputTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int compare = currentTimeDate.compareTo(inputTimeDate);

        return compare;
    }

    //=================================================== 캘린더 관련 ================================
    //캘린더 데이터 받아오기
    private void getDateData() {
        date = customCalendarView_accounting.eventDateFormat.format(customCalendarView_accounting.dates.get(gridView_tempPosition)); //날짜 받아오기
        Log.d("dateCompo", "First date : " + date);
        Log.d("dateCompo", "dates : " + customCalendarView_accounting.dates.get(gridView_tempPosition));

        month = customCalendarView_accounting.monthFormat.format(customCalendarView_accounting.dates.get(gridView_tempPosition)); //월
        Log.d("dateCompo", "First month : " + month);
        day = " " + date.charAt(8) + "" + date.charAt(9) + "일"; //day
        Log.d("dateCompo", "First day : " + day);
        year = customCalendarView_accounting.yearFormat.format(customCalendarView_accounting.dates.get(gridView_tempPosition));
        Log.d("dateCompo", "First day : " + year);
//        eventsDateTv.setText(month + " " + day + " 예약 리스트");
    }

    //=========================================onActivityForResult===================================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //제대로 처리되지 않은 경우에는 아무일도 일어나지 않음
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_POPUP_ACTIVITY:
                //정보값 받아오기
                boolean complete = data.getBooleanExtra("complete", false);
                boolean noShow = data.getBooleanExtra("noshow", false);
                String cardCash = data.getStringExtra("cardcash");
                String materialMemo = data.getStringExtra("materialMemo");
                String surgeryMemo = data.getStringExtra("surgeryMemo");

                String customer_name = data.getStringExtra("customer_name");
                String reserv_date = data.getStringExtra("reserv_date");
                String reserv_time = data.getStringExtra("reserv_time");

                //노쇼일 때
                if (complete && noShow) {
                    //complete와 noshow를 true로 바꾼다.
                    completeEvent(customer_name, reserv_date, reserv_time,
                            "true", "true", ""
                            , materialMemo, surgeryMemo);
                }

                //노쇼 아닌 시술완료
                else if (complete && !noShow) {
                    completeEvent(customer_name, reserv_date, reserv_time,
                            "true", "false", cardCash, materialMemo
                            , surgeryMemo);
                }

                customCalendarView_accounting.SetUpCalendar();
                //월 매출 자동 입력
                DecimalFormat formatter = new DecimalFormat("###,###");
                displayTvForMonth.setText(monthFormat.format(customCalendarView_accounting.calendar.getTime())
                        + " 매출 " + formatter.format(customCalendarView_accounting.monthlyPrice) + "원");
                //월 events불러오기
                CollectEventsPerMonth(monthFormat.format(customCalendarView_accounting.calendar.getTime())
                        , yearFormat.format(customCalendarView_accounting.calendar.getTime()));

                break;
        }

    }
}