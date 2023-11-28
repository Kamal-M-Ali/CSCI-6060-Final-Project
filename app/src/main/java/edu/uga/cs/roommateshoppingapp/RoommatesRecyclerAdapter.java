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

import edu.uga.cs.roommateshoppingapp.data.Account;

public class RoommatesRecyclerAdapter extends RecyclerView.Adapter<RoommatesRecyclerAdapter.RoommateListHolder> {
    public static final String DEBUG_TAG = "RoommatesRecyclerAdapter";

    private List<Account> roommateList;
    private Context context;

    /**
     * Constructor
     * @param roommateList a list of roommate account POJO objects
     * @param context the context of the caller (used for editing dialog popup)
     */
    public RoommatesRecyclerAdapter(List<Account> roommateList, Context context) {
        this.roommateList = roommateList;
        this.context = context;
    }

    // ShoppingListHolder subclass
    static class RoommateListHolder extends RecyclerView.ViewHolder {
        TextView roommateText;

        public RoommateListHolder(View itemView) {
            super(itemView);
            roommateText = itemView.findViewById(R.id.roommateText);
        }
    }

    /**
     * Called to create the view
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return a RoommateListHolder object.
     */
    @NonNull
    @Override
    public RoommatesRecyclerAdapter.RoommateListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.roommate, parent, false);
        return new RoommatesRecyclerAdapter.RoommateListHolder(view);
    }

    /**
     * Called after each view has been created or recycled.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RoommatesRecyclerAdapter.RoommateListHolder holder, int position) {
        Account roommate = roommateList.get(position);

        Log.d(DEBUG_TAG, "onBindViewHolder: " + roommate);

        String key = roommate.getKey();
        String accountName = roommate.getAccountName();

        holder.roommateText.setText(accountName);
    }

    /**
     * @return the number of items in the shopping list
     */
    @Override
    public int getItemCount() { return roommateList.size(); }
}
