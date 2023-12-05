package edu.uga.cs.roommateshoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

/**
 * The main class for signed-in users. Used to navigate to most screens.
 */
public class HomeActivity extends LoggedInActivity {
    public static final String DEBUG_TAG = "HomeActivity";

    /**
     * Called at the start of the activity's lifecycle. Does the main initializing of the view.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "HomeActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // defining views
        Button shoppingList = findViewById(R.id.shoppingList);
        Button cart = findViewById(R.id.cart);
        Button viewPurchased = findViewById(R.id.viewPurchased);
        Button settle = findViewById(R.id.settle);

        // setting up button listeners
        shoppingList.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "shoppingList.onClick(): starting view shopping list activity");
            startActivity(new Intent(this.getApplicationContext(), ShoppingListActivity.class));
        });

        cart.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "cart.onClick(): starting view cart activity");
            startActivity(new Intent(this.getApplicationContext(), CartActivity.class));
        });

        viewPurchased.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "viewPurchased.onClick(): starting view purchases activity");
            // TODO: startActivity(...)
        });

        settle.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "settle.onClick(): starting settle the cost activity");
            // TODO: startActivty(...)
            startActivity(new Intent(this.getApplicationContext(), SettleCostActivity.class));
        });
    }
}