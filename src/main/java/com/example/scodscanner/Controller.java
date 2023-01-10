package com.example.scodscanner;

import com.example.scodscanner.functionality.LogFileOpener;
import com.example.scodscanner.functionality.LogFilesScanner;
import com.example.scodscanner.functionality.ShowAboutInfo;
import com.example.scodscanner.objects.Scod;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Controller {
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
    private void onSelectMenuItemClick() {
        LogFilesScanner.execute(idColumn, cashInColumn, cashOutColumn, dateColumn, groupColumn, timeColumn, scodTable);
    }

    @FXML
    private void onTableViewLineClick() {
        LogFileOpener.execute(logFileListView, scodTable);
    }

    @FXML
    private void onQuitMenuItemClick() {
        Platform.exit();
    }

    @FXML
    private void onAboutMenuItemClick() {
        ShowAboutInfo.execute();
    }
}
