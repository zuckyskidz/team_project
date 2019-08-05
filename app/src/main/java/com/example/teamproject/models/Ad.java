package com.example.teamproject.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;


@ParseClassName("Ad")
public class Ad extends ParseObject {
    private static final String KEY_STARTTIME = "startTime";
    private static final String KEY_ENDTIME = "endTime";
    private static final String KEY_DATE = "date";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_IMAGES = "images";

    public static final String KEY_USER = "user";
    private static final String KEY_CREATIONTIME = "createdAt";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_TAGS = "tags";
    private static final String KEY_RSVP = "rsvp";
    private static final String KEY_ATTENDEES = "attendees";

    public static final String KEY_GEOPPOINT = "geoPoints";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }
    public List<ParseFile> getImages() {
        return getList(KEY_IMAGES);
    }


    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }
    public void setImages(List<ParseFile> images) {
        put(KEY_IMAGES, images);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getTimeStamp() {
        return getString(KEY_CREATIONTIME);
    }

    public void setGeoPoint(ParseGeoPoint geoPoint) {
        put(KEY_GEOPPOINT, geoPoint);
    }

    public ParseGeoPoint getGeoPoint() { return getParseGeoPoint(KEY_GEOPPOINT); }

    public String getLocation() {
        return getString(KEY_LOCATION);
    }

    //right now, "tags" are an array in parse dashboard
    //Todo - create tags model? getTags should return an array of Tags.each tag has an image

    public List<String> getTags() {
        return getList(KEY_TAGS);
    }
    public void setTags(List<String> tags) {
        put(KEY_TAGS, tags);
    }


    public String getAddress() {
        return getString(KEY_LOCATION);
    }

    public void setAddress(String address) {
        put(KEY_LOCATION, address);
    }

    public Date getDate() {
        return getDate(KEY_DATE);
    }

    public void setDate(Date date) {
        put(KEY_DATE, date);
    }

    public String getStartTime() {
        return getString(KEY_STARTTIME);
    }

    public void setStartTime(String startTime) {
        put(KEY_STARTTIME, startTime);
    }

    public String getEndTime() {
        return getString(KEY_ENDTIME);
    }

    public void setEndTime(String endTime) {
        put(KEY_ENDTIME, endTime);
    }

    public List<Object> getRSVP() {
        return getList(KEY_RSVP);
    }

    public List<Object> getAttendees() {
        return getList(KEY_ATTENDEES);
    }
    public void setAttendees(List<Object> users) {
        put(KEY_ATTENDEES, users);
    }


    public void setRSVP(List<Object> list) {
        put(KEY_RSVP, list);
    }

    public int getRSVPCount() {
        return getRSVP().size();
    }

    public static class Query extends ParseQuery<Ad> {
        public Query() {
            super(Ad.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }

    }

//    public GeoPoint getLocationFromAddress(Context context, String strAddress){
//
//        Geocoder coder = new Geocoder(context);
//        List<Address> address;
//        GeoPoint p1 = null;
//
//        try {
//            address = coder.getFromLocationName(strAddress,5);
//            if (address==null) {
//                return null;
//            }
//            Address location=address.get(0);
//            location.getLatitude();
//            location.getLongitude();
//
//            p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
//                    (double) (location.getLongitude() * 1E6));
//
//            return p1;
//        }
//    }

}
