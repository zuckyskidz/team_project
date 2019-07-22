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
import com.bumptech.glide.request.RequestOptions;
import com.example.teamproject.models.Ad;
import com.parse.Parse;
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
    int userCount;
    boolean flag_RSVP;

    //TODO - add location and dates
    ImageView imageIV;
    TextView titleTV;
    TextView descriptionTV;
    TextView userNameTV;
    TextView emailTV;
    Button rsvpBT;
    TextView attendingCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ad = (Ad) Parcels.unwrap(getIntent().getParcelableExtra(Ad.class.getSimpleName()));
        rsvpList = ad.getRSVP();
        userCount  = ad.getRSVPCount();

        imageIV = findViewById(R.id.ivImage);
        titleTV = findViewById(R.id.tvTitle);
        descriptionTV = findViewById(R.id.tvDescription);
        userNameTV = findViewById(R.id.tvUserName);
        emailTV = findViewById(R.id.tvUserEmail);
        rsvpBT = findViewById(R.id.btRSVP);
        attendingCount = findViewById(R.id.tvAttendingCount);

        //initialize button text to reflect user attendance status
        if(isUserRegistered()){
            registerUser();
        }
        else{
            unRegisterUser();
        }

        rsvpBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag_RSVP) {
                    unRegisterUser();
                } else {
                    registerUser();
                }
            }
        });


        titleTV.setText(ad.getTitle());
        descriptionTV.setText(ad.getDescription());

        ParseFile imageFile = ad.getImage();
        String imageURL = null;
        try {
            imageURL = imageFile.getUrl();
        } catch (NullPointerException e) {

        }
        Glide.with(getApplicationContext())
                .load(imageURL)
                .apply(new RequestOptions()
                .placeholder(R.drawable.dog))
                .into(imageIV);

        //need to use fetchIfNeeded, or else causes error
        try {
            userNameTV.setText(ad.getUser().fetchIfNeeded().getString("username"));
            emailTV.setText(ad.getUser().fetchIfNeeded().getString("email"));
        } catch (ParseException e) {
            e.printStackTrace();
        };
    }

    //Registers/RSVP the user for the event
    private void registerUser() {
        ad.registerUser();
        ad.saveInBackground();
        flag_RSVP = true;
        rsvpBT.setText("Un-RSVP");
        userCount  = ad.getRSVPCount();
        attendingCount.setText("See you there!");
        //ad.saveInBackground();
    }

    //Un-Registers/RSVP the user for the event
    private void unRegisterUser() {
        ad.unRegisterUser();
        ad.saveInBackground();
        flag_RSVP = false;
        rsvpBT.setText("RSVP");
        userCount  = ad.getRSVPCount();
        attendingCount.setText(userCount + " people attending!");
    }

    //checks to see if the user is currently registered/RSVPed
    private boolean isUserRegistered(){
        ParseUser user = ParseUser.getCurrentUser();
        List attendingEvents = user.getList("attendingEvents");
        return attendingEvents.contains(ad.getObjectId());

    }

}
