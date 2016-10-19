package com.coderafe.opinionated.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.coderafe.opinionated.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * An activity that allows a user to log in to the firebase database
 */
public class LoginActivity extends AppCompatActivity {

    private final String NULL_TAG = "NULL";

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mSubmitButton;
    private ProgressBar mProgressBar;

    private FirebaseAuth mFirebaseAuth;

    /**
     * Sets up the references to all the views in this activity
     * @param savedInstanceState Reference to the bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize the firebase auth
        mFirebaseAuth = FirebaseAuth.getInstance();

        mEmailEditText = (EditText) findViewById(R.id.log_in_email_et);
        mPasswordEditText = (EditText) findViewById(R.id.log_in_password_et);
        mSubmitButton = (Button) findViewById(R.id.log_in_submit_btn);
        mProgressBar = (ProgressBar) findViewById(R.id.log_in_progress_bar);
    }

    /**
     * An on click method that is called on clicking the log in submit button
     * It will authenticate to make sure the user exists on the database,
     * if not then it will ask them to try again or direct them to create a new
     * account. If it is correct then they will be sent to the home screen for that
     * particular user
     * @param v A reference to the log in view
     */
    public void authenticateUser(View v) {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        email = email.trim();
        password = password.trim();

        if (email.isEmpty() || password.isEmpty()) {
            //Display an alert
            showAlert(getString(R.string.log_in_alert_title),
                    getString(R.string.log_in_alert_message),
                    getString(R.string.log_in_alert_button_text));
        } else {
            mSubmitButton.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        /**
                         * Runs on completion of the sign in authentication
                         * @param task A reference to the task
                         */
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                //Clears the application stack
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                try {
                                    showAlert(getString(R.string.log_in_alert_title),
                                            task.getException().getMessage(),
                                            getString(R.string.log_in_alert_button_text));
                                    mSubmitButton.setVisibility(View.VISIBLE);
                                    mProgressBar.setVisibility(View.GONE);
                                } catch (NullPointerException e) {
                                    Log.d(NULL_TAG, "The exception message is null");
                                }

                            }
                        }
                    });
        }
    }

    /**
     * A helper method that creates and displays an alert dialog
     * @param title The title of the alert
     * @param message The message of the alert
     * @param okButtonText The text on the button of the alert
     */
    private void showAlert(String title, String message, String okButtonText) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(okButtonText, null);
        AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }
}
