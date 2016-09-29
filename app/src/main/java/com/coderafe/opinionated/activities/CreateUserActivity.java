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

    /**
     * TODO: Make it an ASYNC Task implementation
     * On click method for the create user view that will attempt to post the information
     * to the database
     * @param view A reference to the create user view
     */
    public void postData(View view) {

        String email = mUserEmail.getText().toString();
        String password = mUserPassword.getText().toString();
        String yearOfBirth = mUserYearOfBirth.getText().toString();
        String postcode = mPostcode.getText().toString();
        String gender = mGender.getText().toString();

        String data = "";

        try {
            data = URLEncoder.encode("email", "UTF-8") + "=" +
                    URLEncoder.encode(email, "UTF-8");
            data += URLEncoder.encode("password", "UTF-8") + "=" +
                    URLEncoder.encode(password, "UTF-8");
            data += URLEncoder.encode("yearOfBirth", "UTF-8") + "=" +
                    URLEncoder.encode(yearOfBirth, "UTF-8");
            data += URLEncoder.encode("postcode", "UTF-8") + "=" +
                    URLEncoder.encode(postcode, "UTF-8");
            data += URLEncoder.encode("gender", "UTF-8") + "=" +
                    URLEncoder.encode(gender, "UTF-8");
        } catch (UnsupportedEncodingException exception) {

        }



        try {

            URL url = new URL(URL_ADD_USER_STRING);

            //Send Post Data Request
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(data);
            wr.flush();

        } catch (MalformedURLException malformedURLException) {

        } catch (IOException ioException) {

        }
    }
}
