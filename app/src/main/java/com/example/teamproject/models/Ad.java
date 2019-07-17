package com.example.teamproject.models;


import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


@ParseClassName("Ad")
public class Ad extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    private static final String KEY_CREATIONTIME = "createdAt";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_TAGS = "tags";
    private static final String KEY_RSVP = "rsvp";


    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public String getTitle(){
        return getString(KEY_TITLE);
    }
    public void setTitle(String title){
        put(KEY_TITLE, title);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public String getTimeStamp(){
        return getString(KEY_CREATIONTIME);
    }

    public void setKeyLocation(ParseGeoPoint geoPoint){
        put(KEY_LOCATION, geoPoint);
    }
    public ParseGeoPoint getLocation(){
        return getParseGeoPoint(KEY_LOCATION);
    }

    //right now, "tags" are an array in parse dashboard
    //Todo - create tags model? getTags should return an array of Tags.each tag has an image

    public ParseObject getTags(){
        return getParseObject(KEY_TAGS);
    }

    public List<Object> getRSVP(){
        return getList(KEY_RSVP);
    }


    public static class Query extends ParseQuery<Ad>{
        public Query() { super(Ad.class); }

        public Query getTop(){
            setLimit(20);
            return this;
        }

        public Query withUser(){
            include("user");
            return this;
        }

    }

}
