package com.example.teamproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.teamproject.models.Ad;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    Ad ad;
    int userCount;

    ViewFlipper viewFlipper;
    TextView titleTV;
    TextView locationTV;
    TextView dateTV;
    TextView timeTV;
    TextView descriptionTV;
    TextView userNameTV;
    TextView emailTV;
    Button rsvpBT;
    ImageView profImageIV;
    TextView attendingCount;
    FloatingActionButton fabDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Log.i(TAG, "in ON Create");
        ad = Parcels.unwrap(getIntent().getParcelableExtra(Ad.class.getSimpleName()));
        userCount = ad.getRSVPCount();

        //imageIV = findViewById(R.id.ivImage);
        viewFlipper = findViewById(R.id.viewFlipper);
        titleTV = findViewById(R.id.tvTitle);
        locationTV = findViewById(R.id.tvLocation);
        dateTV = findViewById(R.id.tvDate);
        timeTV = findViewById(R.id.tvTime);
        descriptionTV = findViewById(R.id.tvDescription);
        userNameTV = findViewById(R.id.tvUserName);
        emailTV = findViewById(R.id.tvUserEmail);
        rsvpBT = findViewById(R.id.btRSVP);
        attendingCount = findViewById(R.id.tvAttendingCount);
        profImageIV = findViewById(R.id.profile_image);
        fabDelete = findViewById(R.id.fabDelete);

        Log.d(TAG, "Ownership is being checked...");
        isOwner();
        Log.d(TAG, "Ownership checked been checked.");

        if (isUserRegistered()) {
            showUserRegistered();
        } else {
            showUserUnregistered();
        }

        rsvpBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserRegistered()) {
                    unRegisterUser();
                    showUserUnregistered();
                } else {
                    registerUser();
                    showUserRegistered();
                }
            }
        });

        initViewFlipper();

        //sets event host details
        //need to use fetchIfNeeded, or else causes error
        try {
            userNameTV.setText(ad.getUser().fetchIfNeeded().getString("username"));
            emailTV.setText(ad.getUser().fetchIfNeeded().getString("email"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //set profile image and details
        ParseFile profImageFile = ad.getUser().getParseFile("profileImage");
        String profImageURL = null;
        try {
            profImageURL = profImageFile.getUrl();
        } catch (NullPointerException e) {

        }
        Glide.with(getApplicationContext())
                .load(profImageURL)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.avatar))
                .into(profImageIV);
        titleTV.setText(ad.getTitle());
        locationTV.setText(ad.getLocation());

        Date date = ad.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
        dateTV.setText(sdf.format(date));

        SimpleDateFormat stf = new SimpleDateFormat("h:mm a");
        timeTV.setText(stf.format(date) + " - " + ad.getEndTime());

        descriptionTV.setText(ad.getDescription());
    }

    private void showUserUnregistered() {
        rsvpBT.setText("RSVP");
        userCount = ad.getRSVPCount();
        attendingCount.setText(userCount + " people attending!");
    }

    private void showUserRegistered() {
        rsvpBT.setText("Un-RSVP");
        userCount = ad.getRSVPCount();
        attendingCount.setText("See you there!");
    }

    //Registers/RSVP the user for the event
    private void registerUser() {
        ParseUser.getCurrentUser().addUnique("attendingEvents", ad);
        ParseUser.getCurrentUser().saveInBackground();
        ad.addUnique("rsvp", ParseUser.getCurrentUser());
        ad.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                showUserRegistered();
            }
        });
        Log.i(TAG, "User registered");
    }

    //Un-Registers/RSVP the user for the event
    private void unRegisterUser() {
        ParseUser.getCurrentUser().removeAll("attendingEvents", Collections.singleton(ad));
        ParseUser.getCurrentUser().saveInBackground();
        ad.removeAll("rsvp", Collections.singleton(ParseUser.getCurrentUser()));
        ad.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                showUserUnregistered();
            }
        });
    }

    //checks to see if the user is currently registered/RSVPed
    private boolean isUserRegistered() {
        ParseConfig.getInBackground();
        List<Object> rsvpList = ad.getRSVP();
        for (int i = 0; i < rsvpList.size(); i++) {
            ParseUser obj = (ParseUser) rsvpList.get(i);
            if (obj.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                Log.i(TAG, "isUserRegistered: true");
                return true;
            }
        }
        Log.i(TAG, "isUserRegistered: false");
        return false;
    }


    private void initViewFlipper() {
        if (viewFlipper != null) {
            viewFlipper.setInAnimation(getApplicationContext(), android.R.anim.slide_in_left);
            viewFlipper.setOutAnimation(getApplicationContext(), android.R.anim.slide_out_right);
        }

        if (viewFlipper != null) {
            for (ParseFile file : ad.getImages()) {
                ImageView imageView = new ImageView(getApplicationContext());

                Bitmap bmp = null;
                try {
                    bmp = BitmapFactory.decodeByteArray(file.getData(), 0, file.getData().length);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bmp);
                viewFlipper.addView(imageView);
                //starts flipping for more than one image
                if (ad.getImages().size() > 1) {
                    viewFlipper.startFlipping();
                }
            }
        }
    }

    public boolean isOwner() {
        if (ParseUser.getCurrentUser().getUsername().equals(ad.getUser().getUsername())) {
            Log.d(TAG, "User is Owner!");
            fabDelete.show();
            return true;
        } else {
            Log.d(TAG, "User is NOT Owner!");
            fabDelete.hide();
            return false;
        }
    }

    public void onDelete(View view) {
        if (isOwner()) {
            Intent home = new Intent(DetailActivity.this, HomeFeedActivity.class);
            ad.deleteInBackground();
            startActivity(home);
        }
    }
}
