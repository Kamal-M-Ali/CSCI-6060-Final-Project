package edu.uga.cs.roommateshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A top level class for shared menu options.
 */
public class MenuActivity extends AppCompatActivity {
    public static final String DEBUG_TAG = "MenuActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        if (item.getItemId() == R.id.menu_logout) {
            Log.d(DEBUG_TAG, "menuItemSelected: logout");
            FirebaseAuth.getInstance().signOut();
        } else if (item.getItemId() == R.id.menu_settings) {
            Log.d(DEBUG_TAG, "menuItemSelected: settings");
            startActivity(new Intent(this.getApplicationContext(), SettingsActivity.class));
        } else return super.onOptionsItemSelected(item);

        return true;
    }
}
