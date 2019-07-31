package com.example.refresh;
/*
Description:
    This activity utilizes the GoogleMap API and searches for locations on Google Maps.

Specific Features:
    Search for Destination
    View Current Location
    GoogleMaps Functionality

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, Serializable {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private String address;

    /*
    Methods that occur when the Activity starts.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        address = getIntent().getStringExtra("address");
        getLocationPermission();
        setupLocationButtons();
    }


    /*
    Initializes the GoogleMap and searches given address.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapSearch(address);
    }


    /*
    Creates location buttons and sets their onClickListeners
     */
    private void setupLocationButtons(){
        final FancyButton myLocationButton = findViewById(R.id.myLocationButton);
        final FancyButton myDestinationButton = findViewById(R.id.geolocateButton);

        myLocationButton.setOnClickListener(w -> {
            getDeviceLocation();
            myLocationButton.setBackgroundColor(getResources().getColor(R.color.teal));
            myDestinationButton.setBackgroundColor(getResources().getColor(R.color.middle_blue));
        });

        myDestinationButton.setOnClickListener(w -> {
            mapSearch(address);
            myLocationButton.setBackgroundColor(getResources().getColor(R.color.middle_blue));
            myDestinationButton.setBackgroundColor(getResources().getColor(R.color.teal));
        });
    }


    /*
    Moves the camera to a specific position on the map and marks that position.
     */
    private void moveCamera(LatLng latlng, float zoom, String title) {
        if(mMap!=null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));

            MarkerOptions options = new MarkerOptions()
                    .position(latlng)
                    .title(title);
            mMap.addMarker(options);
        }
    }


    /*
    Gets the devices location.
     */
    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Location currentLocation = (Location) task.getResult();
                        LatLng coordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        moveCamera(coordinates, DEFAULT_ZOOM, "my location");
                    }
                    else{
                        Toast.makeText(MapActivity.this, "Unable to get current location.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (SecurityException e){
            Log.e("MapActivity", "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }


    /*
    Initialize Map.
     */
    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }


    /*
    Check permissions to see if android can view user's current location.
     */
    private void getLocationPermission(){
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    /*
    Deals with user's response after permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }


    /*
    Finds location on map given a valid address.
     */
    private void mapSearch(String location) {
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(location, 1);
        }catch (IOException e){
            Log.e("MapActivity", "Geolocate: IOException: " + e.getMessage());
        }

        if(list.size() > 0){
            Address address = list.get(0);
            LatLng coordinates = new LatLng(address.getLatitude(), address.getLongitude());
            moveCamera(coordinates, DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }
}
