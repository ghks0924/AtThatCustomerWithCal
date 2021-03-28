package com.example.atthatcustomerwithcal;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MyGridAdapter extends ArrayAdapter {
    //그리드뷰를 위한 어댑터
    List<Date> dates;
    Calendar currentDate;
    List<Events> events;
    LayoutInflater inflater;

    Calendar todayCalendar = Calendar.getInstance();
    Integer todayInt = todayCalendar.get(Calendar.DAY_OF_MONTH);
    String todayStr = String.valueOf(todayInt);

    int tempPosition = -1;
    boolean isSelectedAtFirst;
    boolean isAppOpen = false;

    public HashMap<Integer, Boolean> hashMapSelected;

    public MyGridAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate, List<Events> events) {
        super(context, R.layout.single_cell_layout);

        this.dates = dates;
        this.currentDate = currentDate;
        this.events = events;

        hashMapSelected = new HashMap<>();
        for (int i = 0; i < dates.size(); i++) {
            hashMapSelected.put(i, false);
        }

        inflater = LayoutInflater.from(context);


    }

    public void makeAllUnselect(int position) {
        hashMapSelected.put(position, true);
        for (int i = 0; i < hashMapSelected.size(); i++) {
            if (i != position)
                hashMapSelected.put(i, false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int DayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH) + 1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.single_cell_layout, parent, false);
        }
        TextView Day_Number = view.findViewById(R.id.calendar_day);
        TextView EventNumber = view.findViewById(R.id.events_id);
        Day_Number.setText(String.valueOf(DayNo));

        if (displayMonth == currentMonth && displayYear == currentYear) { //현재달
//            view.setBackgroundColor(getContext().getResources().getColor(R.color.green));
            Day_Number.setTextColor(Color.parseColor("#211f1f")); //예스!!
//            Day_Number.setTypeface(null, Typeface.BOLD);
            if (position % 7 == 0) {
                Day_Number.setTextColor(Color.parseColor("#e50000"));
            }

            //토요일
//            if (position % 7 == 6) {
//                Day_Number.setTextColor(Color.BLUE);
//            }
        } else { //이전달과 다음달
//            view.setBackgroundColor(Color.parseColor("#cccccc"));

            Day_Number.setTextColor(Color.parseColor("#80e5e5e5"));
        }


        //오늘 표시
        if (displayMonth == todayCalendar.get(Calendar.MONTH) + 1 && todayInt == DayNo) {
//            Day_Number.setTextSize(20);
            Day_Number.setTypeface(Typeface.DEFAULT_BOLD);
            Day_Number.setTextColor(getContext().getResources().getColor(R.color.mainPink));
        }


        //event 개수 표시
        Log.d("DayNo", DayNo + "+" + todayStr);
        Calendar eventCalendar = Calendar.getInstance();
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            eventCalendar.setTime(ConvertStingToDate(events.get(i).getDATE()));
            if (DayNo == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH) + 1
                    && displayYear == eventCalendar.get(Calendar.YEAR)) {
                arrayList.add(events.get(i).getEVENT());
                EventNumber.setText(arrayList.size() + " 개 예약");
            }
        }


//        if (isAppOpen){
//            if (position == tempPosition) {
//                Log.d("posi", position + "");
//                Log.d("posi", "temp" + tempPosition + "");
//                view.setBackgroundColor(Color.DKGRAY);
//            }
//        } else {
//
//        }



        return view;
    }

    private Date ConvertStingToDate(String eventDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Date date = null;
        try {
            date = format.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Nullable
    @Override
    public Object getItem(int position) {

        Log.d("posi", "temp" + tempPosition + "");
        return dates.get(position);
    }
}

