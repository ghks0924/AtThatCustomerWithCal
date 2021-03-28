package com.example.atthatcustomerwithcal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Fragment_Memo extends Fragment {

    HomeActivity homeActivity;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    ArrayList<Instance_Memo> arr = new ArrayList<>();
    RecyclerAdapter_Memo adapter_memo;
//    MyAdapter adapter;

    boolean isRefresh = false;


    public Fragment_Memo() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        homeActivity = (HomeActivity) getActivity();
        Log.d("lifeCycle_memo", "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeActivity = null;
        Log.d("lifeCycle_memo", "onDetach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Firebase 초기화
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Log.d("lifeCycle_memo", "onCreate");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("lifeCycle_memo", "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("lifeCycle_memo", "onDestroy");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_memo,
                container, false);

        //화면요소
        FloatingActionButton floatingActionButton;
        RecyclerView recyclerView;
        ListView listView;


        //initLayout
        floatingActionButton = rootview.findViewById(R.id.fab);
//        listView = rootview.findViewById(R.id.listview_fragment_memo);

        adapter_memo = new RecyclerAdapter_Memo(arr);
        recyclerView = rootview.findViewById(R.id.recyclerview_memo);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter_memo);

        RecyclerDecoration_Height decoration_height = new RecyclerDecoration_Height(20);
        recyclerView.addItemDecoration(decoration_height);


        adapter_memo.setOnItemClickListener(new RecyclerAdapter_Memo.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String pickedMemoTitle = arr.get(position).title.trim();
                String pickedMemoDate = arr.get(position).date.trim();
                String pickedMemoContent = arr.get(position).content.trim();

                Log.d("memoData", "pickedMemoTitle : " + pickedMemoTitle);
                Log.d("memoData", "pickedMemoDate : " + pickedMemoDate);
                Log.d("memoData", "pickedMemoContent : " + pickedMemoContent);

                Intent updateMemoIntent = new Intent(homeActivity, Update_Memo.class);
                updateMemoIntent.putExtra("pickedMemoTitle", pickedMemoTitle);
                updateMemoIntent.putExtra("pickedMemoDate", pickedMemoDate);
                updateMemoIntent.putExtra("pickedMemoContent", pickedMemoContent);

                startActivity(updateMemoIntent);


                Toast.makeText(getActivity(), "뭐냐", Toast.LENGTH_SHORT).show();
            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.onGoToCreateMemoActivity();
                Toast.makeText(getActivity(), "메모추가", Toast.LENGTH_SHORT).show();
            }
        });

        if (!isRefresh) {
            Log.d("lifeCycle_memo", "when Refresh is false : " + isRefresh);
            LoadMemoDataFromDB();
            isRefresh = true;
        } else {
            Log.d("lifeCycle_memo", "when Refresh is true : " + isRefresh);
        }


//        adapter = new MyAdapter(homeActivity);
//        listView.setAdapter(adapter);


        //리스트뷰 아이템클릭 리스너
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                String pickedMemoTitle = arr.get(position).title.trim();
//                String pickedMemoDate = arr.get(position).date.trim();
//                String pickedMemoContent = arr.get(position).content.trim();
//
//                Log.d("memoData", "pickedMemoTitle : " + pickedMemoTitle );
//                Log.d("memoData", "pickedMemoDate : " + pickedMemoDate );
//                Log.d("memoData", "pickedMemoContent : " + pickedMemoContent );
//
//                Intent updateMemoIntent = new Intent(homeActivity, Update_Memo.class);
//                updateMemoIntent.putExtra("pickedMemoTitle", pickedMemoTitle);
//                updateMemoIntent.putExtra("pickedMemoDate", pickedMemoDate);
//                updateMemoIntent.putExtra("pickedMemoContent", pickedMemoContent);
//
//                startActivity(updateMemoIntent);
//
//
//                Toast.makeText(getActivity(), "뭐냐", Toast.LENGTH_SHORT).show();
//            }
//        });


        return rootview;

    }


    //    class RowHolder{
//        TextView titleTvHolder;
//        TextView dateTvHolder;
//        TextView contentTvHolder;
//    }
//
//    class MyAdapter extends ArrayAdapter {
//        LayoutInflater lnf;
//
//        public MyAdapter(Activity context) {
//            super(context, R.layout.recycler_view_row_memo, arr);
//            lnf = (LayoutInflater) context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//
//        @Override
//        public int getCount() {
//            // TODO Auto-generated method stub
//            return arr.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            // TODO Auto-generated method stub
//            return arr.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            // TODO Auto-generated method stub
//            return position;
//        }
//
//        @SuppressLint("ResourceAsColor")
//        public View getView(int position, View convertView, ViewGroup parent) {
//            RowHolder viewHolder;
//            if(convertView == null) {
//                convertView = lnf.inflate(R.layout.recycler_view_row_memo, parent, false);
//                viewHolder = new RowHolder();
//
//                viewHolder.titleTvHolder = convertView.findViewById(R.id.titleTvForMemo);
//                viewHolder.dateTvHolder = convertView.findViewById(R.id.dateTvForMemo);
//                viewHolder.contentTvHolder = convertView.findViewById(R.id.contentTvForMemo);
//
//                convertView.setTag(viewHolder);
//            }else{
//                viewHolder = (RowHolder) convertView.getTag();
//            }
//
//            // 현재시간을 msec 으로 구한다.
//            long now = System.currentTimeMillis();
//            // 현재시간을 date 변수에 저장한다.
//            Date date = new Date(now);
//            // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
//            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddhhmmss");
//
//            // nowDate 변수에 값을 저장한다.
//            String nowYear = sdfNow.format(date).substring(0,4);
//            String nowMonthDay = sdfNow.format(date).substring(4,8);
//
//            //메모의 데이터 구하기
//            String tmpDate = arr.get(position).date;
//            String memoYear = tmpDate.substring(0,4);
//            String memoMonthDay = tmpDate.substring(4,8);
//
//            String dspDateStr;
//            if (nowYear.equals(memoYear)){ //현재 년도와 메모의 저장된 년도가 같으면
//                if (nowMonthDay.equals(memoMonthDay)){ //월,일도 같으면
//                    dspDateStr = tmpDate.substring(8,10)+ "시 " + tmpDate.substring(10,12)+"분";
//                } else { //다르면
//                    dspDateStr = tmpDate.substring(4,6) + "월 "
//                            + tmpDate.substring(6,8) + "일";
//                }
//            } else { //다르면
//                dspDateStr = tmpDate.substring(0,4) + "년 " + tmpDate.substring(4,6) + "월 "
//                        + tmpDate.substring(6,8) + "일";
//            }
//
//            String titleStr = arr.get(position).title.trim();
//
//            if (titleStr.equals("미입력_제목")){
//                viewHolder.titleTvHolder.setVisibility(View.GONE);
//            } else {
//                viewHolder.titleTvHolder.setText("무야호");
//            }
//
//            viewHolder.dateTvHolder.setText(dspDateStr);
//            viewHolder.contentTvHolder.setText(arr.get(position).content);
//
//            viewHolder.dateTvHolder.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getActivity(),"이건", Toast.LENGTH_LONG);
//                }
//            });
//
//
//            return convertView;
//        }
//
//
//    }
//
    public void LoadMemoDataFromDB() {
        arr.clear();
        CollectionReference colRef = db.collection("users")
                .document(mAuth.getCurrentUser().getEmail())
                .collection("memos");


        colRef.orderBy("memoDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String memoTitle = document.getString("memoTitle");
                                String memoDate = document.getString("memoDate");
                                String memoContent = document.getString("memoContent");
                                Log.d("FIRESTORE_MEMO", memoDate + " : " + memoContent);
                                arr.add(new Instance_Memo(memoTitle, memoDate, memoContent));
                            }
                            ;
                            adapter_memo.notifyDataSetChanged();
                        } else {
                            Log.d("Err getting documents:", String.valueOf(task.getException()));
                        }
                    }
                });
    }

}
