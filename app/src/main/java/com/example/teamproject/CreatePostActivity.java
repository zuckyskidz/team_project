package com.example.teamproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.teamproject.models.Ad;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;

import static com.parse.Parse.getApplicationContext;

public class CreatePostActivity extends AppCompatActivity {

    EditText etAdName;
    CalendarView cvAdDate;
    EditText etAdStartTime;
    EditText etAdEndTime;
    EditText etAdAddress;
    EditText etAdDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        etAdName = (EditText) findViewById(R.id.etAdName);
        cvAdDate = (CalendarView) findViewById(R.id.cvAdDate);
        etAdStartTime = (EditText) findViewById(R.id.etAdStartTime);
        etAdEndTime = (EditText) findViewById(R.id.etEndTime);
        etAdAddress = (EditText) findViewById(R.id.etAdAddress);
        etAdDesc = (EditText) findViewById(R.id.etAdDesc);

    }

    public void submitAd(View view) {
        Ad newAd = new Ad();
        newAd.setUser(ParseUser.getCurrentUser());
        newAd.setTitle(etAdName.getText().toString());
        newAd.setDate(new Date(cvAdDate.getDate() * 1000));
        newAd.setStartTime(etAdStartTime.getText().toString());
        newAd.setEndTime(etAdEndTime.getText().toString());
        newAd.setAddress(etAdAddress.getText().toString());
        newAd.setDescription(etAdDesc.getText().toString());

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