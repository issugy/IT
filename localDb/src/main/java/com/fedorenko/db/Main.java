package com.fedorenko.db;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/views/databaseList.fxml"));
        stage.setTitle("Local DBMS");
        stage.setMinHeight(300);
        stage.setMinWidth(400);
        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }
}