package com.example.calorietracker.API;

import android.util.Log;

import com.example.calorietracker.API.Form.RegisterForm;
import com.example.calorietracker.utils.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Consume the RESTful web service created in the Phase 1, access data
 */

public class RestClient {
    // log tag
    public static final String TAG = "RestClient";

    // base url
    private static final String BASE_URL = "http://118.138.66.19:48649/CalorieTracker/webresources/";

    // check when log in
    public static final String checkUserNameAndPwdHash(String username, String hashpwd) {
        final String methodPath = "restws.credential/checkByUsernameAndPwdHash/" + username + "/" + hashpwd;
        String result = HttpHelper.downloadUrl(BASE_URL + methodPath);
        Log.i(TAG, BASE_URL + methodPath);
        try {
            JSONObject jsonObject = new JSONObject(result);

            if (jsonObject != null) {
                Log.i(TAG, "result is" + jsonObject.getString("result"));
                return jsonObject.getString("result");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "NO INFO FOUND");
        }

        return "NO INFO FOUND";
    }

    // find user infomation according to "username" (when log in, call it and store it in SharedPreference
    public static final JSONObject getUserInfo(String username) {
        final String methodPath = "restws.appuser/findByUsername/" + username;
        String result = HttpHelper.downloadUrl(BASE_URL + methodPath);

        Log.i(TAG, BASE_URL + methodPath);
        Log.i(TAG, "result is" + result);

        JSONObject resJson = null;

        try {
            resJson = new JSONObject(result);


            return resJson;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "NO INFO FOUND");
        }
        try {
            Log.i("RestClient", resJson.getString("firstname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resJson;
    }



    // "post" user's infomation when register
    public static final JSONObject register(RegisterForm registerForm) {

        String methodPath = "restws.appuser/register";
        String params = "name=" + registerForm.getName() + "&"  +
                "surname=" + registerForm.getSurname() + "&" +
                "email=" + registerForm.getEmail() + "&"  +
                "dob=" + registerForm.getDob() + "&" +
                "height=" +registerForm.getHeight() + "&" +
                "weight=" + registerForm.getWeight() + "&" +
                "gender=" + registerForm.getGender() + "&" +
                "address=" + registerForm.getAddress() + "&" +
                "postcode=" + registerForm.getPostcode() + "&" +
                "levelOfActivivty=" + registerForm.getLevelOfActivity() + "&" +
                "stepsPerMile=" + registerForm.getStepsPerMile() + "&" +
                "username="+ registerForm.getUsername() + "&" +
                "hash=" + registerForm.getHash();


        String result = "";
        JSONObject resJson = null;
        result = HttpHelper.postUrl(BASE_URL + methodPath, params);
        Log.i(TAG, result);
        try {
            if (result != null) {
                resJson = new JSONObject(result);
                return resJson;
            }
            resJson = new JSONObject().put("code", 400)
                    .put("message", "Server wrong");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resJson;
    }

    // find food infomation according to "foodname"
    public static String findFood(String foodName) {
        final String methodPath = "restws.food/findByFoodName/" + foodName;
        String result = HttpHelper.downloadUrl(BASE_URL + methodPath);
        Log.i(TAG, BASE_URL + methodPath);
        Log.i(TAG, "result is" + result);
        return result;
    }


    // find all food
    public static final JSONArray getFood() {
        final String methodPath = "restws.food/";
        String result = HttpHelper.downloadUrl(BASE_URL + methodPath);

        Log.i(TAG, BASE_URL + methodPath);
        Log.i(TAG, "result is" + result);

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;

    }

    // save new food to server
    public static final JSONObject saveNewFood(HashMap<String, Object> food) {
        String methodPath = "restws.food/saveFood";
        String params = "foodName=" + food.get("foodName").toString() +
                "&"  + "category=" + food.get("category").toString() +
                "&"  + "calorie=" + food.get("calorie").toString() +
                "&"  + "serving_unit=" + food.get("serving_unit").toString() +
                "&"  + "serving_amount=" + food.get("serving_amount").toString() +
                "&"  + "fat=" + food.get("fat");
        Log.i("TEST", params);

        String result = "";
        JSONObject resJson = null;
        result = HttpHelper.postUrl(BASE_URL + methodPath, params);
        Log.i("TEST", "result " + result);
        try {
            if (!result.equals("")) {
                resJson = new JSONObject(result);
                return resJson;
            } else {
                resJson = new JSONObject().put("code", 400)
                        .put("message", "Server wrong");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resJson;
    }

    // save comsumptionx
    public static final JSONObject saveToComsumption(String email, String foodName) {
        String methodPath = "restws.consumption/saveComsumption";
        String params = "email=" + email + "&"  +
                "foodName=" + foodName;


        String result = "";
        JSONObject resJson = null;
        result = HttpHelper.postUrl(BASE_URL + methodPath, params);
        Log.i(TAG, result);
        try {
            if (!result.equals("")) {
                resJson = new JSONObject(result);
                return resJson;
            } else {
                resJson = new JSONObject().put("code", 400)
                        .put("message", "Server wrong");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resJson;
    }

    // get calorie consumed
    public static final int getDailyCalorieConsumed(Integer userId, String date) {
        final String methodPath = "restws.appuser/dailyCaloriesConsumed/" + userId + "/" + date;
        String result = HttpHelper.downloadUrl(BASE_URL + methodPath);

        Log.i(TAG, BASE_URL + methodPath);
        Log.i(TAG, "result is" + result);


        return Integer.parseInt(result);
    }

    // get calorie burned per step
    public static final double getCalorieBurnedPerStep(Integer userId) {
        final String methodPath = "restws.appuser/caloriesBurnedPerSteps/" + userId;
        String result = HttpHelper.downloadUrl(BASE_URL + methodPath);

        Log.i(TAG, BASE_URL + methodPath);
        Log.i(TAG, "result is" + result);


        return Double.parseDouble(result);
    }

    // get calorie burned at rest
    public static final double getCalorieBurnedAtRest(Integer userId) {
        final String methodPath = "restws.appuser/caloriesBurnedAtRest/" + userId;
        String result = HttpHelper.downloadUrl(BASE_URL + methodPath);

        Log.i(TAG, BASE_URL + methodPath);
        Log.i(TAG, "result is" + result);


        return Double.parseDouble(result);
    }

    // get period data for repo
    public static final JSONObject getPeriodReport(Integer userId, String startDate, String endDate) {
        final String methodPath = "restws.report/periodReport/" + userId + "/" + startDate + "/" + endDate;
        String result = HttpHelper.downloadUrl(BASE_URL + methodPath);

        JSONObject resJson = null;
        Log.i(TAG, BASE_URL + methodPath);
        Log.i(TAG, "result is" + result);

        try {
            resJson = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return resJson;
    }

    // get period data for repo
    public static final JSONArray getPeriodReportPerDay(Integer userId, String startDate, String endDate) {
        final String methodPath = "restws.report/periodReportPerDay/" + userId + "/" + startDate + "/" + endDate;
        String result = HttpHelper.downloadUrl(BASE_URL + methodPath);

        JSONArray resJson = null;
        Log.i(TAG, BASE_URL + methodPath);
        Log.i(TAG, "result is" + result);

        try {
            resJson = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return resJson;
    }


    // save new food to server
    public static final JSONObject saveReport(String email,String d, int caloriesConsumed,int caloriesBurned, int steps, int calorieGoal ) {
        String methodPath = "restws.report/saveReport";
        String params = "email=" + email +
                "&"  + "date=" + d +
                "&"  + "caloriesConsumed=" + caloriesConsumed +
                "&"  + "caloriesBurned=" + caloriesBurned +
                "&"  + "steps=" + steps +
                "&"  + "calorieGoal=" + calorieGoal;
        Log.i("TEST", params);

        String result = "";
        JSONObject resJson = null;
        result = HttpHelper.postUrl(BASE_URL + methodPath, params);
        Log.i("TEST", "result " + result);
        try {
            if (!result.equals("")) {
                resJson = new JSONObject(result);
                return resJson;
            } else {
                resJson = new JSONObject().put("code", 400)
                        .put("message", "Server wrong");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resJson;
    }
}


