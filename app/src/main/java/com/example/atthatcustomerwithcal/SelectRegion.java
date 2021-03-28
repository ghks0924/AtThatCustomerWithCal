package com.example.atthatcustomerwithcal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectRegion extends Activity_Base {

    GridView gridView;
    ArrayList<String> regionArrayList;
    MyAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_region);


        setData();

        gridView = findViewById(R.id.gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setData();
                Intent intent = new Intent();
                intent.putExtra("region", regionArrayList.get(position).trim());
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        adapter = new MyAdapter(this);
        gridView.setAdapter(adapter);


    }

    class RowHolder{
        TextView tvHolder;
    }

    class MyAdapter extends ArrayAdapter {
        LayoutInflater lnf;

        public MyAdapter(android.app.Activity context) {
            super(context, R.layout.single_cell_layout_region, regionArrayList);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return regionArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return regionArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            RowHolder viewHolder;
            if(convertView == null) {
                convertView = lnf.inflate(R.layout.single_cell_layout_region, parent, false);
                viewHolder = new RowHolder();

                viewHolder.tvHolder = convertView.findViewById(R.id.regionTv);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (RowHolder)convertView.getTag();
            }

            viewHolder.tvHolder.setText(regionArrayList.get(position));


            return convertView;
        }


    }

    private void setData(){
        regionArrayList = new ArrayList<>();
        regionArrayList.add("서울");
        regionArrayList.add("경기");
        regionArrayList.add("인천");
        regionArrayList.add("부산");
        regionArrayList.add("대전");
        regionArrayList.add("대구");
        regionArrayList.add("울산");
        regionArrayList.add("광주");
        regionArrayList.add("세종");
        regionArrayList.add("제주");
        regionArrayList.add("강원");
        regionArrayList.add("충북");
        regionArrayList.add("충남");
        regionArrayList.add("전북");
        regionArrayList.add("전남");
        regionArrayList.add("경북");
        regionArrayList.add("경남");
    }
}