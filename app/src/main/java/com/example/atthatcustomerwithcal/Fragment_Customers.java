package com.example.atthatcustomerwithcal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Fragment_Customers extends Fragment {

    HomeActivity homeActivity;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ArrayList<Customers> arr = new ArrayList<>();
    MyAdapter adapter;

    //고객 보여주는 순서 저장변수
    boolean isABC;

    public Fragment_Customers() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        homeActivity = (HomeActivity) getActivity();

    }

    @Override
    public void onDetach() {
        super.onDetach();

        homeActivity = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            //Firebase 초기화
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_customers,
                container, false);

        //화면요소
        FloatingActionButton floatingActionButton;
        final TextView abcTv;
        final TextView savedTv;
        EditText searchEt;
        ListView listView;


        //initLayout
        floatingActionButton = rootview.findViewById(R.id.fab);
        abcTv = rootview.findViewById(R.id.abcTv);
        savedTv = rootview.findViewById(R.id.savedTv);
        searchEt = rootview.findViewById(R.id.searchEt);
        listView = rootview.findViewById(R.id.listview);

        LoadCusDataFromDB();
//        arr.add(new Customers("asd", "asd", "01055445555","01055445555",
//                "01055445555","01055445555","01055445555","01055445555","01055445555", 6));

        adapter = new MyAdapter(homeActivity);
        listView.setAdapter(adapter);

        //====버튼 클릭 리스터 ====
        floatingActionButton.setOnClickListener(new View.OnClickListener() { //플로팅버튼
            @Override
            public void onClick(View v) {

                homeActivity.onGoToAddCus();
            }
        });


        abcTv.setOnClickListener(new View.OnClickListener() { //가나다순 버튼
            @Override
            public void onClick(View v) {
                abcTv.setTextColor(Color.parseColor("#4f4f4f"));
                savedTv.setTextColor(Color.parseColor("#4D4f4f4f"));
            }
        });

        savedTv.setOnClickListener(new View.OnClickListener() { //저장순 버튼
            @Override
            public void onClick(View v) {
                abcTv.setTextColor(Color.parseColor("#4D4f4f4f"));
                savedTv.setTextColor(Color.parseColor("#4f4f4f"));
            }
        });

        //====리스트뷰 아이템 클릭 리스너====
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "이거쥐", Toast.LENGTH_SHORT).show();
            }
        });

        return rootview;

    }


    class RowHolder{
        TextView historyTvHolder;
        TextView nameTvHolder;
        TextView numberTvHolder;
        ImageView ivHolder;
    }

    class MyAdapter extends ArrayAdapter {
        LayoutInflater lnf;

        public MyAdapter(Activity context) {
            super(context, R.layout.single_row_memberlsit, arr);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arr.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return arr.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @SuppressLint("ResourceAsColor")
        public View getView(int position, View convertView, ViewGroup parent) {
            RowHolder viewHolder;
            if(convertView == null) {
                convertView = lnf.inflate(R.layout.single_row_memberlsit, parent, false);
                viewHolder = new RowHolder();

                viewHolder.historyTvHolder = convertView.findViewById(R.id.historyTv);
                viewHolder.nameTvHolder = convertView.findViewById(R.id.name_tv);
                viewHolder.numberTvHolder = convertView.findViewById(R.id.number_tv);
                viewHolder.ivHolder = convertView.findViewById(R.id.profile_iv);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (RowHolder)convertView.getTag();
            }




            viewHolder.nameTvHolder.setText(arr.get(position).CUSTOMER_NAME);


            String bfrNumber = arr.get(position).NUMBER;
            String dspNumber = bfrNumber.substring(0,3) + "-" + bfrNumber.substring(3,7) + "-" + bfrNumber.substring(7);

            viewHolder.numberTvHolder.setText(dspNumber);

            return convertView;
        }
    }

    public void LoadCusDataFromDB(){
        CollectionReference colRef = db.collection("users")
                .document(mAuth.getCurrentUser().getEmail())
                .collection("customers");


        final DocumentReference docRef = colRef.document();

        Log.d("readData", "collection path" + docRef.getPath());

        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Log.d("readData", document.getId() + " => " + document.getData());
                        arr.add(new Customers("owner", document.getString("cusName"), document.getString("cusNumber")
                        , "", "", "", "", document.getString("cusMemo"), "", 0));
                    };
                    Log.d("readData", arr.size()+ "");
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("Err getting documents:", String.valueOf(task.getException()));
                }
            }
        });
    }

}