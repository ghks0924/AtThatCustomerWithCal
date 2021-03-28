package com.example.atthatcustomerwithcal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerAdapter_Memo extends RecyclerView.Adapter<RecyclerAdapter_Memo.ViewHolder_Memo> {

    ArrayList<Instance_Memo> list;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }


    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    RecyclerAdapter_Memo(ArrayList<Instance_Memo> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolder_Memo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_view_row_memo, parent, false);


        return new ViewHolder_Memo(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_Memo holder, int position) {

        //====================date====================
        //현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddhhmmss");

        // nowDate 변수에 값을 저장한다.
        String nowYear = sdfNow.format(date).substring(0, 4);
        String nowMonthDay = sdfNow.format(date).substring(4, 8);

        //메모의 데이터 구하기
        String tmpDate = list.get(position).date;
        String memoYear = tmpDate.substring(0, 4);
        String memoMonthDay = tmpDate.substring(4, 8);

        String dspDateStr;
        if (nowYear.equals(memoYear)) { //현재 년도와 메모의 저장된 년도가 같으면
            if (nowMonthDay.equals(memoMonthDay)) { //월,일도 같으면
                dspDateStr = tmpDate.substring(8, 10) + "시 " + tmpDate.substring(10, 12) + "분";
            } else { //다르면
                dspDateStr = tmpDate.substring(4, 6) + "월 "
                        + tmpDate.substring(6, 8) + "일";
            }
        } else { //다르면
            dspDateStr = tmpDate.substring(0, 4) + "년 " + tmpDate.substring(4, 6) + "월 "
                    + tmpDate.substring(6, 8) + "일";
        }

        //==============================title===============================

        String titleStr = list.get(position).title.trim();

        if (titleStr.equals("미입력_제목")) {
            holder.title.setVisibility(View.GONE);
        } else {
            holder.title.setText(list.get(position).title);
        }


        holder.date.setText(dspDateStr);
        holder.content.setText(list.get(position).content);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder_Memo extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title, date, content;

        public ViewHolder_Memo(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.single_row_cardview_memo);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) { // pos가 있다면
                        //리스너 객체의 메서드 호출.
                        if (mListener != null) ;
                        mListener.onItemClick(v, pos);
                    }


                }
            });
            title = itemView.findViewById(R.id.titleTvForMessage);
            date = itemView.findViewById(R.id.dateTvForMessage);
            content = itemView.findViewById(R.id.contentTvForMessage);
        }
    }
}


