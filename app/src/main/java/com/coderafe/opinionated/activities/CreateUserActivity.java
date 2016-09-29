package com.coderafe.opinionated.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.coderafe.opinionated.R;

public class CreateUserActivity extends AppCompatActivity {

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

    /**
     * On click method for the create user view that will attempt to post the information
     * to the database
     * @param view A reference to the create user view
     */
    public void postData(View view) {

    }
}
