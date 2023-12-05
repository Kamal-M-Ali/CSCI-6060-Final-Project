package edu.uga.cs.roommateshoppingapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.roommateshoppingapp.data.Purchase;

/**
 * This activity is for viewing purchases. It displays a list of roommates and how much they spent.
 * Tapping on a list item starts a purchase group activity where the user can edit the group of
 * purchased items
 */
public class PurchasesActivity extends LoggedInActivity implements EditPurchasedGroupDialog.DialogListener {
    public static final String DEBUG_TAG = "PurchaseActivity";
    public static final String PURCHASES_REF = "purchases";
    private PurchasesRecyclerAdapter recyclerAdapter;

    private List<Purchase> purchaseList;
    private FirebaseDatabase database;

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
        setContentView(R.layout.activity_purchases);

        // descendant activity, enable up button
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        // setting up recycler adapter
        purchaseList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recyclerView3);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new PurchasesRecyclerAdapter(purchaseList, PurchasesActivity.this);
        recyclerView.setAdapter(recyclerAdapter);

        // setting up database listener
        database = FirebaseDatabase.getInstance();
        DatabaseReference dbr = database.getReference(PURCHASES_REF);

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                purchaseList.clear();
                for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Purchase purchased = postSnapshot.getValue(Purchase.class);
                    assert purchased != null;
                    purchased.setKey(postSnapshot.getKey());
                    purchaseList.add(purchased);
                    Log.d(DEBUG_TAG, "ValueEventListener: added: " + purchased);
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

    /**
     * Implement update method for EditPurchasedGroupDialog, for modifying the dollar amount of a list
     * of purchased items.
     * @param position the position of the item
     * @param newAmount the new total for that group of purchased items
     */
    @Override
    public void updatePurchased(int position, double newAmount) {
        Purchase purchase = purchaseList.get(position);
        Log.d(DEBUG_TAG, "Update purchase list: " + purchase);
        purchase.setAmount(newAmount);

        DatabaseReference dbr = database.getReference(PURCHASES_REF).child(purchase.getKey());

        dbr.child("amount").setValue(purchase.getAmount())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Update item success
                        recyclerAdapter.notifyItemChanged(position);

                        Log.d(DEBUG_TAG, "setValue: success");
                        Toast.makeText(getApplicationContext(),
                                "Updated total: " + purchase.getAccountName(), Toast.LENGTH_SHORT).show();
                    } else {
                        // If update item fails, display a message to the user.
                        Log.w(DEBUG_TAG, "setValue: failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Update total failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}