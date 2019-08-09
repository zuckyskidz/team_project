package com.example.teamproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import android.widget.ViewFlipper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.widget.EmojiButton;

import com.example.teamproject.models.Ad;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

//import android.support.v7.app.AppCompatActivity;
//import com.google.android.gms.location.places.Place;

public class CreatePostActivity extends AppCompatActivity {

    private static final String TAG = "CreatePostActivity";
    private final static int PICK_PHOTO_CODE = 1046;

    private final Calendar myCalendar = Calendar.getInstance();
    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }

    };
    PlacesClient placesClient;
    String localeString;
    ParseGeoPoint geoPoint;
    AutocompleteSupportFragment autocompleteFragment;

    EditText etAdName;
    TextView tvDisplayDate;
    TextView tvStartTime;
    TextView tvEndTime;
    EditText etAdDesc;
    ParseFile photoFile;
    ViewFlipper viewFlipper;
    private MenuItem miActionProgressItem;

    EmojiButton foodBTN;
    EmojiButton sportsBTN;
    EmojiButton ageBTN;
    EmojiButton artsBTN;
    EmojiButton holidayBTN;
    EmojiButton musicBTN;
    ArrayList<String> tags;


    private ArrayList<Bitmap> mBitmapsSelected;
    private ArrayList<ParseFile> mImages;

    RatingBar rbSetLevel;
    TextView tvLevelDisp;
    private Toolbar myToolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        setContentView(R.layout.activity_create_post);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_api_key));
        placesClient = Places.createClient(this);
        initPlacesSearch();

        foodBTN = findViewById(R.id.food);
        sportsBTN = findViewById(R.id.sports);
        ageBTN = findViewById(R.id.ageRestrictive);
        artsBTN = findViewById(R.id.arts);
        holidayBTN = findViewById(R.id.holiday);
        musicBTN = findViewById(R.id.music);
        tags = new ArrayList<>();

        foodBTN.setText(new StringBuilder(new String(Character.toChars(0x1F37D))));
        foodBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(foodBTN);
            }
        });
        sportsBTN.setText(new StringBuilder(new String(Character.toChars(0x1F3C3))));
        sportsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(sportsBTN);
            }
        });
        ageBTN.setText(new StringBuilder(new String(Character.toChars(0x1F37E))));
        ageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(ageBTN);
            }
        });
        artsBTN.setText(new StringBuilder(new String(Character.toChars(0x1F3AD))));
        artsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(artsBTN);
            }
        });
        holidayBTN.setText(new StringBuilder(new String(Character.toChars(0x1F383))));
        holidayBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(holidayBTN);
            }
        });
        musicBTN.setText(new StringBuilder(new String(Character.toChars(0x1F3B6))));
        musicBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(musicBTN);
            }
        });


        etAdName = findViewById(R.id.etAdName);
        tvEndTime = findViewById(R.id.tvTimeDisplay2);
        etAdDesc = findViewById(R.id.etAdDesc);
        tvDisplayDate = findViewById(R.id.tvDateDisplay);
        tvStartTime = findViewById(R.id.tvTimeDisplay);
        viewFlipper = findViewById(R.id.viewFlipper);

        etAdName = (EditText) findViewById(R.id.etAdName);
        tvEndTime = (TextView) findViewById(R.id.tvTimeDisplay2);
        etAdDesc = (EditText) findViewById(R.id.etAdDesc);
        tvDisplayDate = (TextView) findViewById(R.id.tvDateDisplay);
        tvStartTime = (TextView) findViewById(R.id.tvTimeDisplay);
        rbSetLevel = (RatingBar) findViewById(R.id.rbSetLevel);
        tvLevelDisp = (TextView) findViewById(R.id.tvLevelDisp);


        tvDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreatePostActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreatePostActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendar.set(Calendar.MINUTE, selectedMinute);
                        updateTimeLabel();
                    }
                }, 12, 00, false);
                mTimePicker.show();
            }
        });

        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreatePostActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String myFormatTime = "h:mm a"; //In which you need put here
                        SimpleDateFormat sdfTime = new SimpleDateFormat(myFormatTime, Locale.US);
                        Calendar myCal = Calendar.getInstance();
                        myCal.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCal.set(Calendar.MINUTE, selectedMinute);
                        tvEndTime.setText(String.format(sdfTime.format(myCal.getTime())));
                    }
                }, 12, 00, false);
                mTimePicker.show();
            }
        });


        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        myToolbar = (Toolbar) findViewById(R.id.local_toolbar);
        myToolbar.setTitle("Create Post");
//        myToolbar.inflateMenu(R.menu.menu_create_post);
//        miActionProgressItem = myToolbar.getMenu().getItem(1);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.quantum_white_text));
        setSupportActionBar(myToolbar);
    }

    private void initPlacesSearch() {
        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place LAT_LNG: " + place.getLatLng() + ", " + place.getName());
                //btnAdAddress.setText(place.getName());
                localeString = place.getAddress();
                geoPoint = new ParseGeoPoint();
                makeGeoPoint(place.getLatLng().toString());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    public void submitAd() {
        showProgressBar();
        Log.d(TAG, "Posting...");
        Ad newAd = new Ad();
        if (makeSurePostable()) {
            newAd = new Ad();
            newAd.setUser(ParseUser.getCurrentUser());
            newAd.setTitle(etAdName.getText().toString());
            newAd.setDate(myCalendar.getTime());
            newAd.setEndTime(tvEndTime.getText().toString());
            newAd.setAddress(localeString);
            newAd.setGeoPoint(geoPoint);
            newAd.setDescription(etAdDesc.getText().toString());
            newAd.setRSVP(new ArrayList<Object>());
            newAd.setImages(mImages);
            newAd.setLevel((int) rbSetLevel.getRating());
            newAd.setAttendees(new ArrayList<Object>());
            newAd.setTags(getTags());
        } else {
            Toast.makeText(CreatePostActivity.this, "Missing information.", Toast.LENGTH_SHORT).show();
            hideProgressBar();
            return;
            //newAd.setImage(photoFile);
        }
        postAd(newAd);
        hideProgressBar();
    }

    private List<String> getTags() {
        return tags;
    }

    private void postAd(Ad newAd) {
        newAd.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Posting successful!");
                    Toast.makeText(getApplicationContext(), "Posting successful!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CreatePostActivity.this, HomeFeedActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                } else {
                    Log.i(TAG, "FAILED");
                    Toast.makeText(getApplicationContext(), "Posting Failed!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    //Checks each field and verifies they are all filled out, or else changes color of field.
    private boolean makeSurePostable() {
        boolean isPostable = true;
        if (etAdName.getText().length() == 0) {
            Log.i(TAG, "title missing");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                etAdName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.local_orange)));
            } else {
                etAdName.setHintTextColor(getResources().getColor(R.color.local_orange));
            }
            isPostable = false;
        }
        if (tvDisplayDate.getText().length() == 0) {
            Log.i(TAG, "date missing");
            tvDisplayDate.setHintTextColor(getResources().getColor(R.color.local_orange));
            isPostable = false;
        }
        if (tvEndTime.getText().length() == 0) {
            Log.i(TAG, "end time missing");
            tvEndTime.setHintTextColor(getResources().getColor(R.color.local_orange));
            isPostable = false;
        }
        if (tvStartTime.getText().length() == 0) {
            Log.i(TAG, "end time missing");
            tvStartTime.setHintTextColor(getResources().getColor(R.color.local_orange));
            isPostable = false;
        }
        if (etAdDesc.getText().length() == 0) {
            Log.i(TAG, "description missing");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                etAdDesc.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.local_orange)));
            } else {
                etAdDesc.setHintTextColor(getResources().getColor(R.color.local_orange));
            }
            isPostable = false;
        }
        if (localeString == null) {
            Log.i(TAG, "location missing");
            isPostable = false;
        }
        if (mImages == null) {
            Log.i(TAG, "photo missing");
            isPostable = false;
        }
        if (rbSetLevel.getRating() > ParseUser.getCurrentUser().getInt("level")) {
            isPostable = false;
            rbSetLevel.setRating(1);
            tvLevelDisp.setVisibility(View.VISIBLE);
            tvLevelDisp.setText("You must be at least Level " + ((int) rbSetLevel.getRating()) + " to create this event");
            tvLevelDisp.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.local_orange)));
        }
        return isPostable;
    }

    //updates Date Labal
    private void updateDateLabel() {
        String myFormatDate = "EEE, MMM d, yyyy"; //In which you need put here
        SimpleDateFormat sdfDATE = new SimpleDateFormat(myFormatDate, Locale.US);

        tvDisplayDate.setText(sdfDATE.format(myCalendar.getTime()));
    }

    //updates Time label
    private void updateTimeLabel() {
        String myFormatTime = "h:mm a"; //In which you need put here
        SimpleDateFormat sdfTime = new SimpleDateFormat(myFormatTime, Locale.US);
        tvStartTime.setText(String.format(sdfTime.format(myCalendar.getTime())));
    }

    //launches Gallery
    public void uploadPhoto(View view) {
        // Create intent for picking photos from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_PHOTO_CODE);
        }
    }

    //on result of picking multiple photos, saves photos as bitmaps and parseFiles
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        mBitmapsSelected = new ArrayList<Bitmap>();
                        mImages = new ArrayList<ParseFile>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            // !! You may need to resize the image if it's too large
                            Bitmap selectedImageBitmap = null;
                            try {
                                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mBitmapsSelected.add(selectedImageBitmap);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            // Compress image to lower quality scale 1 - 100
                            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] image = stream.toByteArray();

                            photoFile = new ParseFile("picture_" + i + ".jpeg", image);
                            mImages.add(photoFile);
                        }
                        initViewFlipper();
                    }

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //initializes viewFlipper to preview images
    private void initViewFlipper() {
        viewFlipper.setVisibility(View.VISIBLE);

        if (viewFlipper != null) {
            viewFlipper.setInAnimation(getApplicationContext(), android.R.anim.slide_in_left);
            viewFlipper.setOutAnimation(getApplicationContext(), android.R.anim.slide_out_right);
        }

        if (viewFlipper != null) {
            for (Bitmap bmp : mBitmapsSelected) {
                ImageView imageView = new ImageView(getApplicationContext());

                imageView.setImageBitmap(bmp);
                viewFlipper.addView(imageView);
                //if more than one image, start flipping
                if (mBitmapsSelected.size() > 1) {
                    viewFlipper.startFlipping();
                }
            }
        }
    }

    public void makeGeoPoint(String s) {
        String[] lat_long = s.substring(9).split("[(,)]");
        for (int i = 0; i < lat_long.length; i++) {
            Log.d(TAG, "" + i + ": " + lat_long[i]);
        }
        geoPoint.setLatitude(Double.parseDouble(lat_long[1]));
        geoPoint.setLongitude(Double.parseDouble(lat_long[2]));
    }

    private void toggle(EmojiButton button) {
        if (button.isSelected()) {
            button.setSelected(false);
            button.setBackgroundColor(getResources().getColor(R.color.quantum_white_100));
            tags.remove(button.getTag().toString());
        } else {
            button.setSelected(true);
            button.setBackgroundColor(getResources().getColor(R.color.local_orange));
            tags.add(button.getTag().toString());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.submit:
                item.setEnabled(false);
                submitAd();
                item.setEnabled(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        //getMenuInflater().inflate(R.menu.menu_create_post, menu);

        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_post, menu);
        return true;
    }


    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }
}
