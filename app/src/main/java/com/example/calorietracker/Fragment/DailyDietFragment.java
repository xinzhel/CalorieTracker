package com.example.calorietracker.Fragment;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calorietracker.API.EdamamAPI;
import com.example.calorietracker.API.GoogleSearchAPI;
import com.example.calorietracker.API.RestClient;
import com.example.calorietracker.R;
import com.example.calorietracker.utils.Validation;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_MULTI_PROCESS;

public class DailyDietFragment extends Fragment {
    View v;

    private String TAG = "DailyDietFragment";

    private Spinner spFoodCate;
    private Spinner spFoodItem;

    private EditText etSearchFood;
    private Button btnAddFood;
    private TextView tvFoodDetail;
    private ImageView imageFood;
    private TextView tvFoodDesc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_daily_diet, container, false);
        spFoodCate = v.findViewById(R.id.spinner_food_category);
        spFoodItem = v.findViewById(R.id.spinner_food_item);

        etSearchFood = (EditText) v.findViewById(R.id.et_search_food);
        btnAddFood = (Button) v.findViewById(R.id.btn_add_food);
        tvFoodDetail = (TextView) v.findViewById(R.id.tv_food_detail);
        imageFood = (ImageView) v.findViewById(R.id.img_food);
        tvFoodDesc = (TextView) v.findViewById(R.id.tv_food_desc);

        // populate the food categoty list
        FoodCateAsyncTask foodCatetAsyncTask = new FoodCateAsyncTask();
        foodCatetAsyncTask.execute();

        // populate the food item list when the food category chosen
        spFoodCate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Food Category")) {

                } else {
                    FoodItemAsyncTask foodItemAsyncTask = new FoodItemAsyncTask();
                    foodItemAsyncTask.execute();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // post to the server when the food item is chosen from spinner
        spFoodItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Food")) {

                } else {
                    SaveComsumptionAsyncTask saveComsumptionAsyncTask = new SaveComsumptionAsyncTask();
                    saveComsumptionAsyncTask.execute();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //
        btnAddFood.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // get food to add from user
                String food = etSearchFood.getText().toString().trim();
                // add food to server and display the food info to user
                if (food.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please give a food name!", Toast.LENGTH_LONG).show();
                } else {
                    AddNewFood addNewFood = new AddNewFood();
                    addNewFood.execute(food);
                }


            }
        });
        return v;
    }


    private class FoodCateAsyncTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> list = new ArrayList<String>();
            list.add("Food Category");
            JSONArray food = RestClient.getFood();

            for (int i = 0; i < food.length(); i++) {
                JSONObject f = null;
                try {
                    f = (JSONObject) food.get(i);
                    if (!list.contains(f.getString("category")))
                        list.add(f.getString("category"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return list;
        }


        @Override
        protected void onPostExecute(List<String> list) {

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spFoodCate.setAdapter(spinnerAdapter);


        }
    }

    private class FoodItemAsyncTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {

            String category = spFoodCate.getSelectedItem().toString();
            JSONArray food = RestClient.getFood();

            List<String> list = new ArrayList<String>();
            list.add("Food");
            for (int i = 0; i < food.length(); i++) {
                JSONObject f = null;
                try {
                    f = (JSONObject) food.get(i);
                    if (category.equals(f.getString("category")))
                        list.add(f.getString("foodName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return list;
        }


        @Override
        protected void onPostExecute(List<String> list) {

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spFoodItem.setAdapter(spinnerAdapter);


        }
    }

    private class SaveComsumptionAsyncTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {

            SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("User", MODE_MULTI_PROCESS);
            String email = sp.getString("email", null);
            Log.i("test", email);
            String foodName = spFoodItem.getSelectedItem().toString();
            JSONObject result = RestClient.saveToComsumption(email, foodName);
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject res) {
            try {
                if (res.getInt("code") == 200) {
                    Toast.makeText(getActivity().getApplicationContext(), "Successfully record the comsumption!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Server wrong!", Toast.LENGTH_LONG).show();
            }

        }
    }


    private class AddNewFood extends AsyncTask<String, Void, HashMap<String, Object>> {
        @Override
        protected HashMap<String, Object> doInBackground(String... input) {
            // get the new food infomation
            String result = EdamamAPI.findFood(input[0]);
            HashMap<String, Object> res = EdamamAPI.parseDetail(result);
            res.put("foodName",input[0]);

            // save the new food to server
            JSONObject responseCode =RestClient.saveNewFood(res);
            Log.i(TAG, "saveRes:" + responseCode.toString());

            // transfer the image url to bitmap
            String urldisplay = res.get("image").toString();
            Log.i("TEST", "URL:"+urldisplay);
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            res.put("bitmap",mIcon11 );

            // get food description
            String foodDesc = GoogleSearchAPI.searchFoodDesc(input[0]);
            String parseDesc = GoogleSearchAPI.getFoodDesc(foodDesc);
            res.put("foodDesc", parseDesc);

            // get the response code to "res" to show "save" result to user
            try {
                res.put("code", responseCode.getString("code"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return res;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> parseRes) {
            // show "save" result to user
            try {
                if (Integer.parseInt(parseRes.get("code").toString()) == 200) {
                    Toast.makeText(getActivity().getApplicationContext(), "Successfully record the food!", Toast.LENGTH_LONG).show();
                }
                if (Integer.parseInt(parseRes.get("code").toString()) == 401) {
                    Toast.makeText(getActivity().getApplicationContext(), "Food exists!", Toast.LENGTH_LONG).show();
                }
                if (Integer.parseInt(parseRes.get("code").toString()) == 400) {
                    Toast.makeText(getActivity().getApplicationContext(), "Server wrong!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Server wrong!", Toast.LENGTH_LONG).show();
            }
            // Show detail about new food:
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("Fat");
            stringBuilder.append(":");
            stringBuilder.append(parseRes.get("fat"));
            stringBuilder.append("\n");
            stringBuilder.append("Calorie");
            stringBuilder.append(":");
            stringBuilder.append(parseRes.get("calorie"));
            tvFoodDetail.setText(stringBuilder.toString());
            imageFood.setImageBitmap((Bitmap) parseRes.get("bitmap"));
            tvFoodDesc.setText(parseRes.get("foodDesc").toString());
        }
    }


}


//