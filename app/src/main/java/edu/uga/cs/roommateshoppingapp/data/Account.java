package edu.uga.cs.roommateshoppingapp.data;

import java.util.HashMap;
import java.util.Map;

/**
 * A Java POJO class representing a roommate's account.
 */
public class Account {
    private String key;
    private String accountName;
    private Map<String, String> cart;

    public Account() {
        this.key = null;
        this.accountName = null;
        this.cart = null;
    }

    public Account(String accountName) {
        this.key = null;
        this.accountName = accountName;
        this.cart = new HashMap<>();
    }

    public Account(String accountName, Map<String, String> cart) {
        this.key = null;
        this.accountName = accountName;
        this.cart = cart;
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

    public Map<String, String> getCart() {
        return cart;
    }

    public void setCart(Map<String, String> cart) {
        this.cart = cart;
    }
}
