package edu.uga.cs.roommateshoppingapp.data;

import java.util.Map;

/**
 * A Java POJO class representing a roommate's account.
 */
public class Purchase {
    private String key;
    private String accountName;
    private Map<String, Map<String, String>> purchased;
    private double amount;

    public Purchase() {
        this.key = null;
        this.accountName = null;
        this.purchased = null;
        this.amount = 0;
    }

    public Purchase(String accountName, Map<String, Map<String, String>> purchased, double amount) {
        this.key = null;
        this.accountName = accountName;
        this.purchased = purchased;
        this.amount = amount;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Map<String, Map<String, String>> getPurchased() {
        return purchased;
    }

    public void setPurchased(Map<String, Map<String, String>> purchased) {
        this.purchased = purchased;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "key='" + key + '\'' +
                ", accountName='" + accountName + '\'' +
                ", cart=" + purchased +
                ", amount=" + amount +
                '}';
    }
}
