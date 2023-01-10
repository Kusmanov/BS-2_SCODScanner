package com.example.scodscanner.functionality;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class ShowAboutInfo {
    public static void execute() {
        //Create Stage
        Stage newWindow = new Stage();
        newWindow.setTitle("About");
        //Create view in Java
        Label title = new Label("""
                SCODScanner
                Version: 1 (2023-01-09)
                Company: BS/2 Kazakhstan""");
        VBox container = new VBox(title);
        //Style title
        title.setTextAlignment(TextAlignment.CENTER);
        //Style container
        container.setPadding(new Insets(50));
        container.setAlignment(Pos.CENTER);
        //Set view in window
        newWindow.setScene(new Scene(container));
        //Launch
        newWindow.show();
    }
}