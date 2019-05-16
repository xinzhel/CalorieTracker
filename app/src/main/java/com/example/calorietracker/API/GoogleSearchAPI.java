package com.example.calorietracker.API;

import android.nfc.Tag;
import android.util.Log;

import com.example.calorietracker.utils.HttpHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class GoogleSearchAPI {
    private static final String TAG = "GoogleSearchAPI";
    private static final String API_KEY = "AIzaSyDuoGbGnxys37TN63m9jKYpUE3iBlbTZQI";
    private static final String SEARCH_ID_cx = "009544938281723967362:e589gapcgf4";

    public static String search(String keyword, String[] params, String[] values) {
        keyword = keyword.replace(" ", "+");
        String query_parameter = "";

        if (params != null && values != null) {
            for (int i = 0; i < params.length; i++) {
                query_parameter += "&";
                query_parameter += params[i];
                query_parameter += "=";
                query_parameter += values[i];
            }
        }


        StringBuilder stringBuilder = new StringBuilder("https://www.googleapis.com/customsearch/v1?");
        stringBuilder.append("key=");
        stringBuilder.append(API_KEY);
        stringBuilder.append("&cx=");
        stringBuilder.append(SEARCH_ID_cx);
        stringBuilder.append("&q=");
        stringBuilder.append(keyword);
        stringBuilder.append(query_parameter);

        String result = HttpHelper.downloadUrl(stringBuilder.toString());

        return result;
    }

    public static String getSnippet(String result) {
        String snippet = null;

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            if (jsonArray != null && jsonArray.length() > 0) {
                snippet = jsonArray.getJSONObject(0).getString("snippet");
            }
        } catch (Exception e) {
            e.printStackTrace();
            snippet = "NO INFO FOUND";
        }

        return snippet;
    }


    // search the nearby parks
    public static String searchNearbyPark(double lat, double lng) {
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location=").append(lat).append(",").append(lng);
        stringBuilder.append("&type=park");
        stringBuilder.append("&radius=5000");
        stringBuilder.append("&key=");
        stringBuilder.append(API_KEY);
        String result = HttpHelper.downloadUrl(stringBuilder.toString());

        Log.i(TAG, result);
        return result;
    }


    // parse the response and get the locations and names of parks from the result
    public static HashMap<String, List<Double>> getParkPos(String results) {

        HashMap<String, List<Double>> parseRes = new HashMap<String, List<Double>>();
        try {
            JSONObject jsonObject = new JSONObject(results);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String name = null;
                    List<Double> position = new ArrayList<Double>();
                    JSONObject result = (JSONObject) jsonArray.get(i);
                    name = result.getString("name");
                    Log.i(TAG, name);
                    // add latitude & longitude
                    JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
                    position.add(location.getDouble("lat"));
                    position.add(location.getDouble("lng"));
                    Log.i(TAG, location.getDouble("lat") + "");
                    Log.i(TAG, location.getDouble("lng") + "");
                    parseRes.put(name, position);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            parseRes = null;
        }

        return parseRes;
    }

// search the food description
    public static String searchFoodDesc(String food) {
        StringBuilder stringBuilder = new StringBuilder("https://www.googleapis.com/customsearch/v1?");
        stringBuilder.append("key=").append(API_KEY);
        stringBuilder.append("&cx=").append(SEARCH_ID_cx);
        stringBuilder.append("&q=");
        stringBuilder.append(food);
        String result = HttpHelper.downloadUrl(stringBuilder.toString());

        Log.i(TAG, result);
        return result;
    }

    // parse the results for food description
    // parse the response and get the locations and names of parks from the result
    public static String getFoodDesc(String results) {

        String parseRes = "";
        try {
            JSONObject jsonObject = new JSONObject(results);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            JSONObject result = (JSONObject) jsonArray.get(0);
            parseRes = result.getString("snippet");
            parseRes = parseRes.substring(16, parseRes.length());
            Log.i(TAG, parseRes);

        } catch (Exception e) {
            e.printStackTrace();
            parseRes = null;
        }

        return parseRes;
    }


}
