package com.fedorenko.db.controllers;

import com.fedorenko.db.models.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class RowCreateController {
    private final Table table;
    private final Row row;

    @FXML
    public VBox fields;

    @FXML
    public void initialize() {
        for (int i = 0; i < table.getAttributes().size(); i++) {
            Attribute attribute = table.getAttributes().get(i);
            Object object = row != null ? row.getValues().get(i) : null;
            addField(attribute.getName(), attribute.getType(), object);
        }
    }

    public RowCreateController(Table table, Row row){
        this.table = table;
        this.row = row;
    }

    private void addField(String name, Type type, Object object) {
        Label fieldName = new Label(name);
        fieldName.setPrefSize(180.0, 25.0);

        TextField textField = new TextField();
        textField.setPromptText(type.toString());
        textField.setPrefSize(195.0, 25.0);
        if(object != null)
            textField.setText(object.toString());

        HBox hbox = new HBox();
        hbox.setSpacing(30.0);
        hbox.getChildren().addAll(fieldName, textField);

        fields.getChildren().add(hbox);
    }

    public void submit(MouseEvent mouseEvent)  {
        List<String> textDataFromFields = getDataFromForm();
        try {
            List<Object> values = getObjectsByText(textDataFromFields);
            if(row == null)
                table.addRow(values);
            else
                table.updateRow(row, values);

            close(mouseEvent);
        } catch (NumberFormatException e) {
            Warning.show(e);
        }
    }

    private List<String> getDataFromForm() {
        List<String> dataFromTextFields = new ArrayList<>();
        for (Node node : fields.getChildren()) {
            List<Node> hBox = ((HBox) node).getChildren();
            if (!hBox.isEmpty()) {
                dataFromTextFields.add(((TextField) hBox.get(1)).getCharacters().toString());
            }
        }
        return dataFromTextFields;
    }

    private List<Object> getObjectsByText(List<String> data) throws NumberFormatException {
        List<Object> parsedObjects = new ArrayList<>();
        List<Attribute> attributes = table.getAttributes();
        for (int column = 0; column < data.size(); column++) {
            Type cellType = attributes.get(column).getType();
            String cellColumn = attributes.get(column).getName();
            String cellData = data.get(column);
            try {
                parsedObjects.add(TypeManager.parseObjectByType(cellData, cellType));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Expected " + cellType + " value in [" +
                        cellColumn + "] column but " + cellData + " found");
            }
        }
        return parsedObjects;
    }


    public void close(MouseEvent mouseEvent) {
        ((Node) mouseEvent.getSource()).getScene().getWindow().hide();
    }
}
