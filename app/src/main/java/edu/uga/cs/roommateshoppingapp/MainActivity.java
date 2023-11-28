package edu.uga.cs.roommateshoppingapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.List;

/**
 * The splash activity for the app. Presents basic app info to the user and allows for account
 * registration/login.
 */
public class MainActivity extends AppCompatActivity {
    public static final String DEBUG_TAG = "MainActivity";
    private Button buttonLogin;
    private Button buttonRegister;

    /**
     * Called at the start of the activity's lifecycle. Does the main initializing of the view.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, R.string.app_name + ": MainActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.signOut(); // log out the old user
        }

        // ancestor activity, disable up button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setTitle(R.string.app_name);
        }

        // define views
        buttonLogin = findViewById(R.id.button);
        buttonRegister = findViewById(R.id.button2);

        // set up button event listeners
        buttonLogin.setOnClickListener(view -> {
            List<AuthUI.IdpConfig> providers = Collections.singletonList(
                    new AuthUI.IdpConfig.EmailBuilder().build()
            );

            Log.d(DEBUG_TAG, "MainActivity.SignInButtonClickListener: Signing in started");

            // Create an Intent to sign in to Firebase.
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    // this sets our own theme (color scheme, sizing, etc.) for the AuthUI's appearance
                    .setTheme(com.firebase.ui.auth.R.style.FirebaseUI)
                    .build();
            signInLauncher.launch(signInIntent);
        });

        buttonRegister.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "buttonRegister.onClick(): starting account register activity");
            startActivity(new Intent(view.getContext(), RegisterActivity.class));
        });
    }

    /**
     * The ActivityResultLauncher class provides a new way to invoke an activity for some result.
     * It is a replacement for the deprecated method startActivityForResult.
     *
     * The signInLauncher variable is a launcher to start the AuthUI's logging in process that
     * should return to the MainActivity when completed.  The overridden onActivityResult is then
     * called when the Firebase logging-in process is finished.
     */
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    /**
     * This method is called once the Firebase sign-in activity (launched above) returns (completes).
     * Then, the current (logged-in) Firebase user can be obtained. Subsequently, there is a
     * transition to the HomeActivity.
     *
     * @param result the result of the firebase authentication
     */
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            if(response != null) {
                Log.d(DEBUG_TAG, "MainActivity.onSignInResult: response.getEmail(): " + response.getEmail());
            }

            // after a successful sign in, start the home activity
            startActivity(new Intent(this.getApplicationContext(), HomeActivity.class));
        }
        else {
            Log.d(DEBUG_TAG, "MainActivity.onSignInResult: Failed to sign in");
            // Sign in failed. If response is null the user canceled the
            Toast.makeText(getApplicationContext(),
                    "Sign in failed",
                    Toast.LENGTH_SHORT).show();
        }
    }
}