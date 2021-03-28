package com.example.atthatcustomerwithcal;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Add_SmsTemplate extends Activity_Base implements View.OnClickListener {

    TextView smsTitleTv, smsContentTv;
    EditText smsTitleEt, smsContentEt;
    Button cancelBtn, saveBtn;

    int prevIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__sms_template);

        //팝업 액티비티의 창 크기조절
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.9); // Display 사이즈의 90%
        int height = (int) (dm.heightPixels * 0.7); // Display 사이즈의 90%
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        //이전 화면으로부터 정보 받아오기.
        Intent intent = getIntent();
        boolean isNew = intent.getBooleanExtra("isNew", false);
        int position = intent.getIntExtra("position", -1);



        smsTitleTv = findViewById(R.id.smsTitleTv);
        smsContentTv = findViewById(R.id.smsContentTv);
        smsTitleEt = findViewById(R.id.smsTitleEt);
        smsContentEt = findViewById(R.id.smsContentEt);
        cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(this);
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        if (isNew){

        } else {
            smsTitleEt.setVisibility(View.GONE);
            smsContentEt.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            saveBtn.setVisibility(View.GONE);
            smsTitleTv.setVisibility(View.VISIBLE);
            smsContentTv.setVisibility(View.VISIBLE);
            smsTemplatesArrayList = ReadSmsTemplates();

            smsTitleTv.setText(smsTemplatesArrayList.get(position).TITLE);
            smsContentTv.setText(smsTemplatesArrayList.get(position).CONTENTS);
            smsContentTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        }

        //복사하기
        smsContentTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("number data", smsContentTv.getText().toString());
                clipboard.setPrimaryClip(clipData);

                showToast("복사되었습니다");
                return false;
            }

        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancelBtn:
                finish();
                break;

            case R.id.saveBtn:
                if (smsTitleEt.getText().toString().startsWith("ex") || smsContentEt.getText().toString().startsWith("ex")){

                    showToast("문자 유형과 내용을 입력해주세요");

                } else {
                    saveTemplate(smsTitleEt.getText().toString()
                            , smsContentEt.getText().toString());

                    finish();

                }

                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return true;
        } else {
            finish();
            return false;
        }
    }

    public void saveTemplate(String title, String content){
        dbOpenHelper_smsTemplate = new DBOpenHelper_SmsTemplate(getApplicationContext());
        SQLiteDatabase database = dbOpenHelper_smsTemplate.getWritableDatabase();
        dbOpenHelper_smsTemplate.SaveTemplate(title, content, database);
        dbOpenHelper_smsTemplate.close();
    }
}