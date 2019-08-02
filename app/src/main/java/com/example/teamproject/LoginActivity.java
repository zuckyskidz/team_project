package com.example.teamproject;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;
    private TextView textError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//      User already signed in

        if (ParseUser.getCurrentUser() != null) {
            final Intent intent = new Intent(LoginActivity.this, HomeFeedActivity.class);
            startActivity(intent);
            finish();
        } else {
            etEmail = findViewById(R.id.etName);
            etPassword = findViewById(R.id.etPassword);
            btnLogin = findViewById(R.id.btnLogin);
            btnSignup = findViewById(R.id.btnSignup);
            textError = findViewById(R.id.tvError);
            btnLogin.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final String email = etEmail.getText().toString();
                    final String password = etPassword.getText().toString();

                    login(email, password);
                }
            });

            btnSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void login(String email, String password){
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e == null){
                    Log.d("LoginActivity", "Login successful!");

                    final Intent intent = new Intent(LoginActivity.this, HomeFeedActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("LoginActivity", "Login failure.");
                    textError.setText("User not found. Please try again.");
                    e.printStackTrace();
                }
            }
        });
    }
}
