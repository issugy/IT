package com.fedorenko.db.controllers;

import javafx.scene.control.Alert;

class Warning {
    private Warning() {}

    static void show (String message, Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(Alert.AlertType.WARNING.toString());
        alert.setHeaderText(message);
        alert.setContentText(throwable.getMessage());
        alert.showAndWait();
    }

    static void show (Throwable throwable) {
        show("", throwable);
    }

    static void show (String message) {
        show(message, new Throwable());
    }
}