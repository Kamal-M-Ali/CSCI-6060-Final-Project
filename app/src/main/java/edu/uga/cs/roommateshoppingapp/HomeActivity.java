package edu.uga.cs.roommateshoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;


/**
 * The main class for signed-in users. The app navigation class.
 */
public class HomeActivity extends LoggedInActivity {
    public static final String DEBUG_TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(DEBUG_TAG, "HomeActivity.onCreate()");

        // defining views
        Button shoppingList = findViewById(R.id.shoppingList);
        Button cart = findViewById(R.id.cart);
        Button settle = findViewById(R.id.settle);

        // setting up button listeners
        shoppingList.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "cart.onClick(): starting view shopping list activity");
            startActivity(new Intent(this.getApplicationContext(), ShoppingListActivity.class));
        });

        cart.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "cart.onClick(): starting view cart activity");
            // TODO: startActivty(...)
        });

        settle.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "cart.onClick(): starting settle the cost activity");
            // TODO: startActivty(...)
        });
    }
}