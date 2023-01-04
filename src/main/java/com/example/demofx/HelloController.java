package com.example.demofx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HelloController {
    @FXML
    private TableView<Scod> scodTable;
    @FXML
    private TableColumn<Scod, Integer> idColumn;
    @FXML
    private TableColumn<Scod, Integer> cashInColumn;
    @FXML
    private TableColumn<Scod, Integer> cashOutColumn;
    @FXML
    private TableColumn<Scod, String> dateColumn;
    @FXML
    private TableColumn<Scod, String> groupColumn;
    @FXML
    private TableColumn<Scod, String> timeColumn;
    @FXML
    private ListView<String> logFileListView;


    @FXML
    private void onOpenMenuItemClick() {
        LogFilesScanner.execute( idColumn, cashInColumn, cashOutColumn, dateColumn, groupColumn, timeColumn, scodTable);
    }

    @FXML
    private void onTableViewLineClick() {
        LogFileOpener.execute(logFileListView, scodTable);
    }

    @FXML
    private void onQuitMenuItemClick() {
        Platform.exit();
    }
}
