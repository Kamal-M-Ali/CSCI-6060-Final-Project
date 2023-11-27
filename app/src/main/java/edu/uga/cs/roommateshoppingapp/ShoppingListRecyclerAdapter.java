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

public class ShoppingListRecyclerAdapter extends RecyclerView.Adapter<ShoppingListRecyclerAdapter.ShoppingListHolder> {
    public static final String DEBUG_TAG = "ShoppingListRecyclerAdapter";

    private List<ShoppingItem> shoppingList;
    private Context context;

    public ShoppingListRecyclerAdapter(List<ShoppingItem> shoppingList, Context context) {
        this.shoppingList = shoppingList;
        this.context = context;
    }

    static class ShoppingListHolder extends RecyclerView.ViewHolder {
        TextView shoppingItemText;

        public ShoppingListHolder(View itemView) {
            super(itemView);
            shoppingItemText = itemView.findViewById(R.id.shoppingItemText);
        }
    }

    @NonNull
    @Override
    public ShoppingListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item, parent, false);
        return new ShoppingListHolder(view);
    }

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

    @Override
    public int getItemCount() { return shoppingList.size(); }
}
