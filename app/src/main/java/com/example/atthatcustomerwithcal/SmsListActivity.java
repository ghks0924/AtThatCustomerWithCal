package com.example.atthatcustomerwithcal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SmsListActivity extends Activity_Base {

    final String TAG_SMS = "TAG_SMS";
    ListView listView;
    MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_list);

        // toolbar
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //뒤로가기 버튼, 디폴트값임
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //앱 제목 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //저장된 smsTemplates 문자템플릿 불러오기
        smsTemplatesArrayList = ReadSmsTemplates();

        listView = findViewById(R.id.listview);
        adapter = new MyAdapter(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent addIntent = new Intent(getApplicationContext(), Add_SmsTemplate.class);
                addIntent.putExtra("isNew", false);
                addIntent.putExtra("position", position);
                startActivity(addIntent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder ab = new AlertDialog.Builder(SmsListActivity.this);
                ab.setTitle("문자 템플릿 삭제");
                ab.setIcon(R.mipmap.ic_launcher);
                ab.setMessage("삭제하시겠습니까?");
                ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTemplate(smsTemplatesArrayList.get(position).TITLE);


                        smsTemplatesArrayList = ReadSmsTemplates();
                        adapter = new MyAdapter(SmsListActivity.this);
                        listView.setAdapter(adapter);
                    }
                });
                ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                ab.setCancelable(true);
                ab.show();

                return true;
            }
        });


    }

    //뒤로가기 버튼에 기능 추가
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
            case R.id.action_btn1:
                Intent addIntent = new Intent(getApplicationContext(), Add_SmsTemplate.class);
                addIntent.putExtra("isNew", true);
                startActivity(addIntent);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //toolbar에 버튼 추가하려고
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_smslist, menu);
        return true;
    }

    class RowHolder{
        TextView idxTvHolder;
        TextView titleTvHolder;
    }

    //============================ sms 문자 리스트뷰 ===========================


    class MyAdapter extends ArrayAdapter {
        LayoutInflater lnf;

        public MyAdapter(android.app.Activity context) {
            super(context, R.layout.custom_listview_sms_row, smsTemplatesArrayList);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return smsTemplatesArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return smsTemplatesArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            RowHolder viewHolder;
            if(convertView == null) {
                convertView = lnf.inflate(R.layout.custom_listview_sms_row, parent, false);
                viewHolder = new RowHolder();

                viewHolder.idxTvHolder = convertView.findViewById(R.id.idx_tv);
                viewHolder.titleTvHolder = convertView.findViewById(R.id.title_tv);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (RowHolder)convertView.getTag();
            }


            viewHolder.idxTvHolder.setText(String.valueOf(position+1));
            viewHolder.titleTvHolder.setText(smsTemplatesArrayList.get(position).TITLE);

            viewHolder.idxTvHolder.setTextColor(Color.BLACK);
            viewHolder.titleTvHolder.setTextColor(Color.BLACK);

            return convertView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        smsTemplatesArrayList = ReadSmsTemplates();
        adapter = new MyAdapter(SmsListActivity.this);
        listView.setAdapter(adapter);

    }

    public void deleteTemplate(String title){
        dbOpenHelper_smsTemplate = new DBOpenHelper_SmsTemplate(getApplicationContext());
        SQLiteDatabase database = dbOpenHelper_smsTemplate.getWritableDatabase();
        dbOpenHelper_smsTemplate.DeleteTemplate(title, database);
        dbOpenHelper_smsTemplate.close();
    }
}