package com.example.calorietracker.Fragment;


import android.app.DatePickerDialog;
import android.app.Fragment;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.calorietracker.API.RestClient;
import com.example.calorietracker.R;
import com.example.calorietracker.Step;
import com.example.calorietracker.StepDatabase;
import com.example.calorietracker.utils.TypeConverter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_MULTI_PROCESS;

public class ReportFragment extends Fragment {
    View v;

    private float[] yData = new float[3];
    private String[] xData = {"Calorie consumed", "Calorie burned", "Calorie Remaining"};

    PieChart pieChart;
    DatePicker date;
    Button btnChooseD;

    BarChart barChart;
    EditText startDate, endDate;
    Button btnStartDate, btnEndDate;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_report, container, false);

        pieChart = (PieChart) v.findViewById(R.id.chart);
        date = (DatePicker) v.findViewById(R.id.dp_report);
        btnChooseD = (Button) v.findViewById(R.id.btn_chooseD);

        barChart = (BarChart) v.findViewById(R.id.barchart);
        startDate = (EditText) v.findViewById(R.id.start_date);
        endDate = (EditText) v.findViewById(R.id.end_date);
        btnStartDate = (Button) v.findViewById(R.id.btn_start_date);
        btnEndDate = (Button) v.findViewById(R.id.btn_end_date);

        // show daily report via pie chart
        String chooseD = date.getYear() + "-" + (date.getMonth() + 1) + "-" + date.getDayOfMonth();
        Description description = new Description();
        description.setText("Daily Report");
        pieChart.setDescription(description);
        pieChart.setHoleRadius(25f);
        pieChart.setCenterText(chooseD);
        pieChart.setCenterTextSize(10);
        pieChart.setUsePercentValues(true);

        Calendar now = Calendar.getInstance();
        date.init(
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    }
                });

        btnChooseD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chooseD = date.getYear() + "-" + (date.getMonth() + 1) + "-" + date.getDayOfMonth();
                Log.i("ReportFragment", chooseD);
                DispleyPieChart displeyPieChart = new DispleyPieChart();
                displeyPieChart.execute(chooseD);
            }
        });

        // show period report via bar chart
        String startD = "";
        String endD = "";
        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                startDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                endDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                String startD = startDate.getText().toString();
                                String endD = endDate.getText().toString();
                                if (startD != "") {
                                    Log.i("ReportFragment", startD);
                                    DisplayBarChart displayBarChart = new DisplayBarChart();
                                    displayBarChart.execute(startD, endD);
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        return v;
    }

    private class DispleyBarChart extends AsyncTask<String, Void, BarData> {
        @Override
        protected BarData doInBackground(String... params) {

            // get calorieConsumed
            SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("User", MODE_MULTI_PROCESS);
            int userId = sp.getInt("userId", 0);
            JSONObject result = RestClient.getPeriodReport(userId, params[0], params[1]);
            Log.i("ReportFragment", params[0] + params[1]);

            int calorieConsumed = 0;
            int calorieBurned = 0;
            try {
                calorieConsumed = result.getInt("caloriesConsumed");
                calorieBurned = result.getInt("caloriesBurned");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            List<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0f,(float) calorieConsumed ));
            entries.add(new BarEntry(1f, (float) calorieBurned));

            BarDataSet set = new BarDataSet(entries, "BarDataSet");
            set.setColors(ColorTemplate.VORDIPLOM_COLORS);

            BarData data = new BarData(set);
            data.setBarWidth(0.9f); // set custom bar width

            return data;
        }


        @Override
        protected void onPostExecute(BarData data) {

            barChart.setData(data);
            barChart.setFitBars(true); // make the x-axis fit exactly all bars
            barChart.invalidate(); // refresh

            // the labels that should be drawn on the XAxis
            final String[] report = new String[] { "total calorie consumed", "total calorie burned"};
            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return report[(int) value];
                }
            };
            XAxis xAxis = barChart.getXAxis();
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        }
    }


    private class DisplayBarChart extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... params) {

            SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("User", MODE_MULTI_PROCESS);
            int userId = sp.getInt("userId", 0);
            JSONArray result = RestClient.getPeriodReportPerDay(userId, params[0], params[1]);
//            Log.i(TAG, params[0] + params[1]);

            return result;
        }


        @Override
        protected void onPostExecute(JSONArray result) {
            int[] calorieConsumed = new int[result.length()];
            int[] calorieBurned = new int[result.length()];
            final String[] xlabel = new String[result.length()];
            for (int i = 0 ; i < result.length(); i++) {
                JSONObject obj = null;
                try {
                    obj = result.getJSONObject(i);
                    calorieConsumed[i] = obj.getInt("caloriesConsumed");
                    Log.i("TEST", calorieConsumed[i]+"");
                    calorieBurned[i] = obj.getInt("caloriesBurned");
                    Log.i("TEST", calorieBurned[i]+"");
                    xlabel[i] = obj.getString("date");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            List<BarEntry> entriesGroup1 = new ArrayList<>();
            List<BarEntry> entriesGroup2 = new ArrayList<>();
            // fill the lists
            for(int i = 0; i < calorieConsumed.length; i++) {
                entriesGroup1.add(new BarEntry(i, calorieConsumed[i]));
                entriesGroup2.add(new BarEntry(i, calorieBurned[i]));
            }
            BarDataSet set1 = new BarDataSet(entriesGroup1, "caloriesConsumed");
            set1.setColor(Color.BLUE);
            BarDataSet set2 = new BarDataSet(entriesGroup2, "caloriesBurned");
            set2.setColor(Color.CYAN);


            float barWidth = 0.45f; // x2 dataset

            BarData data = new BarData(set1, set2);
            data.setBarWidth(barWidth); // set the width of each bar

            float groupSpace = 0.06f;
            float barSpace = 0.02f; // x2 dataset
            barChart.setData(data);
            barChart.setFitBars(true); // make the x-axis fit exactly all bars
            barChart.groupBars(0f, groupSpace, barSpace); // perform the "explicit" grouping
            barChart.invalidate(); // refresh

//            // the labels that should be drawn on the XAxis

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return xlabel[(int) value];
                }
            };
            XAxis xAxis = barChart.getXAxis();
            xAxis.setLabelRotationAngle(7f);
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        }
    }


    private class DispleyPieChart extends AsyncTask<String, Void, PieData> {
        @Override
        protected PieData doInBackground(String... params) {

            // get calorieConsumed
            SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("User", MODE_MULTI_PROCESS);
            int userId = sp.getInt("userId", 0);
            int calorieConsumed = 0;
            calorieConsumed = RestClient.getDailyCalorieConsumed(userId, params[0]);
            Log.i("ReportFragment", params[0]);

            // get the info we need to calculate calorie burned
            double calorieBurned = 0;
            double calorieBurnedPerStep = RestClient.getCalorieBurnedPerStep(userId);
            double calorieBurnedAtRest = RestClient.getCalorieBurnedAtRest(userId);
            StepDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(),
                    StepDatabase.class, "StepDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
            Step step = db.stepDAO().findByDate(params[0]);

            if (step == null) {
                calorieBurned = calorieBurnedAtRest;
            } else {
                calorieBurned = step.getSteps() * calorieBurnedPerStep + calorieBurnedAtRest;
            }


            // get the goal if it is set for today
            SharedPreferences.Editor ed = sp.edit();
            int goal = sp.getInt("goal", 0);


            yData[0] = calorieConsumed;
            yData[1] = (float) calorieBurned;
            yData[2] = goal + yData[1] - yData[0];

            List<PieEntry> yEntrys = new ArrayList<>();
            List<String> xEntrys = new ArrayList<>();

            for (int i = 0; i < yData.length; i++) {
                yEntrys.add(new PieEntry(yData[i], xData[i]));
            }

            // create the data set
            PieDataSet pieDataSet = new PieDataSet(yEntrys, "Report");
            pieDataSet.setSliceSpace(2);
            pieDataSet.setValueTextSize(12);

            // create pie data object
            PieData pieData = new PieData(pieDataSet);


            return pieData;
        }


        @Override
        protected void onPostExecute(PieData pieData) {
            pieChart.setData(pieData);
            pieChart.invalidate(); // refresh
        }
    }

}
