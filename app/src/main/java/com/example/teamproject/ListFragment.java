package com.example.teamproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.teamproject.models.Ad;
import com.parse.FindCallback;
import com.parse.ParseException;

import org.parceler.Parcels;

import java.util.ArrayList;
import android.widget.TextView;

import java.util.List;


public class ListFragment extends Fragment {
    private static final String TAG = "ListFragment";

    RecAdAdapter adapter;
    RecyclerView mRecyclerView;
    List<Ad> ads;



    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        gvPostGrid = (GridView) view.findViewById(R.id.gvPostGrid);
//        grabAds();
//
//        gvPostGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                Intent i = new Intent(getActivity(), DetailActivity.class);
//                //serialize the movie using parceler, use its short name as a key
//                i.putExtra(Ad.class.getSimpleName(), Parcels.wrap(ads[position]));
//                startActivity(i);
//            }
//        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvPinterestFeed);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        ads = new ArrayList<>();
        grabAds();
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
                        //ads.add(objects.get(i));
                        //adapter.notifyItemInserted(i);
                    }
                    //adAdapter = new AdAdapter(getContext(), ads);
                    //gvPostGrid.setAdapter(adAdapter);
                    ads.addAll(objects);
                    Log.d(TAG,"size of ads: " + ads.size());
                    Log.d(TAG,"size of objects: " + objects.size());
                    adapter = new RecAdAdapter(ads, getContext());
                    mRecyclerView.setAdapter(adapter);
                    //Collections.reverse(ads);
                    //swipeContainer.setRefreshing(false);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
