package com.example.teamproject;

import android.app.Application;
import android.util.Log;

import com.example.teamproject.models.Ad;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import okhttp3.OkHttpClient;

public class ParseApp extends Application {

    @Override
    public void onCreate(){
        super.onCreate();

        // Use for troubleshooting -- remove this line for production
        //Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);


        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("localAdId")
                .clientKey("localAdMasterKey")
                .server("http://localad.herokuapp.com/parse")
                .build();

        Parse.enableLocalDatastore(this);
        Parse.initialize(configuration);
        //ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseObject.registerSubclass(Ad.class);
/*
        ParseObject gameScore = new ParseObject("GameScore");
        gameScore.put("score", 1337);
        gameScore.put("playerName", "Sean Plott");
        gameScore.put("cheatMode", false);
        gameScore.saveInBackground();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ad");
        query.getInBackground("0wEMnUBE7e", new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Log.i("ParseApp", object.toString());
                    // object will be your game score
                } else {
                    e.printStackTrace();
                    // something went wrong
                }
            }
        });
*/
    }

}
