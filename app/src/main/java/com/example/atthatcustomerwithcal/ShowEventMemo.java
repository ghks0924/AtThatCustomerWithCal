package com.example.atthatcustomerwithcal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ShowEventMemo extends AppCompatActivity {

    TextView eventMemoTv, materialMemoTv, surgeryMemoTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event_memo);

        eventMemoTv = findViewById(R.id.eventMemo);
        materialMemoTv = findViewById(R.id.materialMemo);
        surgeryMemoTv = findViewById(R.id.surgeryMemo);


        Intent intent = getIntent();
        String eventMemo = intent.getStringExtra("reserv_memo");
        String materialMemo = intent.getStringExtra("material_memo");
        String surgeryMemo = intent.getStringExtra("surgery_memo");

        eventMemoTv.setText(eventMemo);
        materialMemoTv.setText(materialMemo);
        surgeryMemoTv.setText(surgeryMemo);
    }
}