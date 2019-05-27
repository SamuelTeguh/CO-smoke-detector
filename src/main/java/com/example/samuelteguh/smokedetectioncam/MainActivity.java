package com.example.samuelteguh.smokedetectioncam;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;


import com.example.samuelteguh.smokedetectioncam.Fragment.BarChartFrag;
import com.example.samuelteguh.smokedetectioncam.Fragment.DetectionListFrag;
import com.example.samuelteguh.smokedetectioncam.Fragment.PhotoListFrag;
import com.example.samuelteguh.smokedetectioncam.Fragment.SensorSettingFrag;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setActionBarTitle("Home");


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PhotoListFrag()).commit();
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PhotoListFrag()).commit();
                break;
            case R.id.nav_graph:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BarChartFrag()).addToBackStack(null).commit();
                break;
            case R.id.nav_database:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DetectionListFrag()).addToBackStack(null).commit();
                break;
            case R.id.nav_setting:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SensorSettingFrag()).addToBackStack(null).commit();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void setBackBar(boolean b){
        getSupportActionBar().setHomeButtonEnabled(b);
        getSupportActionBar().setDisplayHomeAsUpEnabled(b);
    }
}
