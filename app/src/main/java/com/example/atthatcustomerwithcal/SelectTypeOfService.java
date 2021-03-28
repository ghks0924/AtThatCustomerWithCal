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

public class SelectTypeOfService extends Activity_Base {

    GridView gridView;
    ArrayList<String> serviceArrayList;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type_of_service);

        setData();

        gridView = findViewById(R.id.gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setData();
                Intent intent = new Intent();
                intent.putExtra("region", serviceArrayList.get(position).trim());
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
            super(context, R.layout.single_cell_layout_region, serviceArrayList);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return serviceArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return serviceArrayList.get(position);
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
                viewHolder = (RowHolder) convertView.getTag();
            }

            viewHolder.tvHolder.setText(serviceArrayList.get(position));


            return convertView;
        }


    }

    private void setData(){
        serviceArrayList = new ArrayList<>();
        serviceArrayList.add("반영구");
        serviceArrayList.add("속눈썹");
        serviceArrayList.add("기타");

    }
}