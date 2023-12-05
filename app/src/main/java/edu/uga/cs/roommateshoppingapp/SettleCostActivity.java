package edu.uga.cs.roommateshoppingapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uga.cs.roommateshoppingapp.data.ShoppingItem;

public class SettleCostActivity extends LoggedInActivity {
    public static final String DEBUG_TAG = "SettleCostActivity";
    private FirebaseDatabase database;
    private double total = 0.0;
    private double averagePerRoommate = 0.0;
    private double numRoommates = 0.0;
    private String userEmail = "";
    private List<String> userEmails = new ArrayList<>();
    private List<Double> roommateTotals = new ArrayList<>();
    StringBuilder result = new StringBuilder();
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "SettleCostActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_cost);

      textView = findViewById(R.id.textView8);

        // descendant activity, enable up button
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        // show items
        List<ShoppingItem> shoppingList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();

        setupSettleCost();

        // set up settle cost event
        Button checkout = findViewById(R.id.checkout);
        checkout.setOnClickListener(view -> {
            Log.d(DEBUG_TAG, "Starting settle cost process");
            database.getReference(PurchasesActivity.PURCHASES_REF).removeValue();
            Log.d(DEBUG_TAG, "Deleted Purchases");
            textView.setText("Cost has been settled!");
        });
    }

    /**
     * Helper method for initializing the purchases
     */
    private void setupSettleCost() {
        DatabaseReference dbr = database.getReference(PurchasesActivity.PURCHASES_REF);

        Query query = dbr.orderByChild("accountName");

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // we have a list of values containing the users that match the query (should be 1)
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists() && dataSnapshot.getKey() != null) {
                    for (DataSnapshot roommate : dataSnapshot.getChildren()) {
                        numRoommates++;
                        String totalString = (roommate.child("amount").toString());
                        // Remove non-numeric characters
                        String numericPart = totalString.replaceAll("[^\\d.]", "");
                        double numericValue = Double.parseDouble(numericPart);
                        //add to roommateTotals array
                        roommateTotals.add(numericValue);

                        String roommateName = roommate.child("accountName").toString();
                        //regular expression pattern to match the email address
                        Pattern pattern = Pattern.compile("\\bvalue = (\\S+@\\S+)\\b");
                        //matcher for the input string
                        Matcher matcher = pattern.matcher(roommateName);
                        //Check if found
                        if (matcher.find()) {
                            //Extract email from the matched group
                            userEmail = matcher.group(1);
                            userEmails.add(userEmail);
                        } else {
                            Log.e(DEBUG_TAG, "Failed to find email");
                        }
                        total = total + numericValue;
                    }
                    averagePerRoommate = total/numRoommates;
                    for (int i = 0; i < userEmails.size(); i++) {
                        String userEmail = userEmails.get(i);
                        double spentAmount = roommateTotals.get(i);

                        // Append person's email and spending to the StringBuilder
                        result.append("User: ").append(userEmail)
                                .append(", Spent: $").append(spentAmount)
                                .append("\n");
                    }
                    //round the average to two decimal places
                    BigDecimal bd = new BigDecimal(averagePerRoommate);
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    averagePerRoommate = bd.doubleValue();
                    result.append("\nAverage Per Person: $").append(averagePerRoommate);
                    textView.setText(result.toString());
                    Toast.makeText(getApplicationContext(), "Settle the cost calculated.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(DEBUG_TAG, "Failed to find Purchase list.");
                }
            } else {
                Log.w(DEBUG_TAG, "failed to find Purchase list", task.getException());
            }
        });
    }
}
