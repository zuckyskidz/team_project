package com.example.teamproject;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate(){
        super.onCreate();

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("localAdId")
                .clientKey("localAdMasterKey")
                .server("http://localad.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);

        //ParseObject.registerSubclass(Post.class);
    }
}
