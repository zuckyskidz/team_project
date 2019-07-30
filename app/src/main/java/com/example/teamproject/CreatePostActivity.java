package com.example.teamproject;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.teamproject.models.Ad;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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

    EditText etAdName;
    TextView tvDisplayDate;
    TextView tvStartTime;
    TextView tvEndTime;
    EditText etAdAddress;
    EditText etAdDesc;
    //ImageView ivPreview;
    ParseFile photoFile;
    ImageButton btnSubmit;
    ViewFlipper viewFlipper;


    private ArrayList<Uri> mArrayUri;
    private ArrayList<Bitmap> mBitmapsSelected;
    private ArrayList<ParseFile> mImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        etAdName = (EditText) findViewById(R.id.etAdName);
        tvEndTime = (TextView) findViewById(R.id.tvTimeDisplay2);
        etAdAddress = (EditText) findViewById(R.id.etAdAddress);
        etAdDesc = (EditText) findViewById(R.id.etAdDesc);
        tvDisplayDate = (TextView) findViewById(R.id.tvDateDisplay);
        tvStartTime = (TextView) findViewById(R.id.tvTimeDisplay);
        //ivPreview = (ImageView) findViewById(R.id.ivPreview);
        btnSubmit = (ImageButton) findViewById(R.id.btnSubmit);

        viewFlipper = findViewById(R.id.viewFlipper);




        //ivPreview.setVisibility(View.GONE);

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


    }

    public void submitAd(View view) {
        Log.d(TAG, "Posting...");
        Ad newAd = new Ad();
        if (makeSurePostable()) {
            newAd = new Ad();
            newAd.setUser(ParseUser.getCurrentUser());
            newAd.setTitle(etAdName.getText().toString());
            newAd.setDate(myCalendar.getTime());
            newAd.setEndTime(tvEndTime.getText().toString());
            newAd.setAddress(etAdAddress.getText().toString());
            newAd.setDescription(etAdDesc.getText().toString());
            newAd.setRSVP(new ArrayList<Object>());
            newAd.setImages(mImages);
        } else {
            Toast.makeText(CreatePostActivity.this, "Missing information.", Toast.LENGTH_SHORT).show();
            return;
        }
        postAd(newAd);
    }

    private void postAd(Ad newAd) {
        btnSubmit.setEnabled(false);
        newAd.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Posting successful!");
                    Toast.makeText(getApplicationContext(), "Posting successful!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CreatePostActivity.this, HomeFeedActivity.class);
                    startActivity(intent);
                    finish();
                    btnSubmit.setEnabled(true);
                    return;
                } else {
                    Log.i(TAG, "FAILED");
                    Toast.makeText(getApplicationContext(), "Posting Failed!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    btnSubmit.setEnabled(true);
                    return;
                }
            }
        });
    }

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
            ;
        }
        if (etAdAddress.getText().length() == 0) {
            Log.i(TAG, "address missing");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                etAdAddress.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.local_orange)));
            } else {
                etAdAddress.setHintTextColor(getResources().getColor(R.color.local_orange));
            }
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
        if (photoFile == null) {
            Log.i(TAG, "photo missing");
            isPostable = false;
        }
        return isPostable;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        mArrayUri = new ArrayList<Uri>();
                        mBitmapsSelected = new ArrayList<Bitmap>();
                        mImages = new ArrayList<ParseFile>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
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

                            initViewFlipper();
                        }
                    }

                }
            }
        }
    }

    private void initViewFlipper() {
        if (viewFlipper != null) {
            viewFlipper.setInAnimation(getApplicationContext(), android.R.anim.slide_in_left);
            viewFlipper.setOutAnimation(getApplicationContext(), android.R.anim.slide_out_right);
        }

        if (viewFlipper != null) {
            for (Bitmap image : mBitmapsSelected) {
                ImageView imageView = new ImageView(this);
                FrameLayout.LayoutParams layoutParams =
                        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 30, 30, 30);
                layoutParams.gravity = Gravity.CENTER;
                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(image);
                viewFlipper.addView(imageView);
            }
        }
    }

}