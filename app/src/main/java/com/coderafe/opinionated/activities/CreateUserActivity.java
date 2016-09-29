package com.coderafe.opinionated.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.coderafe.opinionated.R;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class CreateUserActivity extends AppCompatActivity {

    public static final String URL_ADD_USER_STRING = "http://www.coderafe.net/add_user.php";

    private EditText mUserEmail;
    private EditText mUserPassword;
    private EditText mUserYearOfBirth;
    private EditText mPostcode;
    private EditText mGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        mUserEmail = (EditText) findViewById(R.id.create_user_email_et);
        mUserPassword = (EditText) findViewById(R.id.create_user_password_et);
        mUserYearOfBirth = (EditText) findViewById(R.id.create_user_year_of_birth_et);
        mPostcode = (EditText) findViewById(R.id.create_user_postcode_et);
        mGender = (EditText) findViewById(R.id.create_user_gender_et);
    }


}
