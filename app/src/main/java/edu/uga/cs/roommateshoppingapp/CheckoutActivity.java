package edu.uga.cs.roommateshoppingapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;

public class CheckoutActivity extends LoggedInActivity {
    public static final String DEBUG_TAG = "CheckoutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "CheckoutActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // descendant activity, enable up button
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);


    }
}