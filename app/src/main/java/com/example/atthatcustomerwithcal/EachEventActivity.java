package com.example.atthatcustomerwithcal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class EachEventActivity extends Activity_Base implements View.OnClickListener {

    final int REQUEST_PICK_CUS = 9999;
    final int REQUEST_ADD_CUS = 8888;

    Button noShowBtn, completeBtn;
    RelativeLayout frameRl;

    TextView nameTv, numberTv, dateTv, priceTv, memoTv;
    LinearLayout radioLl, nameLl, numberLl, addBtnLl;
    RadioButton cardRb, cashRb;
    EditText materialEt, surgeryNoteEt;
    Button sharingBtn, addCusBtn, pickCusBtn;

    String customer_name;
    String customer_number;
    String reserv_date;
    String reserv_time;
    String reserv_menu;
    int price;
    String reserv_retouch;
    String shortmemo;
    boolean reserv_complete;
    boolean reserv_noshow;
    String reserv_cardCash;
    String reserv_materialMemo;
    String reserv_surgeryMemo;
    String reserv_crtdate;

    Customers eachCustomerData;

    //?????? ????????????
    ArrayList<MenuList> menusArrayList = new ArrayList<>(); //?????? ????????? ?????? ????????? ????????????
    MyAdapter myAdapter;
    String tempMenuName;
    boolean isPriceChanged = false;
    int tempPrice = -1;
    int tempPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //????????? ?????? (???????????? ??????)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_each_event);

        //MainAct?????? ?????? ????????????
        getDataFromMain();
        getEachCustomerData();

        //?????? ???????????? ?????????
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.9); // Display ???????????? 90%
        int height = (int) (dm.heightPixels * 0.9); // Display ???????????? 90%
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        nameLl = findViewById(R.id.nameLl);
        nameTv = findViewById(R.id.each_nameTv);
        numberLl = findViewById(R.id.numberLl);
        numberTv = findViewById(R.id.numberTv);

        addBtnLl = findViewById(R.id.addBtnLl);
        addCusBtn = findViewById(R.id.addCusBtn);
        pickCusBtn = findViewById(R.id.pickCusBtn);
        dateTv = findViewById(R.id.each_dateTv);
        priceTv = findViewById(R.id.each_priceTv);
        priceTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reserv_retouch.equals("?????????")) {
                    if (!reserv_complete && reserv_menu.startsWith("??????")) {
                        Log.d("menu", reserv_menu + "?");

                        CustomDialog_Retouch_Nonfree dialog_retouch_nonfree = new CustomDialog_Retouch_Nonfree(EachEventActivity.this);
                        dialog_retouch_nonfree.setDialogListener(new CustomDialog_Retouch_Nonfree.CustomDialogListener() {
                            @Override
                            public void onPositiveClicked(String menuName, int price) {
                                tempMenuName = menuName;
                                tempPrice = price;
                                isPriceChanged = true;

                                priceTv.setText(tempMenuName + " - " + tempPrice + " ???");

                            }

                            @Override
                            public void onNegativeClicked() {

                            }
                        });
                        dialog_retouch_nonfree.show();
                    }
                } else {
                    if (!reserv_complete) {
                        showMenuList();
                    } else {
                        priceTv.setText(reserv_menu + " - " + price + " ???");
                    }

                }


            }
        });

        radioLl = findViewById(R.id.radioLl);
        cardRb = findViewById(R.id.cardRb);
        cardRb.setTextColor(Color.BLACK);
        cashRb = findViewById(R.id.cashRb);
        cashRb.setTextColor(Color.BLACK);

        if (reserv_cardCash.startsWith("??????")) {
            cardRb.setChecked(true);
        } else if (reserv_cardCash.startsWith("??????")) {
            cashRb.setChecked(true);
        }

        memoTv = findViewById(R.id.each_memoTv);
        materialEt = findViewById(R.id.materialEt);
        materialEt.setText(reserv_materialMemo);
        surgeryNoteEt = findViewById(R.id.surgeryNoteEt);
        surgeryNoteEt.setText(reserv_surgeryMemo);


        if (customer_name.contains("?????????")) {
            nameLl.setVisibility(View.INVISIBLE);
            numberLl.setVisibility(View.INVISIBLE);
            addBtnLl.setVisibility(View.VISIBLE);
        } else {
            nameTv.setText(customer_name);
            numberTv.setText(customer_number);
            numberTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        }

        dateTv.setText(reserv_date + " " + reserv_time);
        if (!reserv_retouch.equals("?????????")) { //???????????? ?????? ??????

            if (!reserv_complete) {
                priceTv.setText("????????? ??????????????????");
                priceTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            } else {
                priceTv.setText(reserv_menu + " - " + price);
            }

        } else { //???????????? ??????
            if (price == 0) { //?????? ?????????
                Log.d("menuTest", reserv_menu + "  ???????????????");
                priceTv.setText("???????????????");
                radioLl.setVisibility(View.GONE);
            } else {
                Log.d("menuTest", reserv_menu + "  ???????????????");
                priceTv.setText("??????????????? - " + price + " ???");
                priceTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            }
        }


        memoTv.setText(shortmemo);


        frameRl = findViewById(R.id.frameRl);
        frameRl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //?????? ???????????? ?????? ??????
        findViewById(R.id.noShowBtn).setOnClickListener(this);
        findViewById(R.id.completeBtn).setOnClickListener(this);
        findViewById(R.id.cancelBtn).setOnClickListener(this);
        findViewById(R.id.goToMemberBtn).setOnClickListener(this);
        findViewById(R.id.sharingBtn).setOnClickListener(this);
        addCusBtn.setOnClickListener(this);
        pickCusBtn.setOnClickListener(this);
        numberTv.setOnClickListener(this);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //??????????????? ????????? ????????????
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return true;
        } else {
            finish();
            return false;
        }
    }

//    @Override
//    public void onBackPressed() {
//        //??????????????? ????????? ??????
//        return;
//    }


    //MainActivity?????? ????????? ??????
    public void getDataFromMain() {

        Intent fromMainActivity = getIntent();
        customer_name = fromMainActivity.getStringExtra("name");
        Log.d("name2", customer_name);
        customer_number = fromMainActivity.getStringExtra("number");
        Log.d("name2", customer_number);
        reserv_date = fromMainActivity.getStringExtra("date");
        reserv_time = fromMainActivity.getStringExtra("time");
        reserv_retouch = fromMainActivity.getStringExtra("retouch");
        price = fromMainActivity.getIntExtra("price", 0);
        shortmemo = fromMainActivity.getStringExtra("memo");
        reserv_complete = fromMainActivity.getBooleanExtra("complete", false);
        reserv_noshow = fromMainActivity.getBooleanExtra("noshow", false);
        reserv_menu = fromMainActivity.getStringExtra("menu");
        reserv_cardCash = fromMainActivity.getStringExtra("cardcash");
        reserv_materialMemo = fromMainActivity.getStringExtra("materialMemo");
        reserv_surgeryMemo = fromMainActivity.getStringExtra("surgeryMemo");
        reserv_crtdate = fromMainActivity.getStringExtra("crtDate");
        Log.d("cardCash", reserv_cardCash + "? ");
    }

    //?????? ?????? ?????????
    @SuppressLint("LongLogTag")
    @Override
    public void onClick(View v) {
        Intent intentForNoShow = new Intent();
        getEachCustomerData();
        switch (v.getId()) {
            case R.id.noShowBtn:
                if (customer_name.contains("?????????")) {//????????? ????????? ???  ??????????????? ?????????
                    showToast("????????? ?????? ?????? ?????? ????????????");
                } else {
                    //?????? ????????? ?????????
                    if (!reserv_complete) {
                        updateEventData2(reserv_crtdate, customer_name, customer_number);
                        Log.d("updateEventData Test - name : ", customer_name);
                        Log.d("updateEventData Test - nbr : ", customer_number);

                        int currentNoShow = eachCustomerData.NOSHOWCOUNT;
                        updateNoShow(customer_name, customer_number, currentNoShow + 1);
                        intentForNoShow.putExtra("complete", true);
                        intentForNoShow.putExtra("noshow", true);
                        intentForNoShow.putExtra("materialMemo", materialEt.getText().toString());
                        intentForNoShow.putExtra("surgeryMemo", surgeryNoteEt.getText().toString());

                        //??????????????? ?????? ??????????????? ??????
                        intentForNoShow.putExtra("customer_name", customer_name);
                        intentForNoShow.putExtra("reserv_date", reserv_date);
                        intentForNoShow.putExtra("reserv_time", reserv_time);

                        setResult(RESULT_OK, intentForNoShow);
                        finish();
                    }
                    //??? ?????? ????????? ?????? ???
                    else {
                        finish();
                    }
                }


                break;

            case R.id.completeBtn:
                if (customer_name.contains("?????????")) {//????????? ????????? ???  ??????????????? ?????????
                    showToast("????????? ?????? ?????? ?????? ????????????");
                } else {

                    updateEventData2(reserv_crtdate, customer_name, customer_number);
                    Log.d("complete Test - name : ", customer_name);
                    Log.d("complete Test - nbr : ", customer_number);

                    //?????? ????????? ?????????
                    if (!reserv_complete) {
                        //?????????
                        if (reserv_retouch.equals("?????????")) {
                            //?????? ?????????
                            if (priceTv.getText().toString().startsWith("??????")) {
                                intentForNoShow.putExtra("complete", true);
                                intentForNoShow.putExtra("noshow", false);
                                intentForNoShow.putExtra("cardcash", "free");
                                intentForNoShow.putExtra("materialMemo", materialEt.getText().toString());
                                intentForNoShow.putExtra("surgeryMemo", surgeryNoteEt.getText().toString());

                                //??????????????? ?????? ??????????????? ??????
                                intentForNoShow.putExtra("customer_name", customer_name);
                                intentForNoShow.putExtra("reserv_date", reserv_date);
                                intentForNoShow.putExtra("reserv_time", reserv_time);

                                setResult(RESULT_OK, intentForNoShow);
                                finish();
                            } else {
                                //?????? ?????????
                                if (!cashRb.isChecked() && !cardRb.isChecked()) {
                                    showToast("?????? ????????? ??????????????????");
                                } else {
                                    //????????? ?????????????????? ????????? ???????????? ????????? ?????????

                                    //?????? ????????? ?????? ????????? ???????????? ?????????
                                    if (isPriceChanged) {
                                        //????????? ???????????? ????????????
                                        updateEventForPrice(customer_name, reserv_date, reserv_time, tempPrice);

                                        intentForNoShow.putExtra("complete", true);
                                        intentForNoShow.putExtra("noshow", false);
                                        if (cardRb.isChecked()) {//??????????????? ?????????
                                            intentForNoShow.putExtra("cardcash", "??????");
                                        } else {//??????????????? ????????????
                                            intentForNoShow.putExtra("cardcash", "??????");
                                        }

                                        intentForNoShow.putExtra("materialMemo", materialEt.getText().toString());
                                        intentForNoShow.putExtra("surgeryMemo", surgeryNoteEt.getText().toString());

                                        //??????????????? ?????? ??????????????? ??????
                                        intentForNoShow.putExtra("customer_name", customer_name);
                                        intentForNoShow.putExtra("reserv_date", reserv_date);
                                        intentForNoShow.putExtra("reserv_time", reserv_time);

                                        setResult(RESULT_OK, intentForNoShow);
                                        finish();
                                    } else { //?????? ?????? X
                                        intentForNoShow.putExtra("complete", true);
                                        intentForNoShow.putExtra("noshow", false);
                                        if (cardRb.isChecked()) {//??????????????? ?????????
                                            intentForNoShow.putExtra("cardcash", "??????");
                                        } else {//??????????????? ????????????
                                            intentForNoShow.putExtra("cardcash", "??????");
                                        }
                                        intentForNoShow.putExtra("materialMemo", materialEt.getText().toString());
                                        intentForNoShow.putExtra("surgeryMemo", surgeryNoteEt.getText().toString());

                                        //??????????????? ?????? ??????????????? ??????
                                        intentForNoShow.putExtra("customer_name", customer_name);
                                        intentForNoShow.putExtra("reserv_date", reserv_date);
                                        intentForNoShow.putExtra("reserv_time", reserv_time);

                                        setResult(RESULT_OK, intentForNoShow);
                                        finish();
                                    }

                                }

                            }

                        }

                        //????????? ?????? ??????
                        else {
                            //?????? ????????? ?????? ????????????
                            if (priceTv.getText().toString().startsWith("??????")) {
                                showToast("????????? ??????????????????");
                            } else {
                                if (!cardRb.isChecked() && !cashRb.isChecked()) {
                                    showToast("??????????????? ??????????????????");
                                } else {
                                    if (tempPrice == -1) { //???????????? ????????? ???????????? ???
                                        updateEventForPrice(customer_name, reserv_date, reserv_time, menusArrayList.get(tempPosition).PRICE);
                                        Log.d("menuTest", "price : " + menusArrayList.get(tempPosition).PRICE);
                                        updateEventForMenu(customer_name, reserv_date, reserv_time, menusArrayList.get(tempPosition).ITEM);
                                    } else {
                                        //?????? ????????? ??????
                                        updateEventForPrice(customer_name, reserv_date, reserv_time, tempPrice);
                                        updateEventForMenu(customer_name, reserv_date, reserv_time, tempMenuName);
                                    }

                                    intentForNoShow.putExtra("complete", true);
                                    intentForNoShow.putExtra("noshow", false);
                                    if (cardRb.isChecked()) {
                                        intentForNoShow.putExtra("cardcash", "??????");
                                    } else {
                                        intentForNoShow.putExtra("cardcash", "??????");
                                    }
                                    intentForNoShow.putExtra("materialMemo", materialEt.getText().toString());
                                    intentForNoShow.putExtra("surgeryMemo", surgeryNoteEt.getText().toString());

                                    //??????????????? ?????? ??????????????? ??????
                                    intentForNoShow.putExtra("customer_name", customer_name);
                                    intentForNoShow.putExtra("reserv_date", reserv_date);
                                    intentForNoShow.putExtra("reserv_time", reserv_time);

                                    setResult(RESULT_OK, intentForNoShow);
                                    finish();
                                }


                            }
                        }
                    }
                    //??? ?????? ?????? ????????? ?????????
                    else {
                        intentForNoShow.putExtra("complete", true);
                        intentForNoShow.putExtra("noshow", false);
                        intentForNoShow.putExtra("cardcash", reserv_cardCash);
                        intentForNoShow.putExtra("materialMemo", materialEt.getText().toString());
                        intentForNoShow.putExtra("surgeryMemo", surgeryNoteEt.getText().toString());

                        //??????????????? ?????? ??????????????? ??????
                        intentForNoShow.putExtra("customer_name", customer_name);
                        intentForNoShow.putExtra("reserv_date", reserv_date);
                        intentForNoShow.putExtra("reserv_time", reserv_time);

                        setResult(RESULT_OK, intentForNoShow);
                        finish();
                    }
                }
                break;

            case R.id.cancelBtn:
                finish();
                break;

            case R.id.goToMemberBtn:
                Intent goToMemberIntent = new Intent(getApplicationContext(), EachMember.class);
                goToMemberIntent.putExtra("customername", customer_name);
                goToMemberIntent.putExtra("number", customer_number);
//                goToMemberIntent.putExtra("noshow", );

                finish();

                startActivity(goToMemberIntent);
                break;

            case R.id.sharingBtn:
                Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
                Sharing_intent.setType("text/plain");

                String Test_Message = "????????? Text";

                Sharing_intent.putExtra(Intent.EXTRA_TEXT, Test_Message);

                Intent Sharing = Intent.createChooser(Sharing_intent, "????????????");
                startActivity(Sharing);
                break;

            case R.id.addCusBtn:
                Intent intentToAddCus = new Intent(getApplicationContext(), Activity_Create_Customer.class);
                intentToAddCus.putExtra("toMemberList", "each_event_activity");
                startActivityForResult(intentToAddCus, REQUEST_ADD_CUS);
                break;

            case R.id.numberTv:
                ShowAlertDialogWithListview();
                break;
            case R.id.pickCusBtn:
                Intent intent = new Intent(getApplicationContext(), MemberList.class);
                intent.putExtra("toMemberList", "each_event_activity");
                startActivityForResult(intent, REQUEST_PICK_CUS);
                break;

        }
    }

    //?????? ???????????? ????????????
    public Customers getEachCustomerData() {
        Log.d("tag", ownername);
        dbOpenHelper_customer = new DBOpenHelper_Customer(this);
        SQLiteDatabase database = dbOpenHelper_customer.getReadableDatabase();
        Cursor cursor = dbOpenHelper_customer.ReadEachMember(customer_name, customer_number, database);
        while (cursor.moveToNext()) {
            String ownername2 = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.OWNER_NAME));
            String customername = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.CUSTOMER_NAME));
            String number = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.NUMBER));
            String grade = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.GRADE));
            String recommend = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.RECOMMEND));
            String point = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.POINT));
            String visit = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.VISIT));
            String memo = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.MEMO));
            String savedate = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.SAVEDATE));
            int noshowcount = cursor.getInt(cursor.getColumnIndex(DBStructure_Customer.NOSHOWCOUNT));
            eachCustomerData = new Customers(ownername2, customername, number, grade, recommend, point, visit, memo, savedate, noshowcount);
        }
        cursor.close();
        return eachCustomerData;
    }

    //?????? ??????????????? ?????????
    public void showMenuList() {
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.8); // Display ???????????? 90%
        int height = (int) (dm.heightPixels * 0.6); // Display ???????????? 90%

        menusArrayList = loadMenusData();
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(EachEventActivity.this);
        builder1.setCancelable(true);
        View addView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_dialog_menulist, null);

        final ListView listView = addView.findViewById(R.id.menuListView);
        myAdapter = new MyAdapter(EachEventActivity.this);
        listView.setAdapter(myAdapter);

        final Button selfBtn = addView.findViewById(R.id.selfBtn);

        builder1.setView(addView);
        final AlertDialog ab4 = builder1.create();
        ab4.show();

        ViewGroup.LayoutParams params = ab4.getWindow().getAttributes();
        params.width = width;
        params.height = height;
        ab4.getWindow().setAttributes((WindowManager.LayoutParams) params);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tempPosition = position;
                tempPrice = -1;
                priceTv.setText(menusArrayList.get(position).ITEM + " - " + menusArrayList.get(position).PRICE + " ???");
                ab4.dismiss();
            }
        });

        selfBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog_Price dialog = new CustomDialog_Price(EachEventActivity.this);
                dialog.setDialogListener(new CustomDialog_Price.CustomDialogListener() {

                    @Override
                    public void onPositiveClicked(String menuName, int price) {
                        tempMenuName = menuName;
                        tempPrice = price;

                        showToast("????????? ???????????? ????????? ?????????????????????.");

                        priceTv.setText(tempMenuName + " - " + tempPrice + " ???");
                        ab4.dismiss();


                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });
                dialog.show();

            }
        });


    }

    //============== ?????? ??????????????? ?????? ????????? ======================
    class RowHolder {
        TextView iconTvHolder;
        TextView itemTvHolder;
        TextView priceHolder;
    }

    class MyAdapter extends ArrayAdapter {
        LayoutInflater lnf;

        public MyAdapter(android.app.Activity context) {
            super(context, R.layout.custom_listview_menu_row, menuArraylist);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return menuArraylist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return menuArraylist.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            RowHolder viewHolder;
            if (convertView == null) {
                convertView = lnf.inflate(R.layout.custom_listview_menu_row, parent, false);
                viewHolder = new RowHolder();

                viewHolder.iconTvHolder = convertView.findViewById(R.id.icon);
                viewHolder.itemTvHolder = convertView.findViewById(R.id.menu_tv);
                viewHolder.priceHolder = convertView.findViewById(R.id.price_tv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RowHolder) convertView.getTag();
            }

            DecimalFormat myFormatter = new DecimalFormat("###,###");
            int intPrice = menuArraylist.get(position).PRICE;
            String formattedStringPrice = myFormatter.format(intPrice);

            viewHolder.itemTvHolder.setText(menuArraylist.get(position).ITEM);
            viewHolder.priceHolder.setText(formattedStringPrice + " ???");

            return convertView;
        }
    }

    //=================================???????????? ????????? ??????=================================
    public void ShowAlertDialogWithListview() {
        List<String> numberMethod = new ArrayList<String>();
        numberMethod.add("???????????? " + "[" + customer_name + "]");
        numberMethod.add("SMS ????????? " + "[" + customer_name + "]");
        numberMethod.add("??????");

        //Create sequence of items
        final CharSequence[] Animals = numberMethod.toArray(new String[numberMethod.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(customer_number);
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = Animals[item].toString(); //Selected item in listview
                if (selectedText.startsWith("??????")) {
                    Uri uri = Uri.parse("tel:" + customer_number);
                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);

                    startActivity(intent);
                } else if (selectedText.startsWith("SMS")) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    Uri uri = Uri.parse("sms:" + customer_number);

                    intent.setData(uri);
//                    intent.putExtra("sms_body", customer_number);

                    startActivity(intent);
                } else {
                    //??????????????? ????????? ????????????
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("number data", customer_number);
                    clipboard.setPrimaryClip(clipData);

                    Toast toast = Toast.makeText(getApplicationContext(), "?????????????????????", Toast.LENGTH_SHORT);

                    toast.setGravity(Gravity.TOP, 0, 500);

                    toast.show();
                }
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_PICK_CUS:
                addBtnLl.setVisibility(View.INVISIBLE);
                nameLl.setVisibility(View.VISIBLE);
                numberLl.setVisibility(View.VISIBLE);

                customer_name = data.getStringExtra("customer_name");
                customer_number = data.getStringExtra("customer_number");
                nameTv.setText(customer_name);
                nameTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                nameTv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), MemberList.class);
                        intent.putExtra("toMemberList", "each_event_activity");
                        startActivityForResult(intent, REQUEST_PICK_CUS);
                    }
                });
                numberTv.setText(customer_number);
                numberTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                showToast("??? ??????????");
                break;

            case REQUEST_ADD_CUS:
                addBtnLl.setVisibility(View.INVISIBLE);
                nameLl.setVisibility(View.VISIBLE);
                numberLl.setVisibility(View.VISIBLE);

                customer_name = data.getStringExtra("customer_name");
                customer_number = data.getStringExtra("customer_number");
                nameTv.setText(customer_name);

                numberTv.setText(customer_number);
                numberTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                break;
        }
    }
}