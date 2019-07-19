package com.example.teamproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.teamproject.models.Ad;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    private EditText etusername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etOrganization;
    private EditText etPhone;
    private Button btnSignup;
    private Button btnProfilePic;


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
                     signup(username, password, email, etOrg, phone);
                 }
             }
         });

        btnProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void signup(String username, String password, String email, final String org, final String phone){
        Log.d("SignupActivity", "entered method");
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put("organization", org);
        user.put("phone", phone);
        user.put("attendingEvents", new ArrayList<Ad>());
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("SignupActivity", "Signup successful!");
                    Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_LONG).show();
                    final Intent intent = new Intent(SignupActivity.this, HomeFeedActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.d("SignupActivity", "Signup failure", e);
                    e.printStackTrace();
                }
            }
        });
    }


}
