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

public class AddShoppingItemDialog extends DialogFragment {
    private EditText addItem;

    public interface DialogListener {
        void addItem(ShoppingItem shoppingItem);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the AlertDialog view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.add_shopping_item_dialog, getActivity().findViewById(R.id.root));

        // get the view objects in the AlertDialog
        addItem = layout.findViewById(R.id.addItem);

        // create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);

        builder.setTitle(R.string.dialog_add_item);
        builder.setPositiveButton(android.R.string.ok, new AddItemListener());
        builder.setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> {
            // close the dialog
            dialog.dismiss();
        });

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class AddItemListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            ShoppingItem shoppingItem = new ShoppingItem(addItem.getText().toString());
            DialogListener listener = (DialogListener) getActivity();

            assert listener != null;
            listener.addItem(shoppingItem);
            dismiss();
        }
    }
}