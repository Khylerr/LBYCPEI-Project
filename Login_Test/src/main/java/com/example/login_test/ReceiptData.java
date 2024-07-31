package com.example.login_test;

import java.util.ArrayList;

public class ReceiptData {

    private static ArrayList<String> receiptName = new ArrayList<>();
    private static ArrayList<Double> receiptPrice = new ArrayList<>();
    private static ArrayList<Integer> receiptQty = new ArrayList<>();

    public static ArrayList<String> getReceiptName() {
        return receiptName;
    }

    public static ArrayList<Double> getReceiptPrice() {
        return receiptPrice;
    }

    public static ArrayList<Integer> getReceiptQty() {
        return receiptQty;
    }

    public static void setReceiptName(ArrayList<String> name) {
        receiptName = name;
    }

    public static void setReceiptPrice(ArrayList<Double> price) {
        receiptPrice = price;
    }

    public static void setReceiptQty(ArrayList<Integer> qty) {
        receiptQty = qty;
    }

    public static void printReceipt() {
        if (receiptName.isEmpty()) {
            System.out.println("No items to print.");
            return;
        }

        System.out.println("Receipt:");
        double total = 0.0;
        for (int i = 0; i < receiptName.size(); i++) {
            String name = receiptName.get(i);
            double price = receiptPrice.get(i);
            int qty = receiptQty.get(i);
            double itemTotal = price * qty;
            total += itemTotal;
            System.out.printf("%s: $%.2f x %d = $%.2f%n", name, price, qty, itemTotal);
        }
        System.out.printf("Total: $%.2f%n", total);
    }
}
