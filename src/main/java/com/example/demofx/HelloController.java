package com.example.demofx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloController {
    private final ObservableList<Scod> scodOL = FXCollections.observableArrayList();
    private int scodIdCount = 1;
    private int scodLineNumberCount = 1;

    @FXML
    private TableView<Scod> scodTable;
    @FXML
    private TableColumn<Scod, Integer> idColumn;
    @FXML
    private TableColumn<Scod, Integer> cashIn;
    @FXML
    private TableColumn<Scod, Integer> cashOut;
    @FXML
    private TableColumn<Scod, String> dateColumn;
    @FXML
    private TableColumn<Scod, String> groupColumn;
    @FXML
    private ListView<String> logFileListView;

    @FXML
    private void onOpenMenuItemClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(new Stage());

        // проверяем на наличие выбранной директории
        if (selectedDirectory == null) {
            System.out.println("No Directory selected");
        } else {
            File dir = new File(selectedDirectory.getAbsolutePath());
            // получаем все вложенные файлы в каталоге
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                // проверяем расширение файлов
                if (isXml(file)) {
                    Path path = file.toPath();
                    // читаем с потока данных
                    try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                        String currentLine;
                        while ((currentLine = reader.readLine()) != null) {
                            // заполняем ObservableList данными из потока
                            if (currentLine.contains("LEN=0179")) {
                                addToObservableListNewScod("Cash-in", path, currentLine);
                            } else if (currentLine.contains("LEN=0279")
                                    || currentLine.contains("LEN=0250")) {
                                addToObservableListNewScod("Cash-out", path, currentLine);
                            }
                            // увеличиваем счетчик номера строки
                            scodLineNumberCount++;
                        }
                        // сбрасываем счетчик номера строки
                        scodLineNumberCount = 1;
                    } catch (IOException e) {
                        System.out.println("Handle file I/O exception");
                    }
                }
            }
        }
        // заполняем таблицу данными из ObservableList
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        cashIn.setCellValueFactory(new PropertyValueFactory<>("cashIn"));
        cashOut.setCellValueFactory(new PropertyValueFactory<>("cashOut"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("group"));

        scodTable.setItems(scodOL);

        TableColumn<Scod, String> tc = (TableColumn<Scod, String>) scodTable.getColumns().get(4);
        tc.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (Objects.equals(item, "1")) {
                        setText("medium");
                        setStyle("""
                                -fx-background-color: #FFE1B3;
                                -fx-border-color: #FFFFFF;
                                """);
                    } else if (Objects.equals(item, "2")) {
                        setText("high");
                        setStyle("""
                                -fx-background-color: #FFB3B3;
                                -fx-border-color: #FFFFFF;
                                """);
                    } else {
                        setStyle("");
                    }
                }
            }
        });

//        scodTable.setRowFactory((param) -> new TableRow<>() {
//            protected void updateItem(Scod item, boolean empty) {
//                super.updateItem(item, empty);
//
//                if (item == null || empty) {
//                    setStyle("");
//                } else {
//                    if (Objects.equals(item.getGroup(), "1")) {
//                        setStyle("-fx-background-color: #FFE1B3;");
//                    } else if (Objects.equals(item.getGroup(), "2")) {
//                        setStyle("-fx-background-color: #FFB3B3;");
//                    } else if (item.) {
//
//                    } else {
//                        setStyle("");
//                    }
//                }
//            }
//        });

    }

    @FXML
    private void openLogFile() {
        TableView.TableViewSelectionModel<Scod> sm = scodTable.getSelectionModel();
        ObservableList<Scod> tableViewOL = sm.getSelectedItems();
        Path path = null;
        int lineNumber = 0;

        try {
            path = tableViewOL.get(0).getFile();
            lineNumber = tableViewOL.get(0).getLine() - 1;
            sm.clearAndSelect(sm.getSelectedIndex());
        } catch (Exception e) {
            sm.clearSelection();
        }

        ObservableList<String> logFileOL = FXCollections.observableArrayList();

        try {
            if (path != null) {
                try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    String currentLine;
                    while ((currentLine = reader.readLine()) != null) {
                        logFileOL.add(currentLine);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Handle file I/O exception");
        }

        logFileListView.setItems(logFileOL);
        logFileListView.scrollTo(lineNumber);
        logFileListView.getSelectionModel().select(lineNumber);
    }

    private boolean isXml(File file) {
        String ex = ".XML";
        int index = file.getName().length() - ex.length();
        String fileEx = file.getName().substring(index);

        return fileEx.toUpperCase().equals(ex);
    }

    private void addToObservableListNewScod(String scodType, Path path, String currentLine) {
        String fileName = path.getFileName().toString();
        String yyyy = fileName.substring(0, 4);
        String mm = fileName.substring(4, 6);
        String dd = fileName.substring(6, 8);
        String date = yyyy + "-" + mm + "-" + dd;

        Pattern pattern = Pattern.compile("SCOD=.{2}");
        Matcher matcher = pattern.matcher(currentLine);
        String scod = null;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            scod = currentLine.substring(start, end);
        }

        if (scod != null) {
            try {
                int num = Integer.parseInt(scod.substring(5));
                if (num != 0) {
                    if (num == 9 || num == 12 || num == 14) {
                        if (scodType.equals("Cash-in")) {
                            addNew(String.valueOf(num), null, date, path, "1");
                        } else if (scodType.equals("Cash-out")) {
                            addNew(null, String.valueOf(num), date, path, "1");
                        }
                    } else {
                        if (scodType.equals("Cash-in")) {
                            addNew(String.valueOf(num), null, date, path, "2");
                        } else if (scodType.equals("Cash-out")) {
                            addNew(null, String.valueOf(num), date, path, "2");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                addNew(null, null, date, path, null);
            }
        }
    }

    private void addNew(String cashIn, String cashOut, String date, Path path, String group) {
        scodOL.add(new Scod(scodIdCount, cashIn, cashOut, scodLineNumberCount, date, path, group));
        scodIdCount++;
    }
}
