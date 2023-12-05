package edu.uga.cs.roommateshoppingapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import edu.uga.cs.roommateshoppingapp.data.Purchase;

public class PurchasesRecyclerAdapter extends RecyclerView.Adapter<PurchasesRecyclerAdapter.PurchaseListHolder> {
    public static final String DEBUG_TAG = "RoommatesRecyclerAdapter";

    private List<Purchase> purchaseList;
    private Context context;

    /**
     * Constructor
     * @param purchaseList a list of roommate purchase group POJO objects
     * @param context the context of the caller (used for editing dialog popup)
     */
    public PurchasesRecyclerAdapter(List<Purchase> purchaseList, Context context) {
        this.purchaseList = purchaseList;
        this.context = context;
    }

    // ShoppingListHolder subclass
    static class PurchaseListHolder extends RecyclerView.ViewHolder {
        TextView roommateText;
        Button updatePrice;

        public PurchaseListHolder(View itemView) {
            super(itemView);
            roommateText = itemView.findViewById(R.id.roommateName);
            updatePrice = itemView.findViewById(R.id.updatePrice);
        }
    }

    /**
     * Called to create the view
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return a PurchaseListHolder object.
     */
    @NonNull
    @Override
    public PurchasesRecyclerAdapter.PurchaseListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_list, parent, false);
        return new PurchasesRecyclerAdapter.PurchaseListHolder(view);
    }

    /**
     * Called after each view has been created or recycled.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(PurchasesRecyclerAdapter.PurchaseListHolder holder, int position) {
        Purchase purchased = purchaseList.get(position);

        Log.d(DEBUG_TAG, "onBindViewHolder: " + purchased);

        String key = purchased.getKey();
        String accountName = purchased.getAccountName();
        double amount = purchased.getAmount();

        DecimalFormat df = new DecimalFormat("####0.00");

        holder.roommateText.setText(accountName + ": $" + df.format(purchased.getAmount()));
        holder.itemView.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "View purchase group onClick(): " + purchased);

            Intent intent = new Intent(context.getApplicationContext(), PurchaseGroupActivity.class);
            intent.putExtra("key", key);
            context.startActivity(intent);
        });
        holder.updatePrice.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "Update purchased: " + purchased);
            EditPurchasedGroupDialog editPurchasedGroupDialog = EditPurchasedGroupDialog.newInstance(holder.getAdapterPosition(), key, amount);
            editPurchasedGroupDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), null);
        });
    }

    /**
     * @return the number of items in the shopping list
     */
    @Override
    public int getItemCount() { return purchaseList.size(); }
}
