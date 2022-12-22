package com.fedorenko.db.controllers;

import com.fedorenko.db.models.Base;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.beans.Expression;
import java.io.File;
import java.net.URL;

public class DatabaseCreateController {
    @FXML
    public TextField textField;

    @FXML
    public void submit(MouseEvent mouseEvent) {
        try {
            createDatabase();
            close(mouseEvent);
        }
        catch (Exception e){
            Warning.show(e);
        }
    }

    @FXML
    public void close(MouseEvent mouseEvent) {
        ((Node) mouseEvent.getSource()).getScene().getWindow().hide();
    }

    private void createDatabase() throws Exception {
        Base base = new Base(textField.getText());
        File file = new File(getClass().getClassLoader().getResource("databases/").getFile());
        File dbFile = new File(file + "/" + base.getName() + ".json");
        if(!dbFile.exists())
            base.saveToFile(file);
        else
            throw new Exception("Database with this name already exists");
    }
}
