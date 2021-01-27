package com.google.firebase.example.fireeats.java.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.helper.TaskDirectionRequest;

public class FragmentMap extends Fragment implements OnMapReadyCallback{

    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;

    private LatLng deliveryAddress;
    private LatLng courierLocation;
    private Marker courierMarker;

    private long UPDATE_INTERVAL = 10*1000; // 10 seconds
    private long FASTEST_INTERVAL = 2*1000; // 10 seconds

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        // TODO: INITIALIZE USER ADDRESS HERE
        // deliveryAddress = ????
        // updateCourierLocation();

        startLocationUpdate();
    }

    public void updateCourierLocation(){
        //TODO: ADD FIRESTORE GET COURIER LOCATION HERE
        //courierLocation = newLocation;
    }

    @SuppressLint({"RestrictedApi", "MissingPermission"})
    public void startLocationUpdate(){
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        client.requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                new TaskDirectionRequest(mMap, getActivity()).execute(buildDirectionRequestUrl(courierLocation, deliveryAddress));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(courierLocation);
                builder.include(deliveryAddress);

                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));

                // TODO: Calculate rotation
                if(courierMarker == null){
                    courierMarker = mMap.addMarker(new MarkerOptions().position(courierLocation)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.top_down_courier_icon_50x50)));
                } else{
                    courierMarker.setPosition(courierLocation);
                }

            }
        }, null);

    }

    // Used to generate API request to get route from courier to customer's address
    private String buildDirectionRequestUrl(LatLng origin, LatLng destination) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String param = strOrigin + "&" + strDestination + "&" + sensor + "&" + mode;
        String output = "json";

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=AIzaSyBlRMSeyPVLOfy5v8LwF5LQZXnZRNPp5D8";
    }


}