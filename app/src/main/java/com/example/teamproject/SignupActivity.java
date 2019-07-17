package com.example.teamproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {


    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etOrganization;
    private EditText etBirthday;
    private EditText etPhone;
    private Button btnSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etOrganization = findViewById(R.id.etOrganization);
        etBirthday = findViewById(R.id.etBirthday);
        etPhone = findViewById(R.id.etPhone);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v) {
                 final String email = etEmail.getText().toString();
                 final String password = etPassword.getText().toString();

                 signup(email, password);
             }
         });
    }

    private void signup(final String email, final String password){
        Log.d("SignupActivity", "entered method");
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(email);
        user.setPassword(password);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("SignupActivity", "Signup successful!");
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
