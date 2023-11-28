package edu.uga.cs.roommateshoppingapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uga.cs.roommateshoppingapp.data.ShoppingItem;

/**
 * RecyclerViewAdapter for displaying the list of shopping items.
 */
public class ShoppingListRecyclerAdapter extends RecyclerView.Adapter<ShoppingListRecyclerAdapter.ShoppingListHolder> {
    public static final String DEBUG_TAG = "ShoppingListRecyclerAdapter";

    private List<ShoppingItem> shoppingList;
    private Context context;

    /**
     * Constructor
     * @param shoppingList a list of ShoppingItem POJO objects
     * @param context the context of the caller (used for editing dialog popup)
     */
    public ShoppingListRecyclerAdapter(List<ShoppingItem> shoppingList, Context context) {
        this.shoppingList = shoppingList;
        this.context = context;
    }

    // ShoppingListHolder subclass
    static class ShoppingListHolder extends RecyclerView.ViewHolder {
        TextView shoppingItemText;

        public ShoppingListHolder(View itemView) {
            super(itemView);
            shoppingItemText = itemView.findViewById(R.id.shoppingItemText);
        }
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
    @Override
    public void onBindViewHolder(ShoppingListHolder holder, int position) {
        ShoppingItem shoppingItem = shoppingList.get(position);

        Log.d(DEBUG_TAG, "onBindViewHolder: " + shoppingItem);

        String key = shoppingItem.getKey();
        String itemName = shoppingItem.getItemName();

        holder.shoppingItemText.setText(itemName);
        holder.itemView.setOnClickListener(v -> {
            System.out.println("TEMP");
            //EditJobLeadDialogFragment editJobFragment = EditJobLeadDialogFragment.newInstance(holder.getAdapterPosition(), key, company);
            //editJobFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), null);
        });
    }

    /**
     * @return the number of items in the shopping list
     */
    @Override
    public int getItemCount() { return shoppingList.size(); }
}
