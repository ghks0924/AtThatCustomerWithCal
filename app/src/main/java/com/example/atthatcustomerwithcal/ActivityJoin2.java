package com.example.atthatcustomerwithcal;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityJoin2 extends Activity_Base implements View.OnClickListener {

    final String FIRESTORE_TAG2 = "FIRESTORE_TAG";
    final int REQUEST_SELECT_SERVICETYPE = 1;

    //화면 요소
    ImageView backIv;
    TextView serviceTypeTv;
    EditText goalEt, rcmdEt;
    MaterialButton completeBtn;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join2);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Log.d("TAG1", "getUid : "+mAuth.getCurrentUser().getUid());
        Log.d("TAG1", "getCurrentUser : "+mAuth.getCurrentUser().toString());
        Log.d("TAG1", "getTenantId : "+mAuth.getCurrentUser().getTenantId());
        Log.d("TAG1", "getDisplayname : "+ mAuth.getCurrentUser().getDisplayName());
        Log.d("TAG1", "just getUid : "+ mAuth.getUid());

        initLayout();


    }

//    //모든 정보들이 유효한지 확인하는 메서드
//    public boolean validData() {
//        if(shopNmEt.getText().toString().length()==0){
//            showToast("샵 이름을 입력해주세요");
//            return  false;
//        }
//        return true;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.completeBtn:
//                Intent intent = new Intent(getApplicationContext(), JoinActivity3.class);
//                startActivity(intent);
                putData();
                Intent intent = new Intent(getApplicationContext(), Activity_Join3.class);
                startActivity(intent);
                break;
            case R.id.serviceTypeTv:
                Intent intent2 = new Intent(getApplicationContext(), SelectTypeOfService.class);
                startActivityForResult(intent2, REQUEST_SELECT_SERVICETYPE);
                break;

            case R.id.backIv:
                finish();

                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_SELECT_SERVICETYPE:

                String selectedService = data.getStringExtra("region");
                serviceTypeTv.setText(selectedService);
                serviceTypeTv.setBackgroundResource(R.drawable.rounded_corner_maingrey);
                break;
        }
    }

    //고객정보 데이터베이스에 추가하기
    private void putData(){
        final String emailId = mAuth.getCurrentUser().getEmail();
        final String serviceType = serviceTypeTv.getText().toString().trim();
        final String goal = goalEt.getText().toString().trim();
        final String rcmdID = rcmdEt.getText().toString().trim();

        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
        // nowDate 변수에 값을 저장한다.
        String nowDate = sdfNow.format(date);

        final String rgstDate = nowDate;
        final String typePayment = "free";
        final String nextPmtDate = "";
        final String endDate = "";


        // 데이터 추가하기
        Map<String, Object> data = new HashMap<>();
        data.put("serviceType", serviceType);
        data.put("goal", goal);
        data.put("rcmdID", rcmdID);

        data.put("rgstDate", rgstDate);
        data.put("typePayment", typePayment);
        data.put("nextPmtDate", nextPmtDate);
        data.put("endDate", endDate);


        db.collection("users").document(emailId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(FIRESTORE_TAG2, "DocumentSnapshot successfully written!");
                        Log.d("123", mAuth.getUid());
                        Log.d("123", mAuth.getCurrentUser().getTenantId()+"?");
                        Log.d("123", mAuth.getCurrentUser()+"?  " + emailId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(FIRESTORE_TAG2, "Error adding document", e);
                    }
                });
    }

    private void initLayout(){
        backIv = findViewById(R.id.backIv);
        backIv.setOnClickListener(this);
        //TextView
        serviceTypeTv = findViewById(R.id.serviceTypeTv);
        serviceTypeTv.setOnClickListener(this);
        //EditText
        goalEt = findViewById(R.id.goalEt);
        goalEt.addTextChangedListener(watcher);
        rcmdEt = findViewById(R.id.rcmdEt);
        //Button
        completeBtn = findViewById(R.id.completeBtn);
        completeBtn.setOnClickListener(this);
    }

    private DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private String result="";

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)){
                result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                goalEt.setText(result);
                goalEt.setSelection(result.length());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    //======================================백버튼 ================================================
    //아무일도 일어나지 않게 하기
    @Override
    public void onBackPressed(){

    }
}