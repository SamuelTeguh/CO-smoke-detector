package com.example.samuelteguh.smokedetectioncam.Fragment;

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
import com.example.samuelteguh.smokedetectioncam.Model.Date;
import com.example.samuelteguh.smokedetectioncam.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetectionListFrag extends Fragment {

    private RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    FirebaseRecyclerAdapter<Date, ViewHolder> firebaseRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list, container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Date");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Date, ViewHolder>(
                        Date.class,
                        R.layout.list_item,
                        ViewHolder.class,
                        mRef
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, final Date date, int position){
                        viewHolder.setDetail(getActivity(), date.getDateSort());
                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongCLick) {
                                Bundle bundle = new Bundle();
                                bundle.putString("dateSort", date.getDateSort());
                                DatabaseDetail fragobj=new DatabaseDetail();
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
                .setActionBarTitle("Data List");
        //((MainActivity) getActivity())
        //        .setBackBar(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity())
                .setActionBarTitle("Home");
    }
}

