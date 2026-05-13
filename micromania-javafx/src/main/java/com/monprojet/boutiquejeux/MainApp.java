package com.monprojet.boutiquejeux;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApp extends Application {

    public static Stage primaryStage;

    /** Résolution des ressources compatible JavaFX 23 + module system */
    public static java.net.URL resource(String path) {
        return Objects.requireNonNull(
            MainApp.class.getResource(path),
            "Ressource introuvable : " + path
        );
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showLogin();
        stage.setTitle("BoutiqueJeux — Espace Employé");
        stage.setResizable(true);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void showLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(resource("/fxml/Login.fxml"));
        Scene scene = new Scene(loader.load(), 480, 540);
        scene.getStylesheets().add(resource("/css/dark-theme.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
    }

    public static void showMain() throws IOException {
        FXMLLoader loader = new FXMLLoader(resource("/fxml/Main.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 760);
        scene.getStylesheets().add(resource("/css/dark-theme.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
