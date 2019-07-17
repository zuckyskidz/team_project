package com.example.teamproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.teamproject.models.Ad;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    Ad ad;
    List rsvpList;

    ImageView imageIV;
    TextView titleTV;
    TextView descriptionTV;
    TextView userNameTV;
    TextView emailTV;
    Button rsvpBT;
    TextView attendingCount;
    private boolean flag_RSVP = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // unwrap the movie passed in via intent, using its simple name as a key
        ad = (Ad) Parcels.unwrap(getIntent().getParcelableExtra(Ad.class.getSimpleName()));

        imageIV = findViewById(R.id.ivImage);
        titleTV = findViewById(R.id.tvTitle);
        descriptionTV = findViewById(R.id.tvDescription);
        userNameTV = findViewById(R.id.tvUserName);
        emailTV = findViewById(R.id.tvUserEmail);
        rsvpBT = findViewById(R.id.btRSVP);
        attendingCount = findViewById(R.id.tvAttendingCount);


        rsvpList = ad.getRSVP();
        final int userCount  = rsvpList.size();
        if(rsvpList != null){
            Log.d(TAG, Integer.toString(rsvpList.size()));
            attendingCount.setText( userCount+" people already attending!");
        }

        rsvpBT.setOnClickListener(new View.OnClickListener() {
            //TODO -toggle button to correct position when the user reopens app
            @Override
            public void onClick(View v) {
                if (flag_RSVP) {
                    flag_RSVP = false; //Button ON
                    rsvpBT.setText("RSVP");
                    attendingCount.setText( userCount+" people already attending!");
                    //removes user from RSVP List
                    ad.removeAll("rsvp", Collections.singleton(ParseUser.getCurrentUser()));
                    ad.saveInBackground();
                    Toast.makeText(DetailActivity.this, "Un-RSVP successful!", Toast.LENGTH_SHORT).show();
                    //attendingCount.setText( userCount +" people already attending!");

                } else {
                    flag_RSVP = true; //Button OFF
                    rsvpBT.setText("Un-RSVP");
                    attendingCount.setText("See you there!");
                    //add user to RSVP List
                    ad.addUnique("rsvp", ParseUser.getCurrentUser());
                    ad.saveInBackground();
                    Toast.makeText(DetailActivity.this, "RSVP successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        titleTV.setText(ad.getTitle());
        descriptionTV.setText(ad.getDescription());

        ParseFile imageFile = ad.getImage();
        String imageURL = imageFile.getUrl();
        Glide.with(getApplicationContext())
                .load(imageURL)
                //.load(ad.getImage())
                .into(imageIV);

        //need to use fetchIfNeeded, or else causes error
        try {
            userNameTV.setText(ad.getUser().fetchIfNeeded().getString("username"));
        } catch (ParseException e) {
            e.printStackTrace();
        };
        try {
            emailTV.setText(ad.getUser().fetchIfNeeded().getString("email"));
            //Log.i(TAG, "success");
        } catch (ParseException e) {
            //Log.i(TAG, "error");
            e.printStackTrace();
        };


    }

}
