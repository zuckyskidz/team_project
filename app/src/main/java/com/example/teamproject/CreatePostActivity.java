package com.example.teamproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.teamproject.models.Ad;
import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.places.Place;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.ParseException;
import com.parse.ParseFile;
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

public class CreatePostActivity extends AppCompatActivity {

    private static final String TAG = "CreatePostActivity";
    private final static int PICK_PHOTO_CODE = 1046;
    int AUTOCOMPLETE_REQUEST_CODE = 1;
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
    List<Place.Field> fields;
    PlacesClient placesClient;

    EditText etAdName;
    TextView tvDisplayDate;
    TextView tvStartTime;
    TextView tvEndTime;
    Button btnAdAddress;
    EditText etAdDesc;
    ImageView ivPreview;
    ParseFile photoFile;
    ImageButton btnSubmit;
//    EditText mSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_api_key));
        placesClient = Places.createClient(this);


        etAdName = (EditText) findViewById(R.id.etAdName);
        tvEndTime = (TextView) findViewById(R.id.tvTimeDisplay2);
        btnAdAddress = (Button) findViewById(R.id.btnAdAddress);
        etAdDesc = (EditText) findViewById(R.id.etAdDesc);
        tvDisplayDate = (TextView) findViewById(R.id.tvDateDisplay);
        tvStartTime = (TextView) findViewById(R.id.tvTimeDisplay);
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        btnSubmit = (ImageButton) findViewById(R.id.btnSubmit);
//        mSearchText = (EditText) findViewById(R.id.btnAdAddress);

        ivPreview.setVisibility(View.GONE);

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

        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        //init();
    }

    public void submitAd(View view) {
        Log.d(TAG, "Posting...");
        Ad newAd = new Ad();
        if(makeSurePostable()) {
            newAd = new Ad();
            newAd.setUser(ParseUser.getCurrentUser());
            newAd.setTitle(etAdName.getText().toString());
            newAd.setDate(myCalendar.getTime());
            newAd.setEndTime(tvEndTime.getText().toString());
            newAd.setAddress(btnAdAddress.getText().toString());
            newAd.setDescription(etAdDesc.getText().toString());
            newAd.setRSVP(new ArrayList<Object>());
            newAd.setImage(photoFile);
        }
        else{
            Toast.makeText(CreatePostActivity.this, "Missing informtaion.", Toast.LENGTH_SHORT).show();
            return;
        }
        postAd(newAd);
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

    private boolean makeSurePostable() {
        if(etAdName.getText().equals("")) {
            Log.i(TAG, "title missing");
            return false;
        }
        if (tvDisplayDate.getText().equals(R.string.ad_date)){
            Log.i(TAG, "date missing");
            return false;
        }
        if(tvEndTime.getText().equals(R.string.end_time)){
            Log.i(TAG, "end time missing");
            return false;
        }
        if(tvStartTime.getText().equals(R.string.start_time)){
            Log.i(TAG, "end time missing");
            return false;
        }
        if(btnAdAddress.getText().equals("")){
            Log.i(TAG, "address missing");
            return false;
        }
        if(etAdDesc.getText().equals("")){
            Log.i(TAG, "description missing");
            return false;
        }
        if(photoFile == null){
            Log.i(TAG, "photo missing");
            return false;
        }
        return true;
    }

    private void updateDateLabel() {
        String myFormatDate = "EEE, MMM d, yyyy"; //In which you need put here
        SimpleDateFormat sdfDATE = new SimpleDateFormat(myFormatDate, Locale.US);

        tvDisplayDate.setText(sdfDATE.format(myCalendar.getTime()));
    }

    private void updateTimeLabel() {
        String myFormatTime = "h:mm a"; //In which you need put here
        SimpleDateFormat sdfTime = new SimpleDateFormat(myFormatTime, Locale.US);
        ;
        tvStartTime.setText(String.format(sdfTime.format(myCalendar.getTime())));
    }

    public void uploadPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public void onClickLocation(View view) {
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri photoUri = data.getData();
                    // Do something with the photo based on Uri
                    Bitmap selectedImage = null;
                    try {
                        selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Compress image to lower quality scale 1 - 100
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] image = stream.toByteArray();

                    // Create the ParseFile
                    photoFile  = new ParseFile("picture_1.jpeg", image);
                    Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    Glide.with(getApplicationContext())
                            .load(image)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_launcher_background))
                            .into(ivPreview);
                    ivPreview.setVisibility(View.VISIBLE);
                }
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

//    private void geoLocate(){
//        Log.d(TAG, "geoLocate: geolocating");
//
//        String searchString = mSearchText.getText().toString();
//
//        Geocoder geocoder = new Geocoder(CreatePostActivity.this);
//        List<Address> list = new ArrayList<>();
//        try{
//            list = geocoder.getFromLocationName(searchString, 1);
//        }catch (IOException e){
//            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
//        }
//
//        if(list.size() > 0){
//            Address address = list.get(0);
//
//            Log.d(TAG, "geoLocate: found a location: " + address.toString());
//            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
//
//        }
//    }
//
//    private void init(){
//        Log.d(TAG, "init: initializing");
//
//        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
//
//                    //execute our method for searching
//                    geoLocate();
//                }
//
//                return false;
//            }
//        });
//    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
//                // TODO: Handle the error.
//                Status status = Autocomplete.getStatusFromIntent(data);
//                Log.i(TAG, status.getStatusMessage());
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//        }
//    }

}