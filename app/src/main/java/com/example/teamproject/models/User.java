package com.example.teamproject.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("User")
public class User extends ParseUser {

    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    public static final String KEY_ATTENDING_EVENTS = "attendingEvents";
    public static final String KEY_LEVEL = "level";

    public String getFirstName(){
        return getString(KEY_FIRST_NAME);
    }
    public String getLastName(){
        return getString(KEY_LAST_NAME);
    }
    public ParseFile getProfileImage(){
        return getParseFile(KEY_PROFILE_IMAGE);
    }
    public List<Ad> getAttendingEvents(){
        return getList(KEY_ATTENDING_EVENTS);
    }
    public int getLevel() { return getInt(KEY_LEVEL); }
    public void setLevel(int newLevel) { put(KEY_LEVEL, newLevel); }

    public static ParseUser getCurrentUser(){
        return ParseUser.getCurrentUser();
    }

    public static class Query extends ParseQuery<User> {
        public Query() { super(User.class); }
    }
}