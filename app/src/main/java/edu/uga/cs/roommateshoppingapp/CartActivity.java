package edu.uga.cs.roommateshoppingapp;

import static edu.uga.cs.roommateshoppingapp.ShoppingListActivity.SHOPPING_LIST_REF;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import edu.uga.cs.roommateshoppingapp.data.ShoppingItem;

public class CartActivity extends LoggedInActivity {
    public static final String DEBUG_TAG = "CartActivity";
    public static final String ROOMMATE_CARTS_REF = "carts";
    private RecyclerView recyclerView;
    private CartRecyclerAdapter recyclerAdapter;
    private List<ShoppingItem> myCart;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "CartActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // descendant activity, enable up button
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        // show items
        myCart = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView2);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new CartRecyclerAdapter(myCart, CartActivity.this);
        recyclerView.setAdapter(recyclerAdapter);
        database = FirebaseDatabase.getInstance();

        setupMyCart();

        // set up checkout event
        Button checkout = findViewById(R.id.checkout);
        checkout.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "Starting checkout process");
            startActivity(new Intent(this.getApplicationContext(), CheckoutActivity.class));
        });
    }

    /**
     * Helper method for initializing the user's cart.
     */
    private void setupMyCart() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null ) {
            // next find the cart of the user (it is automatically created on creation
            DatabaseReference dbr = database.getReference(CartActivity.ROOMMATE_CARTS_REF);
            Query query = dbr.orderByChild("accountName").equalTo(user.getEmail());

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

                            // initialize the RecyclerView
                            cart.addListenerForSingleValueEvent(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    myCart.clear();
                                    for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                                        ShoppingItem shoppingItem = postSnapshot.getValue(ShoppingItem.class);
                                        assert shoppingItem != null;
                                        shoppingItem.setKey(postSnapshot.getKey());
                                        myCart.add(shoppingItem);
                                        Log.d(DEBUG_TAG, "ValueEventListener: added: " + shoppingItem);
                                        Log.d(DEBUG_TAG, "ValueEventListener: key: "     + postSnapshot.getKey());
                                    }

                                    Log.d(DEBUG_TAG, "ValueEventListener: notifying recyclerAdapter");
                                    recyclerAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d(DEBUG_TAG, "ValueEventListener: reading failed: " + databaseError.getMessage());
                                }
                            });

                            break; // only look at the first roommate
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