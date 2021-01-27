package com.google.firebase.example.fireeats.java.helper;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskParseDirection extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
    private GoogleMap mMap;
    private Context context;
    private static Polyline poly;

    TaskParseDirection(GoogleMap mMap, Context context){
        this.mMap = mMap;
        this.context = context;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonString) {
        List<List<HashMap<String, String>>> routes = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonString[0]);
            DirectionParser parser = new DirectionParser();
            routes = parser.parse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
        super.onPostExecute(lists);
        ArrayList<LatLng> points;
        PolylineOptions polylineOptions = null;

        for (List<HashMap<String, String>> path : lists) {
            points = new ArrayList<>();
            polylineOptions = new PolylineOptions();

            for (HashMap<String, String> point : path) {
                double lat = Double.parseDouble(point.get("lat"));
                double lon = Double.parseDouble(point.get("lng"));

                points.add(new LatLng(lat, lon));
            }
            polylineOptions.addAll(points);
            polylineOptions.width(15f);
            polylineOptions.color(Color.BLACK);
            polylineOptions.geodesic(true);
        }
        if (polylineOptions != null) {

            poly = mMap.addPolyline(polylineOptions);
        } else {
            Toast.makeText(context, "Direction not found", Toast.LENGTH_LONG).show();
        }
    }

    public static void removePoly(){
        if (poly != null){
            poly.remove();
        }
    }
}
