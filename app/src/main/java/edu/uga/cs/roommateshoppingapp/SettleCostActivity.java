package edu.uga.cs.roommateshoppingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import edu.uga.cs.roommateshoppingapp.data.ShoppingItem;

public class SettleCostActivity extends LoggedInActivity {
    public static final String DEBUG_TAG = "SettleCostActivity";
    public static final String ROOMMATE_SETTLE_REF = "carts";
    private RecyclerView recyclerView;
    private CartRecyclerAdapter recyclerAdapter;
    private List<ShoppingItem> shoppingList;
    private FirebaseDatabase database;
    private List<Account> roommateList;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "SettleCostActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_cost);

        // descendant activity, enable up button
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        // show items
        shoppingList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView2);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new CartRecyclerAdapter(shoppingList, SettleCostActivity.this);
        recyclerView.setAdapter(recyclerAdapter);
        database = FirebaseDatabase.getInstance();

        setupSettleCost();

        // set up settle cost event
        //todo: add implementation for settling the cost
        Button checkout = findViewById(R.id.checkout);
        checkout.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "Starting settle cost process");
            startActivity(new Intent(this.getApplicationContext(), CheckoutActivity.class));
        });
    }

    /**
     * Helper method for initializing the purchases
     */
    private void setupSettleCost() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null ) {
            // next find the cart of the user (it is automatically created on creation0
            DatabaseReference dbr = database.getReference(CartActivity.ROOMMATE_CARTS_REF);
            //Query query = dbr.orderByChild("accountName").equalTo(user.getEmail());
            Query query = dbr.orderByChild("accountName");

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // we have a list of values containing the users that match the query (should be 1)
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists() && dataSnapshot.getKey() != null) {
                        for (DataSnapshot roommate : dataSnapshot.getChildren()) {
                            // remove the item from the roommate's cart
                            DatabaseReference cart = database.getReference(CartActivity.ROOMMATE_CARTS_REF)
                                    .child(roommate.getKey())
                                    .child("cart");
                            Log.d("etc", roommate.toString());
                        }
                    } else {
                        Log.e(DEBUG_TAG, "Failed to find user.");
                    }
                } else {
                    Log.w(DEBUG_TAG, "failed to find user", task.getException());
                }
            });
        } else {
            Log.d(DEBUG_TAG, "No user found.");
        }
    }
}
