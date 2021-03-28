package com.example.atthatcustomerwithcal;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class Activity_Login extends Activity_Base implements View.OnClickListener {

    //화면요소
    MaterialButton loginBtn;
    ImageView loginTv;
    MaterialTextView findIDBtn, findPwdBtn;
    EditText idEt, passwordEt;

    //아이디확인 및 비밀번호 확인
    boolean idExisted;
    boolean idPassMatched;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("구글 로그인을 하기 위해서는 주소록 접근 권한이 필요해요")
                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.READ_CONTACTS
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.CAMERA
                        , Manifest.permission.READ_PHONE_STATE)
                .check();

        mAuth = FirebaseAuth.getInstance();

        //화면요소 생성 및 연결
        initLayout();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                if (isVacant()) {
                    login();
                }

                break;
            case R.id.joinBtn:
                Intent intent = new Intent(this, Activity_Join0.class);
                startActivity(intent);
                break;

            case R.id.findIDBtn:
                Intent intent2 = new Intent(this, Activity_FindPwd.class);
                startActivity(intent2);
                break;

            case R.id.findPwdBtn:
                Intent intent3 = new Intent(this, Activity_FindPwd.class);
                startActivity(intent3);
                break;

        }
    }

    //아이디 && 비밀번호 입력 확인
    private boolean isVacant() {
        if (idEt.getText().toString().length() == 0) {
            showToast("아이디를 입력해주세요");
            return false;
        } else if (passwordEt.getText().toString().length() == 0) {
            showToast("비밀번호를 입력해주세요");
            return false;
        } else {
            return true;
        }
    }

    //로그인 메서드
    public void login() {
        String ID = idEt.getText().toString().trim();
        String pwd = passwordEt.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(ID, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                Toast.makeText(Activity_Login.this,"존재하지 않는 계정 입니다" ,Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(Activity_Login.this,"이메일 또는 비밀번호를 확인해주세요" ,Toast.LENGTH_SHORT).show();
                            } catch (FirebaseNetworkException e) {
                                Toast.makeText(Activity_Login.this,"네트워크 오류" ,Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(Activity_Login.this,"오류 : 잠시 후 다시 시도해주세요" ,Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            currentUser = mAuth.getCurrentUser();

                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);//액티비티 스택제거
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }


    //idPassMatched면 자동로그인
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        idPassMatched = data.getBooleanExtra("idPassMatched", false);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        showToast(error.getLocalizedMessage());
    }

    private void initLayout() {
        //TextView
        loginTv = findViewById(R.id.loginTv);
        findIDBtn = findViewById(R.id.findIDBtn);
        findIDBtn.setTextColor(Color.BLACK);
        findPwdBtn = findViewById(R.id.findPwdBtn);
        findPwdBtn.setTextColor(Color.BLACK);
        //EditText
        idEt = findViewById(R.id.idEt);
        passwordEt = findViewById(R.id.passwordEt);
        //Button
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        findViewById(R.id.joinBtn).setOnClickListener(this);
        findIDBtn.setOnClickListener(this);
        findPwdBtn.setOnClickListener(this);

        idEt.setText("ngh_0924@naver.com");
        passwordEt.setText("dnswjs12!@");
    }


    //======================================백버튼 ================================================
    private long time = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            finish();
        }
    }

    //======================================권한설정 메소드=========================================
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }

    };

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(Activity_Login.this, MainActivity.class));
            finish();
        }

    }
}
