package com.example.atthatcustomerwithcal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.example.atthatcustomerwithcal.Activity_Create_Customer_FromBook.comparator;

public class MemberList extends Activity_Base implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    //화면 구성 요소 생성
    Button backIv, addIv;
    TextView titleTv;

    EditText searchEt;
    ListView listView;

    MyAdapter adapter;

    boolean customersAbcSort = true;
    boolean customersDateSort;

    //회원 dbOpenHelper
    final String OWNER_NAME = "OWNER";

    ArrayList<Customers> customersList = new ArrayList<>();
    ArrayList<Customers> customersList_copy = new ArrayList<>();

    //고객 지우기위한 요소
    String customername;
    String customernumber;

    //연락처 가져오기
    final public int REQUEST_CONTACTS_BOOK = 101; //연락처에서 가져오기
    final public int REQUEST_CONTACTS_MANUAL = 102; //직접 입력하기
    //샵 주인 이름
    final public String OWNERNAME = "OWNER";

    Context context;
    ContactUtil contactUtil;

    //어느 인텐트에서부터 넘어왔는지 체크하는 변수
    String fromWhere;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        //어느 인텐트에서부터 넘어왔는지 체크
        Intent getIntent = getIntent();
        fromWhere = getIntent.getStringExtra("toMemberList");



        //찾아서 연결하고
        listView = findViewById(R.id.listview);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //앱 최초 실행 여부 묻는 이력 가져오기
        SharedPreferences pre1 = getSharedPreferences("MyData", MODE_PRIVATE);
        boolean isOpenAtFirst = pre1.getBoolean("isAppOpenAtFirst", true);
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

            SharedPreferences.Editor editor = pre1.edit();
            editor.clear();
            editor.putBoolean("isAppOpenAtFirst", false);
            editor.commit();

        }


        //정렬 데이터 불러오기
        SharedPreferences pre4 = getSharedPreferences("MyNew", MODE_PRIVATE);
        customersDateSort = pre4.getBoolean("wayToSort", false);
        Log.d("wayToSort", customersDateSort + "?");

        //정렬값에 따라 보여지는 리스트 순서 달라지기
        if (!customersDateSort) {
            customersList.clear();
            customersList = loadCustomersData_abc();
            Collections.sort(customersList, comparator_customers_abc);

            listView.setAdapter(adapter);
        } else {
            loadCustomersData_date();

        }


        context = this;
        contactUtil = new ContactUtil(this);




        searchEt = findViewById(R.id.searchEt);
        //리스너 붙여주고
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
//        searchIv.setOnClickListener(this);
//        backIv.setOnClickListener(this);
//        addIv.setOnClickListener(this);


        //어댑터연결하고 서버에서 리스트 불러오기
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
                adapter.filter(searchText);
//                search(searchText);
            }
        });


    }//onCreate



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
////            case R.id.backIv:
//                finish();
//                break;

//            case R.id.searchIv:
//                if (customersDateSort) {
//                    customersAbcSort = true;
//                    customersDateSort = false;
//
//                    loadCustomersData_abc();
//                    listView.setAdapter(adapter);
//                    searchIv.setText("저장순");
//
//                    SharedPreferences pre3 = getSharedPreferences("MyNew", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = pre3.edit();
//                    editor.putBoolean("wayToSort", customersDateSort);
//
//                    editor.commit();
//
//                    Log.d("wayToSort", "저장순으로 보여질때" + pre3.getBoolean("wayToSort", false));
//
//
//                } else {
//                    customersAbcSort = false;
//                    customersDateSort = true;
//                    loadCustomersData_date();
//                    listView.setAdapter(adapter);
//                    searchIv.setText("가나다순");
//
//                    SharedPreferences pre3 = getSharedPreferences("MyNew", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = pre3.edit();
//                    editor.putBoolean("wayToSort", customersDateSort);
//
//                    editor.commit();
//
//                    Log.d("wayToSort", "가나순으로 보여질때" + pre3.getBoolean("wayToSort", false));
//                }
//
//
//                break;


//            case R.id.addIv:
//                AlertDialog.Builder ab = new AlertDialog.Builder(this);
//                ab.setTitle("회원 등록");
//                ab.setIcon(R.mipmap.ic_launcher);
//                ab.setMessage("회원등록 방법을 선택해 주세요.");
//                ab.setPositiveButton("카카오친구 등록하기", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent addIntent = new Intent(getApplicationContext(), AddMember_kakao.class);
//                        startActivityForResult(addIntent,101);
//                    }
//                });
//                ab.setNegativeButton("핸드폰 번호로 등록하기", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent addIntent_num = new Intent(getApplicationContext(), AddMember_num.class);
//                        startActivityForResult(addIntent_num,102);
//                    }
//                });
//                ab.setCancelable(true);
//                ab.show();


//                break;
        }
    }

    //연락처 받아오는 임시 변수
    String receiveName;
    String receivePhone;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CONTACTS_MANUAL) {

        }

        if (requestCode == REQUEST_CONTACTS_BOOK) {
            Cursor cursor = getContentResolver().query(data.getData(),
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER},
                    null, null, null);

            cursor.moveToFirst();
            //이름획득
            receiveName = cursor.getString(0);
            //전화번호 획득
            receivePhone = cursor.getString(1);
            cursor.close();

            addCustomer(receiveName, receivePhone);


        }

    }

    int position;//포지션 넘버

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (fromWhere.contains("each_event")){
            Intent intent = new Intent();
            intent.putExtra("customer_name", customersList.get(position).CUSTOMER_NAME);
            intent.putExtra("customer_number", customersList.get(position).NUMBER);
            setResult(RESULT_OK, intent);
            finish();
        } else if(fromWhere.contains("main")){
            Intent intent = new Intent(this, EachMember.class);
            intent.putExtra("customername", customersList.get(position).CUSTOMER_NAME);
            intent.putExtra("number", customersList.get(position).NUMBER);
            intent.putExtra("noshow", customersList.get(position).NOSHOWCOUNT);
            startActivity(intent);
        }

    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
        //삭제하려고 파라미터 저장
        customername = customersList.get(position).CUSTOMER_NAME;
        customernumber = customersList.get(position).NUMBER;

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("회원 삭제");
        ab.setIcon(R.mipmap.ic_launcher);
        ab.setMessage(customersList.get(position).CUSTOMER_NAME + " 회원을 삭제하시겠습니까? ");
        ab.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteMem();
//                updateData();
            }
        });
        ab.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ab.setCancelable(true);
        ab.show();

        //디폴트는 false인데 이러면 롱 실행시 그냥 ItemClick이 실행
        //그래서 true로 바꿈
        return true;

    }

    public void addCustomer(String receiveName, String receivePhone) {
        Intent intent2 = new Intent(this, Activity_Create_Customer.class);
        intent2.putExtra("name", receiveName);
        intent2.putExtra("phone", receivePhone);
        startActivity(intent2);
    }

    public void deleteMem() {

        dbOpenHelper_customer = new DBOpenHelper_Customer(this);
        SQLiteDatabase database = dbOpenHelper_customer.getWritableDatabase();
        dbOpenHelper_customer.deleteCustomer(customername, customernumber, database);
        dbOpenHelper_customer.close();

        loadCustomersData_abc();
        adapter.notifyDataSetChanged();

        searchEt.setText("");

    }

    //멤버 리스트를 보여주기 위한 커스텀뷰 홀더 셋팅
    class RowHolder {

        TextView nameTvHolder;
        TextView gradeTvHolder;
        TextView numberTvHolder;

    }

    class MyAdapter extends ArrayAdapter {
        LayoutInflater lnf;

        public MyAdapter(android.app.Activity context) {
            super(context, R.layout.single_row_memberlsit, customersList);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return customersList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return customersList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            RowHolder viewHolder;
            if (convertView == null) {
                convertView = lnf.inflate(R.layout.single_row_memberlsit, parent, false);
                viewHolder = new RowHolder();

                viewHolder.nameTvHolder = convertView.findViewById(R.id.name_tv);
//                viewHolder.gradeTvHolder = convertView.findViewById(R.id.grade_tv);
//                viewHolder.numberTvHolder = convertView.findViewById(R.id.number_tv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RowHolder) convertView.getTag();
            }

            String phoneNum = customersList.get(position).NUMBER.substring(0,3) + "-" + customersList.get(position).NUMBER.substring(3,7)+
                    "-" + customersList.get(position).NUMBER.substring(7);


            viewHolder.nameTvHolder.setText(customersList.get(position).CUSTOMER_NAME + "\n"
                    + phoneNum);


//            viewHolder.gradeTvHolder.setText(customersList.get(position).GRADE);
//            viewHolder.numberTvHolder.setText(customersList.get(position).NUMBER);


            return convertView;
        }

        // Filter Class
        public void filter(String charText) {
            charText = charText.toLowerCase();
            customersList.clear();
            if (charText.length() == 0) {
                customersList.addAll(customersList_copy);
            } else {
                for (int i = 0; i < customersList_copy.size(); i++) {
                    if (customersList_copy.get(i).CUSTOMER_NAME.toLowerCase().contains(charText) || customersList_copy.get(i).NUMBER.toLowerCase().contains(charText)) {
                        //검색된 데이터를 리스트에 추가한다.
                        //list.add(arraylist.get(i));
                        customersList.add(customersList_copy.get(i));
                    }
                }
            }
            notifyDataSetChanged();
        }

    }

    //회원정보 불러오는 메서드_가나다순
    public ArrayList<Customers> loadCustomersData_abc() {
        customersList.clear();
        customersList_copy.clear(); //검색용을 위한 복제본

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

        customersList.addAll(arrayList);
        customersList_copy.addAll(arrayList);

        Collections.sort(customersList, comparator_customers_abc);
        Collections.sort(customersList_copy, comparator_customers_abc);


        return customersList;

    }

    //회원정보 불러오는 메서드_가나다순
    public ArrayList<Customers> loadCustomersData_date() {
        customersList.clear();
        customersList_copy.clear(); //검색용을 위한 복제본

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

        customersList.addAll(arrayList);
        customersList_copy.addAll(arrayList);

        Collections.sort(customersList, comparator_customers_date);
        Collections.sort(customersList_copy, comparator_customers_date);


        return customersList;

    }

    //customers 가나다순 정렬을 위한 메소드
    public static Comparator<Customers> comparator_customers_abc = new Comparator<Customers>() {

        @Override
        public int compare(Customers o1, Customers o2) {
            return o1.CUSTOMER_NAME.compareTo(o2.CUSTOMER_NAME);
        }


    };

    //customers 정렬을 위한 메소드
    public static Comparator<Customers> comparator_customers_date = new Comparator<Customers>() {

        @Override
        public int compare(Customers o1, Customers o2) {
            return o2.SAVEDATE.compareTo(o1.SAVEDATE);
        }

    };

//    public void updateData() {
//        arrAll.clear();//다시 불릴때 데이터가 자꾸 중첩돼서 호출될때마다 비워주기
//        params.clear();
//    }

    @Override
    protected void onResume() {
        if (customersDateSort) {
            loadCustomersData_date();
        } else {
            loadCustomersData_abc();
        }
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    //화면 최초 실행시 동기화 여부 물어서 저장하기
    public void saveCustomersFromBookAtFirst() {
        ArrayList<Contact> tempArrayList = getContacts();
        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(date);

        for (int i = 0; i < tempArrayList.size(); i++) {
            saveCustomer(ownername, tempArrayList.get(i).name, tempArrayList.get(i).phoneNumber, "신규", "", "", "", "", formatDate, 0);
            finish();
        }
        showToast("선택하신 연락처가 저장되었습니다.");
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

            Log.d("test5", "size of tempArrayList" + String.valueOf(tempArrayList.size()));
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

    public void saveCustomer(String ownername, String customername, String number, String grade, String recommend, String point, String recentvisit, String memo, String savedate, int noshowcount) {
        dbOpenHelper_customer = new DBOpenHelper_Customer(this);
        SQLiteDatabase database = dbOpenHelper_customer.getWritableDatabase();
        dbOpenHelper_customer.SaveCustomer(ownername, customername, number, grade, recommend, point, recentvisit, memo, savedate, noshowcount, database);
        dbOpenHelper_customer.close();
    }

    @Override
    public void onBackPressed() {
        Intent intent3 = new Intent();
        intent3.putExtra("OK", "OK");
        setResult(RESULT_OK, intent3);
        finish();
    }
}