package com.example.atthatcustomerwithcal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Activity_Join3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join3);

        handler.sendEmptyMessageDelayed(0, 2000);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);//액티비티 스택제거
            startActivity(mainIntent);
            finish();
            Log.d("heu", "manualLogin");

            handler.removeMessages(0);
        }
    };

    //======================================백버튼 ================================================
    //아무일도 일어나지 않게 하기
    @Override
    public void onBackPressed() {

    }
}