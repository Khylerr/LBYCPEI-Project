package com.example.login_test;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;



public class SceneTransitionUtil {

    public static void switchSceneWithFadeAndScale(Stage stage, Parent newRoot) {
        Scene currentScene = stage.getScene();
        if (currentScene == null) {
            stage.setScene(new Scene(newRoot));
            stage.show();
            return;
        }

        // Use current scene dimensions if not specified
        switchSceneWithFadeAndScale(stage, newRoot, currentScene.getWidth(), currentScene.getHeight());
    }

    public static void switchSceneWithFadeAndScale(Stage stage, Parent newRoot, double newWidth, double newHeight) {
        Scene currentScene = stage.getScene();

        if (currentScene == null) {
            stage.setScene(new Scene(newRoot, newWidth, newHeight));
            stage.show();
            return;
        }

        // Create the new scene
        Scene newScene = new Scene(newRoot, newWidth, newHeight);

        // Fade out current content
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentScene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // Fade in new content
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newRoot);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Play fade out transition and resize in parallel
        ParallelTransition parallelTransition = new ParallelTransition(fadeOut);
        parallelTransition.setOnFinished(event -> {
            stage.setScene(newScene);
            fadeIn.play();
        });

        // Add a listener to manually resize the stage
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Custom resize logic if needed
        });
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            // Custom resize logic if needed
        });

        // Set initial size and perform smooth transition
        stage.setWidth(currentScene.getWidth());
        stage.setHeight(currentScene.getHeight());
        stage.setWidth(newWidth);
        stage.setHeight(newHeight);

        parallelTransition.play();
    }
}
