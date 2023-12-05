package edu.uga.cs.roommateshoppingapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class EditPurchasedGroupDialog extends DialogFragment  {
    public static final String DEBUG_TAG = "EditPurchasedGroupDialog";
    private EditText editItem;
    private int position;

    public interface DialogListener {
        void updatePurchased(int position, double newTotal);
    }

    /**
     * Factory method to create an EditPurchasedGroupDialog
     * @param position the position of the view in the RecyclerView
     * @param key Purchased.key
     * @param oldAmount Purchased.amount
     * @return an EditPurchasedGroupDialog
     */
    public static EditPurchasedGroupDialog newInstance(int position, String key, double oldAmount) {
        EditPurchasedGroupDialog dialog = new EditPurchasedGroupDialog();

        // Provide shopping item values as bundle arguments.
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("key", key);
        args.putDouble("oldAmount", oldAmount);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a shopping item
        assert getArguments() != null;
        position = getArguments().getInt("position");
        String key = getArguments().getString("key");
        double oldAmount = getArguments().getDouble("oldAmount");

        // Create the AlertDialog view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.edit_purchase_list_dialog, getActivity().findViewById(R.id.root2));

        // get the view objects in the AlertDialog
        editItem = layout.findViewById(R.id.editItem2);


        // create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);

        builder.setTitle(R.string.dialog_edit_item);
        builder.setPositiveButton(android.R.string.ok, new EditPurchasedGroupDialog.SaveItemListener());
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
            Log.d(DEBUG_TAG, "save.onClick()");
            double newTotal;

            // need to use try-catch to guard against possible non-numbers entered
            // and given as arguments to Double.parseDouble
            try {
                newTotal = Double.parseDouble(editItem.getText().toString());
            } catch( NumberFormatException nfe ) {
                Toast toast = Toast.makeText(getActivity(),
                        "Enter positive decimal values",
                        Toast.LENGTH_SHORT );
                toast.show();
                editItem.setText("");
                return;
            }

            // Check if value entered is positive
            if (newTotal < 0) {
                Toast toast = Toast.makeText(getActivity(),
                        "Enter only positive decimal values",
                        Toast.LENGTH_SHORT);
                toast.show();
                editItem.setText("");
                return;
            }

            EditPurchasedGroupDialog.DialogListener listener = (EditPurchasedGroupDialog.DialogListener) getActivity();

            assert listener != null;
            listener.updatePurchased(position, newTotal);
            dismiss();
        }
    }
}
