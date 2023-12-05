package edu.uga.cs.roommateshoppingapp;

import android.os.Bundle;
import android.util.Log;

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
import java.util.Map;

import edu.uga.cs.roommateshoppingapp.data.Purchase;
import edu.uga.cs.roommateshoppingapp.data.ShoppingItem;

public class PurchaseGroupActivity extends LoggedInActivity {
    public static final String DEBUG_TAG = "PurchaseGroupActivity";
    private PurchaseItemRecyclerAdapter recyclerAdapter;

    private Purchase purchase;
    private List<ShoppingItem> purchaseList;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "PurchaseGroupActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_group);

        // descendant activity, enable the up button
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        String key = getIntent().getStringExtra("key");
        purchaseList = new ArrayList<>();

        // setting up recycler adapter
        RecyclerView recyclerView = findViewById(R.id.recyclerView4);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new PurchaseItemRecyclerAdapter(purchaseList, PurchaseGroupActivity.this, key);
        recyclerView.setAdapter(recyclerAdapter);

        // setting up database listener
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbr = database.getReference(PurchasesActivity.PURCHASES_REF).child(key);

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                purchaseList.clear();
                Purchase purchased = snapshot.getValue(Purchase.class);
                assert purchased != null;
                purchased.setKey(snapshot.getKey());

                if (purchased.getPurchased() != null) {
                    for (Map.Entry<String, Map<String, String>> pair : purchased.getPurchased().entrySet()) {
                        ShoppingItem shoppingItem = new ShoppingItem();
                        for (String value : pair.getValue().values()) {
                            shoppingItem.setItemName(value);
                        }
                        shoppingItem.setKey(pair.getKey());
                        purchaseList.add(shoppingItem);

                        Log.d(DEBUG_TAG, "ValueEventListener: added: " + shoppingItem);
                        Log.d(DEBUG_TAG, "ValueEventListener: key: " + shoppingItem.getKey());
                    }
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