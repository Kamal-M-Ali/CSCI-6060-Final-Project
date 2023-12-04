package edu.uga.cs.roommateshoppingapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.roommateshoppingapp.data.ShoppingItem;

public class CartActivity extends LoggedInActivity
        implements AddShoppingItemDialog.DialogListener, EditShoppingItemDialog.DialogListener {
    public static final String DEBUG_TAG = "CartActivity";
    public static final String ROOMMATE_CARTS_REF = "cart";
    private SearchView searchView;
    private RecyclerView recyclerView;
    private CartRecyclerAdapter recyclerAdapter;
    private List<ShoppingItem> shoppingList;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Log.d(DEBUG_TAG, "CartActivity.onCreate()");

        // descendant activity, enable up button
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        // setting up floating action bar for adding a new shopping item
        FloatingActionButton floatingButton = findViewById(R.id.floatingActionButton);
        floatingButton.setOnClickListener(v -> {
            DialogFragment newFragment = new AddShoppingItemDialog();
            //DialogFragment newFragment = new EditShoppingItemDialog();
            newFragment.show(getSupportFragmentManager(), null);
        });
        // show items
        shoppingList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new CartRecyclerAdapter(shoppingList, CartActivity.this);
        recyclerView.setAdapter(recyclerAdapter);
        database = FirebaseDatabase.getInstance();
        DatabaseReference dbr = database.getReference(ROOMMATE_CARTS_REF);

        // initialize the RecyclerView
        dbr.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shoppingList.clear();
                for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                    ShoppingItem shoppingItem = postSnapshot.getValue(ShoppingItem.class);
                    assert shoppingItem != null;
                    shoppingItem.setKey(postSnapshot.getKey());
                    shoppingList.add(shoppingItem);
                    Log.d(DEBUG_TAG, "ValueEventListener: added: " + shoppingItem);
                    Log.d(DEBUG_TAG, "ValueEventListener: key: " + postSnapshot.getKey());
                }

                Log.d(DEBUG_TAG, "ValueEventListener: notifying recyclerAdapter");
                recyclerAdapter.notifyDataSetChanged();
                sync();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(DEBUG_TAG, "ValueEventListener: reading failed: " + databaseError.getMessage());
            }
        });

        // setting up filtering
        searchView = findViewById(R.id.search);
        searchView.setQueryHint(getString(R.string.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(DEBUG_TAG, "Query submitted");
                return false;
            }

            // This method will implement an incremental search for the search words
            // It is called every time there is a change in the text in the search box.
            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    /**
     * Implement method for AddShoppingItemDialog, will add a new shopping item to the cart.
     * @param shoppingItem shopping item to add
     */
    public void addItem(ShoppingItem shoppingItem) {
        Log.d(DEBUG_TAG, "Add item: " + shoppingItem);
        DatabaseReference dbr = database.getReference(ROOMMATE_CARTS_REF).push();
        dbr.setValue(shoppingItem)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Add item success
                        shoppingItem.setKey(dbr.getKey());
                        shoppingList.add(shoppingItem);
                        sync();
                        recyclerAdapter.notifyItemInserted(shoppingList.size());
                        recyclerView.post(() -> recyclerView.smoothScrollToPosition(shoppingList.size() - 1));

                        Log.d(DEBUG_TAG, "setValue: success");
                        // Show a quick confirmation
                        Toast.makeText(getApplicationContext(), "Shopping item created for " + shoppingItem.getItemName(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // If add item fails, display a message to the user.
                        Log.w(DEBUG_TAG, "setValue: failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Update item failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Implement update method for EditShoppingItemDialog, will update a shopping item in the shopping list.
     * @param item shopping item to update
     */
    @Override
    public void updateItem(int position, ShoppingItem item) {
        Log.d(DEBUG_TAG, "Update item: " + item);
        DatabaseReference dbr = database.getReference(ROOMMATE_CARTS_REF).child(item.getKey());
        dbr.child("itemName").setValue(item.getItemName())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Update item success
                        shoppingList.set(position, item);
                        sync();
                        recyclerAdapter.notifyItemChanged(position);

                        Log.d(DEBUG_TAG, "setValue: success");
                        Toast.makeText(getApplicationContext(),
                                "Updated item: " + item.getItemName(), Toast.LENGTH_SHORT).show();
                    } else {
                        // If update item fails, display a message to the user.
                        Log.w(DEBUG_TAG, "setValue: failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Update item failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     *
     * Implement delete method for EditShoppingItemDialog, will delete a shopping item in the shopping list.
     * @param item shopping item to delete
     */
    @Override
    public void deleteItem(int position, ShoppingItem item) {
        Log.d(DEBUG_TAG, "Delete item: " + item);

        DatabaseReference dbr = database.getReference(ROOMMATE_CARTS_REF).child(item.getKey());

        dbr.removeValue()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Delete item success
                        shoppingList.remove(position);
                        sync();
                        recyclerAdapter.notifyItemRemoved(position);

                        Log.d(DEBUG_TAG, "removeValue: success");
                        Toast.makeText(getApplicationContext(),
                                "Deleted item: " + item.getItemName(), Toast.LENGTH_SHORT).show();
                    } else {
                        // If delete item fails, display a message to the user.
                        Log.w(DEBUG_TAG, "removeValue: failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Delete item failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * This method syncs the recycler adapter with the new shopping list and refilters the contents
     * in case the user is mid-search while updating/deleting.
     */
    private void sync() {
        recyclerAdapter.sync(shoppingList);
        recyclerAdapter.getFilter().filter(searchView.getQuery());
    }
}