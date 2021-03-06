package com.coderafe.opinionated.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.coderafe.opinionated.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The main activity on start-up. Will allow the user to either sign in or
 * create a new user
 */
public class TitleActivity extends AppCompatActivity {

    //Firebase Variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    /**
     * Sets up firebase database authority
     * @param savedInstanceState Reference to the bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    /**
     * An on click method for the create user button that launches the create user activity
     * @param view A reference to the title activity view
     */
    public void createUser(View view) {
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }

    /**
     * On click method for the log in button that launches the log in activity
     * @param view A reference to the title view
     */
    public void logIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
