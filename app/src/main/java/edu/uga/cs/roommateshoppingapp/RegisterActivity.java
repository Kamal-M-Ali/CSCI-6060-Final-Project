package edu.uga.cs.roommateshoppingapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.uga.cs.roommateshoppingapp.data.Account;

/**
 * The register activity for the app. Used to create a new account for a user.
 */
public class RegisterActivity extends AppCompatActivity {
    public static final String DEBUG_TAG = "RegisterActivity";

    private EditText editTextEmail;
    private EditText editTextPass1;
    private EditText editTextPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Log.d(DEBUG_TAG, "RegisterActivity.onCreate()");

        // descendant activity, enable up button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("Account Creation");
        }

        // defining views
        editTextEmail = findViewById(R.id.username);
        editTextPass1 = findViewById(R.id.password);
        editTextPass2 = findViewById(R.id.password2);
        Button buttonRegister = findViewById(R.id.register);

        // setting up button event listener
        buttonRegister.setOnClickListener(view -> {
            final String email = editTextEmail.getText().toString();
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(RegisterActivity.this, "Invalid email format",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            final String password = editTextPass1.getText().toString();
            if (password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Password can't be empty.",
                        Toast.LENGTH_SHORT).show();
                return;
            } else if (!password.equals(editTextPass2.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "Passwords don't match.",
                        Toast.LENGTH_SHORT).show();
                return;
            } else if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Registered user: " + email, Toast.LENGTH_SHORT).show();

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(DEBUG_TAG, "createUserWithEmail: success");
                            startActivity(new Intent(this.getApplicationContext(), HomeActivity.class));
                            addRoommateCart(firebaseAuth.getCurrentUser());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(DEBUG_TAG, "createUserWithEmail: failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Registration failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    /**
     * A helper method to create a new cart for a registered user.
     * @param user the user whose account has just been created
     */
    private void addRoommateCart(FirebaseUser user) {
        if (user == null) return;
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference(CartActivity.ROOMMATE_CARTS_REF);
        Account roommate = new Account(user.getEmail());

        dbr.push().setValue(roommate)
                .addOnSuccessListener(aVoid -> {
                    Log.d(DEBUG_TAG, "Roommate cart created: " + roommate);
                })
                .addOnFailureListener(e -> Toast.makeText( getApplicationContext(), "Failed to create a cart for " + roommate.getAccountName(),
                        Toast.LENGTH_SHORT).show());
    }
}