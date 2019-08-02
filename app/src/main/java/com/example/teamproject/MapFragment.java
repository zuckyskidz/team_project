package com.example.teamproject;

import android.content.pm.PackageManager;
import android.location.Location;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    private FusedLocationProviderClient fusedLocationClient;
    private GeofencingClient geofencingClient;

    private LocationRequest mLocationRequest;

    Location myLocation;
    List<Geofence> geofenceList;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private int LOCATION_REQUEST_ID = 2222;
    private int RADIUS_IN_METERS = 8000; //roughly 5 miles
    private int RADIUS_IN_KILOMETERS = 8;

//    private int GEOFENCE_RADIUS_IN_METERS = 8000;
//    private int GEOFENCE_EXPIRATION_IN_MILLISECONDS = 1000;

    ArrayList<Ad> closestEvents;
    ArrayList<Ad> allEvents;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout for\ this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);

        fusedLocationClient = getFusedLocationProviderClient(getContext());
        geofencingClient = LocationServices.getGeofencingClient(getContext());

        startLocationUpdates();

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
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_ID);
            return;
        } else {
            myLocation = LocationServices.FusedLocationApi.getLastLocation();
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
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                 myLocation =
                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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

        getClosestEvents();
        pinEvents(allEvents);
      //  pinEvents(closestEvents);

    }


    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

//        googleMap.addMarker(new MarkerOptions().position(new LatLng(40.689247, -74.044502)).title("Statue of Liberty"));

//        CameraPosition Liberty = CameraPosition.builder().target(new LatLng(40.689247, -74.044502)).zoom(16).bearing(0).tilt(45).build();
//
//        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));

//        zoomToCurrentLocation();

        if (myLocation != null) {
            double dLatitude = myLocation.getLatitude();
            double dLongitude = myLocation.getLongitude();
            googleMap.addMarker(new MarkerOptions().position(new LatLng(dLatitude, dLongitude)).title("My Location"));
            CameraPosition myCameraPosition = CameraPosition.builder().target(new LatLng(dLatitude, dLongitude)).zoom(16).bearing(0).tilt(45).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(myCameraPosition));


        } else {
            Toast.makeText(getContext(), "Unable to fetch the current location", Toast.LENGTH_SHORT).show();
        }

        if (checkPermissions()) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
        }

        getCurrentLocation();


    }

    void getCurrentLocation() {
     //   BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_pointer);

        //TODO – uncomment this and figure out how to replace the deprecated function
        Location myLocation = mGoogleMap.getMyLocation();
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

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_ID);
            return false;
        }
    }

    public void getClosestEvents(){
        if(closestEvents != null) closestEvents.clear();
        ParseGeoPoint myGeopoint = new ParseGeoPoint(myLocation.getLatitude(), myLocation.getLongitude());
        ParseQuery<Ad> query = ParseQuery.getQuery("Ad");
        query.whereWithinKilometers("geoPoints", myGeopoint, RADIUS_IN_KILOMETERS);
        query.findInBackground(new FindCallback<Ad>() {
            @Override
            public void done(List<Ad> objects, ParseException e) {
                if(e == null){
                    closestEvents.addAll(objects);
                } else {
                    e.printStackTrace();
                }
            }
        });

    }

    public void getAllEvents(){
        allEvents = new ArrayList<Ad>();
        ParseGeoPoint myGeopoint = new ParseGeoPoint(myLocation.getLatitude(), myLocation.getLongitude());
        ParseQuery<Ad> query = ParseQuery.getQuery("Ad");
        query.findInBackground(new FindCallback<Ad>() {
            @Override
            public void done(List<Ad> objects, ParseException e) {
                if(e == null){
                    allEvents.addAll(objects);
                } else {
                    e.printStackTrace();
                }
            }
        });

    }

    public void pinEvents(ArrayList<Ad> events){
//        ArrayList<LatLng> locations = new ArrayList<LatLng>();
//        for(int i = 0; i < events.size(); i++){
//            LatLng point = new LatLng(events.get(i).getGeoPoint().getLatitude(), events.get(i).getGeoPoint().getLongitude());
//            locations.add(point);
//        }
//        for(LatLng point : locations){
//            mGoogleMap.addMarker(new MarkerOptions().position(point).title("title"));
//        }

        for(Ad event : events){
            LatLng point = new LatLng(event.getGeoPoint().getLatitude(), event.getGeoPoint().getLongitude());
            mGoogleMap.addMarker(new MarkerOptions().position(point).title(event.getTitle()));
        }
    }

    //radius measured in meteres
    boolean isWithinRadius(ParseGeoPoint eventGeopoint, double radius){
        LatLng myGeopoint = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        LatLng eventLocation = new LatLng(eventGeopoint.getLatitude(), eventGeopoint.getLongitude());

        double distance = distanceBetweenGeopoints(myGeopoint, eventLocation);
        if(distance <= radius) {
            return true;
        }
        return false;
    }

    double distanceBetweenGeopoints(LatLng start, LatLng end) {
        int radius = 6371;// radius of earth in Km
        double lat1 = start.latitude;
        double lat2 = end.latitude;
        double lng1 = start.longitude;
        double lng2 = end.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return radius * c;
    }


    public void zoomToCurrentLocation(){
        Location myLocation = mGoogleMap.getMyLocation();
        if (myLocation != null) {
            double dLatitude = myLocation.getLatitude();
            double dLongitude = myLocation.getLongitude();
            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(dLatitude, dLongitude)).title("My Location"));
            CameraPosition myCameraPosition = CameraPosition.builder().target(new LatLng(dLatitude, dLongitude)).zoom(16).bearing(0).tilt(45).build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(myCameraPosition));
    }



}}