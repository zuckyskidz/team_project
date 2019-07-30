package com.example.teamproject;

import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
//import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.teamproject.models.Ad;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.parse.Parse.getApplicationContext;


public class UserProfileFragment extends Fragment {
    private static final String TAG = "UserProfileFragment";
    RecyclerView rvHosting;
    RecyclerView rvAttending;
    RecyclerViewAdapter rvAdapter;


    Button logoutBT;
    ImageView ivProfileImage;
    TextView tvName;

    ParseUser currentUser;
    ArrayList<Ad> attendingEvents;
    ArrayList<Ad> hostingEvents;

    public UserProfileFragment() {
        //TODO update lists when user RSVP from details page and then goes back to this fragment
    }

    public void onResume() {
        super.onResume();
        getAttendingEvents();
        getHostingEvents();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        currentUser = ParseUser.getCurrentUser();

        super.onViewCreated(view, savedInstanceState);


        logoutBT = getView().findViewById(R.id.logout);
        logoutBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            }
        });

        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvName = view.findViewById(R.id.tvName);
        rvHosting = (RecyclerView) view.findViewById(R.id.rvHostingEvents);
        rvAttending = (RecyclerView) view.findViewById(R.id.rvAttendingEvents);


        getAttendingEvents();
        getHostingEvents();
      
        tvName.setText(currentUser.getUsername());

        ParseFile imageFile = currentUser.getParseFile("profileImage");
        String imageURL = null;
        try {
            imageURL = imageFile.getUrl();
        } catch (NullPointerException e) {

        }
        Glide.with(getApplicationContext())
                .load(imageURL)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.dog))
                .into(ivProfileImage);

    }

    private void getHostingEvents() {
        hostingEvents = new ArrayList<Ad>();
        final Ad.Query adQuery = new Ad.Query();
        adQuery.withUser();
        adQuery.whereEqualTo("user", currentUser);
        adQuery.findInBackground(new FindCallback<Ad>() {
            @Override
            public void done(List<Ad> objects, ParseException e) {
                if (e == null) {
                    hostingEvents.addAll(objects);
                    for (int i = 0; i < hostingEvents.size(); i++) {
                        Log.d(TAG, "HOSTING[" + i + "] = "
                                + hostingEvents.get(i).getDescription()
                                + "\nusername = " + hostingEvents.get(i).getUser().getUsername());
                    }
                } else {
                    e.printStackTrace();
                }
                initHostingRecycler();
                rvAdapter.notifyDataSetChanged();
            }
        });

    }

    public void getAttendingEvents() {
        attendingEvents = new ArrayList<Ad>();
        final Ad.Query adQuery = new Ad.Query();
        adQuery.withUser();
        adQuery.whereContains("rsvp", currentUser.getObjectId());
        adQuery.include("rsvp");
        adQuery.whereEqualTo("rsvp", ParseObject.createWithoutData(ParseUser.class, currentUser.getObjectId()));
        adQuery.findInBackground(new FindCallback<Ad>() {
            @Override
            public void done(List<Ad> objects, ParseException e) {
                if (e == null) {
                    attendingEvents.addAll(objects);
                    for (int i = 0; i < attendingEvents.size(); i++) {
                        Log.d(TAG, "ATTENDING[" + i + "] = "
                                + attendingEvents.get(i).getDescription()
                                + "\nusername = " + attendingEvents.get(i).getUser().getUsername());
                    }
                } else {
                    e.printStackTrace();
                }
                initAttendingRecycler();
                rvAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initHostingRecycler() {
       // Log.i(TAG, "initRecyclerView");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvHosting.setLayoutManager(layoutManager);
        rvAdapter = new RecyclerViewAdapter(this.getContext(), hostingEvents);
        rvHosting.setAdapter(rvAdapter);
    }

    private void initAttendingRecycler() {
      //  Log.i(TAG, "initRecyclerView");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvAttending.setLayoutManager(layoutManager);
        rvAdapter = new RecyclerViewAdapter(this.getContext(), attendingEvents);
        rvAttending.setAdapter(rvAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

}