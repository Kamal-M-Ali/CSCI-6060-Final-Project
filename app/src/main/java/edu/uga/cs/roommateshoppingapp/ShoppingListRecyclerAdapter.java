package edu.uga.cs.roommateshoppingapp;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static edu.uga.cs.roommateshoppingapp.ShoppingListActivity.SHOPPING_LIST_REF;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import edu.uga.cs.roommateshoppingapp.data.ShoppingItem;

/**
 * RecyclerViewAdapter for displaying the list of shopping items.
 */
public class ShoppingListRecyclerAdapter
        extends RecyclerView.Adapter<ShoppingListRecyclerAdapter.ShoppingListHolder>
        implements Filterable {
    public static final String DEBUG_TAG = "ShoppingListRecyclerAdapter";

    private List<ShoppingItem> shoppingList;
    private List<ShoppingItem> unfiltered;
    private Context context;
    private FirebaseDatabase database;

    private ShoppingListActivity shoppingListActivity;
    private CartActivity cart;



    /**
     * Constructor
     * @param shoppingList a list of ShoppingItem POJO objects
     * @param context the context of the caller (used for editing dialog popup)
     */
    public ShoppingListRecyclerAdapter(List<ShoppingItem> shoppingList, Context context) {
        this.shoppingList = shoppingList;
        this.unfiltered = new ArrayList<>(shoppingList);
        this.context = context;
        this.database = FirebaseDatabase.getInstance();
    }

    // ShoppingListHolder subclass
    static class ShoppingListHolder extends RecyclerView.ViewHolder {
        TextView shoppingItemText;
        Button purchase;

        public ShoppingListHolder(View itemView) {
            super(itemView);
            shoppingItemText = itemView.findViewById(R.id.shoppingItemText);
            purchase = itemView.findViewById(R.id.purchaseItem);
        }
    }

    /**
     * Should be called after every update to shoppingList to keep the state of unfiltered
     * consistent.
     * @param newShoppingList a list of the shopping items *after* the update
     */
    public void sync(List<ShoppingItem> newShoppingList)
    {
        unfiltered = new ArrayList<>(newShoppingList);

    }

    /**
     * Called to create the view
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return a ShoppingListHolder object.
     */
    @NonNull
    @Override
    public ShoppingListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item, parent, false);
        return new ShoppingListHolder(view);
    }

    /**
     * Called after each view has been created or recycled.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(ShoppingListHolder holder, int position) {
        ShoppingItem shoppingItem = shoppingList.get(position);

        Log.d(DEBUG_TAG, "onBindViewHolder: " + shoppingItem);

        String key = shoppingItem.getKey();
        String itemName = shoppingItem.getItemName();

        // setting up view
        holder.shoppingItemText.setText(context.getString(R.string.item_prefix, itemName));
        holder.purchase.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "Purchase item: " + shoppingItem);
            /** TODO: remove from shopping list and place it in current user's shopping cart
             *
             * can remove like in deleteItem()
             */
            // TODO:Items can be deleted from list and moved to cart but items in cart have null
            //identifier. Also, somehow users are being removed during one or both of those actions
            //however the app still remembers the users even though they no longer show up in the app
            //or the database
            database.getReference(CartActivity.ROOMMATE_CARTS_REF).child(shoppingItem.getKey()).
                    child("cart").push().setValue(shoppingItem);
            DatabaseReference item = database.getReference(SHOPPING_LIST_REF).child(shoppingItem.getKey());
            item.removeValue();
            notifyDataSetChanged();

        });
        holder.itemView.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "Edit item: " + shoppingItem);
            EditShoppingItemDialog editShoppingItemDialog = EditShoppingItemDialog.newInstance(holder.getAdapterPosition(), key, itemName);
            editShoppingItemDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), null);
        });
    }

    /**
     * @return the number of items in the shopping list
     */
    @Override
    public int getItemCount() { return shoppingList.size(); }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ShoppingItem> list = new ArrayList<>(unfiltered);
                FilterResults filterResults = new FilterResults();
                if(constraint == null || constraint.length() == 0) {
                    filterResults.count = list.size();
                    filterResults.values = list;
                } else {
                    List<ShoppingItem> resultsModel = new ArrayList<>();
                    String searchStr = constraint.toString().toLowerCase();

                    for(ShoppingItem shoppingItem : list ) {
                        // check if either the company name or the comments contain the search string
                        if(shoppingItem.getItemName().toLowerCase().contains(searchStr)) {
                            resultsModel.add(shoppingItem);
                        }
                    }

                    filterResults.count = resultsModel.size();
                    filterResults.values = resultsModel;
                }

                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                shoppingList = (ArrayList<ShoppingItem>) results.values;
                notifyDataSetChanged();
                if(shoppingList.size() == 0) {
                    Toast.makeText(context, "Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
    /**
     * This method syncs the recycler adapter with the new shopping list and refilters the contents
     * in case the user is mid-search while updating/deleting.
     */
    private void sync() {
        this.sync(shoppingList);
    }
}
