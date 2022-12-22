package com.fedorenko.db.controllers;

import com.fedorenko.db.models.Base;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseListController {
    @FXML
    public TableColumn<Base, String> databases;

    @FXML
    public TableView<Base> databaseView;

    @FXML
    public void initialize() throws IOException {
        showDatabases();
    }

    private void showDatabases() throws IOException {
        databases.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        databaseView.setItems(getDatabaseList());
    }

    private ObservableList<Base> getDatabaseList() throws IOException {
        List<String> filenames = getResourceFiles("/databases");
        ObservableList<Base> bases = FXCollections.observableArrayList();
        for(String name : filenames){
            if(name.endsWith(".json")) {
                try {
                    Base base = new Base(name.replace(".json", ""));
                    File file = new File(getClass().getClassLoader().getResource("databases/" + name).getFile());
                    base.loadDataFromFile(file);
                    bases.add(base);
                }
                catch (Exception ex) {
                    System.out.print(ex.getMessage());
                }
            }
        }
        return bases;
    }

    private List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();

        try (
                InputStream in = getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }

        return filenames;
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public void createDatabase(MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/views/createDatabase.fxml"));
        stage.setTitle("Create database");
        stage.setMinHeight(80);
        stage.setMinWidth(300);
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)mouseEvent.getSource()).getScene().getWindow());
        stage.setOnHidden(e -> {
            try {
                showDatabases();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        stage.show();
    }

    public void deleteDatabase() throws IOException {
        Base base = databaseView.getSelectionModel().getSelectedItem();
        if (base != null) {
            deleteDatabase(base);
            showDatabases();
        } else {
            noDatabaseSelectedMessage();
        }
    }

    public void selectDatabase(MouseEvent mouseEvent) throws IOException {
        Base base = databaseView.getSelectionModel().getSelectedItem();
        if (base != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/tableList.fxml"));
            loader.setControllerFactory(type -> {
                if (type == TableListController.class) {
                    return new TableListController(base);
                }
                try {
                    return type.getConstructor().newInstance();
                } catch (Exception exc) {
                    throw new RuntimeException(exc);
                }
            });

            Stage stage = new Stage();
            stage.setTitle("Tables");
            stage.setMinHeight(300);
            stage.setMinWidth(400);
            stage.setScene(new Scene(loader.load(), 400, 300));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node)mouseEvent.getSource()).getScene().getWindow());
            stage.show();
        } else {
            noDatabaseSelectedMessage();
        }
    }

    private void deleteDatabase(Base base){
        File file = new File(getClass().getClassLoader().getResource(
                "databases/" + base.getName() + ".json").getFile());

        file.delete();
    }

    private void noDatabaseSelectedMessage() {
        Warning.show("No database selected. Please select a database in the table.");
    }
}
