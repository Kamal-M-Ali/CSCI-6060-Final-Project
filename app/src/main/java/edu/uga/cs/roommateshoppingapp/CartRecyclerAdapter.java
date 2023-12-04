package edu.uga.cs.roommateshoppingapp;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

import edu.uga.cs.roommateshoppingapp.data.ShoppingItem;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.CartListHolder> {
    public static final String DEBUG_TAG = "CartRecyclerAdapter";
    private FirebaseDatabase database;
    private List<ShoppingItem> myCart;
    private Context context;

    /**
     * Constructor
     * @param myCart a list of ShoppingItem POJO objects
     * @param context the context of the caller (used for editing dialog popup)
     */
    public CartRecyclerAdapter(List<ShoppingItem> myCart, Context context) {
        this.myCart = myCart;
        this.context = context;
        this.database = FirebaseDatabase.getInstance();
    }

    // ShoppingListHolder subclass
    static class CartListHolder extends RecyclerView.ViewHolder {
        TextView cartItemText;

        public CartListHolder(View itemView) {
            super(itemView);
            cartItemText = itemView.findViewById(R.id.cartItemText);
        }
    }

    /**
     * Called to create the view
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return a CartListHolder object.
     */
    @NonNull
    @Override
    public CartListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartListHolder(view);
    }

    /**
     * Called after each view has been created or recycled.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(CartListHolder holder, int position) {
        ShoppingItem shoppingItem = myCart.get(position);

        Log.d(DEBUG_TAG, "onBindViewHolder: " + shoppingItem);

        String key = shoppingItem.getKey();
        String itemName = shoppingItem.getItemName();

        // setting up view
        holder.cartItemText.setText(context.getString(R.string.item_prefix, itemName));
        holder.itemView.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "Remove item: " + shoppingItem);

            // first we need to get the user
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null ) {
                // next find the cart of the user (it is automatically created on creation
                DatabaseReference dbr = database.getReference(CartActivity.ROOMMATE_CARTS_REF);
                Query query = dbr.orderByChild("accountName").equalTo(user.getEmail());

                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // we have a list of values containing the users that match the query (should be 1)
                        DataSnapshot dataSnapshot = task.getResult();
                        if (dataSnapshot.exists() && dataSnapshot.getKey() != null) {
                            for (DataSnapshot roommate : dataSnapshot.getChildren()) {
                                // remove the item from the roommate's cart
                                database.getReference(CartActivity.ROOMMATE_CARTS_REF)
                                        .child(roommate.getKey())
                                        .child("cart").child(key).removeValue();
                                database.getReference(SHOPPING_LIST_REF).push().setValue(shoppingItem.getItemName());
                                myCart.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());

                                // inform the user
                                Log.d(DEBUG_TAG, "setValue: success");
                                Toast.makeText(context.getApplicationContext(),
                                        "Removed from cart: " + shoppingItem.getItemName(), Toast.LENGTH_SHORT).show();
                                break; // only look at the first roommate
                            }
                        } else {
                            Log.e(DEBUG_TAG, "Failed to find user.");
                        }
                    } else {
                        Log.w(DEBUG_TAG, "setValue: failure", task.getException());
                        Toast.makeText(context.getApplicationContext(), "Remove from cart failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.d(DEBUG_TAG, "No user found.");
            }
        });
    }

    /**
     * @return the number of items in the shopping list
     */
    @Override
    public int getItemCount() { return myCart.size(); }
}
