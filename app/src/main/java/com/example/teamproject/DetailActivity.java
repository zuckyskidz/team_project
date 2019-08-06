package com.example.teamproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.widget.EmojiTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.teamproject.models.Ad;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    private static final int ZXING_CAMERA_PERMISSION = 1;

    Ad ad;
    int userCount;
    private Class<?> mClss;

    Button qrScanBTN;
    Button viewAttendeesBTN;
    ListView lvAttendees;
    EmojiTextView tagsTV;
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
    RatingBar rbLevel;
    FloatingActionButton fabDelete;

    View popupView;
    PopupWindow popupWindow;
    ArrayAdapter<String> adapter;
    ArrayList<String> names;


    @Override
    protected void onResume(){
        super.onResume();
        if(adapter!=null) {
            populate();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
        setContentView(R.layout.activity_detail);


        popupView = getLayoutInflater().inflate(R.layout.attendees_list_popup, null);

        popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        names = new ArrayList<>();
        lvAttendees = popupView.findViewById(R.id.lvAttendees);

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
        rbLevel = findViewById(R.id.rbLevels);

        rbLevel.setRating(ad.getLevel());
        fabDelete = findViewById(R.id.fabDelete);

        Log.d(TAG, "Ownership is being checked...");
        isOwner();
        Log.d(TAG, "Ownership checked been checked.");
        qrScanBTN = findViewById(R.id.btnQRScan);
        viewAttendeesBTN = findViewById(R.id.btnAttendees);
        tagsTV = (EmojiTextView) findViewById(R.id.tvTags);

        Map<String, Integer> myMap = new HashMap<String, Integer>();
        myMap.put("food", 0x1F37D);
        myMap.put("sports", 0x1F3C3);
        myMap.put("age", 0x1F37E);
        myMap.put("arts", 0x1F3AD);
        myMap.put("holiday", 0x1F383);
        myMap.put("music", 0x1F3B6);

        for(int i =0; i <ad.getTags().size(); i++ ){
            String tag = ad.getTags().get(i);
            tagsTV.setText(tagsTV.getText()+tag + "  ");
            int emoji = myMap.get(tag);
            tagsTV.setText(tagsTV.getText() + new String(Character.toChars(emoji))+ "  ");
        }

        if(ParseUser.getCurrentUser().getObjectId().equals(ad.getUser().getObjectId())){
            qrScanBTN.setVisibility(View.VISIBLE);
            viewAttendeesBTN.setVisibility(View.VISIBLE);

            viewAttendeesBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    populate();
                    adapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, names);
                    lvAttendees.setAdapter(adapter);
                    showPopup(v);
                    Log.i(TAG, String.valueOf(names.size()));
                    Log.i(TAG, String.valueOf(adapter.getCount()));
                }
            });

            qrScanBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), ScannerActivity.class);
                    i.putExtra(Ad.class.getSimpleName(), Parcels.wrap(ad));
                    startActivity(i);
                }
            });
        }

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
                    if (checkLevel()) {
                        registerUser();
                        showUserRegistered();
                    } else {
                        Snackbar.make(rsvpBT,
                                "You need to level up before being able to register for this event!",
                                Snackbar.LENGTH_LONG).show();
                    }
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
        ParseUser.getCurrentUser().put("numAttended",
                (ParseUser.getCurrentUser().getInt("numAttended") + 1));
        ParseUser.getCurrentUser().saveInBackground();
        ad.addUnique("rsvp", ParseUser.getCurrentUser());
        ad.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                showUserRegistered();
            }
        });
        Log.i(TAG, "User registered");
        updateLevel();
    }

    //Un-Registers/RSVP the user for the event
    private void unRegisterUser() {
        ParseUser.getCurrentUser().removeAll("attendingEvents", Collections.singleton(ad));
        ParseUser.getCurrentUser().put("numAttended",
                (ParseUser.getCurrentUser().getInt("numAttended") - 1));
        ParseUser.getCurrentUser().saveInBackground();
        ad.removeAll("rsvp", Collections.singleton(ParseUser.getCurrentUser()));
        ad.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                showUserUnregistered();
            }
        });
        updateLevel();
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

    public boolean checkLevel() {
        return ParseUser.getCurrentUser().getInt("level") >= ad.getLevel();
    }

    public void updateLevel() {
        int numAttended = ParseUser.getCurrentUser().getInt("numAttended");
        int[] totals = new int[5];
        totals[0] = 0; //Level 1
        totals[1] = 3; //Level 2
        totals[2] = 5; //Level 3
        totals[3] = 10; //Level 4
        totals[4] = 50; //Level 5

        for (int i = 0; i < totals.length; i++) {
            if (numAttended == totals[i]) {
                levelUp(i);
                totals[i] = 0;
                break;

            }
        }
    }


    private void levelUp(int index) {
        int level = ParseUser.getCurrentUser().getInt("level");
        if (level == index) {
            ParseUser.getCurrentUser().put("level", level + 1);
            ParseUser.getCurrentUser().put("numAttended", 0);
            ParseUser.getCurrentUser().saveInBackground();
        }
        Toast.makeText(this, "You are now Level " + (level + 1) + "!", Toast.LENGTH_LONG).show();
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

    private void showPopup(View anchorView) {
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        Log.i(TAG, String.valueOf(popupView.isShown()));

    }

    private void populate() {
        try {
            ad.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(names.size() > 0){
            names.clear();
        }
        List<Object> attendees = ad.getAttendees();
        Log.i(TAG, String.valueOf(ad.getAttendees().size()));
        for(int i = 0; i < attendees.size(); i++){
            ParseUser user = (ParseUser) attendees.get(i);
            try {
                names.add( user.fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}
