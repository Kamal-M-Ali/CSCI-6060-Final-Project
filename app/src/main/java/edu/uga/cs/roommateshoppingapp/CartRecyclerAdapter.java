package edu.uga.cs.roommateshoppingapp;

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

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.roommateshoppingapp.data.ShoppingItem;

public class CartRecyclerAdapter
        extends RecyclerView.Adapter<CartRecyclerAdapter.CartHolder>
        implements Filterable {
    public static final String DEBUG_TAG = "ShoppingListRecyclerAdapter";

    private List<ShoppingItem> shoppingList;
    private List<ShoppingItem> unfiltered;
    private Context context;

    /**
     * Constructor
     * @param shoppingList a list of ShoppingItem POJO objects
     * @param context the context of the caller (used for editing dialog popup)
     */
    public CartRecyclerAdapter(List<ShoppingItem> shoppingList, Context context) {
        this.shoppingList = shoppingList;
        this.unfiltered = new ArrayList<>(shoppingList);
        this.context = context;
    }

    // ShoppingListHolder subclass
    static class CartHolder extends RecyclerView.ViewHolder {
        TextView shoppingItemText;
        Button purchase;

        public CartHolder(View itemView) {
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
    public CartRecyclerAdapter.CartHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item, parent, false);
        return new CartRecyclerAdapter.CartHolder(view);
    }

    /**
     * Called after each view has been created or recycled.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(CartRecyclerAdapter.CartHolder holder, int position) {
        ShoppingItem shoppingItem = shoppingList.get(position);

        Log.d(DEBUG_TAG, "onBindViewHolder: " + shoppingItem);

        String key = shoppingItem.getKey();
        String itemName = shoppingItem.getItemName();

        // setting up view
        holder.shoppingItemText.setText(context.getString(R.string.item_prefix, itemName));
        holder.purchase.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "Purchase item: " + shoppingItem);
            /** TODO: remove from cart and place it in current user's purchased
             * can remove like in deleteItem()
             */
            // TODO:
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
}
