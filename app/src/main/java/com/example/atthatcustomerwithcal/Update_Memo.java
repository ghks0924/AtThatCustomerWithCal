package com.example.atthatcustomerwithcal;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Update_Memo extends Activity_Base {

    //화면
    EditText memoTitleEt;
    ImageView backIv;
    EditText editText;

    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    CollectionReference memoCol;
    DocumentReference docRef;
    String memoContent;

    boolean isRevised = false;
    String memoTitleStr, memoDateStr, memoContentStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update__memo);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initLayout();
    }

    public void initLayout() {

        Intent memoDataIntent = getIntent();
        memoTitleStr = memoDataIntent.getStringExtra("pickedMemoTitle");

        memoDateStr = memoDataIntent.getStringExtra("pickedMemoDate");
        memoContentStr = memoDataIntent.getStringExtra("pickedMemoContent");

        Log.d("memoData", "memoTitleStr : " + memoTitleStr );
        Log.d("memoData", "memoDateStr : " + memoDateStr );
        Log.d("memoData", "memoContentStr : " + memoContentStr );

        memoTitleEt = findViewById(R.id.memoTitleEt);
        memoTitleEt.setText(memoTitleStr);
        editText = findViewById(R.id.editText);
        editText.setText(memoContentStr);

        backIv = findViewById(R.id.backIv);
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoContent = editText.getText().toString().trim();

                if (memoContent.isEmpty()) { //들어와서 그냥 나가는 경우
                    //키보드 내려
                    KeyboardDown();
                    Intent intent = new Intent();
                    intent.putExtra("memoEventType", "nothing");
                    setResult(RESULT_OK, intent);
                    finish();

                } else { //수정 행위를 했으면
                    if (isRevised) {
                        saveMemo();
                        KeyboardDown();
                        editText.clearFocus();

                        isRevised = false;
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("memoEventType", "update");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }

            }
        });


        editText.requestFocus();
        KeyboardUp();

        Log.d("FIRESTORE_MEMO_FOCUS", isRevised + "?");

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isRevised = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void saveMemo() {

        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(date);


        final String memoTitle = memoTitleEt.getText().toString().trim();
        final String memoDate = formatDate;
        final String memoContent = editText.getText().toString().trim();


        // 데이터 특정 문서에 추가하기
        Map<String, Object> memo = new HashMap<>();

        if (memoTitle.startsWith("제목")) {
            memo.put("memoTitle", "미입력_제목");
        } else {
            memo.put("memoTitle", memoTitle);
        }

        memo.put("memoDate", memoDate);
        memo.put("memoContent", memoContent);

        if (isRevised) {
            db.collection("users").document(mAuth.getCurrentUser().getEmail()).collection("memos")
                    .document(docRef.getId())
                    .set(memo, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("FIRESTORE_TAG_MEMO", "DocumentSnapshot successfully written!");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("FIRESTORE_TAG_MEMO", "Error adding document", e);
                        }
                    });
        }

    }

    public void fillTitle(){
//        eventName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(MainActivity.this);
//                builder1.setCancelable(true);
//
//                customersArrayList.clear();
//                customersArrayList = loadCustomersData();
//
//                View addView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_dialog_customers, null);
//
//                TextView title = addView.findViewById(R.id.title);
//                final EditText searchEt = addView.findViewById(R.id.searchEt);
//                searchEt.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        String searchText = searchEt.getText().toString().toLowerCase();
//                        search(searchText);
//                    }
//                });
//
//                final ListView listView2 = addView.findViewById(R.id.listview_picking);
//                myListAdapter_customers = new MainActivity.MyListAdapter_customers(MainActivity.this);
//                listView2.setAdapter(myListAdapter_customers);
//
//                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        ListTempPosition = position;
//                        eventName.setText(customersArrayList.get(position).CUSTOMER_NAME);
//                        alertDialog_events.dismiss();
//                    }
//                });
//
//                builder1.setView(addView);
//                alertDialog_events = builder1.create();
//                alertDialog_events.show();
//            }
//        });
    }
}