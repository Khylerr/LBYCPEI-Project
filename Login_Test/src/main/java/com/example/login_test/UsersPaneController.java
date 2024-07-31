package com.example.login_test;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UsersPaneController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    ArrayList<String> CSVUsername = new ArrayList<>();
    ArrayList<String> CSVPassword = new ArrayList<>();
    ArrayList<String> CSVRole = new ArrayList<>();

    private String filepath = "users.csv";

    @FXML
    private TextField AddPassField;

    @FXML
    private TextField AddRoleField;

    @FXML
    private TextField AddUserField;

    @FXML
    private TextField DelUserField;

    @FXML
    private TreeTableView<User> userTableView;

    @FXML
    private TreeTableColumn<User, String> PasswordColumn;

    @FXML
    private TreeTableColumn<User, String> RoleColumn;

    @FXML
    private TreeTableColumn<User, String> usernameColumn;

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getUsername()));
        PasswordColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getPassword()));
        RoleColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getRole()));
        loadUserData();
    }

    private void loadUserData() {
        List<String[]> csvData = readCSV();
        ObservableList<User> users = FXCollections.observableArrayList();

        for (String[] row : csvData) {
            if (row.length == 3) {
                users.add(new User(row[0], row[1], row[2]));
                CSVUsername.add(row[0]);
                CSVPassword.add(row[1]);
                CSVRole.add(row[2]);
            }
        }

        TreeItem<User> root = new TreeItem<>(new User("Root", "", ""));
        for (User user : users) {
            root.getChildren().add(new TreeItem<>(user));
        }

        userTableView.setRoot(root);
        userTableView.setShowRoot(false);
    }

    private List<String[]> readCSV() {
        List<String[]> csvData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                csvData.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvData;
    }

    private void writeCSV(List<String[]> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            bw.write("Username,Password,Role");
            bw.newLine();
            for (String[] row : data) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAddUserAction(ActionEvent event) {
        String usernameToAdd = AddUserField.getText();
        String passwordToAdd = AddPassField.getText();
        String roleToAdd = AddRoleField.getText();

        List<String[]> csvData = readCSV();
        csvData.add(new String[]{usernameToAdd, passwordToAdd, roleToAdd});
        writeCSV(csvData);

        User newUser = new User(usernameToAdd, passwordToAdd, roleToAdd);
        TreeItem<User> newUserItem = new TreeItem<>(newUser);
        userTableView.getRoot().getChildren().add(newUserItem);

        AddUserField.clear();
        AddPassField.clear();
        AddRoleField.clear();

        CSVUsername.add(usernameToAdd);
        CSVPassword.add(passwordToAdd);
        CSVRole.add(roleToAdd);

        showAlert(Alert.AlertType.INFORMATION, "Added", "User added successfully");
    }

    @FXML
    void onDelUserAction(ActionEvent event) {
        String usernameToDelete = DelUserField.getText();
        List<String[]> csvData = readCSV();
        boolean userFound = false;

        csvData.removeIf(row -> row[0].equals(usernameToDelete));
        writeCSV(csvData);

        ObservableList<TreeItem<User>> items = userTableView.getRoot().getChildren();
        for (TreeItem<User> item : items) {
            if (item.getValue().getUsername().equals(usernameToDelete)) {
                items.remove(item);
                userFound = true;
                break;
            }
        }

        if (userFound) {
            int index = CSVUsername.indexOf(usernameToDelete);
            if (index != -1) {
                CSVUsername.remove(index);
                CSVPassword.remove(index);
                CSVRole.remove(index);
            }
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "User deleted successfully.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "No user found.");
        }

        DelUserField.clear();
    }

    @FXML
    void onReturnAction(ActionEvent event) {
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

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}