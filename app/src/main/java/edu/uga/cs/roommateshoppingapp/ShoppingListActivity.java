package edu.uga.cs.roommateshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.roommateshoppingapp.data.ShoppingItem;


public class ShoppingListActivity extends LoggedInActivity implements AddShoppingItemDialog.DialogListener {
    public static final String DEBUG_TAG = "ShoppingListActivity";
    public static final String SHOPPING_LIST_REF = "shopping_list";

    private RecyclerView recyclerView;
    private ShoppingListRecyclerAdapter recyclerAdapter;

    private List<ShoppingItem> shoppingList;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        // descendant activity, enable up button
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);


        FloatingActionButton floatingButton = findViewById(R.id.floatingActionButton);
        floatingButton.setOnClickListener(v -> {
            DialogFragment newFragment = new AddShoppingItemDialog();
            newFragment.show(getSupportFragmentManager(), null);
        });

        shoppingList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new ShoppingListRecyclerAdapter(shoppingList, ShoppingListActivity.this);
        recyclerView.setAdapter(recyclerAdapter);

        database = FirebaseDatabase.getInstance();
        DatabaseReference dbr = database.getReference(SHOPPING_LIST_REF);

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shoppingList.clear();
                for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                    ShoppingItem shoppingItem = postSnapshot.getValue(ShoppingItem.class);
                    shoppingItem.setKey(postSnapshot.getKey());
                    shoppingList.add(shoppingItem);
                    Log.d(DEBUG_TAG, "ValueEventListener: added: " + shoppingItem);
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
     * Callback for AddShoppingItemDialog, will add a new shopping item to the shopping list.
     * @param shoppingItem shopping item to add
     */
    public void onDialogPositiveClick(ShoppingItem shoppingItem) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbr = database.getReference(SHOPPING_LIST_REF);

        dbr.push().setValue(shoppingItem)
                .addOnSuccessListener(aVoid -> {
                    // Reposition the RecyclerView to show the ShoppingItem most recently added (as the last item on the list).
                    // Use of the post method is needed to wait until the RecyclerView is rendered, and only then
                    // reposition the item into view (show the last item on the list).
                    // the post method adds the argument (Runnable) to the message queue to be executed
                    // by Android on the main UI thread. It will be done *after* the setAdapter call
                    // updates the list items, so the repositioning to the last item will take place
                    // on the complete list of items.
                    recyclerView.post(() -> recyclerView.smoothScrollToPosition(shoppingList.size() - 1));

                    Log.d(DEBUG_TAG, "Shopping item saved: " + shoppingItem);
                    // Show a quick confirmation
                    Toast.makeText(getApplicationContext(), "Shopping item created for " + shoppingItem.getItemName(),
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText( getApplicationContext(), "Failed to create a shopping item for " + shoppingItem.getItemName(),
                        Toast.LENGTH_SHORT).show());
    }
}