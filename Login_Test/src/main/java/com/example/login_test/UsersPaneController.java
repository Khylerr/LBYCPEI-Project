package com.example.login_test;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.io.*;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

import java.io.FileWriter;
import java.util.Objects;


public class UsersPaneController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    ArrayList<String> CSVUsername = new ArrayList<>();
    ArrayList<String> CSVPassword = new ArrayList<>();
    ArrayList<String> CSVRole = new ArrayList<>();

    String filepath =  "C:\\Users\\Khyler\\IdeaProjects\\LBYCPEI-Project-main\\Login_Test\\src\\main\\resources\\users.csv";
      //MAKE SURE TO CHANGE FILEPATH!!!!  -Khyler

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

    public void UserCSVRead()
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

                CSVUsername.add(row[0]);
                CSVPassword.add(row[1]);
                CSVRole.add(row[2]);
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

    public void loadUserData() {
        UserCSVRead();
        ObservableList<User> users = FXCollections.observableArrayList();
        for (int i = 0; i < CSVUsername.size(); i++) {
            users.add(new User(CSVUsername.get(i), CSVPassword.get(i), CSVRole.get(i)));
        }

        TreeItem<User> root = new TreeItem<>(new User("Root", "", ""));
        for (User user : users) {
            root.getChildren().add(new TreeItem<>(user));
        }

        userTableView.setRoot(root);
        userTableView.setShowRoot(false);
    }

    public void puttoArray()
    {
        UserCSVRead();
        ObservableList<User> users = FXCollections.observableArrayList();
        for (int i = 0; i < CSVUsername.size(); i++)
        {
            users.add(new User(CSVUsername.get(i), CSVPassword.get(i), CSVRole.get(i)));
        }


    }



    @FXML
    void onAddUserAction(ActionEvent event)
    {
        String usernameToAdd = AddUserField.getText();
        String passwordToAdd = AddPassField.getText();
        String roleToAdd = AddRoleField.getText();
        try {
            FileWriter writer = new FileWriter(filepath, true);
            writer.append(usernameToAdd);
            writer.append(",");
            writer.append(passwordToAdd);
            writer.append(",");
            writer.append(roleToAdd);
            writer.append("\n");
            writer.close();

            User newUser = new User(usernameToAdd, passwordToAdd, roleToAdd);
            TreeItem<User> newUserItem = new TreeItem<>(newUser);
            userTableView.getRoot().getChildren().add(newUserItem);

            AddUserField.clear();
            AddPassField.clear();
            AddRoleField.clear();

            CSVUsername.add(usernameToAdd);
            CSVPassword.add(passwordToAdd);
            CSVRole.add(roleToAdd);

            for(int i = 0; i < CSVUsername.size(); i++)
            {
                System.out.println(CSVUsername.get(i));
            }




        }
        catch (IOException e) {
            e.printStackTrace();
        }


        showAlert(Alert.AlertType.INFORMATION, "Added", "User added successfully");
    }


    @FXML
    void onDelUserAction(ActionEvent event)
    {

        String usernameToDelete = DelUserField.getText();
        int index = 0;

        for(int i = 0; i < CSVUsername.size(); i++)
        {
            if (Objects.equals(usernameToDelete, CSVUsername.get(i)))
            {
                index = i+1;
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "User deleted successfully.");

                break;
            }


        }

        for(int i = 0; i < CSVUsername.size(); i++)
        {
           System.out.println(CSVUsername.get(i));
        }

        System.out.print(index);

        delete(filepath, index);

        if (index == 0) {
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "No user found.");
        }


        DelUserField.clear();
        index = 0;

        ObservableList<TreeItem<User>> items = userTableView.getRoot().getChildren();
        for (TreeItem<User> item : items) {
            if (item.getValue().getUsername().equals(usernameToDelete)) {
                items.remove(item);
                break;
            }
        }
        CSVUsername.clear();
        CSVPassword.clear();
        CSVRole.clear();

        puttoArray();


    }

    @FXML
    void onReturnAction(ActionEvent event) {
        initialize();

        try {

            root = FXMLLoader.load(getClass().getResource("cashiermainmenu.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

   public void delete(String filepath, int deleteLine)
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
