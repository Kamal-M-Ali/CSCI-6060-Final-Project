package edu.uga.cs.roommateshoppingapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

/**
 * Settings class for allowing the user to delete their account and also review participating
 * roommates.
 */
public class SettingsActivity extends LoggedInActivity {
    public static final String DEBUG_TAG = "SettingsActivity";

    /**
     * Called at the start of the activity's lifecycle. Does the main initializing of the view.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.d(DEBUG_TAG, "SettingsActivity.onCreate()");

        // descendant activity, enable up button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("Settings");
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }
}