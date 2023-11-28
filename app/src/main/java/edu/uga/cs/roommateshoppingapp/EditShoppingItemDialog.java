package edu.uga.cs.roommateshoppingapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import edu.uga.cs.roommateshoppingapp.data.ShoppingItem;

public class EditShoppingItemDialog extends DialogFragment {
    public static final String DEBUG_TAG = "EditShoppingItemDialog";
    private EditText editItem;
    private int position;
    private String key;
    private String itemName;
    private ShoppingItem shoppingItem;

    public interface DialogListener {
        void updateItem(int position, ShoppingItem item);
        void deleteItem(int position, ShoppingItem item);
    }

    /**
     * Factory method to create an EditShoppingItemDialog
     * @param position the position of the view in the RecyclerView
     * @param key ShoppingItem.key
     * @param itemName ShoppingItem.itemName
     * @return an EditShoppingItemDialog
     */
    public static EditShoppingItemDialog newInstance(int position, String key, String itemName) {
        EditShoppingItemDialog dialog = new EditShoppingItemDialog();

        // Provide shopping item values as bundle arguments.
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("key", key);
        args.putString("itemName", itemName);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a shopping item
        assert getArguments() != null;
        position = getArguments().getInt("position");
        key = getArguments().getString( "key");
        itemName = getArguments().getString("itemName");
        shoppingItem = new ShoppingItem(itemName);
        shoppingItem.setKey(key);

        // Create the AlertDialog view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.edit_shopping_item_dialog, getActivity().findViewById(R.id.root2));

        // get the view objects in the AlertDialog
        editItem = layout.findViewById(R.id.editItem);


        // create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);

        builder.setTitle(R.string.dialog_edit_item);
        builder.setPositiveButton(android.R.string.ok, new EditShoppingItemDialog.SaveItemListener());
        builder.setNeutralButton(R.string.delete, new EditShoppingItemDialog.DeleteItemListener());
        builder.setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> {
            // close the dialog
            dialog.dismiss();
        });

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class SaveItemListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            shoppingItem.setItemName(editItem.getText().toString());
            EditShoppingItemDialog.DialogListener listener = (EditShoppingItemDialog.DialogListener) getActivity();

            assert listener != null;
            listener.updateItem(position, shoppingItem);
            dismiss();
        }
    }

    private class DeleteItemListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            EditShoppingItemDialog.DialogListener listener = (EditShoppingItemDialog.DialogListener) getActivity();

            assert listener != null;
            listener.deleteItem(position, shoppingItem);
            dismiss();
        }
    }
}
