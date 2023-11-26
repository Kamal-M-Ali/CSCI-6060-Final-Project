package edu.uga.cs.roommateshoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;

import com.google.firebase.auth.FirebaseAuth;

/**
 * This class should be extended for all activities that involve logged-in users.
 */
public class LoggedInActivity extends MenuActivity {
    public static final String DEBUG_TAG = "LoggedInActivity";

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(DEBUG_TAG, "LoggedInActivity.onCreate()");

        // ancestor activity, disable up button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setTitle(R.string.app_name);
        }

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                Log.d(DEBUG_TAG, "Logged out. Starting MainActivity.");
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
            Log.d(DEBUG_TAG, ".onStart(): added auth state listener");
        }

    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
            Log.d(DEBUG_TAG, ".onStop(): removed auth state listener");
        }


        if (isFinishing()) {
            FirebaseAuth.getInstance().signOut();
            Log.d(DEBUG_TAG, ".onStop(): logged out user");
        }
    }
}