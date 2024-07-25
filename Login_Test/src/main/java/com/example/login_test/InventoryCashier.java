package com.example.login_test;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryCashier {

    @FXML
    private Label amount_paid;

    @FXML
    private TextField amount_paid_value;

    @FXML
    private TreeTableColumn<Item, String> item_id;

    @FXML
    private TreeTableColumn<Item, String> item_name;

    @FXML
    private TreeTableColumn<Item, Double> price;

    @FXML
    private TreeTableColumn<Item, String> item_name_selected;

    @FXML
    private Button manage_button;

    @FXML
    private TreeTableColumn<Item, Integer> numberOfItems;

    @FXML
    private TreeTableColumn<Item, Double> price_selected;

    @FXML
    private Button print_receipt;

    @FXML
    private TreeTableColumn<Item, Integer> quantity;

    @FXML
    private Button search_id_button;

    @FXML
    private TextField search_id_field;

    @FXML
    private TextField search_item_name_field;

    @FXML
    private Button search_name_button;

    @FXML
    private Label subtotal;

    @FXML
    private Label subtotal_value;

    @FXML
    private TreeTableView<Item> table_1;

    @FXML
    private TreeTableView<Item> table_2;

    @FXML
    private Label total_change_value;

    @FXML
    private Button update_inventory_button;

    private ObservableList<Item> items = FXCollections.observableArrayList();

    private TreeItem<Item> originalRoot; // Store the original root

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public void initialize() {
        // Initialize columns for table_1
        item_id.setCellValueFactory(cellData -> cellData.getValue().getValue().idProperty());
        item_name.setCellValueFactory(cellData -> cellData.getValue().getValue().nameProperty());
        price.setCellValueFactory(cellData -> cellData.getValue().getValue().priceProperty().asObject());
        quantity.setCellValueFactory(cellData -> cellData.getValue().getValue().quantityProperty().asObject());

        // Initialize columns for table_2
        item_name_selected.setCellValueFactory(cellData -> cellData.getValue().getValue().nameProperty());
        numberOfItems.setCellValueFactory(cellData -> cellData.getValue().getValue().quantityProperty().asObject());
        price_selected.setCellValueFactory(cellData -> cellData.getValue().getValue().priceProperty().asObject());

        try {
            List<Item> loadedItems = Item.loadItemsFromCSV(getClass().getResourceAsStream("/items.csv"));
            System.out.println("Loaded " + loadedItems.size() + " items");
            items.addAll(loadedItems);
            populateTreeView(); // Populate the TreeTableView with items
        } catch (IOException e) {
            System.err.println("Error loading items: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load items from CSV.");
        }

        // Add listener to table_1 selection changes
        table_1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                addItemToTable2(newValue.getValue());
                calculateAndUpdateSubtotal(); // Ensure subtotal is updated
            }
        });

        // Initialize table_2 with a root node
        TreeItem<Item> root2 = new TreeItem<>(new Item("0", "Root", 0.0, 0));
        root2.setExpanded(true);
        table_2.setRoot(root2);
        table_2.setShowRoot(false);

        // Add listener to table_2 changes
        table_2.getRoot().getChildren().addListener((javafx.collections.ListChangeListener<TreeItem<Item>>) change -> {
            calculateAndUpdateSubtotal();
        });
    }

    private void populateTreeView() {
        originalRoot = new TreeItem<>(new Item("0", "Root", 0.0, 0));
        originalRoot.setExpanded(true);

        for (Item item : items) {
            TreeItem<Item> itemNode = new TreeItem<>(item);
            originalRoot.getChildren().add(itemNode);
        }

        table_1.setRoot(originalRoot);
        table_1.setShowRoot(false);
    }

    @FXML
    void handleSearchButton(ActionEvent event) {
        String searchTerm = search_item_name_field.getText().toLowerCase();
        System.out.println("Searching for item name: " + searchTerm); // Debug statement
        filterItems(item -> item.getName().toLowerCase().contains(searchTerm));
    }

    @FXML
    void handleSearchButtonID(ActionEvent event) {
        String searchTerm = search_id_field.getText().toLowerCase();
        System.out.println("Searching for item ID: " + searchTerm); // Debug statement
        filterItems(item -> item.getId().toLowerCase().contains(searchTerm));
    }

    private void filterItems(java.util.function.Predicate<Item> predicate) {
        List<TreeItem<Item>> filteredItems = new ArrayList<>();
        for (TreeItem<Item> treeItem : originalRoot.getChildren()) {
            if (predicate.test(treeItem.getValue())) {
                filteredItems.add(treeItem);
            }
        }

        TreeItem<Item> newRoot = new TreeItem<>(new Item("0", "Filtered Root", 0.0, 0));
        newRoot.setExpanded(true);
        newRoot.getChildren().setAll(filteredItems);
        table_1.setRoot(newRoot);

        if (filteredItems.isEmpty()) {
            System.out.println("No items match the search criteria.");
        }
    }

    @FXML
    void handleCashAmountChange() {
        try {
            double cash = Double.parseDouble(amount_paid_value.getText());
            double total = calculateTotal();
            double change = cash - total;
            subtotal.setText(String.format("Total: $%.2f", total));
            total_change_value.setText(String.format("$%.2f", change));
        } catch (NumberFormatException e) {
            total_change_value.setText("Invalid cash amount");
        }
        calculateAndUpdateSubtotal(); // Ensure subtotal is updated
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
        // Check if the item already exists in table_2
        boolean itemExists = false;
        for (TreeItem<Item> treeItem : table_2.getRoot().getChildren()) {
            if (treeItem.getValue().getId().equals(item.getId())) {
                itemExists = true;
                item.setQuantity(item.getQuantity() + 1);
                table_2.refresh();
                break;
            }
        }

        // If item doesn't exist, add it with a quantity of 1
        if (!itemExists) {
            TreeItem<Item> newItem = new TreeItem<>(new Item(item.getId(), item.getName(), item.getPrice(), 1));
            table_2.getRoot().getChildren().add(newItem);
        }

        // Ensure that the changes in table_2 are reflected in the subtotal
        calculateAndUpdateSubtotal();
    }

    public void reloadDataFromCSV() {
        try {
            items.clear();
            originalRoot.getChildren().clear();

            // Load new items from the CSV
            List<Item> loadedItems = Item.loadItemsFromCSV(getClass().getResourceAsStream("/items.csv"));
            System.out.println("Reloaded " + loadedItems.size() + " items");

            items.addAll(loadedItems);

            populateTreeView();
        } catch (IOException e) {
            System.err.println("Error reloading items: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Could not reload items from CSV.");
        }
    }




    private void calculateAndUpdateSubtotal() {
        Platform.runLater(() -> {
            double total = calculateTotal();
            subtotal_value.setText(String.format("$%.2f", total));
            handleCashAmountChange(); // Update change value based on the latest total
        });
    }

    @FXML
    void handlePrintReceipt(ActionEvent event) {
        try {
            updateCSV();
            // Code to print receipt can be added here
            showAlert(Alert.AlertType.INFORMATION, "Print", "Receipt printed successfully!");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update CSV file.");
        }
    }



    @FXML
    void switchToManageAccounts(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("userControl.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Account Manager");

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchToUpdateInventory(ActionEvent event)
    {
        try {
            root = FXMLLoader.load(getClass().getResource("InventoryEdit.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Inventory Manager");

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void logout(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("login.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void updateCSV() throws IOException {
        // Get the path to the CSV file in the resources folder
        InputStream inputStream = getClass().getResourceAsStream("/items.csv");
        if (inputStream == null) {
            throw new FileNotFoundException("CSV file not found in resources");
        }
        String tempFilePath = System.getProperty("java.io.tmpdir") + "items.csv";
        File tempFile = new File(tempFilePath);

        // Copy the file from resources to a writable location
        try (InputStream in = inputStream; FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }

        // Write to the temporary CSV file
        try (PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
            writer.println("ID,Name,Price,Quantity"); // Header
            for (TreeItem<Item> treeItem : table_2.getRoot().getChildren()) {
                Item item = treeItem.getValue();
                writer.printf("%s,%s,%.2f,%d%n", item.getId(), item.getName(), item.getPrice(), item.getQuantity());
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


}
