package com.example.login_test;

import java.util.ArrayList;

public class ReceiptData {

    private static ArrayList<String> receiptName;
    private static ArrayList<Double> receiptPrice;
    private static ArrayList<Integer> receiptQty;


    public static ArrayList<String> getReceiptName() {
        return receiptName;
    }

    public static ArrayList<Double> getReceiptPrice() {
        return receiptPrice;
    }

    public static ArrayList<Integer> getReceiptQty() {
        return receiptQty;
    }

    public static void setReceiptName(ArrayList<String> name)
    {
        ReceiptData.receiptName = name;
    }

    public static void setReceiptPrice(ArrayList<Double> price){
        ReceiptData.receiptPrice = price;
    }

    public static void setReceiptQty (ArrayList<Integer> qty) {
        ReceiptData.receiptQty = qty;
    }
}


