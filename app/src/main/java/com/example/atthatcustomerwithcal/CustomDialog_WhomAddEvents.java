package com.example.atthatcustomerwithcal;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class CustomDialog_WhomAddEvents extends Dialog implements View.OnClickListener {


    private String ownername2 = "12341234";

    private TextView newCustomer;
    private TextView oldCustomer;

    private Context context;

    private CustomDialogListener customDialogListener;



    public CustomDialog_WhomAddEvents(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.newCustomer:
                customDialogListener.onPositiveClicked();
                dismiss();


                break;
            case R.id.oldCustomer: //취소 버튼을 눌렀을 때
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

        setContentView(R.layout.custom_dialog_whomaddevents);

        //init
        newCustomer = findViewById(R.id.newCustomer);
        oldCustomer = findViewById(R.id.oldCustomer);

        //버튼 클릭 리스너 등록
        newCustomer.setOnClickListener(this);
        oldCustomer.setOnClickListener(this);

    }


}
