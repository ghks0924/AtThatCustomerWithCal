package com.example.atthatcustomerwithcal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Fragment_Message extends Fragment {

    HomeActivity homeActivity;

    ArrayList<Instance_Messages> list;
    RecyclerAdapter_Messages adapter_messages;

    public Fragment_Message() {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_message,
                container, false);

        //화면요소
        ImageView smsIv = rootview.findViewById(R.id.smsIv);
        smsIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.onGoToSMS();
            }
        });


        list = new ArrayList<>();
        list.add(new Instance_Messages("제목", "날짜", "내용"));
        list.add(new Instance_Messages("제목", "날짜", "내용"));
        list.add(new Instance_Messages("제목", "날짜", "내용"));
        list.add(new Instance_Messages("제목", "날짜", "내용"));
        list.add(new Instance_Messages("제목", "날짜", "내용"));


        adapter_messages = new RecyclerAdapter_Messages(list);
        RecyclerView recyclerView = rootview.findViewById(R.id.recyclerview_messages);

        int numberOfCoulmns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),numberOfCoulmns));
        recyclerView.setAdapter(adapter_messages);

        RecyclerDecoration_Height decoration_height = new RecyclerDecoration_Height(20);
        recyclerView.addItemDecoration(decoration_height);

        RecyclerDecoration_Width decoration_width = new RecyclerDecoration_Width(20);
        recyclerView.addItemDecoration(decoration_width);


        return rootview;

    }
}
