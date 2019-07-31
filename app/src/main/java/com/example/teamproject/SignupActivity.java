package com.example.teamproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
//import android.support.v4.content.FileProvider;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamproject.models.Ad;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
    public final static int PICK_PHOTO_CODE = 1046;
    private static final String TAG = "SignUpActivity";

    private EditText etusername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etOrganization;
    private EditText etPhone;
    private Button btnSignup;
    private Button btnProfilePic;
    ParseFile photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        etusername = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etOrganization = findViewById(R.id.etOrganization);
        etPhone = findViewById(R.id.etPhone);

        btnSignup = findViewById(R.id.btnSignup);
        btnProfilePic = findViewById(R.id.btProfileImage);

        btnSignup.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v) {
                 final String username = etusername.getText().toString();
                 final String password = etPassword.getText().toString();
                 final String confirmPassword = etConfirmPassword.getText().toString();
                 final String email = etEmail.getText().toString();
                 final String etOrg = etOrganization.getText().toString();
                 final String phone = etPhone.getText().toString();

                 if(!password.equals(confirmPassword)){
                     Toast.makeText(SignupActivity.this, "Passwords must match.", Toast.LENGTH_SHORT).show();
                 }
                 else {
                     photoFile.saveInBackground(new SaveCallback() {
                         public void done(ParseException e) {
                             // If successful add file to user and signUpInBackground
                             if(null == e)
                                 signup(username, password, email, etOrg, phone, photoFile);
                             else
                                 e.printStackTrace();
                         }
                     });
                 }
             }
         });

        btnProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGallery();
            }
        });
    }

    private void launchGallery() {
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

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
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

                }
            }
        }
    }

    private void signup(String username, String password, String email, final String org, final String phone, ParseFile imageFile){
        Log.d(TAG, "entered method");
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put("organization", org);
        user.put("phone", phone);
        user.put("attendingEvents", new ArrayList<Ad>());
        user.put("profileImage",imageFile);
        user.put("level", 1);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Signup successful!");
                    Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_LONG).show();
                    final Intent intent = new Intent(SignupActivity.this, HomeFeedActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.d(TAG, "Signup failure", e);
                    e.printStackTrace();
                }
            }
        });
    }


}
