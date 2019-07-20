package com.example.teamproject.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {

    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_PROFILE_IMAGE = "profileImage";

    public String getFirstName(){
        return getString(KEY_FIRST_NAME);
    }
    public String getLastName(){
        return getString(KEY_LAST_NAME);
    }
    public ParseFile getProfileImage(){
        return getParseFile(KEY_PROFILE_IMAGE);
    }

    public static class Query extends ParseQuery<User> {
        public Query() { super(User.class); }
    }
}