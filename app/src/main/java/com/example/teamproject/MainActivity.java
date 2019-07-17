package com.example.teamproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.teamproject.models.Ad;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.xml.sax.Parser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    EditText usernameET;
    EditText passwordET;
    Button loginBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameET = findViewById(R.id.etUsername);
        passwordET = findViewById(R.id.etPassword);
        loginBT = findViewById(R.id.btLogin);

        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = usernameET.getText().toString();
                String pass = passwordET.getText().toString();
                login(user, pass);
            }
        });
        queryPosts();
    }
    
    private void login(String user, String pass) {
        Log.d(TAG, "login here");

        ParseUser.logInInBackground(user, pass, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e == null){
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "login works");
                }
                else{
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }


    public void queryPosts() {
        final Ad.Query postQuery = new Ad.Query();
        postQuery.getTop().withUser();
        postQuery.findInBackground(new FindCallback<Ad>() {
            @Override
            public void done(List<Ad> ads, ParseException e) {
                if(e == null){
                    for (int i = 0; i < ads.size(); i++) {
                        Log.d(TAG, "Post[" + i + "]: "
                                + ads.get(i).getDescription()
                                + "\nusername = " + ads.get(i).getUser().getUsername()
                                + "\nCreated at: " + ads.get(i).getCreatedAt()
                        );
                    }
                }
                else{
                    e.printStackTrace();
                }
            }
        });
    }
}
