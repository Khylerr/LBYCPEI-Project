package com.example.login_test;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LoginController {

    @FXML
    private ImageView icon_image;

    @FXML
    private Button login_button;

    @FXML
    private PasswordField password_field;

    @FXML
    private TextField username_field;

    @FXML
    private ImageView techshop_image;

    private List<User> users;
    private Stage stage;
    private Scene scene;
    private Parent root;
    public String roleToSend;

    // Define the path to the users.csv file in the user's home directory
    private String filepath = System.getProperty("user.home") + File.separator + "users.csv";

    public LoginController() {
        initializeFile();
        loadUsers();
    }

    @FXML
    private void initialize() {
        loadUsers();
        loadImages();
    }

    private void initializeFile() {
        File file = new File(filepath);

        if (!file.exists()) {
            try (InputStream in = getClass().getResourceAsStream("/users.csv"); // Resource file
                 OutputStream out = new FileOutputStream(file)) {

                if (in == null) {
                    throw new FileNotFoundException("Resource file /users.csv not found in classpath");
                }

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                System.out.println("File copied successfully to: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error copying file: " + e.getMessage());
            }
        } else {
            System.out.println("File already exists: " + file.getAbsolutePath());
        }
    }

    private void loadUsers() {
        users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    users.add(new User(data[0], data[1], data[2]));
                }
            }
            System.out.println("Users loaded: " + users.size());
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
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
            RoleData.setRole(user.getRole());
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("cashiermainmenu.fxml"));
                Parent newRoot = loader.load();

                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                double newWidth = 820;
                double newHeight = 500;

                SceneTransitionUtil.switchSceneWithFadeAndScale(stage, newRoot, newWidth, newHeight);

            } catch (IOException e) {
                e.printStackTrace();
            }
            username_field.clear();
            password_field.clear();

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

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setRole(String role) {
        this.roleToSend = role;
    }

    public String getRole() {
        return roleToSend;
    }
}
