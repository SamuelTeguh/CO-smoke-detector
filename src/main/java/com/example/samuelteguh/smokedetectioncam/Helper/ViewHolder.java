package com.example.samuelteguh.smokedetectioncam.Helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.samuelteguh.smokedetectioncam.Interface.ItemClickListener;
import com.example.samuelteguh.smokedetectioncam.R;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View mView;
    private ItemClickListener itemClickListener;
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ViewHolder(View itemView){
        super(itemView);
        mView = itemView;
        itemView.setOnClickListener(this);
    }

    public void setDetail(Context ctx, String title){
        TextView dateList = mView.findViewById(R.id.dateList);
        dateList.setText(title);
    }



    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
