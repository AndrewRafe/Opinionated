package com.coderafe.opinionated.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.VectorEnabledTintResources;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.coderafe.opinionated.R;
import com.coderafe.opinionated.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CreateUserActivity extends AppCompatActivity {

    public final String USER_TABLE = "users";
    public final String YEAR_OF_BIRTH = "birthYear";
    public final String POSTCODE = "postcode";
    public final String GENDER = "gender";
    public final String NUMBER_OF_QUESTIONS_ANSWERED = "questionsAnswered";

    //User information variables
    private EditText mUserEmail;
    private EditText mUserPassword;
    private EditText mUserYearOfBirth;
    private EditText mPostcode;
    private EditText mGender;
    private Button mSubmitButton;
    private ProgressBar mProgressBar;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        mUserEmail = (EditText) findViewById(R.id.create_user_email_et);
        mUserPassword = (EditText) findViewById(R.id.create_user_password_et);
        mUserYearOfBirth = (EditText) findViewById(R.id.create_user_year_of_birth_et);
        mPostcode = (EditText) findViewById(R.id.create_user_postcode_et);
        mGender = (EditText) findViewById(R.id.create_user_gender_et);
        mSubmitButton = (Button) findViewById(R.id.create_user_submit_btn);
        mProgressBar = (ProgressBar) findViewById(R.id.create_user_progress_bar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    /**
     * On click method that sends the user information to firebase
     * @param v A reference to the view
     */
    public void sendUserInformation(View v) {

        String email = mUserEmail.getText().toString().trim();
        String password = mUserPassword.getText().toString().trim();
        String yearOfBirth = mUserYearOfBirth.getText().toString().trim();
        String postcode = mUserPassword.getText().toString().trim();
        String gender = mGender.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || yearOfBirth.isEmpty() ||
                postcode.isEmpty() || gender.isEmpty()) {
            showAlert(getString(R.string.create_user_alert_title),
                    getString(R.string.create_user_alert_message),
                    getString(R.string.create_user_alert_button_text));
        } else {

            //Make the progress bar visible
            mSubmitButton.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(CreateUserActivity.this,
                            new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Add all the other information about the user to the database
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        String userId = user.getUid();
                        mDatabase.child(USER_TABLE).child(userId).child(YEAR_OF_BIRTH)
                                .setValue(mUserYearOfBirth.getText().toString());
                        mDatabase.child(USER_TABLE).child(userId).child(POSTCODE)
                                .setValue(mPostcode.getText().toString());
                        mDatabase.child(USER_TABLE).child(userId).child(GENDER)
                                .setValue(mGender.getText().toString());
                        mDatabase.child(USER_TABLE).child(userId).child(NUMBER_OF_QUESTIONS_ANSWERED)
                                .setValue(0);
                        //Launch the home activity while closing this activity
                        Intent intent = new Intent(CreateUserActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        showAlert(getString(R.string.create_user_alert_title),
                                task.getException().getMessage(),
                                getString(R.string.log_in_alert_button_text));
                        mSubmitButton.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            });
        }

    }

    /**
     * A helper method that creates and displays an alert dialog
     * @param title
     * @param message
     * @param okButtonText
     */
    private void showAlert(String title, String message, String okButtonText) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CreateUserActivity.this);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(okButtonText, null);
        AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }


}
