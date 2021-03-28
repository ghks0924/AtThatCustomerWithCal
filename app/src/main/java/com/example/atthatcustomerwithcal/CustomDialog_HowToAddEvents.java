package com.example.atthatcustomerwithcal;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class CustomDialog_HowToAddEvents extends Dialog implements View.OnClickListener {


    private String ownername2 = "12341234";

    private TextView newEvent;
    private TextView retouchEvent;

    private Context context;

    private CustomDialogListener customDialogListener;



    public CustomDialog_HowToAddEvents(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.newEvent:


                customDialogListener.onPositiveClicked();
                dismiss();


                break;
            case R.id.retouchEvent: //취소 버튼을 눌렀을 때
                customDialogListener.onNegativeClicked();
                dismiss();
                break;
        }
    }

    //인터페이스 설정
    interface CustomDialogListener{
        void onPositiveClicked();
        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_dialog_howtoaddevent);

        //init
        newEvent = findViewById(R.id.newEvent);
        retouchEvent = findViewById(R.id.retouchEvent);

        //버튼 클릭 리스너 등록
        newEvent.setOnClickListener(this);
        retouchEvent.setOnClickListener(this);

    }


}
