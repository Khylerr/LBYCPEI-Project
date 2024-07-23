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
    private final StringProperty id;
    private final StringProperty name;
    private final DoubleProperty price;
    private final IntegerProperty quantity;

    public Item(String id, String name, double price, int quantity) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.quantity = new SimpleIntegerProperty(quantity);
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public String getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public double getPrice() {
        return price.get();
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public static List<Item> loadItemsFromCSV(String filename) {
        List<Item> items = new ArrayList<>();
        try (InputStream is = Item.class.getResourceAsStream(filename);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            if (is == null) {
                throw new IOException("Resource not found: " + filename);
            }
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 4) {
                    Item item = new Item(values[0], values[1], Double.parseDouble(values[2]), Integer.parseInt(values[3]));
                    items.add(item);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

}