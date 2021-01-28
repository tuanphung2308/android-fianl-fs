package com.google.firebase.example.fireeats.java.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.adapter.OrderCartObjectAdapter;
import com.google.firebase.example.fireeats.java.helper.TaskDirectionRequest;
import com.google.firebase.example.fireeats.java.model.Order;
import com.google.firebase.example.fireeats.java.utils.Tools;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static androidx.core.location.LocationManagerCompat.isLocationEnabled;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager manager;
    private static LatLng lastLocation;
    private FusedLocationProviderClient client;
    private Marker currentLocationMarker;
    private LocationRequest locationRequest;
    private String orderId;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FASTEST_INTERVAL = 5000; // 5 sec
    private static final int MY_LOCATION_REQUEST = 99;
    private static int DISPLACEMENT = 10; // 10 meters
    private Order order;
    private FirebaseFirestore mFirestore;
    private LatLng currentLocation;
    SupportMapFragment mMapFragment;
    MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet_map);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("fuck you");
        Log.e("FUck you", orderId);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();


        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        initToolbar();

        try {
            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            } else {
                Log.e("TAG", "Provider avaiable");
                mMapFragment.getMapAsync(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        DocumentReference docRef = mFirestore.collection("orders").document(orderId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    order = snapshot.toObject(Order.class);
                    if (markerOptions != null) {
                        mMap.clear();
                    }
                    currentLocation = new LatLng(order.getLatitude(), order.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                    int height = 250;
                    int width = 250;
                    Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.badge_goal);
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                    mMap.addMarker(new MarkerOptions()
                            .position(currentLocation)
                            .icon(smallMarkerIcon));

                    markerOptions = new MarkerOptions().position(new LatLng(order.getDriverLatitude(), order.getDriverLongitude()));
                    mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.top_down_courier_icon_50x50)));

                    List<LatLng> destinations = new ArrayList<>();
                    destinations.add(new LatLng(order.getDriverLatitude(), order.getDriverLongitude()));
                    drawDirectionToMultipleDestinations(destinations);

                } else {
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tracking Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        client = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        mMap = Tools.configActivityMaps(googleMap);
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        MapActivity.this, R.raw.style_json));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST);
        }
    }

    private String buildDirectionRequestUrl(LatLng origin, LatLng destination, List<LatLng> waypoints) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;

        String strWaypoints = "";
        if (!waypoints.isEmpty()) {
            strWaypoints = "waypoints=" + waypoints.get(0).latitude + "," + waypoints.get(0).longitude;
            for (int i = 1; i < waypoints.size(); i++) {
                Log.e("WITHIN LOOP", waypoints.get(i).toString());

                strWaypoints = strWaypoints.concat("|" + waypoints.get(i).latitude + ',' + waypoints.get(i).longitude);
            }
        }


        String sensor = "sensor=false";
        String mode = "mode=driving";

        String param;
        if (strWaypoints.equals("")) {
            param = strOrigin + "&" + strDestination + "&" + sensor + "&" + mode;
        } else {
            param = strOrigin + "&" + strDestination + "&" + strWaypoints + "&" + sensor + "&" + mode;
        }

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=AIzaSyBlRMSeyPVLOfy5v8LwF5LQZXnZRNPp5D8";
        return url;
    }

    public void drawDirectionToMultipleDestinations(List<LatLng> destinations) {
        List<LatLng> route = getOptimalRoute(currentLocation, destinations);
        LatLng destination = route.get(route.size() - 1);
        route.remove(route.size() - 1);
        new TaskDirectionRequest(mMap, this).execute(buildDirectionRequestUrl(currentLocation, destination, route));
    }

    // Return a list of greedy optimal route from closest delivery point to destination
    private List<LatLng> getOptimalRoute(LatLng origin, List<LatLng> remainingLatLng) {
        List<LatLng> route = new ArrayList<>();
        List<LatLng> bufferRemainingLatLng = new ArrayList<>(remainingLatLng);
        LatLng currentLocation = origin;
        int closestIndex;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < remainingLatLng.size(); i++) {
            closestIndex = 0;
            for (int j = 0; j < bufferRemainingLatLng.size(); j++) {
                double distance = getDistanceFromLatLng(currentLocation, bufferRemainingLatLng.get(closestIndex));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestIndex = j;
                }
            }

            route.add(bufferRemainingLatLng.get(closestIndex));
            currentLocation = bufferRemainingLatLng.get(closestIndex);
            bufferRemainingLatLng.remove(closestIndex);
        }
        //Collections.reverse(route);
        return route;
    }

    private double getDistanceFromLatLng(LatLng origin, LatLng destination) {
        double lat_dist = Math.abs(origin.latitude - destination.latitude);
        double long_dist = Math.abs(origin.longitude - destination.longitude);
        return Math.sqrt(lat_dist * lat_dist + long_dist * long_dist);
    }

}