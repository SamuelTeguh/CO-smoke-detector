package com.example.samuelteguh.smokedetectioncam.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.samuelteguh.smokedetectioncam.MainActivity;
import com.example.samuelteguh.smokedetectioncam.Model.Date;
import com.example.samuelteguh.smokedetectioncam.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class BarChartFrag extends Fragment {

    GraphView graphView;
    BarGraphSeries series;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    java.util.Date dateFormat;
    int numOfDate = 5;
    String numOfDateS;
    Button buttonDate;
    EditText editDateNum;
    TextView textDateNum;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chart, container,false);
        graphView = (GraphView) view.findViewById(R.id.graphView);
        buttonDate = (Button) view.findViewById(R.id.buttonDate);
        editDateNum = (EditText) view.findViewById(R.id.editDateNum);
        textDateNum = (TextView) view.findViewById(R.id.cSensorCO);
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Date");
        series = new BarGraphSeries();
        graphView.addSeries(series);
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(90);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(numOfDate);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(10);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScrollableY(true);
        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numOfDateS = editDateNum.getText().toString();
                numOfDate = Integer.parseInt(numOfDateS);
                series = new BarGraphSeries();
                graphView.addSeries(series);
                graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
                graphView.getGridLabelRenderer().setHorizontalLabelsAngle(90);
                graphView.getGridLabelRenderer().setNumHorizontalLabels(numOfDate);
                graphView.getViewport().setYAxisBoundsManual(true);
                graphView.getViewport().setMinY(0);
                graphView.getViewport().setMaxY(10);
                graphView.getViewport().setScrollable(true);
                graphView.getViewport().setScrollableY(true);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataPoint[] dp = new DataPoint[(int) dataSnapshot.getChildrenCount()];
                int index = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Date date = ds.getValue(Date.class);
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        dateFormat = format.parse(date.getDateSort());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    int i = Integer.parseInt(date.getCount());
                    dp[index]= new DataPoint(dateFormat,i);
                    index++;
                }
                series.resetData(dp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void onResume(){
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle("Data Chart");
        //((MainActivity) getActivity())
        //        .setBackBar(true);
    }
}
