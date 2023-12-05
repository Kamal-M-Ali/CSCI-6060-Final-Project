package edu.uga.cs.roommateshoppingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PurchasesActivity extends AppCompatActivity {
    public static final String DEBUG_TAG = "PurchaseActivity";
    public static final String PURCHASES_REF = "purchases";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases);
    }
}