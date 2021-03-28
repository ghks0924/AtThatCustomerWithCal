package com.example.atthatcustomerwithcal;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.material.card.MaterialCardView;


public class HomeMenuActivity extends Activity_Base {

    GestureDetector mDetector;

    RelativeLayout Rl;
    Button btn;

    MaterialCardView back_cardview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);

        back_cardview = findViewById(R.id.previous_cardview);
        back_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fadein, R.anim.horizon_exit);
            }
        });


    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fadein, R.anim.horizon_exit);
    }
}