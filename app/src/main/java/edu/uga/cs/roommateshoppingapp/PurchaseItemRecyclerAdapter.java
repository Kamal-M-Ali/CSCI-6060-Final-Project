package edu.uga.cs.roommateshoppingapp;

import static edu.uga.cs.roommateshoppingapp.ShoppingListActivity.SHOPPING_LIST_REF;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import edu.uga.cs.roommateshoppingapp.data.ShoppingItem;

public class PurchaseItemRecyclerAdapter extends RecyclerView.Adapter<PurchaseItemRecyclerAdapter.PurchaseItemHolder> {
    public static final String DEBUG_TAG = "CartRecyclerAdapter";
    private FirebaseDatabase database;
    private List<ShoppingItem> shoppingList;
    private Context context;
    String purchasesKey;

    /**
     * Constructor
     * @param shoppingList a list of purchased shopping items
     * @param context the context of the caller (used for editing dialog popup)
     * @param purchasesKey the database primary key of the purchase list this recycler adapter is representing
     */
    public PurchaseItemRecyclerAdapter(List<ShoppingItem> shoppingList, Context context, String purchasesKey) {
        this.shoppingList = shoppingList;
        this.context = context;
        this.purchasesKey = purchasesKey;
        this.database = FirebaseDatabase.getInstance();
    }

    // PurchaseItemHolder subclass
    static class PurchaseItemHolder extends RecyclerView.ViewHolder {
        TextView purchasedItemText;

        public PurchaseItemHolder(View itemView) {
            super(itemView);
            purchasedItemText = itemView.findViewById(R.id.purchasedItemText);
        }
    }

    /**
     * Called to create the view
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return a PurchaseItemHOlder object.
     */
    @NonNull
    @Override
    public PurchaseItemRecyclerAdapter.PurchaseItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_item, parent, false);
        return new PurchaseItemRecyclerAdapter.PurchaseItemHolder(view);
    }

    /**
     * Called after each view has been created or recycled.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(PurchaseItemRecyclerAdapter.PurchaseItemHolder holder, int position) {
        ShoppingItem shoppingItem = shoppingList.get(position);

        Log.d(DEBUG_TAG, "onBindViewHolder: " + shoppingItem);

        String key = shoppingItem.getKey();
        String itemName = shoppingItem.getItemName();

        // setting up view
        holder.purchasedItemText.setText(context.getString(R.string.item_prefix, itemName));
        holder.itemView.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "Remove item: " + shoppingItem);
            DatabaseReference dbr = database.getReference(PurchasesActivity.PURCHASES_REF);

            // remove the value
            dbr.child(purchasesKey).child("purchased").child(key).removeValue();
            Toast.makeText(context.getApplicationContext(),
                    "Removed from purchased: " + shoppingItem.getItemName(), Toast.LENGTH_SHORT).show();

            // move item back onto the shopping cart
            database.getReference(SHOPPING_LIST_REF).push().setValue(shoppingItem);
        });
    }

    /**
     * @return the number of items in the shopping list
     */
    @Override
    public int getItemCount() { return shoppingList.size(); }
}
