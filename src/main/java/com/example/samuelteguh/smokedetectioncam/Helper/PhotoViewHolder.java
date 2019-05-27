package com.example.samuelteguh.smokedetectioncam.Helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.samuelteguh.smokedetectioncam.Interface.ItemClickListener;
import com.example.samuelteguh.smokedetectioncam.R;
import com.squareup.picasso.Picasso;

public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //View mView;
    public TextView textDate;
    public ImageView photoView;

    private ItemClickListener itemClickListener;
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public PhotoViewHolder(View itemView){
        super(itemView);
        textDate = itemView.findViewById(R.id.textDate);
        photoView = itemView.findViewById(R.id.photoView);
        itemView.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
