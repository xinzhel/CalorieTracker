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
import android.support.v7.widget.Toolbar;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_MULTI_PROCESS;

public class ReportFragment extends Fragment {
    View v;

    private float[] yData = new float[3];
    private String[] pieLabel = {"Calorie consumed", "Calorie burned", "Calorie Remaining"};

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

        // set the title of toolbar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Report");

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
        pieChart.setCenterTextSize(10);
//        pieChart.setUsePercentValues(true);

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
                    Log.i("TEST", i + "" + calorieConsumed[i]);
                    calorieBurned[i] = obj.getInt("caloriesBurned");
                    Log.i("TEST", i + "" + calorieBurned[i]);
                    xlabel[i] = obj.getString("date");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            List<BarEntry> entriesGroup1 = new ArrayList<>();
            List<BarEntry> entriesGroup2 = new ArrayList<>();
            // fill the lists
            for(int i = 0; i < calorieConsumed.length; i++) {
                entriesGroup1.add(new BarEntry( i, calorieConsumed[i], xlabel[i]));
                Log.i("TEST", "i is:" + i);
                entriesGroup2.add(new BarEntry( i, calorieBurned[i],  xlabel[i]));
            }
            BarDataSet set1 = new BarDataSet(entriesGroup1, "caloriesConsumed");
            set1.setColor(Color.BLUE);
            BarDataSet set2 = new BarDataSet(entriesGroup2, "caloriesBurned");
            set2.setColor(Color.CYAN);






            float groupSpace = 0.06f;
            float barSpace = 0.02f; // x2 dataset
            float barWidth = 0.45f; // x2 dataset
            // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"

            BarData data = new BarData(set1, set2);
            data.setBarWidth(barWidth); // set the width of each bar

            barChart.setData(data);
//            barChart.setFitBars(true); // make the x-axis fit exactly all bars
            barChart.groupBars(-0.5f, groupSpace, barSpace); // perform the "explicit" grouping
            barChart.invalidate(); // refresh


            // customize x axis
            XAxis xAxis = barChart.getXAxis();
            xAxis.setLabelRotationAngle(27f);
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            // the labels that should be drawn on the XAxis
            xAxis.setValueFormatter(new IAxisValueFormatter(){

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    String res = "";
                    String strVal = String.valueOf((int)value);
                    Log.i("TEST", "VALUE" + strVal);
                    if (strVal.equals("-1") || strVal.equals(String.valueOf(xlabel.length)))
                        res = "";
                    else {
                        try{
                            res = String.valueOf(xlabel[(int) value]);
                        } catch (Exception e) {
                            res = "";
                        }

                    }
                    return res;

                }
            });
            xAxis.setEnabled(true);
            xAxis.setDrawLabels(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setCenterAxisLabels(true);

            // customize legend
            Legend legend = barChart.getLegend();
            legend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
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

            // calorie remaining
            yData[2] = goal + yData[1] - yData[0];

            // get total for calculating the percentage
            float total =  yData[0] + yData[1] + yData[2];

            List<PieEntry> yEntrys = new ArrayList<>();

            for (int i = 0; i < yData.length; i++) {
                yEntrys.add(new PieEntry(yData[i]/total*100, pieLabel[i]));
            }

            // create the data set
            PieDataSet pieDataSet = new PieDataSet(yEntrys, "Report");
            pieDataSet.setColors(new int[]{ Color.CYAN, Color.GRAY, Color.RED });
            pieDataSet.setSliceSpace(2);
            pieDataSet.setValueTextSize(12);

            // create pie data object
            PieData pieData = new PieData(pieDataSet);

            pieData.setValueFormatter(new PercentFormatter());

            return pieData;
        }


        @Override
        protected void onPostExecute(PieData pieData) {
            pieChart.setData(pieData);
            pieChart.invalidate(); // refresh
        }
    }

}



class PercentFormatter implements IValueFormatter {

    public DecimalFormat mFormat;
    private PieChart pieChart;
    private boolean percentSignSeparated;

    public PercentFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0");
        percentSignSeparated = true;
    }

    // Can be used to remove percent signs if the chart isn't in percent mode
    public PercentFormatter(PieChart pieChart) {
        this();
        this.pieChart = pieChart;
    }

    // Can be used to remove percent signs if the chart isn't in percent mode
    public PercentFormatter(PieChart pieChart, boolean percentSignSeparated) {
        this(pieChart);
        this.percentSignSeparated = percentSignSeparated;
    }


    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format(value) + (percentSignSeparated ? " %" : "%");
    }
}
