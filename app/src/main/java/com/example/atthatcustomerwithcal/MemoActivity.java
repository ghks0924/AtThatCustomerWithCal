package com.example.atthatcustomerwithcal;

import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class MemoActivity extends Activity_Base {

    EditText memoEt1, memoEt2;
    boolean isMemoOpenFirst = true;
    boolean isSaved = true;
    private long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        // toolbar
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //뒤로가기 버튼, 디폴트값임
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //앱 제목 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        memoEt1 = findViewById(R.id.memoEt1);
        memoEt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isSaved = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isSaved = false;
                Log.d("memoChange", isSaved + "?");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        memoEt2 = findViewById(R.id.memoEt2);
        memoEt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        isSaved = true;

        loadMemo();


    }

    //뒤로가기 버튼에 기능 추가
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작

                if (System.currentTimeMillis() - time >= 2000) {
                    time = System.currentTimeMillis();
                    memoEt1.clearFocus();
                    memoEt2.clearFocus();
                } else if (System.currentTimeMillis() - time < 2000) {
                    saveMemo();
                    finish();
                }


                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //toolbar에 버튼 추가하려고
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_addeventswithtab, menu);
        return true;
    }

    public void saveMemo() {
        SharedPreferences pre = getSharedPreferences("MyNew", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("memo1", memoEt1.getText().toString());
        editor.putString("memo2", memoEt2.getText().toString());
        editor.commit(); //저장
        isSaved = true;
        memoEt1.clearFocus();
        memoEt2.clearFocus();
        Log.d("memoChange", isSaved + "save?");
    }

    public void loadMemo() {
        SharedPreferences pre = getSharedPreferences("MyNew", MODE_PRIVATE);
        String memo1 = pre.getString("memo1", "");
        String memo2 = pre.getString("memo2", "");

        if (memo1.isEmpty()) {
            memoEt1.setHint("메모를 입력해주세요");
        } else {
            memoEt1.setText(memo1);
        }

        if (memo2.isEmpty()) {
            memoEt2.setHint("재료 메모");
        } else {
            memoEt2.setText(memo2);
            ;
        }
    }

    @Override
    public void onBackPressed() {
        if (!memoEt1.isFocused() && !memoEt2.isFocused()) {
            saveMemo();
            finish();
        } else {
            if (System.currentTimeMillis() - time >= 2000) {
                time = System.currentTimeMillis();
                memoEt1.clearFocus();
                memoEt2.clearFocus();
            } else if (System.currentTimeMillis() - time < 2000) {
                saveMemo();
                finish();
            }
        }

    }

    //다른곳 클릭시 키보드 내리기
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}