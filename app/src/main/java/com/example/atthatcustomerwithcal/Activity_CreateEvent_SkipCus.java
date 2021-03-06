package com.example.atthatcustomerwithcal;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Activity_CreateEvent_SkipCus extends Activity_Base {

    ImageView backIv;
    RadioGroup radioGroup;
    RadioButton newRb, retouchRb;
    EditText memoEt;
    TextView dateTv, timeTv, newTv, retouchTv, okBtn;
    MaterialCardView back_cardview;

    FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event__skip_cus);


        initLayout();

    }

    public void initLayout() {

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor("#ebbdc5"));
        }
        back_cardview = findViewById(R.id.back_cardview);
        back_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        backIv = findViewById(R.id.backIv);


        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.newRb) { //?????? ??????
                    showToast("??????");
                } else { //????????? ??????
                    showToast("?????????");
                }
            }
        });
        newRb = findViewById(R.id.newRb);
        newTv = findViewById(R.id.newTv);
        newTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRb.setChecked(true);
            }
        });
        retouchRb = findViewById(R.id.retouchRb);
        retouchTv = findViewById(R.id.retouchTv);
        retouchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retouchRb.setChecked(true);
            }
        });
        dateTv = findViewById(R.id.dateContentTv);
        dateTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        timeTv = findViewById(R.id.timeContentTv);
        timeTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        memoEt = findViewById(R.id.memoEt);
        memoEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    memoEt.setTextCursorDrawable(R.drawable.cursor_edittext);
                }
            }
        });

        okBtn = findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()){
                    showToast("?????? ?????? ??????");
                }

            }
        });
    }

    //????????? ????????? ?????? ?????????
    public boolean isValid(){
        //?????? ?????? ??????
        if (!newRb.isChecked() && !retouchRb.isChecked()){
            showToast("?????? ????????? ??????????????????");
            return false;
        }

        //?????? ?????? ??????
        String dateStr = dateTv.getText().toString().trim();
        if (dateStr.startsWith("??????")){
            showToast("?????? ????????? ??????????????????");
            return false;
        }

        //?????? ?????? ??????
        String timeStr = timeTv.getText().toString().trim();
        if (timeStr.startsWith("??????")){
            showToast("?????? ????????? ??????????????????");
            return false;
        }

        //?????? ?????? && ???????????? ????????????

        return true;
    }
}