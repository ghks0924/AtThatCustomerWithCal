package com.example.atthatcustomerwithcal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.ErrorListener;

import static android.widget.Toast.LENGTH_SHORT;

public class Activity_Base extends AppCompatActivity implements Response.ErrorListener, Response.Listener<String> {
    ProgressBar loadingBar;
    Map<String, String> params = new HashMap<String, String>();

    //DBOpenHelper
    DBOpenHelper dbOpenHelper;
    DBOpenHelper_Customer dbOpenHelper_customer;
    DBOpenHelper_Menu dbOpenHelper_menu;
    DBOpenHelper_SmsTemplate dbOpenHelper_smsTemplate;

    //=================================  ArrayList  =================================

    //메뉴
    ArrayList<MenuList> menuArraylist = new ArrayList<>();
    //고객 리스트뷰
    ArrayList<Customers> customersArrayList = new ArrayList<>(); //고객을 위한 어레이 리스트뷰
    ArrayList<Customers> customersArrayList_copy = new ArrayList<>(); //검색을 위해서
    //문자템플릿
    ArrayList<SmsTemplates> smsTemplatesArrayList = new ArrayList<>();


    //자동로그인
    static boolean autoLogin = false;
    static String ownername = "12341234";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    //========================= ToolBar setting =====================


    //============================================기타 공통 메서드 =================================
    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void goToActivity( Class class2){
        Intent intent = new Intent(getApplicationContext(), class2);
        startActivity(intent);
    }

    //리스너없이 그냥
    public void requestPost(String url) {
        if (loadingBar != null) {
            loadingBar.setVisibility(View.VISIBLE);
        }
        RequestQueue stringRequest = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.POST, url,
                this, this) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError { //key, value로 넣음
                return params;
            }
        };
        myReq.setRetryPolicy(new DefaultRetryPolicy(3000, 0, 1f));
        stringRequest.add(myReq);

    }

    //리스너있을때
    public void requestPost(String url, Response.Listener<String> listener) {
        if (loadingBar != null) {
            loadingBar.setVisibility(View.VISIBLE);
        }
        RequestQueue stringRequest = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.POST, url,
                listener, this) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError { //key, value로 넣음
                return params;
            }
        };
        myReq.setRetryPolicy(new DefaultRetryPolicy(3000, 0, 1f));
        stringRequest.add(myReq);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("hey", "여기");
    }

    @Override
    public void onResponse(String response) {
        Log.d("hey", "여기");
    }

    //비밀번호 감싸는 메서드
    public String setEnc(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes());

            byte byteData[] = md.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            str = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static Comparator<Customers> comparator_customers = new Comparator<Customers>() {
        @Override
        public int compare(Customers o1, Customers o2) {
            return o1.CUSTOMER_NAME.compareTo(o2.CUSTOMER_NAME);
        }
    };

    public static Comparator<Events> comparator_events = new Comparator<Events>() {

        @Override
        public int compare(Events o1, Events o2) {
            return o1.TIME.compareTo(o2.TIME);
        }

    };

    public static Comparator<Events> comparator_events_date = new Comparator<Events>() {

        @Override
        public int compare(Events o1, Events o2) {
            return o1.DATE.compareTo(o2.DATE);
        }

    };

    //====================== Event 관련 메소드 =========================

    //시술 완료 후 가격 업데이트하는 메소드
    protected void updateEventForPrice(String event, String date, String time, int price) {
        dbOpenHelper = new DBOpenHelper(getApplicationContext());
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.updateEventForPrice(event, date, time, price, database);
        dbOpenHelper.close();
    }

    //시술 완료 처리 메서드
    protected void completeEvent(String event, String date, String time, String complete, String noshow,
                                 String cardCash, String materialMemo, String contentMemo) {
        dbOpenHelper = new DBOpenHelper(getApplicationContext());
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.completeEvent(event, date, time, complete, noshow
                , cardCash, materialMemo, contentMemo, database);
        dbOpenHelper.close();
    }


    //예약 저장 메소드
    protected void saveEvent(String event, String time, String date, String month, String year, String retouch, int price,
                             boolean complete, String shortmemo, String number, boolean noshow, String menu, String cardcash
            , String materialmemo, String contentmemo, String crtdate) {
        dbOpenHelper = new DBOpenHelper(getApplicationContext());
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.SaveEvent(event, time, date, month, year, retouch, price, complete, shortmemo, number, noshow
                , menu, cardcash, materialmemo, contentmemo, crtdate,
                database);
        dbOpenHelper.close();
        Toast.makeText(getApplicationContext(), "예약이 저장되었습니다.", LENGTH_SHORT).show();
    }

    //날짜별 예약 수집 메소드
    protected ArrayList<Events> collectEventByDate(String date) {
        ArrayList<Events> arrayList = new ArrayList<>();
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

        }
        cursor.close();
        dbOpenHelper.close();

        return arrayList;
    }

    //고객별 예약 수집 메소드
    protected ArrayList<Events> collectEventByName(String name, String number0) {
        ArrayList<Events> arrayList = new ArrayList<>();
        dbOpenHelper = new DBOpenHelper(this);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEvents_name(name, number0, database);
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

        }
        cursor.close();
        dbOpenHelper.close();

        return arrayList;
    }

    //고객정보수정에 따른 이벤트 수정 w.이름, 전화번호로 찾기
    public void updateEventData(String beforeName, String beforeNumber, String updateName, String updateNumber) {
        dbOpenHelper = new DBOpenHelper(getApplicationContext());
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.UpdateEventData(beforeName, beforeNumber, updateName, updateNumber, database);
        dbOpenHelper.close();
    }

    //고객정보수정에 따른 이벤트 수정 w.이벤트 생성일자로 찾기
    public void updateEventData2(String crtdate, String updateNm, String updateNbr){
        dbOpenHelper = new DBOpenHelper(getApplicationContext());
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.UpdateEventData2(crtdate, updateNm, updateNbr, database);
        dbOpenHelper.close();
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

    //고객정보 수정
    public void updateCustomerData(String beforeName, String beforeNumber, String updateName, String updateNumber, String grade, String point) {
        dbOpenHelper_customer = new DBOpenHelper_Customer(getApplicationContext());
        SQLiteDatabase database = dbOpenHelper_customer.getWritableDatabase();
        dbOpenHelper_customer.UpdateCustomerData(beforeName, beforeNumber, updateName, updateNumber, grade, point, database);
        dbOpenHelper_customer.close();
    }



    //customers 정렬을 위한 메소드
    public static Comparator<Customers> comparator_customers_date = new Comparator<Customers>() {

        @Override
        public int compare(Customers o1, Customers o2) {
            return o2.SAVEDATE.compareTo(o1.SAVEDATE);
        }

    };

    //노쇼 카운트 메서드

    public void updateNoShow(String name, String number, int noshow) {
        dbOpenHelper_customer = new DBOpenHelper_Customer(getApplicationContext());
        SQLiteDatabase database = dbOpenHelper_customer.getWritableDatabase();
        dbOpenHelper_customer.updateNoShow(name, number, noshow, database);
        dbOpenHelper_customer.close();
    }

    public void saveCustomer(String ownername, String customername, String number, String grade, String recommend, String point, String recentvisit, String memo, String savedate, int noshowcount) {
        dbOpenHelper_customer = new DBOpenHelper_Customer(this);
        SQLiteDatabase database = dbOpenHelper_customer.getWritableDatabase();
        dbOpenHelper_customer.SaveCustomer(ownername, customername, number, grade, recommend, point, recentvisit, memo, savedate
                , noshowcount, database);
        dbOpenHelper_customer.close();

        Toast.makeText(this, "고객이 추가되었습니다.", Toast.LENGTH_SHORT).show();
    }
    //===================== Menu 관련 메소드 ===========================

    //메뉴 불러오는 메소드
    public ArrayList<MenuList> loadMenusData() {
        menuArraylist.clear();

        ArrayList<MenuList> arrayList = new ArrayList<>();
        dbOpenHelper_menu = new DBOpenHelper_Menu(getApplicationContext());
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

    //시술완료시 메뉴 저장하는 메소드
    public void updateEventForMenu(String event, String date, String time, String menu) {
        dbOpenHelper = new DBOpenHelper(getApplicationContext());
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.updateEventForMenu(event, date, time, menu, database);
        dbOpenHelper.close();
    }


    //==============================================================

    public static boolean isValidCellPhoneNumber(String cellphoneNumber) {

        boolean returnValue = false;
        try {
            String regex = "^\\s*(010|011|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(cellphoneNumber);
            if (m.matches()) {
                returnValue = true;
            }

            if (returnValue && cellphoneNumber != null
                    && cellphoneNumber.length() > 0
                    && cellphoneNumber.startsWith("010")) {
                cellphoneNumber = cellphoneNumber.replace("-", "");
                if (cellphoneNumber.length() != 11) {
                    returnValue = false;
                }
            }
            return returnValue;
        } catch (Exception e) {
            return false;
        }

    }

    //========================================SMS TEMPLATE 문자 템플릿 =========================================
    public ArrayList<SmsTemplates> ReadSmsTemplates() {
        ArrayList<SmsTemplates> arrayList = new ArrayList<>();
        dbOpenHelper_smsTemplate = new DBOpenHelper_SmsTemplate(getApplicationContext());
        SQLiteDatabase database = dbOpenHelper_smsTemplate.getReadableDatabase();
        Cursor cursor = dbOpenHelper_smsTemplate.ReadTemplates(database);
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(DBStructure_SmsTemplate.SMSTITLE));
            String content = cursor.getString(cursor.getColumnIndex(DBStructure_SmsTemplate.SMSCONTENT));
            SmsTemplates smsTemplate = new SmsTemplates(title, content);
            arrayList.add(smsTemplate);

        }
        cursor.close();
        dbOpenHelper_smsTemplate.close();

        return arrayList;
    }

    //======================================메모 / MEMO 관련 ==============================================
    public void SaveMemo(String name, String number, String memo) {
        dbOpenHelper_customer = new DBOpenHelper_Customer(getApplicationContext());
        SQLiteDatabase database = dbOpenHelper_customer.getWritableDatabase();
        dbOpenHelper_customer.UpdateMemo(name, number, memo, database);
        dbOpenHelper_customer.close();
    }

    //=============================================키보드 관련 메서드=======================================
    public void KeyboardDown(){
        InputMethodManager immhide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void KeyboardUp(){
        // 키보드 띄우기
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}