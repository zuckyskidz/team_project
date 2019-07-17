package com.example.teamproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.teamproject.models.Ad;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {

    private static final String TAG = "ListFragment";
    List<Ad> mAds;

    TextView eventTV;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);

        mAds = new ArrayList<>();
        queryPosts();

        eventTV = view.findViewById(R.id.tvTitle);
        eventTV.setClickable(true);


        final Ad[] ad = new Ad[1];
        ParseQuery<Ad> query = ParseQuery.getQuery("Ad");
        query.getInBackground("SJiW8rUJmh", new GetCallback<Ad>() {
            public void done(Ad object, ParseException e) {
                if (e == null) {
                    ad[0] = object;
                    eventTV.setText(ad[0].getTitle());

                } else {
                    // something went wrong
                }
            }
        });
        eventTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DetailActivity.class);
                //serialize the movie using parceler, use its short name as a key
                i.putExtra(Ad.class.getSimpleName(), Parcels.wrap(ad[0]));
                startActivity(i);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }


    public void queryPosts() {
        final Ad.Query postQuery = new Ad.Query();
        postQuery.getTop().withUser();
        postQuery.findInBackground(new FindCallback<Ad>() {
            @Override
            public void done(List<Ad> ads, ParseException e) {
                if(e == null){
                    mAds.addAll(ads);
                    Log.i(TAG, Integer.toString(mAds.size()));
                    for (int i = 0; i < ads.size(); i++) {
                        Log.d(TAG, "Ad[" + i + "]: "
                                + ads.get(i).getTitle()
                                + "\ndescription = " + ads.get(i).getDescription()
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
