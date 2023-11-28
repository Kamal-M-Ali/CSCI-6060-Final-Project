package edu.uga.cs.roommateshoppingapp.data;

import java.util.HashMap;
import java.util.Map;

/**
 * A Java POJO class representing a roommate's account.
 */
public class Account {
    private String key;
    private String accountName;
    private Map<String, String> myCart;

    public Account() {
        this.key = null;
        this.accountName = null;
        this.myCart = null;
    }

    public Account(String accountName) {
        this.key = null;
        this.accountName = accountName;
        this.myCart = new HashMap<>();
    }

    public Account(String accountName, Map<String, String> myCart) {
        this.key = null;
        this.accountName = accountName;
        this.myCart = myCart;
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

    public Map<String, String> getMyCart() {
        return myCart;
    }

    public void setMyCart(Map<String, String> myCart) {
        this.myCart = myCart;
    }
}
