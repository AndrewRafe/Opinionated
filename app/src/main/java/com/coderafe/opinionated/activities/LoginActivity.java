package com.coderafe.opinionated.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.coderafe.opinionated.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private FirebaseAuth mFireBaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize the firebase auth
        mFireBaseAuth = FirebaseAuth.getInstance();

        mEmailEditText = (EditText) findViewById(R.id.log_in_email_et);
        mPasswordEditText = (EditText) findViewById(R.id.log_in_password_et);
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
            mFireBaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        /**
                         * Runs on completion of the sign in authentication
                         * @param task
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
                                showAlert(getString(R.string.log_in_alert_title),
                                        task.getException().getMessage(),
                                        getString(R.string.log_in_alert_button_text));
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
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(okButtonText, null);
        AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }
}
