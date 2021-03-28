package com.example.atthatcustomerwithcal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter_Messages extends RecyclerView.Adapter<RecyclerAdapter_Messages.ViewHolder_Messages> {

    ArrayList<Instance_Messages> list;

    RecyclerAdapter_Messages(ArrayList<Instance_Messages> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolder_Messages onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_view_row_message, parent, false);


        return new ViewHolder_Messages(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_Messages holder, int position) {
        holder.title.setText(list.get(position).title);
        holder.date.setText(list.get(position).date);
        holder.content.setText(list.get(position).content);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder_Messages extends RecyclerView.ViewHolder {
        TextView title, date, content;

        public ViewHolder_Messages(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTvForMessage);
            date = itemView.findViewById(R.id.dateTvForMessage);
            content = itemView.findViewById(R.id.contentTvForMessage);
        }
    }
}


