package com.example.teamproject;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HomeFeedActivity extends AppCompatActivity {

    Fragment listFrag;
    Fragment mapFrag;
    Fragment profileFrag;
    ViewPager vpHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);

        vpHome = (ViewPager) findViewById(R.id.vpHome);
    }
}
