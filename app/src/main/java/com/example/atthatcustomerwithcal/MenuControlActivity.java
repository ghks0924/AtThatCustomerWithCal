package com.example.atthatcustomerwithcal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MenuControlActivity extends Activity_Base implements AdapterView.OnItemLongClickListener {

    //리스트뷰를 위한 요소
    ArrayList<MenuList> menuArraylist = new ArrayList<>();
    MyAdapter adapter;


    //화면 생성요소
    ListView listView;
    Button addBtn;

    //메뉴 저장을 위한 임시 요소
    String tempMenuName;
    int tempPrice;

    //고객 지우기위한 요소
    String menuName;
    int menuPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_control);

        loadMenusData();

        // toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //뒤로가기 버튼, 디폴트값임
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //앱 제목 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //화면요소
        listView = findViewById(R.id.listview);
        listView.setOnItemLongClickListener(this);



        addBtn = findViewById(R.id.addBtn);




        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog_Price dialog = new CustomDialog_Price(MenuControlActivity.this);
                dialog.setDialogListener(new CustomDialog_Price.CustomDialogListener() {


                    @Override
                    public void onPositiveClicked(String menuName, int price) {
                        tempMenuName = menuName;
                        tempPrice = price;

                        if (menuName.equals("null") || price == -1){
                            showToast("메뉴의 이름이나 가격을 입력하여주세요.");

                        } else {
                            saveMenu(ownername,tempMenuName,tempPrice);

                            loadMenusData();
                            adapter.notifyDataSetChanged();
                        }


                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });
                dialog.show();
            }
        });

        adapter = new MyAdapter(this);
        listView.setAdapter(adapter);


    } //onCreate



    //뒤로가기 버튼에 기능 추가
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    int position;//포지션 넘버

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;

        //삭제하려고 파라미터 저장
        menuName = menuArraylist.get(position).ITEM;
        menuPrice = menuArraylist.get(position).PRICE;

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("회원 삭제");
        ab.setIcon(R.mipmap.ic_launcher);
        ab.setMessage(menuArraylist.get(position).ITEM + " 메뉴를 삭제하시겠습니까? ");
        ab.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteMenu();
            }
        });
        ab.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ab.setCancelable(true);
        ab.show();

        //디폴트는 false인데 이러면 롱 실행시 그냥 ItemClick이 실행
        //그래서 true로 바꿈
        return true;

    }

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
            viewHolder.priceHolder.setText(formattedStringPrice + " 원");

            return convertView;
        }
    }


    //메뉴정보 불러오는 메서드
    public ArrayList<MenuList> loadMenusData() {
        menuArraylist.clear();

        Log.d("tag", ownername);

        ArrayList<MenuList> arrayList = new ArrayList<>();
        dbOpenHelper_menu = new DBOpenHelper_Menu(this);
        SQLiteDatabase database = dbOpenHelper_menu.getReadableDatabase();
        Cursor cursor = dbOpenHelper_menu.ReadMenus(ownername, database);
        while (cursor.moveToNext()) {
            String ownername2 = cursor.getString(cursor.getColumnIndex(DBStructure_Menu.OWNER_NAME));
            String menuname = cursor.getString(cursor.getColumnIndex(DBStructure_Menu.MENUNAME));
            int menuprice = cursor.getInt(cursor.getColumnIndex(DBStructure_Menu.MENUPRICE));

            MenuList menuList = new MenuList(ownername2, menuname, menuprice);
            arrayList.add(menuList);

        }
        cursor.close();

        menuArraylist.addAll(arrayList);


        return menuArraylist;

    }

    private void saveMenu(String ownername, String menuname, int menuprice){
        dbOpenHelper_menu = new DBOpenHelper_Menu(this);
        SQLiteDatabase database = dbOpenHelper_menu.getWritableDatabase();
        dbOpenHelper_menu.SaveMenu(ownername, menuname, menuprice, database);
        dbOpenHelper_menu.close();

        Toast.makeText(this,"메뉴가 추가되었습니다.", Toast.LENGTH_SHORT);
    }

    public void deleteMenu() {

        dbOpenHelper_menu = new DBOpenHelper_Menu(this);
        SQLiteDatabase database = dbOpenHelper_menu.getWritableDatabase();
        dbOpenHelper_menu.deleteMenu(menuName, menuPrice, database);
        dbOpenHelper_menu.close();

        loadMenusData();
        adapter.notifyDataSetChanged();

    }



}