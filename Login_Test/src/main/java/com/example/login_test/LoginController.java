package com.example.login_test;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField; // Updated import
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginController {

    @FXML
    private ImageView icon_image;

    @FXML
    private Button login_button;

    @FXML
    private PasswordField password_field; // Updated field type

    @FXML
    private TextField username_field;

    @FXML
    private ImageView techshop_image;

    private List<User> users;
    private Stage stage;
    private Scene scene;
    private Parent root;

    public LoginController() {
        try {
            this.users = CSVReader.readUsersFromCSV(getClass().getResourceAsStream("/users.csv"));
            if (this.users != null) {
                System.out.println("Users loaded: " + users.size());
            } else {
                System.err.println("No users loaded.");
            }
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            this.users = new ArrayList<>(); // Avoid null pointer
        }
    }

    @FXML
    private void initialize() {
       
        loadImages();

    }

    private void loadImages() {
        try {
            Image userIcon = new Image(getClass().getResourceAsStream("/assets/user_icon.png"));
            icon_image.setImage(userIcon);
            Image techShopImage = new Image(getClass().getResourceAsStream("/assets/tech_shop_image.jpg"));
            techshop_image.setImage(techShopImage);
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void handleLogin(ActionEvent event) {
        if (users == null) {
            showAlert(AlertType.ERROR, "Error", "User data not loaded.");
            return;
        }


        String username = username_field.getText();
        String password = password_field.getText();

        User user = findUserByUsername(username);

        if (user != null && user.getPassword().equals(password)) {

            String role = user.getRole();
            try {
                root = FXMLLoader.load(getClass().getResource("cashiermainmenu.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Inventory Manager");

                stage.show();
                showAlert(AlertType.INFORMATION, "Login Successful", "Welcome " + role);

            } catch (IOException e) {
                e.printStackTrace();
            }
            username_field.clear();
            password_field.clear();
            username = "";
            password = "";


        } else {
            showAlert(AlertType.ERROR, "Login Failed", "Invalid username or password");
        }
    }

    private User findUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private void openCashierMainMenu(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cashiermainmenu.fxml"));
            Parent root = loader.load();

            InventoryCashier controller = loader.getController();
            controller.setStage(stage);

            Scene scene = new Scene(root, 800, 480);
            stage.setScene(scene);
            stage.setTitle("Cashier Main Menu");
            stage.show();

            showAlert(AlertType.INFORMATION, "Login Successful", "Welcome " + role);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Could not open main menu.");
        }
    }

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
