package com.example.login_test;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class PaneController {

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleInventoryButton() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("cashiermainmenu.fxml"));
        stage.setScene(new Scene(root, 800, 480));
    }

    @FXML
    private void handleSalesButton() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("sales.fxml"));
        stage.setScene(new Scene(root, 800, 480));
    }
}