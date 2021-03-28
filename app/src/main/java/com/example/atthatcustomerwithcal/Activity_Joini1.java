package com.example.atthatcustomerwithcal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Activity_Joini1 extends Activity_Base implements View.OnClickListener {

    final String FIRESTORE_TAG = "FIRESTORE_TAG";
    final int REQUEST_SELECT_REGION = 1;

    //화면요소
    ImageView backIv;
    TextView regionTv, nextTv;
    EditText phoneNbEt, shopNmEt, birthEt;
    CheckBox fullAgree, serviceAgree, privacyAgree;
    ScrollView scrollView;
    RadioGroup radioGroup;
    RadioButton maleRb, femaleRb;

    // 체크박스 체크여부
    private String TERMS_AGREE_ALL = "N";
    private String TERMS_AGREE_SERVICE = "N"; // No Check = 0, Check = 1
    private String TERMS_AGREE_PRIVACY = "N"; // No Check = 0, Check = 1

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    String email;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join1);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode(Locale.getDefault().getLanguage());
        db = FirebaseFirestore.getInstance();

        Log.d("TAG", mAuth.getCurrentUser().getEmail());

        Intent intent2 = getIntent();
        email = intent2.getStringExtra("email");
        initLayout();

    }

    //약관동의 확인 메서드
    public boolean checkTermAgree() {
        if ((TERMS_AGREE_ALL.equals("Y")) || (TERMS_AGREE_PRIVACY.equals("Y") && TERMS_AGREE_SERVICE.equals("Y"))) {
            return true;
        } else {
            return false;
        }

    }

    //전화번호 유효성 검사
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

    //생년월일유효성검사
    private boolean isVaildBirth(String birth) {

        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy");
        // nowDate 변수에 값을 저장한다.
        String nowDate = sdfNow.format(date);

        String year = birth.substring(0, 4);
        String month = birth.substring(4, 6);
        String day = birth.substring(6);
        int yearInt = Integer.parseInt(year);
        int monthInt = Integer.parseInt(month);
        int dayaInt = Integer.parseInt(day);
        int yearNow = Integer.parseInt(nowDate);

        if (birth.length() != 8) {
            showToast("생년월일을 확인해주세요");
            return false;
        } else {
            if (1900 > yearInt || yearInt > yearNow) {
                showToast("생년월일을 확인해주세요");
                return false;
            } else if (monthInt < 1 || monthInt > 12) {
                showToast("생년월일을 확인해주세요");
                return false;
            } else if (dayaInt < 1 || dayaInt > 31) {
                showToast("생년월일을 확인해주세요");
                return false;
            } else if ((monthInt == 4 || monthInt == 6 || monthInt == 9 || monthInt == 11) && dayaInt == 31) {
                showToast("생년월일을 확인해주세요");
                return false;
            } else if (monthInt == 2) {
                boolean isLeap = (yearInt % 4 == 0 && (yearInt % 100 != 0 || yearInt % 400 == 0));
                if (dayaInt > 29 || (dayaInt == 29 && !isLeap)) {
                    showToast("생년월일을 확인해주세요");
                    return false;
                } else {
                    return true;
                } //end of if (day>29 || (day==29 && !isleap))
            } else {
                return true;
            }
        }
    }
        //입력 잘했는지 확인하는 메서드(빈칸 X)
        public boolean isValid () {
            if (!checkTermAgree()) {
                showToast("약관에 동의해주세요");
                return false;
            }

            String numberStr = phoneNbEt.getText().toString().trim();
            if (!isValidCellPhoneNumber(numberStr)) {
                showToast("휴대폰번호를 확인해주세요");
                return false;
            }
            number = numberStr.replaceAll("-", "");

            String shopNm = shopNmEt.getText().toString().trim();
            if (shopNm.isEmpty()) {
                showToast("가게명을 입력해주세요");
                return false;
            }

            String regionStr = regionTv.getText().toString().trim();
            if (regionStr.isEmpty()) {
                showToast("지역을 선택해주세요.");
                return false;
            }

            String birth = birthEt.getText().toString().trim();
            if (!isVaildBirth(birth)) {
                return false;
            }

            if (!maleRb.isChecked() && !femaleRb.isChecked()) {
                showToast("성별을 선택해주세요.");
                return false;
            }

            return true;
        }


        //클릭리스너
        @Override
        public void onClick (View v){
            switch (v.getId()) {

                case R.id.nextTv:
//                Intent intent = new Intent(getApplicationContext(), JoinActivity2.class);
//                startActivity(intent);

                    if (isValid()) {
                        Intent intent = new Intent(getApplicationContext(), ActivityJoin2.class);
                        putData();
                        startActivity(intent);
                    }
                    break;

                case R.id.regionTv:
                    Intent intent2 = new Intent(getApplicationContext(), SelectRegion.class);
                    startActivityForResult(intent2, REQUEST_SELECT_REGION);
                    break;

                case R.id.backIv:
                    finish();
                    break;

            }
        }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode != RESULT_OK) {
                return;
            }

            switch (requestCode) {
                case REQUEST_SELECT_REGION:

                    String selectedRegion = data.getStringExtra("region");
                    regionTv.setText(selectedRegion);
                    regionTv.setBackgroundResource(R.drawable.rounded_corner_maingrey);

                    birthEt.requestFocus();
                    regionTv.requestFocus();

                    break;
            }
        }

        //화면요소 초기화 및 연결
        private void initLayout () {
            backIv = findViewById(R.id.backIv);
            backIv.setOnClickListener(this);
            //ScrollView
            scrollView = findViewById(R.id.scrollView);
            //CheckBox
            fullAgree = findViewById(R.id.fullAgree);
            serviceAgree = findViewById(R.id.serviceAgree);
            serviceAgree.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            privacyAgree = findViewById(R.id.privacyAgree);
            privacyAgree.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            fullAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        serviceAgree.setChecked(true);
                        privacyAgree.setChecked(true);
                        TERMS_AGREE_ALL = "Y";
                    } else {
                        serviceAgree.setChecked(false);
                        privacyAgree.setChecked(false);
                        TERMS_AGREE_ALL = "N";
                    }
                }
            });
            serviceAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        TERMS_AGREE_SERVICE = "Y";
                    } else {
                        TERMS_AGREE_SERVICE = "N";
                    }
                }
            });
            privacyAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        TERMS_AGREE_PRIVACY = "Y";
                    } else {
                        TERMS_AGREE_PRIVACY = "N";
                    }
                }
            });


            String phoneNumber = getPhoneNumber();


            //TextView
            regionTv = findViewById(R.id.regionTv);
            //EditText
            phoneNbEt = findViewById(R.id.phoneNbEt);
            phoneNbEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
            phoneNbEt.setText(phoneNumber);
            showToast(phoneNumber + "?");
            shopNmEt = findViewById(R.id.shopNmEt);
            birthEt = findViewById(R.id.birthEt);

            //RadioGroup
            radioGroup = findViewById(R.id.radioGroup);
            maleRb = findViewById(R.id.maleRb);
            femaleRb = findViewById(R.id.femaleRb);

            //Button
            nextTv = findViewById(R.id.nextTv);
            nextTv.setOnClickListener(this);
            regionTv.setOnClickListener(this);
        }


        //휴대전화 번호 가져오기
        @SuppressLint("MissingPermission")
        public String getPhoneNumber () {


            TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String phoneNumber = "";
            try {
                if (telephony.getLine1Number() != null) {
                    phoneNumber = telephony.getLine1Number();
                } else {
                    if (telephony.getSimSerialNumber() != null) {
                        phoneNumber = telephony.getSimSerialNumber();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (phoneNumber.startsWith("+82")) {
                phoneNumber = phoneNumber.replace("+82", "0"); // +8210xxxxyyyy 로 시작되는 번호

            }
            //phoneNumber = phoneNumber.substring(phoneNumber.length()-10,phoneNumber.length());
            //phoneNumber="0"+phoneNumber;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                phoneNumber = PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().getCountry());
            } else {
                phoneNumber = PhoneNumberUtils.formatNumber(phoneNumber);
            }
            return phoneNumber;
        }

        //고객정보 데이터베이스에 추가하기
        private void putData () {
            final String emailId = mAuth.getCurrentUser().getEmail();
            final String serviceAgree = "Y";
            final String privacyAgree = "Y";
            final String phoneNumber = number;
            final String shopName = shopNmEt.getText().toString().trim();
            final String region = regionTv.getText().toString().trim();
            final String birth = birthEt.getText().toString().trim();
            final String gender;
            if (maleRb.isChecked()) {
                gender = "m";
            } else {
                gender = "f";
            }


            // Create a new user
            final Map<String, Object> user = new HashMap<>();
            user.put("email", emailId);
            user.put("serviceAgree", serviceAgree);
            user.put("privacyAgree", privacyAgree);
            user.put("phoneNumber", phoneNumber);
            user.put("shopName", shopName);
            user.put("region", region);
            user.put("birth", birth);
            user.put("gender", gender);

            db.collection("users").document(emailId)
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(FIRESTORE_TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(FIRESTORE_TAG, "Error adding document", e);
                        }
                    });
        }


        //======================================백버튼 ================================================
        //아무일도 일어나지 않게 하기
        @Override
        public void onBackPressed () {

        }
    }
