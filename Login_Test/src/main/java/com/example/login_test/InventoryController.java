package com.example.login_test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeTableView;
import javafx.beans.property.SimpleStringProperty;

public class InventoryController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    ArrayList<String> CSVItemID = new ArrayList<>();
    ArrayList<String> CSVItemName = new ArrayList<>();
    ArrayList<String> CSVPrice = new ArrayList<>();
    ArrayList<String> CSVQuantity = new ArrayList<>();

    // Path to the writable CSV file
    String filepath = System.getProperty("user.home") + "/items.csv";

    @FXML
    private TextField ItemIDToDel;

    @FXML
    private TextField ItemNameField;

    @FXML
    private TextField PriceField;

    @FXML
    private TextField QtyFIeld;

    @FXML
    private TextField itemIDFieldtoAdd;

    @FXML
    private TextField itemIDFieldtoAddforEdit;

    @FXML
    private TextField PriceFieldforEdit;

    @FXML
    private TextField QtyFIeldforEdit;

    @FXML
    private TreeTableColumn<Inventory, String> QuantityColumn;

    @FXML
    private TreeTableView<Inventory> inventoryTableTree;

    @FXML
    private TreeTableColumn<Inventory, String> NameColumn;

    @FXML
    private TreeTableColumn<Inventory, String> PriceColumn;

    @FXML
    private TreeTableColumn<Inventory, String> IDColumn;

    @FXML
    public void initialize() {
        initializeFile(); // Ensure file is initialized
        IDColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getitemID()));
        PriceColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getPrice()));
        NameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getitemName()));
        QuantityColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getQuantity()));
        loadInventoryData();
    }

    private void initializeFile() {
        URL resource = getClass().getResource("/items.csv");
        File file = new File(filepath);

        if (!file.exists()) {
            try (InputStream in = resource.openStream();
                 OutputStream out = new FileOutputStream(file)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void InventoryCSVRead() {
        BufferedReader reader = null;
        String line = "";
        File file = new File(filepath);

        if (!file.exists()) {
            System.err.println("File not found: " + filepath);
            return;
        }

        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");

                CSVItemID.add(row[0]);
                CSVItemName.add(row[1]);
                CSVPrice.add(row[2]);
                CSVQuantity.add(row[3]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadInventoryData() {
        InventoryCSVRead();
        ObservableList<Inventory> inventories = FXCollections.observableArrayList();
        for (int i = 0; i < CSVItemID.size(); i++) {
            inventories.add(new Inventory(CSVItemID.get(i), CSVItemName.get(i), CSVPrice.get(i), CSVQuantity.get(i)));
        }

        TreeItem<Inventory> root = new TreeItem<>(new Inventory("Root", "", "", ""));
        for (Inventory inventory : inventories) {
            root.getChildren().add(new TreeItem<>(inventory));
        }

        inventoryTableTree.setRoot(root);
        inventoryTableTree.setShowRoot(false);
    }

    @FXML
    void addItems(ActionEvent event) {
        String idToAdd = itemIDFieldtoAdd.getText();
        String nameToAdd = ItemNameField.getText();
        String priceToAdd = PriceField.getText();
        String qtyToAdd = QtyFIeld.getText();

        try {
            FileWriter writer = new FileWriter(filepath, true);
            writer.append(idToAdd);
            writer.append(",");
            writer.append(nameToAdd);
            writer.append(",");
            writer.append(priceToAdd);
            writer.append(",");
            writer.append(qtyToAdd);
            writer.append("\n");
            writer.close();

            Inventory newInventory = new Inventory(idToAdd, nameToAdd, priceToAdd, qtyToAdd);
            TreeItem<Inventory> newUserItem = new TreeItem<>(newInventory);
            inventoryTableTree.getRoot().getChildren().add(newUserItem);

            itemIDFieldtoAdd.clear();
            ItemNameField.clear();
            PriceField.clear();
            QtyFIeld.clear();

            CSVItemID.add(idToAdd);
            CSVItemName.add(nameToAdd);
            CSVPrice.add(priceToAdd);
            CSVQuantity.add(qtyToAdd);

        } catch (IOException e) {
            e.printStackTrace();
        }

        showAlert(Alert.AlertType.INFORMATION, "Added", "Item added successfully");
    }

    @FXML
    void removeItems(ActionEvent event) {
        String idToDelete = ItemIDToDel.getText();
        int index = 0;

        for (int i = 0; i < CSVItemID.size(); i++) {
            if (Objects.equals(idToDelete, CSVItemID.get(i))) {
                index = i + 1;
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Item deleted successfully.");
                break;
            }
        }

        System.out.print(index);

        delete(filepath, index);

        if (index == 0) {
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "No item found.");
        }

        ItemIDToDel.clear();
        index = 0;

        ObservableList<TreeItem<Inventory>> items = inventoryTableTree.getRoot().getChildren();
        for (TreeItem<Inventory> item : items) {
            if (item.getValue().getitemID().equals(idToDelete)) {
                items.remove(item);
                break;
            }
        }
        CSVItemID.clear();
        CSVPrice.clear();
        CSVItemName.clear();
        CSVQuantity.clear();

        puttoArray();
    }

    @FXML
    void editItems(ActionEvent event) {
        String idToEdit = itemIDFieldtoAddforEdit.getText();
        String newPrice = PriceFieldforEdit.getText();
        String newQuantity = QtyFIeldforEdit.getText();

        int index = -1;
        for (int i = 0; i < CSVItemID.size(); i++) {
            if (Objects.equals(idToEdit, CSVItemID.get(i))) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "Item ID not found.");
            return;
        }

        CSVPrice.set(index, newPrice);
        CSVQuantity.set(index, newQuantity);

        updateCSV();
        loadInventoryData();

        itemIDFieldtoAddforEdit.clear();
        PriceFieldforEdit.clear();
        QtyFIeldforEdit.clear();

        showAlert(Alert.AlertType.INFORMATION, "Edited", "Item edited successfully.");
    }

    private void updateCSV() {
        try {
            FileWriter fw = new FileWriter(filepath, false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            for (int i = 0; i < CSVItemID.size(); i++) {
                pw.println(CSVItemID.get(i) + "," + CSVItemName.get(i) + "," + CSVPrice.get(i) + "," + CSVQuantity.get(i));
            }

            pw.flush();
            pw.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void returnToMainMenu(ActionEvent event) {
        try {
            Parent newRoot = FXMLLoader.load(getClass().getResource("cashiermainmenu.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            double currentWidth = 820;
            double currentHeight = 500;

            SceneTransitionUtil.switchSceneWithFadeAndScale(stage, newRoot, currentWidth, currentHeight);

            stage.setTitle("Inventory Manager");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void puttoArray() {
        InventoryCSVRead();
        ObservableList<Inventory> inventories = FXCollections.observableArrayList();

        for (int i = 0; i < CSVItemID.size(); i++) {
            inventories.add(new Inventory(CSVItemID.get(i), CSVItemName.get(i), CSVPrice.get(i), CSVQuantity.get(i)));
        }

        TreeItem<Inventory> root = new TreeItem<>(new Inventory("Root", "", "", ""));
        for (Inventory inventory : inventories) {
            root.getChildren().add(new TreeItem<>(inventory));
        }

        inventoryTableTree.setRoot(root);
        inventoryTableTree.setShowRoot(false);
    }

    private void delete(String filepath, int index) {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            int currentLine = 0;
            while ((line = reader.readLine()) != null) {
                if (currentLine != index - 1) {
                    lines.add(line);
                }
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
