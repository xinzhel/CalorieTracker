package com.example.calorietracker.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calorietracker.API.Form.RegisterForm;
import com.example.calorietracker.R;
import com.example.calorietracker.API.RestClient;
import com.example.calorietracker.utils.Hash;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {
//    DatabaseHelper db;
    private EditText etFirstname;
    private EditText etSurname;
    private EditText etEmail;
    private EditText etHeight;
    private EditText etWeight;
    private EditText etAddress;
    private EditText etPostcode;
    private EditText etStepsPerMile;


    private EditText etUsername;
    private EditText etPassword;
    private  EditText etCnfPassword;

    private RadioGroup radioGenderGroup;
    private RadioButton radioGenderButton;
    private Spinner spin;
    private DatePicker dpDob;

    private  Button btnSignUp;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etFirstname = (EditText)findViewById(R.id.et_firstname);
        etSurname = (EditText)findViewById(R.id.et_surname);
        etEmail = (EditText)findViewById(R.id.et_email);
        etHeight = (EditText)findViewById(R.id.et_height);
        etWeight = (EditText)findViewById(R.id.et_weight);
        etAddress = (EditText)findViewById(R.id.et_address);
        etPostcode = (EditText)findViewById(R.id.et_postcode);
        etStepsPerMile = (EditText)findViewById(R.id.et_steps_per_mile);

        etUsername = (EditText)findViewById(R.id.et_username);
        etPassword = (EditText)findViewById(R.id.et_password);
        etCnfPassword = (EditText)findViewById(R.id.et_cnf_password);
        radioGenderGroup = (RadioGroup) findViewById(R.id.radioGender);
        spin = (Spinner) findViewById(R.id.spinner_level_of_activity);
        dpDob = (DatePicker) findViewById(R.id.dp_dob);
        btnSignUp = (Button)findViewById(R.id.button_register);
        tvLogin = (TextView)findViewById(R.id.tv_login);

        // hide the password
        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etCnfPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(LoginIntent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpAsyncTask signUpAsyncTask = new SignUpAsyncTask();
                signUpAsyncTask.execute();

            }
        });
    }

    private class SignUpAsyncTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject result = null;
            try {


            String firstname = etFirstname.getText().toString().trim();
            String surname = etSurname.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            int height = Integer.parseInt(etHeight.getText().toString().trim());
            int weight = Integer.parseInt(etWeight.getText().toString().trim());
            String address = etAddress.getText().toString().trim();
            String postcode = etPostcode.getText().toString().trim();
            int stepsPerMile = Integer.parseInt(etStepsPerMile.getText().toString().trim());



            String username = etUsername.getText().toString().trim();
            String pwd = etPassword.getText().toString().trim();
            String cnf_pwd = etCnfPassword.getText().toString().trim();

            // get gender
            int selectedId = radioGenderGroup.getCheckedRadioButtonId();
            radioGenderButton = (RadioButton) findViewById(selectedId);
            String gender = radioGenderButton.getText().toString();

            // get level of activity
            String stringLevelOfActivity = spin.getSelectedItem().toString();
            int levelOfActivity = Integer.parseInt(stringLevelOfActivity);

            // get date of birth
            String dob = dpDob.getYear() + "-" + (dpDob.getMonth() + 1) + "-" + dpDob.getDayOfMonth();


            if(pwd.equals(cnf_pwd)) {

                RegisterForm registerForm = new RegisterForm(firstname, surname, email, dob, height,
                        weight, gender.charAt(0), address, postcode, levelOfActivity, stepsPerMile,
                        username, Hash.md5(pwd));

                result = RestClient.register(registerForm);
                return result;
            }
            else{
                try {
                    Log.i("check", "pwd" + pwd);
                    Log.i("check", "pwd_cnf" + pwd);
                    result = new JSONObject().put("code", 400)
                            .put("message", "Password is not matching");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }

            } catch (Exception e) {
                try {
                    result = new JSONObject().put("code", 400)
                            .put("message", "Please check your input");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return result;
            }
        }

        @Override
        protected void onPostExecute (JSONObject result) {
            try {
                if(result.getInt("code") == 200) {
                    Intent HomePage = new Intent(SignupActivity.this,LoginActivity.class);
                    startActivity(HomePage);
                }
                else {
                    Toast.makeText(SignupActivity.this,result.getString("message"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



}
