package com.example.teamproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.teamproject.models.Ad;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;

public class CreatePostActivity extends AppCompatActivity {

    String TAG = "CreatePostActivity";

    //String MAP_KEY = getString(R.string.google_maps_api_key);

    EditText etAdName;
    CalendarView cvAdDate;
    EditText etAdStartTime;
    EditText etAdEndTime;
    EditText etAdDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        etAdName = (EditText) findViewById(R.id.etAdName);
        cvAdDate = (CalendarView) findViewById(R.id.cvAdDate);
        etAdStartTime = (EditText) findViewById(R.id.etAdStartTime);
        etAdEndTime = (EditText) findViewById(R.id.etEndTime);
        etAdDesc = (EditText) findViewById(R.id.etAdDesc);

    }

    public void grabPlace(View view) {

    }

    public void submitAd(View view) {
        Log.d(TAG, "Posting...");
        Ad newAd = new Ad();
        newAd.setUser(ParseUser.getCurrentUser());
        newAd.setTitle(etAdName.getText().toString());
        newAd.setDate(new Date(cvAdDate.getDate() * 1000));
        newAd.setStartTime(etAdStartTime.getText().toString());
        newAd.setEndTime(etAdEndTime.getText().toString());
        newAd.setDescription(etAdDesc.getText().toString());
        newAd.setRSVP(new ArrayList<Object>());

        newAd.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Posting successful!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CreatePostActivity.this, HomeFeedActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Posting Failed!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

}