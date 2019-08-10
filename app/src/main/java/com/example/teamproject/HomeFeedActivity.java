package com.example.teamproject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.RequiresApi;
import android.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeFeedActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;
    private Toolbar myToolbar;
    BottomNavigationView bottomNavigationView;
    String TAG = "HomeFeedActivity";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.vpHome);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        mPager.setCurrentItem(1);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        myToolbar = (Toolbar) findViewById(R.id.local_toolbar);
        myToolbar.setTitle("");
        myToolbar.setTitleTextColor(getResources().getColor(R.color.quantum_white_text));
        setActionBar(myToolbar);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Log.i(TAG, "here");
                        switch (item.getItemId()) {
                            case R.id.action_profile:
                                //Log.d(TAG,"Going to 0");
                                mPager.setCurrentItem(0);
                                break;
                            case R.id.action_list:
                                //Log.d(TAG,"Going to 1");
                                mPager.setCurrentItem(1);
                                break;
                            case R.id.action_map:
                                //Log.d(TAG,"Going to 2");
                                mPager.setCurrentItem(2);
                            default:
                                break;
                        }
                        return true;
                    }
                });
        // Set default selection
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
    }



    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    public void onCreatePost(View view) {
        Intent intent = new Intent(HomeFeedActivity.this, CreatePostActivity.class);
        startActivity(intent);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                Log.d(TAG,"Going to 0");
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                return new UserProfileFragment();
            } else if (position == 1) {
                Log.d(TAG,"Going to 1");
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                return new ListFragment();
            }
            Log.d(TAG,"Going to 2");
            bottomNavigationView.getMenu().getItem(2).setChecked(true);
            return new MapFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}