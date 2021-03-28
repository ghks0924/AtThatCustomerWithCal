package com.example.atthatcustomerwithcal;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CustomDialog_AmPmPicker extends Dialog implements View.OnClickListener {

   private TextView AM;
   private TextView PM;

   private Context context;

    private CustomDialogListener customDialogListener;

    public CustomDialog_AmPmPicker(@NonNull Context context) {
        super(context);
        this.context = context;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.AM:
                customDialogListener.onPositiveClicked();
                dismiss();

                break;

            case R.id.PM:
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

        setContentView(R.layout.custom_dialog_ampm);

        //init
        AM = findViewById(R.id.AM);
        PM = findViewById(R.id.PM);

        //버튼 클릭 리스너 등록
        AM.setOnClickListener(this);
        PM.setOnClickListener(this);

    }

}
