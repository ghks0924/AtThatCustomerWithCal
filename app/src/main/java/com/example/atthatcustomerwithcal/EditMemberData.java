package com.example.atthatcustomerwithcal;

import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class EditMemberData extends Activity_Base {

    //화면요소
    TextView nameTv, numberTv, gradeTv, pointConetentTv, recommendTv;
    EditText nameEt, numberEt, pointEt;

    Button editBtn;

    //이전 액티비티에서 받아오는 정보 받을 객체
    String customer_name;
    String customer_number;

    //고객정보 받아오는 ArrayList
    ArrayList<Customers> eachCustomerData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member_data);

        // toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //뒤로가기 버튼, 디폴트값임
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //앱 제목 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getDataFromPrevActivity();
        loadEachCustomersData();

        //화면 요소 연결
        nameTv = findViewById(R.id.nameTv);
        nameEt = findViewById(R.id.nameEt);
        nameTv.setText(customer_name);
        nameEt.setText(customer_name);
        nameTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        nameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTv.setVisibility(View.GONE);
                nameEt.setVisibility(View.VISIBLE);
            }
        });

        numberTv = findViewById(R.id.numberTv);
        numberEt = findViewById(R.id.numberEt);
        numberTv.setText(customer_number);
        numberEt.setText(customer_number);
        numberTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        numberTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberTv.setVisibility(View.GONE);
                numberEt.setVisibility(View.VISIBLE);
            }
        });

//        gradeTv = findViewById(R.id.gradeBtn);
//        gradeTv.setText(eachCustomerData.get(0).GRADE);
//        gradeTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
//        gradeTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ShowAlertDialogWithListview();
//            }
//        });

        recommendTv = findViewById(R.id.recommendTv);

        pointConetentTv = findViewById(R.id.pointContentTv);
        pointEt = findViewById(R.id.pointEt);
        pointEt.setText(eachCustomerData.get(0).POINT);
        if (eachCustomerData.get(0).POINT.isEmpty()) {
            pointConetentTv.setText("0 원");
        } else {
            pointConetentTv.setText(eachCustomerData.get(0).POINT + " 원");
        }
        pointConetentTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        pointConetentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointConetentTv.setVisibility(View.GONE);
                pointEt.setVisibility(View.VISIBLE);
            }
        });

        editBtn = findViewById(R.id.editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!customer_name.equals(nameEt.getText().toString()) ||
                        !customer_number.equals(numberEt.getText().toString()) ||
                        !eachCustomerData.get(0).POINT.equals(pointEt.getText().toString())) {
                    updateCustomerData(customer_name,
                            customer_number,
                            nameEt.getText().toString(),
                            numberEt.getText().toString(),
                            "신규",
                            pointEt.getText().toString());

                    updateEventData(customer_name,
                            customer_number,
                            nameEt.getText().toString(),
                            numberEt.getText().toString());

                    showToast("회원정보가 수정되었습니다.");
                    Intent intent = new Intent();
                    intent.putExtra("customer_name", nameEt.getText().toString());
                    intent.putExtra("customer_number", numberEt.getText().toString());
//                    intent.putExtra("grade", gradeTv.getText().toString());
                    intent.putExtra("point", pointEt.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showToast("수정된 정보가 없습니다.");
                }

            }
        });

    }

    //이전 액티비티에서 받아오는 정보
    private void getDataFromPrevActivity() {
        Intent intent = getIntent();
        customer_name = intent.getStringExtra("customername");
        customer_number = intent.getStringExtra("number");
    }

    //회원정보 불러오는 메서드
    public ArrayList<Customers> loadEachCustomersData() {
        eachCustomerData.clear();

        Log.d("tag", ownername);

        ArrayList<Customers> arrayList = new ArrayList<>();
        dbOpenHelper_customer = new DBOpenHelper_Customer(EditMemberData.this);
        SQLiteDatabase database = dbOpenHelper_customer.getReadableDatabase();
        Cursor cursor = dbOpenHelper_customer.ReadEachMember(customer_name, customer_number, database);
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

        eachCustomerData.addAll(arrayList);

        return eachCustomerData;

    }

//    // 등급선택 다이얼로그
//    public void ShowAlertDialogWithListview() {
//        List<String> mAnimals = new ArrayList<String>();
//        mAnimals.add("VIP");
//        mAnimals.add("GOLD");
//        mAnimals.add("재방문");
//        mAnimals.add("신규");
//        mAnimals.add("Black");
//
//        //Create sequence of items
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




}