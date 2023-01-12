package com.example.scodscanner.functionality;

import com.example.scodscanner.objects.Scod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class OpenLogFile {
    public static void execute(ListView<String> logFileListView, TableView<Scod> scodTable) {
        TableView.TableViewSelectionModel<Scod> sm = scodTable.getSelectionModel();
        ObservableList<Scod> tableViewOL = sm.getSelectedItems();

        if (!tableViewOL.isEmpty()) {
            Path path = tableViewOL.get(0).getFile();
            int lineNumber = tableViewOL.get(0).getLine();

            sm.clearAndSelect(sm.getSelectedIndex());
            ObservableList<String> logFileOL = FXCollections.observableArrayList();

            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    logFileOL.add(currentLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            logFileListView.setItems(logFileOL);
            logFileListView.scrollTo(lineNumber);
            logFileListView.getSelectionModel().select(lineNumber);
        }
    }
}
