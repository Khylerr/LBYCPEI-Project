package com.example.login_test;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InventoryCashier {

    @FXML
    private TreeTableColumn<Item, String> itemID;
    @FXML
    private TreeTableColumn<Item, String> itemName;
    @FXML
    private TreeTableColumn<Item, Double> itemPrice;
    @FXML
    private TreeTableColumn<Item, Integer> itemNumberSelected;
    @FXML
    private TreeTableColumn<Item, String> ItemNameSelected;
    @FXML
    private TextField searchField;
    @FXML
    private TextField searchFieldID;
    @FXML
    private Button searchbutton;
    @FXML
    private Button searchbuttonID;
    @FXML
    private TreeTableView<Item> table_1;
    @FXML
    private TreeTableView<Item> table_2;
    @FXML
    private Label totalLabel;
    @FXML
    private Label totalLabel1;
    @FXML
    private TextField cashAmount;

    private ObservableList<Item> items = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize the TreeTableView columns
        itemID.setCellValueFactory(cellData -> cellData.getValue().getValue().idProperty());
        itemName.setCellValueFactory(cellData -> cellData.getValue().getValue().nameProperty());
        itemPrice.setCellValueFactory(cellData -> cellData.getValue().getValue().priceProperty().asObject());
        itemNumberSelected.setCellValueFactory(cellData -> cellData.getValue().getValue().quantityProperty().asObject());
        ItemNameSelected.setCellValueFactory(cellData -> cellData.getValue().getValue().nameProperty());

        // Load items from CSV
        List<Item> loadedItems = Item.loadItemsFromCSV("items.csv");
        items.addAll(loadedItems);

        // Create root item
        TreeItem<Item> root = new TreeItem<>(new Item("0", "Root", 0.0, 0));
        root.setExpanded(true);

        // Add items to root
        for (Item item : items) {
            root.getChildren().add(new TreeItem<>(item));
        }

        table_1.setRoot(root);
        table_1.setShowRoot(false);

        // Add listener for item selection
        table_1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                addItemToTable2(newValue.getValue());
            }
        });

        // Initialize the second TreeTableView columns
        TreeItem<Item> root2 = new TreeItem<>(new Item("0", "Root", 0.0, 0));
        root2.setExpanded(true);
        table_2.setRoot(root2);
        table_2.setShowRoot(false);

        // Bind the totalLabel to the total price
        totalLabel.setText(String.format("Total: $%.2f", calculateTotal()));
    }

    @FXML
    void handleSearchButton(ActionEvent event) {
        String searchTerm = searchField.getText().toLowerCase();
        filterItems(item -> item.getName().toLowerCase().contains(searchTerm));
    }

    @FXML
    void handleSearchButtonID(ActionEvent event) {
        String searchTerm = searchFieldID.getText().toLowerCase();
        filterItems(item -> item.getId().toLowerCase().contains(searchTerm));
    }

    private void filterItems(java.util.function.Predicate<Item> predicate) {
        List<TreeItem<Item>> filteredItems = new ArrayList<>();
        for (TreeItem<Item> treeItem : table_1.getRoot().getChildren()) {
            if (predicate.test(treeItem.getValue())) {
                filteredItems.add(treeItem);
            }
        }
        TreeItem<Item> newRoot = new TreeItem<>(new Item("0", "Filtered Root", 0.0, 0));
        newRoot.setExpanded(true);
        newRoot.getChildren().setAll(filteredItems);
        table_1.setRoot(newRoot);
    }

    @FXML
    void handleCashAmountChange() {
        try {
            double cash = Double.parseDouble(cashAmount.getText());
            double total = calculateTotal();
            double change = cash - total;
            totalLabel.setText(String.format("Total: $%.2f", total));
            totalLabel1.setText(String.format("Change: $%.2f", change));
        } catch (NumberFormatException e) {
            totalLabel1.setText("Invalid cash amount");
        }
    }

    private double calculateTotal() {
        double total = 0;
        for (TreeItem<Item> treeItem : table_2.getRoot().getChildren()) {
            Item item = treeItem.getValue();
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    private void addItemToTable2(Item item) {
        for (TreeItem<Item> treeItem : table_2.getRoot().getChildren()) {
            if (treeItem.getValue().getId().equals(item.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                treeItem.getValue().setQuantity(item.getQuantity());
                table_2.refresh();
                return;
            }
        }
        TreeItem<Item> newItem = new TreeItem<>(new Item(item.getId(), item.getName(), item.getPrice(), 1));
        table_2.getRoot().getChildren().add(newItem);
    }
}