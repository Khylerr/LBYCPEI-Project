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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;

import java.io.FileWriter;
import java.util.Objects;

public class InventoryController
{

    private Stage stage;
    private Scene scene;
    private Parent root;

    ArrayList<String> CSVItemID = new ArrayList<>();
    ArrayList<String> CSVItemName = new ArrayList<>();
    ArrayList<String> CSVPrice = new ArrayList<>();
    ArrayList<String> CSVQuantity = new ArrayList<>();


    String filepath =  "C:\\Users\\Khyler\\IdeaProjects\\LBYCPEI-Project-main\\Login_Test\\src\\main\\resources\\items.csv";
    //CHANGE FILEPATH BASED ON WHERE IT IS LOCATEDDDD!!!!

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
        IDColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getitemID()));
        PriceColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getPrice()));
        NameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getitemName()));
        QuantityColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getQuantity()));
        loadInventoryData();
    }

    public void InventoryCSVRead()
    {
        BufferedReader reader = null;
        String line = "";
        String File = filepath;

        if (File == null) {
            System.err.println("Resource not found: users.csv");
            return;
        }

        try {
            reader = new BufferedReader(new FileReader(File));
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


        }
        catch (IOException e) {
            e.printStackTrace();
        }


        showAlert(Alert.AlertType.INFORMATION, "Added", "Item added successfully");
    }



    @FXML
    void removeItems(ActionEvent event) {

        String idToDelete = ItemIDToDel.getText();
        int index = 0;

        for(int i = 0; i < CSVItemID.size(); i++)
        {
            if (Objects.equals(idToDelete, CSVItemID.get(i)))
            {
                index = i+1;
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
    void returnToMainMenu(ActionEvent event)
    {
        initialize();
        try {
            root = FXMLLoader.load(getClass().getResource("cashiermainmenu.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Cashier Main Menu");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void puttoArray()
    {
        InventoryCSVRead();
        ObservableList<Inventory> inventories = FXCollections.observableArrayList();

            for (int i = 0; i < CSVItemID.size(); i++)
            {
                inventories.add(new Inventory(CSVItemID.get(i), CSVItemName.get(i), CSVPrice.get(i), CSVQuantity.get(i)));
            }



    }

    private void delete(String filepath, int deleteLine)
    {
        String tempFile = "temp.txt";
        File oldFile = new File(filepath);
        File newFile = new File(tempFile);

        int line = 0;
        String currentLine;

        try
        {
            FileWriter fw = new FileWriter(tempFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            FileReader fr = new FileReader(filepath);
            BufferedReader br = new BufferedReader(fr);

            while((currentLine = br.readLine()) != null)
            {
                line++;

                if(deleteLine != line)
                {
                    pw.println(currentLine);

                }
            }

            pw.flush();
            pw.close();
            fr.close();
            br.close();
            bw.close();
            fw.close();

            oldFile.delete();
            File dump = new File(filepath);
            newFile.renameTo(dump);

        }

        catch(Exception e)
        {
            System.out.println(e);
        }


    }


    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }



}

