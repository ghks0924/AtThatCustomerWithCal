package com.example.atthatcustomerwithcal;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerDecoration_Width extends RecyclerView.ItemDecoration {
    private final int divWidth;

    public RecyclerDecoration_Width(int divWidth){
        this.divWidth = divWidth;
    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1){
            outRect.right = divWidth;
        }
    }

}
