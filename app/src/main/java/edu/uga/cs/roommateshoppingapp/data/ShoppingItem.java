package edu.uga.cs.roommateshoppingapp.data;

/**
 * A Java POJO class representing a shopping item.
 */
public class ShoppingItem {
    private String key;
    private String itemName;

    public ShoppingItem() {
        this.key = null;
        this.itemName = null;
    }

    public ShoppingItem(String itemName) {
        this.key = null;
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "ShoppingItem{" +
                "key='" + key + '\'' +
                ", itemName='" + itemName + '\'' +
                '}';
    }
}
