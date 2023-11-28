package edu.uga.cs.roommateshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.roommateshoppingapp.data.Account;

/**
 * Settings class for allowing the user to delete their account and also review participating
 * roommates.
 */
public class SettingsActivity extends LoggedInActivity {
    public static final String DEBUG_TAG = "SettingsActivity";

    private RoommatesRecyclerAdapter recyclerAdapter;

    private List<Account> accountList;

    /**
     * Called at the start of the activity's lifecycle. Does the main initializing of the view.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "SettingsActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // descendant activity, enable up button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // setting up delete account event listener
        // TODO: fix this messes stuff when settling the cost
        Button delete = findViewById(R.id.deleteAccount);
        delete.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "delete.onClick()");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Account account = null;
                for (Account roommate : accountList) {
                    if (roommate.getAccountName().equals(user.getEmail())) {
                        account = roommate;
                        break;
                    }
                }
                if (account == null) {
                    Log.d(DEBUG_TAG, "Failed to find account with email '" + user.getEmail() + "'");
                    return;
                }

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference roommate = database.getReference(CartActivity.ROOMMATE_CARTS_REF).child(account.getKey());

                Log.d(DEBUG_TAG, "Moving roommate's cart to shopping list.");
                // TODO: add any items in roommates cart back to the shopping list

                Log.d(DEBUG_TAG, "Removing roommate's cart: " + account.getAccountName());
                roommate.removeValue();

                user.delete().addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(DEBUG_TAG, "Deleted firebase user");
                        Toast.makeText(getApplicationContext(), "Deleted account.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(DEBUG_TAG, "Failed to delete firebase user");
                        Toast.makeText(getApplicationContext(), "Delete account failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // show list of participating roommates
        accountList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.roommates);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new RoommatesRecyclerAdapter(accountList, SettingsActivity.this);
        recyclerView.setAdapter(recyclerAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbr = database.getReference(CartActivity.ROOMMATE_CARTS_REF);

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                accountList.clear();
                for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Account roommate = postSnapshot.getValue(Account.class);
                    assert roommate != null;
                    roommate.setKey(postSnapshot.getKey());
                    accountList.add(roommate);
                    Log.d(DEBUG_TAG, "ValueEventListener: added: " + roommate);
                    Log.d(DEBUG_TAG, "ValueEventListener: key: " + postSnapshot.getKey());
                }

                Log.d(DEBUG_TAG, "ValueEventListener: notifying recyclerAdapter");
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(DEBUG_TAG, "ValueEventListener: reading failed: " + databaseError.getMessage());
            }
        });
    }
}