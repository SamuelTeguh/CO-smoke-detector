package com.example.samuelteguh.smokedetectioncam.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samuelteguh.smokedetectioncam.MainActivity;
import com.example.samuelteguh.smokedetectioncam.Model.Sensor;
import com.example.samuelteguh.smokedetectioncam.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SensorSettingFrag extends Fragment {
    private Button buttonSensor;
    private EditText editCO, editSmoke;
    private TextView textView1, textView2;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private final String sensorID = "-LTVVx742f5hTjLsud9L";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sensor_setting, container,false);
        buttonSensor = (Button) view.findViewById(R.id.buttonSensor);
        editCO = (EditText) view.findViewById(R.id.editCO);
        editSmoke = (EditText) view.findViewById(R.id.editSmoke);
        textView1 = view.findViewById(R.id.cSensorCO);
        textView2 = view.findViewById(R.id.cSensorSmoke);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("sensorValue");

        mRef.child(sensorID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Sensor sensor = dataSnapshot.getValue(Sensor.class);
                textView1.setText("Current CO Sensor: "+ sensor.getCoValue() + "ppm");
                textView2.setText("Current Smoke Sensor: " + sensor.getSmokeValue() + "ppm");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        buttonSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editSensorValue(sensorID);
            }
        });
        return view;
    }

    public void editSensorValue(String sensorID){
        String coValue = editCO.getText().toString();
        String smokeValue = editSmoke.getText().toString();
        if(!TextUtils.isEmpty(coValue) && !TextUtils.isEmpty(smokeValue)){

            DatabaseReference ref = mRef.child(sensorID);
            Sensor sensor = new Sensor(sensorID,coValue,smokeValue);

            ref.setValue(sensor);
            editCO.setText("");
            editSmoke.setText("");


        }else
        {
            Toast.makeText(getActivity(),"Enter Sensor Value", Toast.LENGTH_LONG).show();
        }
    }
    public void onResume(){
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle("Sensor Setting");
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity())
                .setActionBarTitle("Home");
    }
}
