package com.example.login_test;

public class Inventory
{
    private String itemID;
    private String itemName;
    private String price;
    private String quantity;

    public Inventory(String itemID, String itemName, String price, String quantity)
    {
        this.itemID = itemID;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;

    }


    public String getitemID() {
        return itemID;
    }

    public String getitemName() {
        return itemName;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

}