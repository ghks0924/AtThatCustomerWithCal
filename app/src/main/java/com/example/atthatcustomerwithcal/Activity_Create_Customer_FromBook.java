package com.example.atthatcustomerwithcal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Activity_Create_Customer_FromBook extends Activity_Base {

    TextView checkboxAll;
    EditText searchEt;
    ListView listView;

    ContactUtil contactUtil;
    Context context;

    ArrayList<Customers> savedCustomersList;
    ArrayList<Contact> contactArrayList;
    ArrayList<Contact> contactArrayList_copy;
    boolean[] isChecked;
    boolean isCheckedAll = false;

    MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__customer_from_book);

        // toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //뒤로가기 버튼, 디폴트값임
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //앱 제목 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        context = this;
        contactUtil = new ContactUtil(this);

        //화면 요소
        checkboxAll = findViewById(R.id.checkboxAll);
        searchEt = findViewById(R.id.searchEt);
        listView = findViewById(R.id.listview);

        //고객데이터 세팅
        loadCustomersData(); //현재 앱에 저장된 고객명단을 불러오기
        contactArrayList = getContacts(); //내 휴대폰의 연락처 가져오기
        contactArrayList_copy = getContacts(); //검색 기능을 위해 위 리스트의 복사본에도 똑같이
        isChecked = new boolean[contactArrayList.size()];


        //앱 최초 실행 여부 묻는 이력 가져오기
        SharedPreferences pre = getSharedPreferences("MyData", MODE_PRIVATE);
        boolean isOpenAtFirst = pre.getBoolean("isAppOpenAtFirst", true);
        Log.d("isOpenAtFirst", isOpenAtFirst + "");

        if (isOpenAtFirst) { //이 화면이 처음 열리는 거니 ??
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setTitle("연락처 동기화");
            ab.setIcon(R.mipmap.ic_launcher);
            ab.setMessage("스마트폰에 저장된 연락처를 동기화(저장)하시겠습니까??");
            ab.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveCustomersFromBookAtFirst();

                }
            });
            ab.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            ab.setCancelable(false);
            ab.show();

            SharedPreferences.Editor editor = pre.edit();
            editor.clear();
            editor.putBoolean("isAppOpenAtFirst", false);
            editor.commit();

        }

        //아닌 경우
        checkboxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCheckedAll) {
                    isCheckedAll = true;
                    for (int i = 0; i < isChecked.length; i++) {
                        isChecked[i] = true;
                    }
                    checkboxAll.setBackgroundColor(Color.GREEN);
                    adapter.isCheckedAll = true;
                    adapter.notifyDataSetChanged();

                } else {
                    isCheckedAll = false;
                    for (int i = 0; i < isChecked.length; i++) {
                        isChecked[i] = false;
                    }
                    checkboxAll.setBackgroundColor(Color.GRAY);
                    adapter.isCheckedAll = false;
                    adapter.notifyDataSetChanged();
                }
            }
        });


        adapter = new MyAdapter(this);
        listView.setAdapter(adapter);

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //초기색 현재 : #dfdfdf
                TextView checkBox = parent.getChildAt(position).findViewById(R.id.checkbox);
                if (!isChecked[position]) {
                    isChecked[position] = true;
                    checkBox.setBackgroundColor(Color.GREEN);
                } else {
                    isChecked[position] = false;
                    checkBox.setBackgroundColor(Color.GRAY
                    );
                }
            }
        });

    } // End of onCreate

    //뒤로가기 버튼에 기능 추가
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
            case R.id.action_btn1:
                boolean isDataChecked = false;
                for (int i = 0; i < isChecked.length; i++) {
                    if (isChecked[i]) {
                        isDataChecked = true;
                    }
                    break;
                }

                if (!isDataChecked) {
                    showToast("연락처를 선택해주세요");
                } else {
                    saveCustomersFromBook();
                }

        }
        return super.onOptionsItemSelected(item);
    }

    //toolbar에 버튼 추가하려고
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menuformemlist, menu);
        return true;
    }

    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> arrayList = contactUtil.getContactList(); //일단 받아와서

        if (arrayList.size() == 0) { //연락처에 아무번호도 저장되어있지 않은경우를 위한 로직
            showToast("핸드폰의 연락처가 존재하지 않습니다.");
//            finish(); 잠깐 주석처리
            return arrayList;

        } else {
            Collections.sort(arrayList, comparator); //정렬
            for (int i = 0; i < arrayList.size(); i++) {
                Log.d("test1", arrayList.get(i).getName());
                Log.d("test2", arrayList.get(i).getPhoneNumber());
                Log.d("test3", String.valueOf(arrayList.get(i).getId()));
            }
            Log.d("test4", arrayList.size() + "");


            //먼저 핸드폰 번호가 01로 시작하는게 아니면 가져오지말기
            ArrayList<Contact> tempArrayList = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).phoneNumber.startsWith("01")) {
                    tempArrayList.add(arrayList.get(i));
                }
            }

            Log.d("test5", "size of tempArrayList" + tempArrayList.size());
            Collections.sort(tempArrayList, comparator); //한번더 정렬

            //중복 제거 해주기
            ArrayList<Contact> refinedArrayList = new ArrayList<>();
            refinedArrayList.add(tempArrayList.get(0)); //일단 처음은 넣어주고

            for (int i = 0, j = 1; j < tempArrayList.size(); i++, j++) {
                if (refinedArrayList.get(i).phoneNumber.equals(tempArrayList.get(j).phoneNumber)) {
                    i--;
                } else {
                    refinedArrayList.add(tempArrayList.get(j));
                }
            }
            //test
            Log.d("test5", String.valueOf(refinedArrayList.size()));

            return refinedArrayList;
        }

    }


    //일반 정렬
    public static Comparator<Contact> comparator = new Comparator<Contact>() {

        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.name.compareTo(o2.name);
        }

    };

    class RowHolder {
        TextView nameTvHolder;
        TextView checkBoxHolder;
    }

    class MyAdapter extends ArrayAdapter {
        boolean isCheckedAll = false;
        LayoutInflater lnf;

        public MyAdapter(android.app.Activity context) {
            super(context, R.layout.single_row_forcontactsbook, contactArrayList);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return contactArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return contactArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            RowHolder viewHolder;
            if (convertView == null) {
                convertView = lnf.inflate(R.layout.single_row_forcontactsbook, parent, false);
                viewHolder = new RowHolder();

                viewHolder.nameTvHolder = convertView.findViewById(R.id.nameTv);
                viewHolder.checkBoxHolder = convertView.findViewById(R.id.checkbox);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RowHolder) convertView.getTag();
            }


            viewHolder.nameTvHolder.setText(contactArrayList.get(position).name + "  " + contactArrayList.get(position).phoneNumber);

            if (isCheckedAll) {
                viewHolder.checkBoxHolder.setBackgroundColor(Color.GREEN);
            } else {
                viewHolder.checkBoxHolder.setBackgroundColor(Color.GRAY);
            }

            return convertView;
        }
    }

    //검색 기능 메소드 생성
    public void search(String charText) {
        //문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        contactArrayList.clear(); // 원본 비워! 왜냐면 Adapter가 원본이랑 연결되어 있거든
        //문자입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            contactArrayList.addAll(contactArrayList_copy);
        } else { //문자입력시
            for (int i = 0; i < contactArrayList_copy.size(); i++) { //리소스의 모든 데이터를 검색한다.
                if (contactArrayList_copy.get(i).name.toLowerCase().contains(charText)) {
                    //검색된 데이터를 리스트에 추가한다.
                    //list.add(arraylist.get(i));
                    contactArrayList.add(contactArrayList_copy.get(i));
                }
            }
        }
        //리스트 데이터가 변경되었으므로 어댑터 갱신

        listView.setAdapter(adapter);
    }

    //===========아래부터 복수 고객 저장하기==========

    //1. 일단 리스튜에서 선택된 애들만 모은다.
    public ArrayList<Contact> getNewCustomers() {
        ArrayList<Contact> tempArrayList = new ArrayList<>();

        for (int i = 0; i < isChecked.length; i++) {
            if (isChecked[i]) { //리스트뷰에서 체크된 애만 저장해
                tempArrayList.add(contactArrayList.get(i));
            }
        }
        return tempArrayList;
    }

    //2. 선택된 애들중에 중복된 애들이 있는지 체크한다.
    public boolean isExisted() {
        boolean isExistedAlready = false;
        ArrayList<Contact> tempArrayList = getNewCustomers();

        for (int i = 0; i < tempArrayList.size(); i++) {
            for (int j = 0; j < savedCustomersList.size(); j++) {
                if (tempArrayList.get(i).phoneNumber.replace("-", "").equals(savedCustomersList.get(j).NUMBER)) {
                    isExistedAlready = true;
                    break;
                } else {

                }
            }

            if (isExistedAlready) {
                break;
            } else {

            }
        }

        if (isExistedAlready) {
            return true;
        } else {
            return false;
        }
    }

    public void saveCustomersFromBook() {
        ArrayList<Contact> tempArrayList = getNewCustomers();
        if (isExisted()) {
            showToast("이미 저장된 연락처가 존재합니다.");
        } else {
            // 현재시간을 msec 으로 구한다.
            long now = System.currentTimeMillis();
            // 현재시간을 date 변수에 저장한다.
            Date date = new Date(now);
            // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
            // nowDate 변수에 값을 저장한다.
            String formatDate = sdfNow.format(date);

            for (int i = 0; i < tempArrayList.size(); i++) {
                String phoneNum = tempArrayList.get(i).phoneNumber.replace("-", "");
                saveCustomer(ownername, tempArrayList.get(i).name, phoneNum, "신규", "", "", "", "", formatDate, 0);

                finish();
            }
        }
        showToast("선택하신 연락처가 저장되었습니다.");
    }

    //화면 최초 실행시 동기화 여부 물어서 저장하기
    public void saveCustomersFromBookAtFirst() {
        ArrayList<Contact> tempArrayList = getContacts();
        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(date);


        for (int i = 0; i < tempArrayList.size(); i++) {
            saveCustomer(ownername, tempArrayList.get(i).name, tempArrayList.get(i).phoneNumber, "신규", "", "", "", "", formatDate, 0);
            finish();
        }
        showToast("선택하신 연락처가 저장되었습니다.");
    }

    public void saveCustomer(String ownername, String customername, String number, String grade, String recommend, String point, String recentvisit, String memo, String savedate, int noshowcount) {
        dbOpenHelper_customer = new DBOpenHelper_Customer(this);
        SQLiteDatabase database = dbOpenHelper_customer.getWritableDatabase();
        dbOpenHelper_customer.SaveCustomer(ownername, customername, number, grade, recommend, point, recentvisit, memo, savedate, noshowcount, database);
        dbOpenHelper_customer.close();
    }

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