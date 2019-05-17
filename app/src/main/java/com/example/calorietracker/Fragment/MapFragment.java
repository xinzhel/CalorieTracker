package com.example.calorietracker.Fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.calorietracker.API.GoogleSearchAPI;
import com.example.calorietracker.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_MULTI_PROCESS;


public class MapFragment extends Fragment {
    View v;

    String TAG = "MAP";
    MapView mMapView;
    private GoogleMap mMap;
    private double
            HOME_LAT = -33.867487,
            HOME_LNG = 151.20699;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_map, container, false);

        // set the title of toolbar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Map");

//        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
//                .findFragmentById(R.id.map);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);



        if (servicesOK()) {

            mMapView.onResume(); // needed to get the map to display immediately
            MapsInitializer.initialize(getActivity().getApplicationContext());
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    // Add a marker in user's home and move the camera
                    SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("User", MODE_MULTI_PROCESS);
                    String homeString = sp.getString("address", null);
                    Log.i(TAG,homeString);

                    try {
                        Geocoder gc = new Geocoder(getActivity());
                        List<Address> list = gc.getFromLocationName(homeString, 1);
                        if (list.size() > 0) {
                            Address add = list.get(0);
                            String locality = add.getLocality();
//                Toast.makeText(this, "Found: " + locality, Toast.LENGTH_SHORT).show();

                            HOME_LAT = add.getLatitude();
                            HOME_LNG = add.getLongitude();
                            LatLng home = new LatLng(HOME_LAT, HOME_LNG);
                            mMap.addMarker(new MarkerOptions().position(home).title("My Home"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(home, 12.0f));
                            FindNearbyParks findNearbyParks = new FindNearbyParks();
                            findNearbyParks.execute();
                        }
                    } catch (IOException e) {
                        LatLng home = new LatLng(HOME_LAT, HOME_LNG);
                        mMap.addMarker(new MarkerOptions().position(home).title("My Home"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(home));
                        e.printStackTrace();
                    }

                }
            });

        } else {
            v = inflater.inflate(R.layout.fragment_map, container, false);
        }

        return v;
    }

    // Check if the Google Play Service is available
    public boolean servicesOK() {


        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        if (status == ConnectionResult.SUCCESS) {
            return true;
        }

        Log.e(TAG, "Google Play Services not available: " + GooglePlayServicesUtil.getErrorString(status));

        if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
            final Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 1);
            if (errorDialog != null) {
                errorDialog.show();
            }
        }

        return false;
    }




    private class FindNearbyParks extends AsyncTask<Void, Void, HashMap<String, List<Double>>> {
        @Override
        protected HashMap<String, List<Double>> doInBackground(Void... voids) {

            String result = GoogleSearchAPI.searchNearbyPark(HOME_LAT, HOME_LNG);
            HashMap<String, List<Double>> parseResult = GoogleSearchAPI.getParkPos(result);
            return parseResult;
        }


        @Override
        protected void onPostExecute(HashMap<String, List<Double>> parks) {
            double lat = 0;
            double lng = 0;
            String name = "";

            for(Map.Entry<String, List<Double>> entry : parks.entrySet()) {
                name = entry.getKey();
                Log.i(TAG, name);
                lat = entry.getValue().get(0);
                lng = entry.getValue().get(1);
                LatLng park = new LatLng(lat, lng);
                Log.i(TAG,"1" + park.toString());
                mMap.addMarker(new MarkerOptions().position(park).title(name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }

        }
    }
}
