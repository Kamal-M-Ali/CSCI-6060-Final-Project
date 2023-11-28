package edu.uga.cs.roommateshoppingapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;

public class CartActivity extends LoggedInActivity {
    public static final String DEBUG_TAG = "CartActivity";
    public static final String ROOMMATE_CARTS_REF = "carts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Log.d(DEBUG_TAG, "CartActivity.onCreate()");

        // descendant activity, enable up button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }
}