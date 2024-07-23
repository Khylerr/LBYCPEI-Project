package com.example.login_test;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Item {
    private final SimpleStringProperty id;
    private final SimpleStringProperty name;
    private final SimpleDoubleProperty price;
    private final SimpleIntegerProperty quantity;

    public Item(String id, String name, double price, int quantity) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.quantity = new SimpleIntegerProperty(quantity);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public double getPrice() {
        return price.get();
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public static List<Item> loadItemsFromCSV(InputStream csvInputStream) throws IOException {
        List<Item> items = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvInputStream))) {
            String line;
            boolean isHeader = true;  // Flag to skip header row
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;  // Skip the header row
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    try {
                        String id = parts[0];
                        String name = parts[1];
                        double price = Double.parseDouble(parts[2].trim());
                        int quantity = Integer.parseInt(parts[3].trim());
                        items.add(new Item(id, name, price, quantity));
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid line: " + line);
                        // Log error or handle invalid data
                    }
                }
            }
        }
        return items;
    }

}

