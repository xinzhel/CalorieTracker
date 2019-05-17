package com.example.calorietracker.Fragment;


import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calorietracker.API.RestClient;
import com.example.calorietracker.R;
import com.example.calorietracker.Step;
import com.example.calorietracker.StepDatabase;
import com.example.calorietracker.utils.TypeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_MULTI_PROCESS;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.MODE_WORLD_WRITEABLE;
import static com.example.calorietracker.utils.Validation.isInteger;

public class MainFragment extends Fragment {

    View vMain;

    private TextView tvCurrTime;
    private TextView tvWelcome;

    private TextView tvCalorieConsumed;
    private TextView tvCalorieBurned;
    private TextView tvCaloriRemain;

    private Button btnGoal;
    private EditText etGoal;
    private TextView tvShowGoal;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMain = inflater.inflate(R.layout.fragment_main, container, false);

        // set the title of toolbar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Home");

        // display current date and time
          //Date currentTime = Calendar.getInstance().getTime();
          //String strTime = currentTime.toString();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        tvCurrTime = vMain.findViewById(R.id.current_time);
        tvCurrTime.setText(dateFormat.format(date));

        // display the calories consumed and burned
        tvCalorieConsumed = vMain.findViewById(R.id.tv_calorie_consumed);
        tvCalorieBurned = vMain.findViewById(R.id.tv_calorie_burned);
        tvCaloriRemain = vMain.findViewById(R.id.tv_calorie_remain);
        DisplayCalorie displayCalorie = new DisplayCalorie();
        displayCalorie.execute();

        // display welcome message
        tvWelcome = (TextView) vMain.findViewById(R.id.welcome);
        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("User", MODE_MULTI_PROCESS);
        String firstname = sp.getString("firstname", null);
        Log.i("MainFragment", firstname);
        tvWelcome.setText("Welcome, " + firstname);

        // Set the goal
        etGoal = vMain.findViewById(R.id.et_set_goal);
        btnGoal = vMain.findViewById(R.id.btn_set_goal);
        tvShowGoal = vMain.findViewById(R.id.tv_show_goal);
        btnGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input = etGoal.getText().toString().trim();
                // add steps
                if (input.equals("") ) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please give a number!",Toast.LENGTH_LONG).show();
                } else {
                    if (isInteger(input) || input.equals("")) {
                        int goal = Integer.parseInt(input);
                        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("User", MODE_MULTI_PROCESS);
                        SharedPreferences.Editor ed = sp.edit();
                        ed.putInt("goal", goal);
                        ed.commit();
                        tvShowGoal.setText("Your current goal is: \n" + sp.getInt("goal", 0));

                        int calorieComsumed = Integer.parseInt(tvCalorieConsumed.getText().toString().split("\n")[1].trim());

                        int calorieBurned = Integer.parseInt(tvCalorieBurned.getText().toString().split("\n")[1].trim());
                        tvCaloriRemain.setText("Calorie remaining: \n" + (goal - calorieComsumed + calorieBurned));

                    } else
                        Toast.makeText(getActivity().getApplicationContext(), "PLEASE ENTER THE NUMBER", Toast.LENGTH_LONG).show();
                }
            }
        });

        return vMain;
    }

    private class DisplayCalorie extends AsyncTask<Void, Void, List<Integer>> {
        @Override
        protected List<Integer> doInBackground(Void... voids) {
            SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("User", MODE_MULTI_PROCESS );

            // get calorieConsumed
            int userId = sp.getInt("userId", 0);
            int calorieConsumed = 0;
            calorieConsumed = RestClient.getDailyCalorieConsumed(userId, TypeConverter.dateToString(new Date()));

            // get the info we need to calculate calorie burned
            double calorieBurned = 0;
            double calorieBurnedPerStep = RestClient.getCalorieBurnedPerStep(userId);
            double calorieBurnedAtRest = RestClient.getCalorieBurnedAtRest(userId);
            StepDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(),
                    StepDatabase.class, "StepDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
            Step step = db.stepDAO().findByDate(TypeConverter.dateToString(new Date()));
            if (step != null) {
                calorieBurned = step.getSteps() * calorieBurnedPerStep + calorieBurnedAtRest;
            } else {
                calorieBurned = calorieBurnedAtRest;
            }
            // get the goal if it is set for today

            SharedPreferences.Editor ed = sp.edit();
            int goal = sp.getInt("goal", 0);

            List<Integer> result = new ArrayList<>();
            result.add(calorieConsumed);
            result.add((int)calorieBurned);
            result.add(goal);
            return result;
        }


        @Override
        protected void onPostExecute(List<Integer> result) {

            tvCalorieConsumed.setText("Calorie consumed: \n" + result.get(0));
            tvCalorieBurned.setText("Calorie burned: \n" + result.get(1));
            tvCaloriRemain.setText( "Calorie remaining: \n" + (result.get(2) - (result.get(0) - result.get(1))) );
            tvShowGoal.setText("Your current goal is: \n" + result.get(2));
        }
    }
}

