package com.example.teamproject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.teamproject.models.Ad;
import com.example.teamproject.models.RecyclerItemClickListener;
import com.parse.FindCallback;
import com.parse.ParseException;

import org.parceler.Parcels;

import java.util.ArrayList;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class ListFragment extends Fragment {
    private static final String TAG = "ListFragment";

    RecAdAdapter adapter;
    RecyclerView mRecyclerView;
    List<Ad> ads;
    SwipeRefreshLayout swipeContainer;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            grabAds();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvPinterestFeed);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mRecyclerView, new RecyclerItemClickListener
                .OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(getContext(), "Clicked!", Toast.LENGTH_LONG).show();

                Ad ad = ads.get(position);

                Intent details = new Intent(getContext(), DetailActivity.class);

                details.putExtra(Ad.class.getSimpleName(), Parcels.wrap(ad));
                getContext().startActivity(details);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //handle longClick if any
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent i = new Intent(getContext(), DetailActivity.class);
                i.putExtra(Ad.class.getSimpleName(), Parcels.wrap(ads[position]));
                getContext().startActivity(i);
            }
        }));

        ads = new ArrayList<>();
        adapter = new RecAdAdapter(ads, getContext());
        mRecyclerView.setAdapter(adapter);

        grabAds();
        swipeUpRefresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    private void grabAds() {
        final Ad.Query adQuery = new Ad.Query();
        adQuery.getTop().withUser();
        adQuery.findInBackground(new FindCallback<Ad>() {
            @Override
            public void done(List<Ad> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        //ads.add(objects.get(i));
                        Log.d("ListFragment", "Ad[" + i + "] = "
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername());
                        ads.add(i, objects.get(i));
                        adapter.notifyItemInserted(i);
                    }
                    Log.d(TAG,"size of ads: " + ads.size());
                    Log.d(TAG,"size of objects: " + objects.size());
                    Collections.reverse(ads);
                    swipeContainer.setRefreshing(false);

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void swipeUpRefresh() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                adapter.clear();
                grabAds();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}
