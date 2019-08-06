package com.example.teamproject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.teamproject.models.Ad;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    private LocationRequest mLocationRequest;

    Location myLocation;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private int LOCATION_REQUEST_ID = 2222;
    private int RADIUS_IN_METERS = 8000; //roughly 5 miles
    private int RADIUS_IN_KILOMETERS = 15;

    ArrayList<Ad> closestEvents;
    ArrayList<Ad> allEvents;
    ArrayList<Ad> temp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout for\ this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) mView.findViewById(R.id.mapView);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        if (!hasLocationPermission()) {
            requestLocationPermission();
        }



    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_ID);
            return;
        }
        getFusedLocationProviderClient(getContext()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_ID) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // We can now safely use the API we requested access to
//                 myLocation =
//                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                startLocationUpdates();
            } else {
                // Permission was denied or request was cancelled
            }
        }
    }


    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        myLocation.setLatitude(latLng.latitude);
        myLocation.setLongitude(latLng.longitude);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (!hasLocationPermission()) {
            requestLocationPermission();
        }
        mGoogleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager)
                this.getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        myLocation = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
        double latitude = myLocation.getLatitude();
        double longitude = myLocation.getLongitude();
        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Location"));
        CameraPosition myCameraPosition = CameraPosition.builder().target(new LatLng(latitude, longitude)).zoom(16).bearing(0).tilt(45).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(myCameraPosition));
        allEvents = new ArrayList<Ad>();
        closestEvents = new ArrayList<Ad>();
        //getAllEvents();
        getClosestEvents();
    }

    void getCurrentLocation() {
        myLocation = mGoogleMap.getMyLocation();
        if (myLocation != null) {
            double dLatitude = myLocation.getLatitude();
            double dLongitude = myLocation.getLongitude();
            Log.i("APPLICATION", " : " + dLatitude);
            Log.i("APPLICATION", " : " + dLongitude);
            mGoogleMap.addMarker(new MarkerOptions().position(
                    new LatLng(dLatitude, dLongitude)).title("My Location"));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 8));

        } else {
            Toast.makeText(getContext(), "Unable to fetch the current location", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    private void requestLocationPermission() {
        requestPermissions(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_REQUEST_ID);
    }

    public void getClosestEvents() {

        if (myLocation == null) {
            getCurrentLocation();
        }
        final Ad.Query adQuery = new Ad.Query();
        adQuery.getTop().withUser();
        ParseGeoPoint myGeopoint = new ParseGeoPoint(myLocation.getLatitude(), myLocation.getLongitude());
        adQuery.whereWithinKilometers("geoPoints", myGeopoint, RADIUS_IN_KILOMETERS);

        adQuery.findInBackground(new FindCallback<Ad>() {
            @Override
            public void done(List<Ad> objects, ParseException e) {

                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        closestEvents.add(i, objects.get(i));
                        Log.d("MapFragment", "Ad[" + i + "] = "
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername());
                    }
                    closestEvents.addAll(objects);
                } else {
                    e.printStackTrace();
                }
                pinClosestEvents();
            }

        });

    }

    public void getAllEvents() {
        if (myLocation == null) {
            getCurrentLocation();
        }
        final Ad.Query adQuery = new Ad.Query();
        adQuery.getTop().withUser();

        adQuery.findInBackground(new FindCallback<Ad>() {
            @Override
            public void done(List<Ad> objects, ParseException e) {

                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        allEvents.add(i, objects.get(i));
                        Log.d("MapFragment", "Ad[" + i + "] = "
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername());
                    }
                    temp = allEvents;
                    allEvents.addAll(objects);
                } else {
                    e.printStackTrace();
                }
                pinAllEvents();

            }

        });
    }

    public void pinAllEvents() {
        for (Ad event : allEvents) {
            if(event.getGeoPoint() != null) {
                LatLng point = new LatLng(event.getGeoPoint().getLatitude(), event.getGeoPoint().getLongitude());
                mGoogleMap.addMarker(new MarkerOptions().position(point).title(event.getTitle()));
            }
        }
    }

    public void pinClosestEvents() {
        for (Ad event : closestEvents) {
            if(event.getGeoPoint() != null) {
                LatLng point = new LatLng(event.getGeoPoint().getLatitude(), event.getGeoPoint().getLongitude());
                mGoogleMap.addMarker(new MarkerOptions().position(point).title(event.getTitle()));
            }
        }
    }
}