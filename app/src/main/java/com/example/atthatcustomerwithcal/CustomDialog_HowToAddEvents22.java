package com.example.atthatcustomerwithcal;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CustomDialog_HowToAddEvents22 extends Dialog implements View.OnClickListener {


    private String ownername2 = "12341234";

    private TextView newCus;
    private TextView exCus;
    private TextView nonCus;

    private Context context;

    private CustomDialogListener customDialogListener;



    public CustomDialog_HowToAddEvents22(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.newCus:


                customDialogListener.onPositiveClicked();
                dismiss();


                break;
            case R.id.exCus: //취소 버튼을 눌렀을 때
                customDialogListener.onNegativeClicked();
                dismiss();
                break;

            case R.id.nonCus:
                customDialogListener.onNeutralClicked();
                dismiss();
                break;
        }
    }

    //인터페이스 설정
    interface CustomDialogListener{
        void onPositiveClicked();
        void onNegativeClicked();
        void onNeutralClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_dialog_howtoaddevent22);

        //init
        newCus = findViewById(R.id.newCus);
        exCus = findViewById(R.id.exCus);
        nonCus = findViewById(R.id.nonCus);

        //버튼 클릭 리스너 등록
        newCus.setOnClickListener(this);
        exCus.setOnClickListener(this);
        nonCus.setOnClickListener(this);

    }


}
