package com.example.calorietracker.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calorietracker.API.EdamamAPI;
import com.example.calorietracker.R;
import com.example.calorietracker.API.RestClient;
import com.example.calorietracker.utils.Hash;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        tvRegister = (TextView) findViewById(R.id.tv_signup);
        // hide the password
        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());



        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupIntent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(signupIntent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = etUsername.getText().toString().trim();
                String pwd = etPassword.getText().toString().trim();
                String hashpwd = Hash.md5(pwd);
                Log.i("Check", hashpwd);
                LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
                loginAsyncTask.execute(user, hashpwd);



            }
        });

    }


    private class LoginAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {


            String res = RestClient.checkUserNameAndPwdHash(params[0], params[1]);
            Log.i("Login",res);
            if (res.equals("true")) {

                SharedPreferences sp = getApplicationContext().getSharedPreferences("User", MODE_MULTI_PROCESS );
                SharedPreferences.Editor ed=sp.edit();
                try {
                    JSONObject resJson = RestClient.getUserInfo(params[0]);
                    Log.i("LoginActivity",resJson.getString("firstname"));
                    ed.putString("firstname", resJson.getString("firstname") );
                    ed.putString("surname", resJson.getString("surname") );
                    ed.putString("address", resJson.getString("address") );
                    ed.putString("email", resJson.getString("email") );
                    ed.putInt("userId", resJson.getInt("userId") );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ed.commit();
            }

            return res;
        }

        @Override
        protected void onPostExecute (String result) {
            if(result.equals("true")) {
                Intent HomePage = new Intent(LoginActivity.this,HomeActivity.class);

                startActivity(HomePage);
            }
            else {
                Toast.makeText(LoginActivity.this,"Login Error",Toast.LENGTH_SHORT).show();
            }
        }
    }


}
