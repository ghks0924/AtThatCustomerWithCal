package com.example.atthatcustomerwithcal;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Activity_Create_Customer extends Activity_Base {

    final String FIRESTORE_TAG = "FIRESTORE_TAG";

    FirebaseFirestore mDB;
    FirebaseAuth mAuth;

    //현재 고객리스트 가져오기
    ArrayList<Customers> savedCustomersList;


    //화면 요소 생성
    MaterialButton completeBtn;
    EditText nameEt, phNmEt, memoEt;
    ImageView backIv;

    //고객 저장하기
    String grade;
    String recommend;

    //이름으로 중복체크하기
    boolean canUse;

    //Each Event Activity에서 넘어오는 정보
    String fromWhere;

    String origin;

    //INPUT_DATA
    String input_cusName;
    String input_cusNumber;
    String input_cusMemo;

    boolean isDuplicated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__customer);

        //Firebase 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseFirestore.getInstance();


        //fromList에서 넘어왔다면 정보 받아오기
        Intent intent2 = getIntent();

        //check origin
        origin = intent2.getStringExtra("origin");
        //from MemberList.class
        String name = intent2.getStringExtra("name");
        String phone = intent2.getStringExtra("phone");
        //from eachEventActivity
        fromWhere = intent2.getStringExtra("toMemberList");

        //화면 요소 생성
        nameEt = findViewById(R.id.nameEt);
        phNmEt = findViewById(R.id.numberEt);
        phNmEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        memoEt = findViewById(R.id.memoEt);
        completeBtn = findViewById(R.id.completeBtn);
//        completeBtn.setOnClickListener(this);
        backIv = findViewById(R.id.backIv);
//        backIv.setOnClickListener(this);

        nameEt.setText(name);
        phNmEt.setText(phone);

//        loadCustomersData(); //현재 회원 정보 받아오기


        nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                canUse = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phNmEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                canUse = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //자동 하이픈 먹이려고
        phNmEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());


    } //end of onCreate


//    @Override
//    public void onClick(View v) {
//
//        input_cusName = nameEt.getText().toString().trim();
//        input_cusNumber = phNmEt.getText().toString().trim();
//        input_cusMemo = memoEt.getText().toString().trim();
//
//        switch (v.getId()) {
//
//            case R.id.completeBtn: //저장하기
//
//                if (origin.contains("create_event")){
//                    showToast("신규 고객 예약등록으로 가라잇");
//                } else {
//                    showToast("Nothing happened");
//                    //빈값이 없는지 확인
//                    if (isValid()) {
//                        checkDuplicated(); //DB에서 고객이름이 이미 존재하는지 확인합니다.
//                    }
//                }
//
//
//
//                if (fromWhere.contains("each_event")){ //each event에서 넘어온거라면 ?
//                    if (nameEt.getText().toString().isEmpty() || phNmEt.getText().toString().isEmpty()
//                            || !isValidCellPhoneNumber(phNmEt.getText().toString())) {
//                        showToast("이름 또는 전화번호를 확인해주세요.");
//                    } else {
//                        boolean isExistedAlready = false;
//                        //휴대폰 번호로 중복 체크
//                        for (int i = 0; i < savedCustomersList.size(); i++) {
//                            if (phNmEt.getText().toString().equals(savedCustomersList.get(i).NUMBER)) {
//                                isExistedAlready = true;
//                                break;
//                            }
//                        }
//                        if (isExistedAlready) {
//                            showToast("이미 동일한 연락처로 저장된 고객이 있습니다.");
//                        } else {
//                            // 현재시간을 msec 으로 구한다.
//                            long now = System.currentTimeMillis();
//                            // 현재시간을 date 변수에 저장한다.
//                            Date date = new Date(now);
//                            // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
//                            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
//                            // nowDate 변수에 값을 저장한다.
//                            String formatDate = sdfNow.format(date);
//
////                            //하이픈 빼고 전화번호 저장하기
////                            String phoneNum = phNmEt.getText().toString().replace("-", "");
////                            Log.d("numberhipen", phoneNum);
////                            saveCustomer(ownername, nameEt.getText().toString(), phoneNum, "신규", add_num_recommendEt.getText().toString(), ""
////                                    , "", add_num_shortmemoEt.getText().toString(), formatDate, 0);
////
////                            Intent intent = new Intent();
////                            intent.putExtra("customer_name", nameEt.getText().toString());
////                            intent.putExtra("customer_number", phoneNum);
////                            setResult(RESULT_OK, intent);
////                            finish();
//                        }
//                    }
//                } else { //merberList에서 넘어온거자나
//                    if (nameEt.getText().toString().isEmpty() || phNmEt.getText().toString().isEmpty()
//                            || !isValidCellPhoneNumber(phNmEt.getText().toString())) {
//                        showToast("이름 또는 전화번호를 확인해주세요.");
//                    } else {
//                        boolean isExistedAlready = false;
//                        //휴대폰 번호로 중복 체크
//                        for (int i = 0; i < savedCustomersList.size(); i++) {
//                            if (phNmEt.getText().toString().equals(savedCustomersList.get(i).NUMBER)) {
//                                isExistedAlready = true;
//                                break;
//                            }
//                        }
//                        if (isExistedAlready) {
//                            showToast("이미 동일한 연락처로 저장된 고객이 있습니다.");
//                        } else {
//                            // 현재시간을 msec 으로 구한다.
//                            long now = System.currentTimeMillis();
//                            // 현재시간을 date 변수에 저장한다.
//                            Date date = new Date(now);
//                            // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
//                            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                            // nowDate 변수에 값을 저장한다.
//                            String formatDate = sdfNow.format(date);
//
//                            //하이픈 빼고 전화번호 저장하기
//                            String phoneNum = phNmEt.getText().toString().replace("-", "");
//                            Log.d("numberhipen", phoneNum);
//                            saveCustomer(ownername, nameEt.getText().toString(), phoneNum, "신규", add_num_recommendEt.getText().toString(), ""
//                                    , "", add_num_shortmemoEt.getText().toString(), formatDate, 0);
//                            finish();
//                        }
//                    }
//                }
//
//                break;
//
//            case R.id.backIv:
//                showToast("백버튼");
//                finish();
//                break;
//        }
//
//    }
//
//    //회원정보 불러오는 메서드
//    public ArrayList<Customers> loadCustomersData() {
//        savedCustomersList = new ArrayList<>();
//        savedCustomersList.clear();
//
//        Log.d("tag", ownername);
//
//        ArrayList<Customers> arrayList = new ArrayList<>();
//        dbOpenHelper_customer = new DBOpenHelper_Customer(this);
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
//            String savedate = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.SAVEDATE));
//            int noshowcount = cursor.getInt(cursor.getColumnIndex(DBStructure_Customer.NOSHOWCOUNT));
//            Customers customers = new Customers(ownername2, customername, number, grade, recommend, point, visit, memo, savedate, noshowcount);
//            arrayList.add(customers);
//
//        }
//        cursor.close();
//
//        savedCustomersList.addAll(arrayList);
//
//        Collections.sort(savedCustomersList, comparator_customers);
//
//
//        return savedCustomersList;
//
//    }

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
//                add_num_gradeBtn.setText(selectedText);
//                grade = selectedText;
//            }
//        });
//        //Create alert dialog object via builder
//        AlertDialog alertDialogObject = dialogBuilder.create();
//        //Show the dialog
//        alertDialogObject.show();
//    }


    //===============================================Firestore method=============================================
    private boolean isValid() {

        if (input_cusName.isEmpty()) {
            showToast("고객 이름을 입력해주세요");
            return false;
        }

        if (input_cusNumber.isEmpty() || !isValidCellPhoneNumber(input_cusNumber)) {
            showToast("전화번호를 확인해주세요");
            return false;
        }

        return true;
    }

    private void putCusData() {
        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();

        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);

        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");

        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(date);

        String numberWOhipen = input_cusNumber.replaceAll("-","");

        // Create a new user with a first and last name
        final Map<String, Object> user = new HashMap<>();
        user.put("cusName", input_cusName);
        user.put("cusNumber", numberWOhipen);
        user.put("cusMemo", input_cusMemo);
        user.put("cusRgstDate", formatDate);

        mDB.collection("users").document(mAuth.getCurrentUser().getEmail()).collection("customers").document(input_cusName)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(FIRESTORE_TAG, "DocumentSnapshot added with ID: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(FIRESTORE_TAG, "Error adding document", e);
                    }
                });
    }

    private void checkDuplicated() {
        DocumentReference docRef = mDB.collection("users" + "/" + mAuth.getCurrentUser().getEmail() + "/customers").document(input_cusName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Log.d("document123", "DocumentSnapshot data : " + documentSnapshot.getData());
                        Log.d("document123", documentSnapshot.get("cusName") + "?");
                        showToast("동일한 이름의 고객이 있습니다");
                    } else {
                        putCusData();
                        showToast("고객 저장 완료");
                    }
                } else {
                    Log.d("document123", "get failed with", task.getException());
                }
            }
        });
    }
}
