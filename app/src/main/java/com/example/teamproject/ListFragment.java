package com.example.teamproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.teamproject.models.Ad;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ListFragment extends Fragment {
    GridView gvPostGrid;
    AdAdapter adAdapter;
    //List<Ad> ads;
    Ad[] ads;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        gvPostGrid = (GridView) view.findViewById(R.id.gvPostGrid);
        grabAds();
    }

    private void grabAds() {
        final Ad.Query adQuery = new Ad.Query();
        adQuery.getTop().withUser();
        adQuery.findInBackground(new FindCallback<Ad>() {
            @Override
            public void done(List<Ad> objects, ParseException e) {
                if (e == null) {
                    ads = new Ad[objects.size()];
                    for (int i = 0; i < objects.size(); i++) {
                        //ads.add(objects.get(i));
                        Log.d("ListFragment", "Ad[" + i + "] = "
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername());
                        ads[ads.length - (i + 1)] = objects.get(i);
                        //adAdapter.notifyItemInserted(i);
                    }
                    adAdapter = new AdAdapter(getContext(), ads);
                    gvPostGrid.setAdapter(adAdapter);
                    //ads.addAll(objects);
                    //Collections.reverse(ads);
                    //swipeContainer.setRefreshing(false);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}
