package com.coderafe.opinionated.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.coderafe.opinionated.R;

/**
 * Splash screen activity
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * Sets up predifined splash screen layout and instantly starts title activity
     * @param savedInstanceState Reference to the bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent intent = new Intent(SplashActivity.this, TitleActivity.class);
        startActivity(intent);
    }
}
