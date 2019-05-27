package com.example.samuelteguh.smokedetectioncam.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.samuelteguh.smokedetectioncam.Helper.ViewHolder;
import com.example.samuelteguh.smokedetectioncam.Interface.ItemClickListener;
import com.example.samuelteguh.smokedetectioncam.MainActivity;

import com.example.samuelteguh.smokedetectioncam.Model.DetectionInfo;
import com.example.samuelteguh.smokedetectioncam.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseDetail extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference shoeList;

    String categoryId;

    FirebaseRecyclerAdapter<DetectionInfo, ViewHolder> adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list, container,false);

        // Firebase
        database = FirebaseDatabase.getInstance();
        shoeList = database.getReference("detectionInfo");

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoryId=getArguments().getString("dateSort");
        // Get intent
        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter = new FirebaseRecyclerAdapter<DetectionInfo, ViewHolder>(DetectionInfo.class,
                R.layout.list_item,
                ViewHolder.class,
                shoeList.orderByChild("date").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, final DetectionInfo detectionInfo, int position) {
                viewHolder.setDetail(getActivity(),detectionInfo.getTime());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongCLick) {
                        Bundle bundle = new Bundle();
                        bundle.putString("date", detectionInfo.getDate());
                        bundle.putString("url", detectionInfo.getUrl());
                        bundle.putString("time", detectionInfo.getTime());
                        bundle.putString("co", detectionInfo.getCoDValue());
                        bundle.putString("smoke", detectionInfo.getSmokeDValue());
                        bundle.putString("detectionKey",adapter.getRef(position).getKey());
                        DetailData fragobj=new DetailData();
                        fragobj.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, fragobj);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });

            }
        };

        // Set adapter
        recyclerView.setAdapter(adapter);
    }
    public void onResume(){
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle("Sort by Date");
        //((MainActivity) getActivity())
        //        .setBackBar(true);
    }

}
