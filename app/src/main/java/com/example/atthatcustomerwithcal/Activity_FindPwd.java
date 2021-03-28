package com.example.atthatcustomerwithcal;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;

public class Activity_FindPwd extends Activity_Base implements View.OnClickListener {

    //화면요소
    ImageView backIv;
    EditText emailEt;
    MaterialButton sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pass);

        initLayout();
    }

    private void initLayout() {
        backIv = findViewById(R.id.backIv);
        backIv.setOnClickListener(this);

        emailEt = findViewById(R.id.emailEt);

        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backIv:
                finish();
                break;

            case R.id.nextBtn:
                showToast("입력하신 메일을 확인해주세요");
                break;
        }
    }
}