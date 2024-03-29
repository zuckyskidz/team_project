package com.example.teamproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;

import android.widget.PopupWindow;

import android.widget.RatingBar;

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
    private RecyclerView rvHosting;
    private RecyclerView rvAttending;
    private RecyclerViewAdapter rvAdapter;

    private Button logoutBT;
    private Button qrBT;
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvNoAttending;
    private TextView tvNoHosting;
    private RatingBar rbLevel;


    private ParseUser currentUser;
    private ArrayList<Ad> attendingEvents;
    private ArrayList<Ad> hostingEvents;

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


        qrBT = getView().findViewById(R.id.qrCode);
        qrBT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvName = view.findViewById(R.id.tvName);

        ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        tvName = (TextView) view.findViewById(R.id.tvName);

        rvHosting = (RecyclerView) view.findViewById(R.id.rvHostingEvents);
        rvAttending = (RecyclerView) view.findViewById(R.id.rvAttendingEvents);
        tvNoAttending = (TextView) view.findViewById(R.id.tvNoAttending);
        tvNoHosting = (TextView) view.findViewById(R.id.tvNoHosting);
        rbLevel = (RatingBar) view.findViewById(R.id.rbLevels);
      
        tvName.setText(currentUser.getUsername());
        Log.d(TAG, "Does ParseUser contain level? " + currentUser.containsKey("level"));
        Log.d(TAG, "User Level is " + currentUser.getInt("level"));
        rbLevel.setRating(currentUser.getInt("level"));

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
        if(rvAdapter != null){
            rvAdapter.clear();
        }
        hostingEvents = new ArrayList<Ad>();
        final Ad.Query adQuery = new Ad.Query();
        adQuery.withUser();
        adQuery.whereEqualTo("user", currentUser);
        adQuery.findInBackground(new FindCallback<Ad>() {
            @Override
            public void done(List<Ad> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() != 0) {
                        hostingEvents.addAll(objects);
                        for (int i = 0; i < hostingEvents.size(); i++) {
                            Log.d(TAG, "HOSTING[" + i + "] = "
                                    + hostingEvents.get(i).getDescription()
                                    + "\nusername = " + hostingEvents.get(i).getUser().getUsername());
                            tvNoHosting.setVisibility(View.GONE);
                        }
                    } else {
                        tvNoHosting.setVisibility(View.VISIBLE);
                        rvHosting.setVisibility(View.GONE);
                        return;
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
        if (rvAdapter != null) {
            rvAdapter.clear();
        }
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
                    if (objects.size() != 0) {
                        attendingEvents.addAll(objects);
                        for (int i = 0; i < attendingEvents.size(); i++) {
                            Log.d(TAG, "ATTENDING[" + i + "] = "
                                    + attendingEvents.get(i).getDescription()
                                    + "\nusername = " + attendingEvents.get(i).getUser().getUsername());
                            tvNoAttending.setVisibility(View.GONE);
                        }
                    } else {
                        tvNoAttending.setVisibility(View.VISIBLE);
                        rvAttending.setVisibility(View.GONE);
                        return;
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

    public void showPopup(View anchorView) {

        View popupView = getLayoutInflater().inflate(R.layout.qr_popup, null);

        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Example: If you have a TextView inside `popup_layout.xml`
        ImageView ivQR = popupView.findViewById(R.id.ivQR);

        ivQR.setImageBitmap(getQR(ParseUser.getCurrentUser().getObjectId()));


        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        Log.i(TAG, String.valueOf(popupView.isShown()));

    }

    private Bitmap getQR(String str){
            Bitmap bitmap = QRCodeHelper
                    .newInstance(getContext())
                    .setContent(str)
                    .setMargin(2)
                    .getQRCOde();
            return bitmap;
    }

}