package com.fedorenko.db.controllers;

import com.fedorenko.db.models.Base;
import com.fedorenko.db.models.Type;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Pair;

import java.util.List;

public class TableCreateController {
    private Base base;
    @FXML
    public VBox fields;

    @FXML
    public TextField tableNameField;

    @FXML
    public HBox submissionButtons;

    public TableCreateController(Base base){
        this.base = base;
    }

    public void setBase(Base base){
        this.base = base;
    }

    public void submit(MouseEvent mouseEvent) {
        try {
            createTableByForm();
            close(mouseEvent);
        } catch (Exception e) {
            Warning.show(e);
        }
    }

    private void createTableByForm() throws Exception {
        FromData formData = getDataFromForm();
        base.addTable(formData.tableName, formData.names, formData.types);
        //tableService.createTable(formData.tableName, formData.types, formData.names);
    }

    private FromData getDataFromForm() throws IllegalArgumentException {
        String tableName = tableNameField.getCharacters().toString();
        List<Type> types = FXCollections.observableArrayList();
        List<String> names = FXCollections.observableArrayList();
        for (int i = 0; i < fields.getChildren().size(); i++) {
            Pair<String, Type> dataFromLine = getDataFromHBox((HBox) fields.getChildren().get(i));
            String name = dataFromLine.getKey();
            Type type = dataFromLine.getValue();
            names.add(name);
            types.add(type);

        }

        return new FromData(tableName, names, types);
    }

    private static class FromData {
        String tableName;
        List<Type> types;
        List<String> names;

        FromData(String tableName, List<String> names, List<Type> types) {
            this.tableName = tableName;
            this.names = names;
            this.types = types;
        }
    }

    private Pair<String, Type> getDataFromHBox(HBox hBox) {
        Type type = null;
        String name = "";
        for (Node node : hBox.getChildren()) {
            if (node instanceof ComboBox) {
                type = (Type)((ComboBox)node).getValue();
            } else if (node instanceof TextField) {
                name = ((TextField)node).getText();
            }
        }
        return new Pair<>(name, type);
    }

    /*private boolean shouldBeIgnored (String name, Type type) throws IllegalArgumentException {
        boolean nameIsDefined = !name.trim().equals("");
        boolean typeIsNotNull = type != null;
        if (nameIsDefined && typeIsNotNull) {
            return false;
        } else if (nameIsDefined || typeIsNotNull) {
            throw new IllegalArgumentException("It is necessary to fill in either both fields in the line, or none");
        } else {
            return true;
        }
    }*/


    public void addField(MouseEvent mouseEvent) {
        if(fields.getChildren().size() == 10){
            Warning.show("Maximum number of attributes 10");
            return;
        }
        resizeWindow(mouseEvent);

        TextField textField = new TextField();
        textField.setPrefWidth(150.0);

        ComboBox<Type> comboBox = new ComboBox<>(FXCollections.observableArrayList(Type.values()));
        comboBox.setPrefWidth(150.0);

        HBox hbox = new HBox();
        hbox.setSpacing(30.0);
        hbox.getChildren().addAll(textField, comboBox);

        fields.getChildren().add(hbox);
    }

    private void resizeWindow(MouseEvent mouseEvent) {
        submissionButtons.setLayoutY(submissionButtons.getLayoutY());
        Window window = ((Node)mouseEvent.getSource()).getScene().getWindow();
        window.setHeight(window.getHeight() + 30);
    }

    @FXML
    public void close(MouseEvent mouseEvent) {
        ((Node) mouseEvent.getSource()).getScene().getWindow().hide();
    }
}
