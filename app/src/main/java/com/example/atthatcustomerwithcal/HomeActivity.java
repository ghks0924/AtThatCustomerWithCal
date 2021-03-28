package com.example.atthatcustomerwithcal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeActivity extends Activity_Base {


    private static final int REQUEST_CREATE_MEMO = 0001;

    FirebaseAuth mAuth;
    FirebaseFirestore mDB;

    FrameLayout frame_container;
    BottomNavigationView bottomNavigationView; //바텀 네비게이션 뷰
    String initDate;

    Calendar calendar = Calendar.getInstance(Locale.KOREA);
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy.MM", Locale.KOREA);

    CustomCalendarView_event customCalendarView_event;

    //Fragment
    Fragment_Home fragment_home;
    Fragment_Memo fragment_memo;
    Fragment_Message fragment_message;
    Fragment_Accountings fragment_accountings;
    Fragment_Customers fragment_customers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseFirestore.getInstance();

        frame_container = findViewById(R.id.frame_container);
        bottomNavigationView = findViewById(R.id.bottom_navigation); //처음화면


        fragment_home = new Fragment_Home();
        fragment_memo = new Fragment_Memo();
        fragment_customers = new Fragment_Customers();


        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment_memo).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment_customers).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment_home).commit();


        //FrameLayout에 fragment.xml 띄우기
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        //바텀 네비게이션뷰 안의 아이템 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private MenuItem menuItem;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                this.menuItem = menuItem;

                switch (menuItem.getItemId()) { //item을 클릭시 id값을 가져와 FrameLayout에 fragment.xml띄우기
                    case R.id.nav_memo:

                        if (fragment_memo == null){
                            fragment_memo = new Fragment_Memo();
                            getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment_memo).commit();
                        }

                        if (fragment_memo != null){getSupportFragmentManager().beginTransaction().show(fragment_memo).commit();}

                        if (fragment_message != null){getSupportFragmentManager().beginTransaction().hide(fragment_message).commit();}
                        if (fragment_home != null){getSupportFragmentManager().beginTransaction().hide(fragment_home).commit();}
                        if (fragment_accountings != null){getSupportFragmentManager().beginTransaction().hide(fragment_accountings).commit();}
                        if (fragment_customers != null){getSupportFragmentManager().beginTransaction().hide(fragment_customers).commit();}



                        break;
                    case R.id.nav_message:
                        if (fragment_message == null){
                            fragment_message = new Fragment_Message();
                            getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment_message).commit();
                        }

                        if (fragment_message != null){getSupportFragmentManager().beginTransaction().show(fragment_message).commit();}

                        if (fragment_memo != null){getSupportFragmentManager().beginTransaction().hide(fragment_memo).commit();}
                        if (fragment_home != null){getSupportFragmentManager().beginTransaction().hide(fragment_home).commit();}
                        if (fragment_accountings != null){getSupportFragmentManager().beginTransaction().hide(fragment_accountings).commit();}
                        if (fragment_customers != null){getSupportFragmentManager().beginTransaction().hide(fragment_customers).commit();}

//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new Fragment_Message()).commit();
                        break;
                    case R.id.nav_home:
                        if (fragment_home == null){
                            fragment_home = new Fragment_Home();
                            getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment_home).commit();
                        }

                        if (fragment_home != null){getSupportFragmentManager().beginTransaction().show(fragment_home).commit();}

                        if (fragment_memo != null){getSupportFragmentManager().beginTransaction().hide(fragment_memo).commit();}
                        if (fragment_message != null){getSupportFragmentManager().beginTransaction().hide(fragment_message).commit();}
                        if (fragment_accountings != null){getSupportFragmentManager().beginTransaction().hide(fragment_accountings).commit();}
                        if (fragment_customers != null){getSupportFragmentManager().beginTransaction().hide(fragment_customers).commit();}


//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new Fragment_Home()).commit();
                        break;
                    case R.id.nav_accountings:
                        if (fragment_accountings == null){
//                            fragment_accountings = new Fragment_Accountings();
//                            getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment_accountings).commit();

                            showToast("개발중");
                        }

                        if (fragment_accountings != null){getSupportFragmentManager().beginTransaction().show(fragment_accountings).commit();}

                        if (fragment_memo != null){getSupportFragmentManager().beginTransaction().hide(fragment_memo).commit();}
                        if (fragment_message != null){getSupportFragmentManager().beginTransaction().hide(fragment_message).commit();}
                        if (fragment_home != null){getSupportFragmentManager().beginTransaction().hide(fragment_home).commit();}
                        if (fragment_customers != null){getSupportFragmentManager().beginTransaction().hide(fragment_customers).commit();}

//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new Fragment_Accountings()).commit();
                        break;
                    case R.id.nav_customers:
                        if (fragment_customers == null){
                            fragment_customers = new Fragment_Customers();
                            getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment_customers).commit();
                        }

                        if (fragment_customers != null){getSupportFragmentManager().beginTransaction().show(fragment_customers).commit();}

                        if (fragment_memo != null){getSupportFragmentManager().beginTransaction().hide(fragment_memo).commit();}
                        if (fragment_message != null){getSupportFragmentManager().beginTransaction().hide(fragment_message).commit();}
                        if (fragment_accountings != null){getSupportFragmentManager().beginTransaction().hide(fragment_accountings).commit();}
                        if (fragment_home != null){getSupportFragmentManager().beginTransaction().hide(fragment_home).commit();}

//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new Fragment_Customers()).commit();
                        break;
                }
                return true;

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            case REQUEST_CREATE_MEMO:
                String memoEventType = data.getStringExtra("memoEventType");
                if (memoEventType.equals("nothing")){
                    showToast("Nothing happened");
                } else {
                    showToast("Something happened");

                    getSupportFragmentManager().beginTransaction().remove(fragment_memo).commit();
                    fragment_memo = new Fragment_Memo();
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment_memo).commit();

                }


                break;
        }


    }

    //====================================Fragment_Memos=======================================

    public void onGoToCreateMemoActivity(){
        Intent CreateMemoActivity = new Intent(getApplicationContext(), Activity_Create_Memo.class);
        startActivityForResult(CreateMemoActivity, REQUEST_CREATE_MEMO);
    }


    public void onGoToUpdateMemoActivity(){
        Intent UpdateMemoActivity = new Intent(getApplicationContext(), Activity_Create_Memo.class);

        UpdateMemoActivity.putExtra("type", "revise");

        startActivityForResult(UpdateMemoActivity, REQUEST_CREATE_MEMO);
    }

    public void pickMemoDataFromDB(){

    }

    //====================================Fragment_Customers 메서드=======================================


    //회원 추가 메서드
    public void onGoToAddCus() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(HomeActivity.this, R.style.CustomBottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.custom_bottomsheet_addcus);


        bottomSheetDialog.setCanceledOnTouchOutside(true);

        MaterialButton manualBtn = bottomSheetDialog.findViewById(R.id.manuallyAdd);
        manualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("직접 연락");
                Intent intent = new Intent(getApplicationContext(), Activity_Create_Customer.class);
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        MaterialButton fromBookBtn = bottomSheetDialog.findViewById(R.id.fromPhonebook);
        fromBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("연락처");
                Intent intent2 = new Intent(getApplicationContext(), Activity_Create_Customer_FromBook.class);
                startActivity(intent2);
                bottomSheetDialog.dismiss();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bottomSheetDialog.create();
        }


        bottomSheetDialog.show();


    }

    //====================================Fragment_Home=======================================

    //메뉴 버튼 눌렀을 때
    public void onGoToMenuActivity() {
        Intent MenuActivity = new Intent(getApplicationContext(), HomeMenuActivity.class);
        startActivity(MenuActivity);
        overridePendingTransition(R.anim.horizon_enter, R.anim.fadeout);
    }

    //이전달
    public void onChangePrevMonth(){

    }

    //예약 추가 메서드
    public void onGoToAddEvent(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(HomeActivity.this, R.style.CustomBottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.custom_bottomsheet_addevt);

        bottomSheetDialog.setCanceledOnTouchOutside(true);

        MaterialButton newCusBtn = bottomSheetDialog.findViewById(R.id.newCusBtn);
        newCusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("신규 고객 예약");
                Intent intent = new Intent(getApplicationContext(), Activity_Create_Customer.class);
                intent.putExtra("origin", "create_event");
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        MaterialButton oldCusBtn = bottomSheetDialog.findViewById(R.id.oldCusBtn);
        oldCusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(Activity_CreateEvent_OldCus.class);
                bottomSheetDialog.dismiss();
            }
        });

        MaterialButton skipCusBtn = bottomSheetDialog.findViewById(R.id.skipCusBtn);
        skipCusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(Activity_CreateEvent_SkipCus.class);
                bottomSheetDialog.dismiss();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bottomSheetDialog.create();
        }


        bottomSheetDialog.show();
    }

//    //예약등록 FAB 눌렀을 때
//    public void onGoToAddEvent() {
//        Intent AddEventIntent = new Intent(getApplicationContext(), Add_event_retouch.class);
//        startActivity(AddEventIntent);
//    }

    //상단 날짜를 클릭했을 때
    public void onPopUpDatePickerDialog() {
        initDate = dateFormat2.format(calendar.getTime());
        //크기조정
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.8); // Display 사이즈의 90%

        final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setCancelable(true);
        View addView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.year_month_picker, null);

        final NumberPicker pickedYear = addView.findViewById(R.id.yearPicker);
        final NumberPicker pickedMonth = addView.findViewById(R.id.monthPicker);
        Button btn = addView.findViewById(R.id.btn);

        //순환 안되게 막기
        pickedYear.setWrapSelectorWheel(false);
        pickedMonth.setWrapSelectorWheel(false);

        // editText 설정해제
        pickedYear.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        pickedMonth.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        // 최소값 설정
        pickedYear.setMinValue(2017);
        pickedMonth.setMinValue(1);

        // 최대값 설정
        pickedYear.setMaxValue(2050);
        pickedMonth.setMaxValue(12);


//        //초기값 설정
//        String initDate = customCalendarViewEvent.CurrentDate.getText().toString().trim();
//        Log.d(CalendarRenovation, initDate  );
        final String initYear = initDate.substring(0, 4);
        final String initMonth = initDate.substring(5);
//
        final int initYearNbr = Integer.parseInt(initYear);
        final int initMonthNbr = Integer.parseInt(initMonth);

        pickedYear.setValue(initYearNbr);
        pickedMonth.setValue(initMonthNbr);
//
//
        builder.setView(addView);
        final AlertDialog ab2 = builder.create();
        ab2.show();
//
        //크기조정
        ViewGroup.LayoutParams params = ab2.getWindow().getAttributes();
        params.width = width;
        ab2.getWindow().setAttributes((WindowManager.LayoutParams) params);

        final int[] gapYear = new int[1];
        final int[] gapMonth = new int[1];
//
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pickedMonth.getValue() < 10) {
                    //데이터 전달
//                    customCalendarViewEvent.CurrentDate.setText(pickedYear.getValue()+".0"+pickedMonth.getValue());
                } else {
                    //데이터 전달
//                    customCalendarViewEvent.CurrentDate.setText(pickedYear.getValue()+"."+pickedMonth.getValue());
                }

                gapYear[0] = pickedYear.getValue() - initYearNbr;
                gapMonth[0] = pickedMonth.getValue() - initMonthNbr;

//                for (int i = 0; i < isSelected.length; i++) {
//                    isSelected[i] = false;
//                }

                calendar.add(Calendar.YEAR, gapYear[0]);

                calendar.add(Calendar.MONTH, gapMonth[0]);

                initDate = pickedYear + "." + pickedMonth;

//                customCalendarViewEvent.SetUpCalendar();


                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new Fragment_Home()).commit();


                ab2.dismiss();

            }
        });

    }

    //====================================Fragment_Sms=======================================

    //default sms창으로 가기
    public void onGoToSMS() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("vnd.android-dir/mms-sms");
        startActivity(intent);
    }

    //====================================================뒤로가기 버튼을 눌렀을 때 처리 ==============================================
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

}
