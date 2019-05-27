package com.example.samuelteguh.smokedetectioncam.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samuelteguh.smokedetectioncam.MainActivity;
import com.example.samuelteguh.smokedetectioncam.Model.DetectionInfo;
import com.example.samuelteguh.smokedetectioncam.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

public class DetailData extends Fragment {

    private TextView textView, textView1, textView2, textView3;
    private ImageView imageView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef, ref;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri filePath;
    Button buttonActivation;
    DetectionInfo currentDetection;

    String dateID, urlExtra, timeExtra , imgPath, urlFirebase, detectionKey, imgName, coDValue, smokeDValue;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_detail, container,false);
        textView = view.findViewById(R.id.dateDetail);
        imageView = view.findViewById(R.id.imageView);
        textView1 = view.findViewById(R.id.coDetail);
        textView2 = view.findViewById(R.id.smokeDetail);
        textView3 = view.findViewById(R.id.timeDetail);

        buttonActivation = view.findViewById(R.id.buttonActivation);

        dateID = getArguments().getString("date");
        urlExtra = getArguments().getString("url");
        timeExtra =getArguments().getString("time");
        detectionKey = getArguments().getString("detectionKey");
        coDValue = getArguments().getString("co");
        smokeDValue = getArguments().getString("smoke");
        imgName = dateID + "_" + timeExtra;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("detectionInfo");
        ref = mFirebaseDatabase.getReference("sensorActivation");



        textView.setText(dateID);
        textView1.setText("CO Value : " + coDValue );
        textView2.setText("Smoke Value : " + smokeDValue );
        textView3.setText("Time : " + timeExtra);
        buttonActivation.setVisibility(View.GONE);
        if(urlExtra.contains("firebase")==false){
            buttonActivation.setVisibility(View.VISIBLE);
            addFilePath();
            Picasso.with(getActivity()).load(urlExtra).into(picassoImageTarget(getActivity(), "DetectionPhoto",  imgName + ".jpeg"));
            uploadImage();
        }



        Picasso.with(getActivity()).load(urlExtra).into(imageView);

        buttonActivation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ref.child("capture").setValue("1");
                buttonActivation.setVisibility(View.GONE);
            }
        });
        return view;
    }
    private void setUrlFirebase(final String detectionKey) {
        mRef.child(detectionKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentDetection = dataSnapshot.getValue(DetectionInfo.class);

                currentDetection.setUrl(urlFirebase);
                mRef.child(detectionKey).child("url").setValue(urlFirebase);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
        Log.d("picassoImageTarget", " picassoImageTarget");
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                File myDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), imageDir);
                try {



                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }
                    myDir = new File(myDir, imageName);
                    FileOutputStream out = new FileOutputStream(myDir);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                    out.flush();
                    out.close();
                } catch(Exception e){
                    // some action
                }
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };
    }

    private void uploadImage() {

        filePath = Uri.fromFile(new File(imgPath));
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ dateID +"/" + imgName + ".jpeg");
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            urlFirebase = taskSnapshot.getDownloadUrl().toString();

                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                            setUrlFirebase(detectionKey);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private void addFilePath(){
        String path = System.getenv("EXTERNAL_STORAGE");
        imgPath = path + "/Pictures/DetectionPhoto/" + imgName + ".jpeg";
    }

    public void onResume(){
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle("Database");
        //((MainActivity) getActivity())
        //        .setBackBar(true);
    }


}
