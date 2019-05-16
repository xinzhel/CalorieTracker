package com.example.calorietracker.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;



public class HttpHelper {
//    private static final String BASE_URL = "http://118.138.66.19:48649/CalorieTracker/webresources/";

    public static String downloadUrl(String address) {
//        final String methodPath = "restws.report/";

        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";

        try {
            url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());

            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textResult;

    }

    public static String postUrl(String url, String params) {

        URL postUrl = null;
        HttpURLConnection postConn = null;
        String textResult = "";

        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        try {


            postUrl = new URL(url);
            postConn = (HttpURLConnection) postUrl.openConnection();
            postConn.setConnectTimeout(15000);

            postConn.setRequestMethod("POST");
            postConn.setDoOutput(true);
            postConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            postConn.setRequestProperty("Accept", "application/json");



            final DataOutputStream dataOutputStream = new  DataOutputStream(postConn.getOutputStream());
            dataOutputStream.writeBytes(params);
            dataOutputStream.close();

            Scanner inStream = new Scanner(postConn.getInputStream());

            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            postConn.disconnect();
        }
        Log.i("HttpHelper", textResult);
        return  textResult;

}

}
