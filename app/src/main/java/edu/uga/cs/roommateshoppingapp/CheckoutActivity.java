package edu.uga.cs.roommateshoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import edu.uga.cs.roommateshoppingapp.data.Account;
import edu.uga.cs.roommateshoppingapp.data.Purchase;

public class CheckoutActivity extends LoggedInActivity {
    public static final String DEBUG_TAG = "CheckoutActivity";
    private EditText price;

    /**
     * Called at the start of the activity's lifecycle. Does the main initializing of the view.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "CheckoutActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // descendant activity, enable up button
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        // setting up views
        Button purchase = findViewById(R.id.purchase);
        price = findViewById(R.id.price);

        // button listener
        purchase.setOnClickListener(view -> onPurchase());
    }

    /**
     * Called when purchasing a group of items.
     */
    private void onPurchase() {
        Log.d(DEBUG_TAG, "purchase.onClick()");
        double total;
        // need to use try-catch to guard against possible non-numbers entered
        // and given as arguments to Double.parseDouble
        try {
            total = Double.parseDouble(price.getText().toString());
        } catch( NumberFormatException nfe ) {
            // This check is just a precaution, since the user will be able to enter only numbers
            // into the EditText, as currently included in the layout (note the
            // android:inputType="numberDecimal" attribute).
            // However, we should have this check in case someone changes
            // the layout and uses more general EditTexts, accepting any chars as input.

            // Toast is a short message displayed to the user
            Toast toast = Toast.makeText( getApplicationContext(),
                    "Enter positive decimal values",
                    Toast.LENGTH_SHORT );
            toast.show();
            price.setText("");
            return;
        }

        // Check if value entered is positive
        if (total < 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Enter only positive decimal values",
                    Toast.LENGTH_SHORT);
            toast.show();
            price.setText("");
            return;
        }

        // first we need to get the user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null ) {
            // next find the cart of the user (it is automatically created on creation
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dbr = database.getReference(CartActivity.ROOMMATE_CARTS_REF);
            Query query = dbr.orderByChild("accountName").equalTo(user.getEmail());

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // we have a list of values containing the users that match the query (should be 1)
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot roommate : dataSnapshot.getChildren()) {
                            // move the account to the roommates cart
                            Account account = roommate.getValue(Account.class);
                            Purchase purchased = new Purchase(account.getAccountName(), account.getCart(), total);

                            Log.d(DEBUG_TAG, "Purchasing: " + purchased);

                            if (account.getCart() == null) {
                                Log.d(DEBUG_TAG, "Nothing to purchase.");
                                Toast.makeText(getApplicationContext(), "No items to purchase.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // now check if the user has an existing purchase list
                                DatabaseReference purchases = database.getReference(PurchasesActivity.PURCHASES_REF);
                                Query query2 = purchases.orderByChild("accountName").equalTo(user.getEmail());

                                query2.get().addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        DataSnapshot dataSnapshot2 = task2.getResult();
                                        if (dataSnapshot2.exists()) {
                                            Log.d(DEBUG_TAG, "Got purchases.");
                                            Purchase existing = null;
                                            String existingKey = null;
                                            for (DataSnapshot purchaseList : dataSnapshot2.getChildren()) {
                                                existing = purchaseList.getValue(Purchase.class);
                                                existingKey = purchaseList.getKey();
                                                break; // only look at the first roommate
                                            }

                                            if (existing == null || existing.getPurchased() == null || existing.getPurchased().isEmpty()) {
                                                Log.d(DEBUG_TAG, "New purchase list.");
                                                database.getReference(PurchasesActivity.PURCHASES_REF)
                                                        .push().setValue(purchased);
                                            } else {
                                                Log.d(DEBUG_TAG, "Existing purchase list.");
                                                existing.setAmount(existing.getAmount() + total);
                                                existing.getPurchased().putAll(account.getCart());
                                                database.getReference(PurchasesActivity.PURCHASES_REF)
                                                        .child(existingKey).setValue(existing);
                                            }
                                        } else {
                                            Log.d(DEBUG_TAG, "New purchase list.");
                                            database.getReference(PurchasesActivity.PURCHASES_REF)
                                                    .push().setValue(purchased);
                                        }

                                        // remove the cart from carts
                                        database.getReference(CartActivity.ROOMMATE_CARTS_REF)
                                                .child(roommate.getKey())
                                                .child("cart").removeValue();

                                        // inform the user
                                        Toast.makeText(getApplicationContext(), "Successfully purchased items.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.w(DEBUG_TAG, "Query2 failed: " + task.getException());
                                    }
                                });
                            }

                            // go back to the home page activity
                            startActivity(new Intent(this.getApplicationContext(), HomeActivity.class));

                            break; // only look at the first roommate
                        }
                    } else {
                        Log.e(DEBUG_TAG, "Failed to find user.");
                    }
                } else {
                    Log.w(DEBUG_TAG, "purchase cart: failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Purchase failed.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d(DEBUG_TAG, "No user found.");
        }
    }
}