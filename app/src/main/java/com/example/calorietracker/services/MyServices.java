package com.example.calorietracker.services;

import android.app.IntentService;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.calorietracker.API.RestClient;
import com.example.calorietracker.Step;
import com.example.calorietracker.StepDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.example.calorietracker.utils.TypeConverter.dateToString;

public class MyServices extends IntentService {

    public MyServices() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        // get the email
        SharedPreferences sp = getApplicationContext().getSharedPreferences("User", MODE_MULTI_PROCESS);
        String email = sp.getString("email", null);
        int userId = sp.getInt("userId", 0);

        // get the date
        Date date = new Date();
        String d = dateToString(date);


        // get goal
        int calorieGoal = sp.getInt("goal", 0);



        // get the total step
        StepDatabase db = Room.databaseBuilder(getApplicationContext(),
                StepDatabase.class, "StepDatabase")
                .fallbackToDestructiveMigration()
                .build();

        List<Step> stepRecords = db.stepDAO().getAll();

        int steps = 0;
        for (Step step: stepRecords) {
            steps += step.getSteps();
        }

        // get the total calorie consumed
        int caloriesConsumed = RestClient.getDailyCalorieConsumed(userId, dateToString(date));

        // get the total calorie burned
        int caloriesBurned = 0;
        double calorieBurnedPerStep = RestClient.getCalorieBurnedPerStep(userId);
        double calorieBurnedAtRest = RestClient.getCalorieBurnedAtRest(userId);
        double calorieBurnedD = steps * calorieBurnedPerStep + calorieBurnedAtRest;
        caloriesBurned = (int) calorieBurnedD;



        RestClient.saveReport(email, d, caloriesConsumed,caloriesBurned, steps,calorieGoal);

        // delete all records in steps table
        db.stepDAO().deleteAll();



    }
}
