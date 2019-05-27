package com.example.samuelteguh.smokedetectioncam.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.samuelteguh.smokedetectioncam.Helper.MyPageAdapter;
import com.example.samuelteguh.smokedetectioncam.Helper.PhotoViewHolder;
import com.example.samuelteguh.smokedetectioncam.Interface.ItemClickListener;
import com.example.samuelteguh.smokedetectioncam.MainActivity;

import com.example.samuelteguh.smokedetectioncam.Model.DetectionInfo;
import com.example.samuelteguh.smokedetectioncam.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class PhotoListFrag extends Fragment {
    private RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    MyPageAdapter myPageAdapter;

    FirebaseRecyclerAdapter<DetectionInfo, PhotoViewHolder> firebaseRecyclerAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list, container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        ViewPager mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(myPageAdapter);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("detectionInfo");


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<DetectionInfo, PhotoViewHolder>(
                        DetectionInfo.class,
                        R.layout.card_item,
                        PhotoViewHolder.class,
                        mRef.limitToLast(3)
                ) {
                    @Override
                    protected void populateViewHolder(PhotoViewHolder photoViewHolder, final DetectionInfo detectionInfo, int position){
                        photoViewHolder.textDate.setText(detectionInfo.getDate());
                        Picasso.with(getActivity()).load(detectionInfo.getUrl()).into(photoViewHolder.photoView);
                        photoViewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongCLick) {
                                Bundle bundle = new Bundle();
                                bundle.putString("date", detectionInfo.getDate());
                                bundle.putString("url", detectionInfo.getUrl());
                                bundle.putString("time", detectionInfo.getTime());
                                bundle.putString("co", detectionInfo.getCoDValue());
                                bundle.putString("smoke", detectionInfo.getSmokeDValue());
                                bundle.putString("detectionKey",firebaseRecyclerAdapter.getRef(position).getKey());
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
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    public void onResume(){
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle("Home");
        //((MainActivity) getActivity())
        //        .setBackBar(true);
    }

}
