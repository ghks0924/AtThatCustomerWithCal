package com.example.atthatcustomerwithcal;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.regex.Pattern;

public class Activity_Join0 extends Activity_Base implements View.OnClickListener {

    ImageView backIv;
    TextView idTv, pwdMatchTv, validPwdTv, nextTv;
    EditText idEt, pwdEt, rePwdEt;
//    MaterialButton nextBtn;

    String emailStr, pwdStr, rePwdStr;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join0);

        //FirebaseAuth 초기화
        mAuth = FirebaseAuth.getInstance();

        //화면 세팅
        initLayout();
    }

    //화면세팅 메서드
    @SuppressLint("ResourceAsColor")
    private void initLayout() {
        backIv = findViewById(R.id.backIv);
        backIv.setOnClickListener(this);

        idEt = findViewById(R.id.idEt);
        pwdEt = findViewById(R.id.pwdEt);
        pwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("join0", "?");
                pwdStr = pwdEt.getText().toString().trim();
                if (!isValidPasswd(pwdStr)) {
                    validPwdTv.setVisibility(View.VISIBLE);
                } else {
                    validPwdTv.setVisibility(View.GONE);
                }
                ;
            }
        });
        rePwdEt = findViewById(R.id.rePwdEt);
        rePwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pwdStr = pwdEt.getText().toString().trim();
                rePwdStr = rePwdEt.getText().toString().trim();
                if (pwdStr.equals(rePwdStr)) {
                    pwdMatchTv.setVisibility(View.GONE);
                } else {
                    pwdMatchTv.setVisibility(View.VISIBLE);
                }
            }
        });

        idTv = findViewById(R.id.idTv);
        validPwdTv = findViewById(R.id.validPwdTv);
        pwdMatchTv = findViewById(R.id.pwdMatchTv);
        nextTv = findViewById(R.id.nextTv);
        nextTv.setOnClickListener(this);
//        nextBtn = findViewById(R.id.nextBtn);
//        nextBtn.setOnClickListener(this);

        idEt.setText("test0@naver.com");
        pwdEt.setText("test1234");
        rePwdEt.setText("test1234");
    }

    //=========================유효성 검사=========================
    private boolean isValidEmail(String target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private boolean isValidPasswd(String target) {
        if (Pattern.matches("(^.*(?=.{6,100})(?=.*[0-9])(?=.*[a-zA-Z]).*$)", target)) {
            return true;
        } else {
            return false;
        }

    }

    private boolean isValidSignUp() {
        emailStr = idEt.getText().toString().trim();
        if (!isValidEmail(emailStr)) {
            showToast("이메일 형식을 확인해주세요");
            return false;
        } else {
            if (pwdStr.isEmpty() || rePwdStr.isEmpty()
                    || !isValidPasswd(pwdStr) || !isValidPasswd(rePwdStr) || !pwdStr.equals(rePwdStr)) {
                showToast("비밀번호를 확인해주세요");
                return false;
            } else {
                return true;
            }
        }
    }

    //Authentication
    private void signUp() {
        mAuth.createUserWithEmailAndPassword(emailStr, rePwdStr)
                .addOnCompleteListener(Activity_Join0.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                showToast("이메일 형식에 맞지 않습니다");
                            } catch (FirebaseAuthUserCollisionException e){
                                showToast("이미 존재하는 이메일 계정입니다");
                            } catch (Exception e) {
                            }

                        } else {
                            //성공
                            SignIn();
                        }
                    }
                });
    }

    //가입한 아이디로 로그인
    private void SignIn() {
        mAuth.signInWithEmailAndPassword(emailStr, rePwdStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete()){
                            Intent intent = new Intent(getApplicationContext(), Activity_Joini1.class);
                            intent.putExtra("email", emailStr);
                            startActivity(intent);
                            finish();
                        } else {
                            showToast("로그인 실패");
                        }
                    }
                });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextTv:
//                Intent intent = new Intent(getApplicationContext(), JoinActivity1.class);
//                startActivity(intent);
                if (isValidSignUp()) {
                    signUp();
                }
                break;

            case R.id.backIv:
                finish();
                break;
        }
    }

    //회원정보 저장하기
    private void SaveMemberData(){

    }

}