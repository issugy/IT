package com.fedorenko.db.controllers;

import com.fedorenko.db.models.Base;
import com.fedorenko.db.models.Table;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class TableListController {
    private final Base base;
    @FXML
    public TableColumn<Table, String> tables;

    @FXML
    public TableView<Table> tableView;

    @FXML
    public void initialize(){
        showTables();
    }

    public TableListController(Base base){
        this.base = base;
    }

    private void showTables() {
        tables.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        ObservableList<Table> tables = FXCollections.observableArrayList();
        tables.addAll(base.getTables().values());
        tableView.setItems(tables);
    }


    public void createTable(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/createTable.fxml"));
        loader.setControllerFactory(type -> {
            if (type == TableCreateController.class) {
                return new TableCreateController(base);
            }
            try {
                return type.getConstructor().newInstance();
            } catch (Exception exc) {
                throw new RuntimeException(exc);
            }
        });

        Stage stage = new Stage();
        stage.setTitle("Create table");
        stage.setMinWidth(360);
        stage.setMinHeight(310);
        stage.setResizable(false);
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)mouseEvent.getSource()).getScene().getWindow());

        stage.setOnHidden(e -> {
            showTables();
        });

        stage.show();
    }

    public void deleteTable(){
        Table table = tableView.getSelectionModel().getSelectedItem();
        if (table != null) {
            base.deleteTable(table);
            showTables();
        } else {
            noTableSelectedMessage();
        }
    }

    public void selectTable(MouseEvent mouseEvent) throws IOException {
        Table table = tableView.getSelectionModel().getSelectedItem();
        if (table != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/rowList.fxml"));
            loader.setControllerFactory(type -> {
                if (type == RowListController.class) {
                    return new RowListController(table);
                }
                try {
                    return type.getConstructor().newInstance();
                } catch (Exception exc) {
                    throw new RuntimeException(exc);
                }
            });

            Stage stage = new Stage();
            stage.setTitle("Table [" + table.getName() + "]");
            stage.setMinHeight(300);
            stage.setMinWidth(840);
            stage.setScene(new Scene(loader.load(), 840, 300));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node)mouseEvent.getSource()).getScene().getWindow());
            stage.show();
        } else {
            noTableSelectedMessage();
        }
    }

    public void saveTable(MouseEvent mouseEvent){
        File file = new File(getClass().getClassLoader().getResource("databases/").getFile());
        base.saveToFile(file);
        ((Node) mouseEvent.getSource()).getScene().getWindow().hide();
    }

    private void noTableSelectedMessage() {
        Warning.show("No table selected. Please select a table");
    }
}
