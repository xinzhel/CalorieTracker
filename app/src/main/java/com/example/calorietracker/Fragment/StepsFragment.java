package com.example.calorietracker.Fragment;

import android.app.Fragment;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.Update;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calorietracker.R;
import com.example.calorietracker.Step;
import com.example.calorietracker.StepDatabase;
import com.example.calorietracker.services.MyServices;
import com.example.calorietracker.utils.TypeConverter;
import com.example.calorietracker.utils.Validation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.example.calorietracker.utils.Validation.isInteger;

public class StepsFragment extends Fragment {
    View v;
    // declare the list view
    List<HashMap<String, String>> stepListArray;
    SimpleAdapter myListAdapter;
    ListView stepList;



    EditText etSteps = null;
    Button btnSteps = null;
    TextView tvSteps = null;
    StepDatabase db = null;

    EditText etChange = null;
    Button btnChange = null;
    Spinner spChange = null;
    Button btnPost = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_steps, container, false);

        // set the title of toolbar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Step");

        // initialize variables
        stepList = (ListView) v.findViewById(R.id.lv);
        etSteps = (EditText) v.findViewById(R.id.et_steps);
        btnSteps = (Button) v.findViewById(R.id.btn_steps);
        tvSteps = (TextView) v.findViewById(R.id.tv_steps);

        etChange = (EditText) v.findViewById(R.id.et_change);
        btnChange = (Button) v.findViewById(R.id.btn_change);
        spChange = (Spinner) v.findViewById(R.id.spinner_change);
        stepListArray = new ArrayList<HashMap<String, String>>();
        btnPost = (Button) v.findViewById(R.id.btn_post);

        db = Room.databaseBuilder(getActivity().getApplicationContext(),
                StepDatabase.class, "StepDatabase")
                .fallbackToDestructiveMigration()
                .build();

        // populate the spinner with time
        PopulateSpinner populateSpinner = new PopulateSpinner();
        populateSpinner.execute();

        // display the step records
        DisplaySteps displaySteps = new DisplaySteps();
        displaySteps.execute();

        // add the steps
        btnSteps.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // check the input
                String input = etSteps.getText().toString().trim();
                if (isInteger(input) == true && !input.equals("")) {

                    int add = Integer.parseInt(input);
                    AddSteps addSteps = new AddSteps();
                    addSteps.execute(add);

                    // update the step records
                    DisplaySteps displaySteps = new DisplaySteps();
                    displaySteps.execute();
                    // populate the spinner with time
                    PopulateSpinner populateSpinner = new PopulateSpinner();
                    populateSpinner.execute();

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please input a number!",Toast.LENGTH_LONG).show();
                }

            }
        });


        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = spChange.getSelectedItem().toString();
                String changeSteps = etChange.getText().toString();
                if (!time.equals("") && time!= "Time:" ) {
                    if (!changeSteps.equals("") && isInteger(changeSteps)) {
                        // change the step at a given time
                        ChangeStep changeStep = new ChangeStep();
                        changeStep.execute();
                        // update the step records
                        DisplaySteps displaySteps = new DisplaySteps();
                        displaySteps.execute();
                        // populate the spinner with time
                        PopulateSpinner populateSpinner = new PopulateSpinner();
                        populateSpinner.execute();
                    }else {
                        Toast.makeText(getActivity().getApplicationContext(), "Please input a number!",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please choose a time to change!",Toast.LENGTH_LONG).show();
                }

            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyServices.class);
                getActivity().startService(intent);
                // update the step records
                DisplaySteps displaySteps = new DisplaySteps();
                displaySteps.execute();

            }
        });

        return v;
    }



    private class AddSteps extends AsyncTask<Integer, Void, String> {
        @Override protected String doInBackground(Integer... input) {
            int addSteps = input[0];
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Step add = new Step(sdf.format(Calendar.getInstance().getTime()), addSteps);
            db.stepDAO().insert(add);
            return "Success";
        }
        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getActivity().getApplicationContext(), s,Toast.LENGTH_LONG).show();
        }
    }

    private class DisplaySteps extends AsyncTask<Void, Void, List<Step>> {
        @Override protected List<Step> doInBackground(Void... params) {
            List<Step> stepRecords = db.stepDAO().getAll();
            return stepRecords;
        }

        @Override
        protected void onPostExecute(List<Step> stepRecords) {
            // display the each added records
            stepListArray.clear();
            for (Step step: stepRecords) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Time",step.getDate());
                map.put("Steps",Integer.toString(step.getSteps()));
                stepListArray.add(map);
            }
//            Log.i("TEST", stepListArray.get(0).toString());
//            Log.i("TEST", stepListArray.get(5).toString());
            myListAdapter = new SimpleAdapter(getActivity(), stepListArray, R.layout.list_view,
                    new String[] {"Time", "Steps"}, new int[] {R.id.lv_time, R.id.lv_step});
            stepList.setAdapter(myListAdapter);

            // display the total
            int total = 0;
            for (Step step: stepRecords) {
                total += step.getSteps();
            }
            tvSteps.setText("You have taken " + total + " Steps.");

        }
    }

    private class PopulateSpinner extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> list = new ArrayList<>();
            list.add("Choose Time");
            List<Step> steps = db.stepDAO().getAll();
            for (Step step : steps) {
                list.add(step.getDate());
            }
            return list;
        }


        @Override
        protected void onPostExecute(List<String> list) {
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spChange.setAdapter(spinnerAdapter);


        }
    }

    private class ChangeStep extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String time = spChange.getSelectedItem().toString();
            String changeSteps = etChange.getText().toString();
            Step updateStep = new Step(time, Integer.parseInt(changeSteps));
            db.stepDAO().updateStep(updateStep);
            return null;
        }
    }


//
//    // check if there is a record for today
//    Step stepToday = db.stepDAO().findByDate(TypeConverter.dateToString(new Date()));
//
//                if (stepToday == null) {
//        Log.i("test", "today no record" );
//        Step step = new Step(TypeConverter.dateToString(new Date()), addSteps);
//        long id = db.stepDAO().insert(step);
//
//    } else {
//        Log.i("test", "already have record" + stepToday.getSteps() );
//        stepToday.setSteps(stepToday.getSteps() + addSteps);
//        db.stepDAO().updateStep(stepToday);
//    }

}
