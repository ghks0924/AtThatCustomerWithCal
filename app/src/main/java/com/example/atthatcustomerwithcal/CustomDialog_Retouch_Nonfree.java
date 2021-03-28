package com.example.atthatcustomerwithcal;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class CustomDialog_Retouch_Nonfree extends Dialog implements View.OnClickListener {


    private Button positiveButton;
    private Button negativeButton;
    private TextView menuTv;
    private EditText editPrice;
    private TextView titleTv;

    private Context context;

    private CustomDialogListener customDialogListener;



    public CustomDialog_Retouch_Nonfree(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPositive: //확인 버튼을 눌렀을 때
                //각각의 변수에 EidtText에서 가져온 값을 저장

                //메뉴의 이름이나 가격 둘 중 하나라도 입력안했을시 입력하라고 토스트 띄움
                if(editPrice.getText().toString().isEmpty()){

                } else {
                    String name = menuTv.getText().toString();
                    int price = Integer.parseInt(editPrice.getText().toString());

                    //인터페이스의 함수를 호출하여 변수에 저장된 값들을 Activity로 전달
                    customDialogListener.onPositiveClicked(name,price);
                    dismiss();
                }


                break;
            case R.id.btnNegative: //취소 버튼을 눌렀을 때
                cancel();
                break;
        }
    }

    //인터페이스 설정
    interface CustomDialogListener{
        void onPositiveClicked(String menuName, int price);
        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_dialog_retouch);

        //init
        menuTv = findViewById(R.id.menuItemTv);
        positiveButton = (Button)findViewById(R.id.btnPositive);
        negativeButton = (Button)findViewById(R.id.btnNegative);
        editPrice = (EditText)findViewById(R.id.priceEt);
        editPrice.setHint("시술 가격 (숫자만입력)");
        titleTv = (TextView)findViewById(R.id.title);

        //버튼 클릭 리스너 등록
        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);

    }


}
