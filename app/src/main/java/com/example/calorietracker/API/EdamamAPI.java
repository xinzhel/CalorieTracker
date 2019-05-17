package com.example.calorietracker.API;

import android.util.Log;

import com.example.calorietracker.utils.HttpHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EdamamAPI {
    // log tag
    public static final String TAG = "EdamamAPI";

    // base url
    private static final String BASE_URL = "https://api.edamam.com/api/food-database/parser?";

    private static final String API_KEY = "b90ec04983ae402460f746d0c70df53f";
    private static final String API_ID = "a8eb72f0";

    // find all food infomation according to "foodname"
    public static String findFood(String foodName) {

//        'https://api.edamam.com/api/food-database/parser?nutrition-type=logging&ingr=red%20apple&app_id={your app_id}&app_key={your app_key}'
        StringBuilder stringBuilder = new StringBuilder(BASE_URL);
        stringBuilder.append("nutrition-type=logging");
        stringBuilder.append("&ingr=");
        stringBuilder.append(foodName);
        stringBuilder.append("&app_id=");
        stringBuilder.append(API_ID);
        stringBuilder.append("&app_key=");
        stringBuilder.append(API_KEY);
        String result = HttpHelper.downloadUrl(stringBuilder.toString());
        Log.i(TAG, stringBuilder.toString());
        Log.i(TAG, "result is" + result);
        return result;
    }

    // parse the response and get the locations and names of parks from the result
    public static HashMap<String, Object> parseDetail(String result) {

        HashMap<String, Object> parseRes = new HashMap<String, Object>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("parsed");

            if (jsonArray != null && jsonArray.length() > 0) {
                JSONObject parsed = (JSONObject) jsonArray.get(0);

                String serving_amount = parsed.getString("quantity");
                String serving_unit = parsed.getJSONObject("measure").getString("label");
                JSONObject food = parsed.getJSONObject("food");

                String category = food.getString("category");
                String calorie = food.getJSONObject("nutrients").getString("ENERC_KCAL");
                String fat = food.getJSONObject("nutrients").getString("FAT");
                String image = food.getString("image");
                Log.i(TAG, category);
                Log.i(TAG, calorie);
                Log.i(TAG, fat);
                Log.i(TAG, serving_amount);
                Log.i(TAG, serving_unit);

                parseRes.put("category", category);
                parseRes.put("calorie", calorie);
                parseRes.put("fat", fat);
                parseRes.put("serving_amount", serving_amount);
                parseRes.put("serving_unit", serving_unit);
                parseRes.put("image", image);


            }
        } catch (Exception e) {
            e.printStackTrace();
            parseRes = null;
        }

        return parseRes;
    }
}
