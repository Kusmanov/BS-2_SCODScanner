package com.example.demofx;

import javafx.application.Platform;
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
    private int scodIdCount;
    private int scodLineNumberCount;

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
    private TableColumn<Scod, String> timeColumn;

    @FXML
    private void onOpenMenuItemClick() {
        scodOL.clear();
        scodIdCount = 1;
        scodLineNumberCount = 0;

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
                        String previousLine = null;
                        while ((currentLine = reader.readLine()) != null) {
                            // заполняем ObservableList данными из потока
                            if (currentLine.contains("LEN=0179")) {
                                addToObservableListNewScod("Cash-in", path, currentLine, previousLine);
                            } else if (currentLine.contains("LEN=0279")
                                    || currentLine.contains("LEN=0250")) {
                                addToObservableListNewScod("Cash-out", path, currentLine, previousLine);
                            }
                            // увеличиваем счетчик номера строки
                            scodLineNumberCount++;
                            // сохраняем строку перед выходом, чтобы использовать как предыдущую
                            previousLine = currentLine;
                        }
                        // сбрасываем счетчик номера строки
                        scodLineNumberCount = 0;
                    } catch (IOException e) {
                        System.out.println("Handle file I/O exception");
                    }
                }
            }
        }
        // заполняем таблицу данными из ObservableList
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        cashIn.setCellValueFactory(new PropertyValueFactory<>("cashIn"));
        cashOut.setCellValueFactory(new PropertyValueFactory<>("cashOut"));
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("group"));

        scodTable.setItems(scodOL);

        TableColumn<Scod, String> tc;
        tc = (TableColumn<Scod, String>) scodTable.getColumns().get(5);

        tc.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText("");
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
                        setText("");
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
    private void onTableViewLineClick() {
        LogFileOpen.execute(logFileListView, scodTable);
    }

    @FXML
    private void onQuitMenuItemClick() {
        Platform.exit();
    }

    private boolean isXml(File file) {
        String ex = ".XML";
        int index = file.getName().length() - ex.length();
        String fileEx = file.getName().substring(index);

        return fileEx.toUpperCase().equals(ex);
    }

    private void addToObservableListNewScod(String scodType, Path path, String currentLine, String previousLine) {
        String fileName = path.getFileName().toString();
        String yyyy = fileName.substring(0, 4);
        String mm = fileName.substring(4, 6);
        String dd = fileName.substring(6, 8);
        String date = yyyy + "-" + mm + "-" + dd;

        Pattern pattern1 = Pattern.compile("SCOD=.{2}");
        Matcher matcher1 = pattern1.matcher(currentLine);
        String scod = null;
        while (matcher1.find()) {
            int start = matcher1.start();
            int end = matcher1.end();
            scod = currentLine.substring(start, end);
        }

        Pattern pattern2 = Pattern.compile("time=\"\\d{2}:\\d{2}:\\d{2}\"");
        Matcher matcher2 = pattern2.matcher(previousLine);
        String timeCashIn = null;
        while (matcher2.find()) {
            int start = matcher2.start() + 6;
            int end = matcher2.end() - 1;
            timeCashIn = previousLine.substring(start, end);
        }

        Pattern pattern3 = Pattern.compile("time=\"\\d{2}:\\d{2}:\\d{2}\"");
        Matcher matcher3 = pattern3.matcher(currentLine);
        String timeCashOut = null;
        while (matcher3.find()) {
            int start = matcher3.start() + 6;
            int end = matcher3.end() - 1;
            timeCashOut = currentLine.substring(start, end);
        }

        if (scod != null) {
            try {
                int num = Integer.parseInt(scod.substring(5));
                if (num != 0) {
                    if (num == 9 || num == 12 || num == 14) {
                        if (scodType.equals("Cash-in")) {
                            addNew(String.valueOf(num), null, date, path, "1", timeCashIn);
                        } else if (scodType.equals("Cash-out")) {
                            addNew(null, String.valueOf(num), date, path, "1", timeCashOut);
                        }
                    } else {
                        if (scodType.equals("Cash-in")) {
                            addNew(String.valueOf(num), null, date, path, "2", timeCashIn);
                        } else if (scodType.equals("Cash-out")) {
                            addNew(null, String.valueOf(num), date, path, "2", timeCashOut);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                addNew(null, null, null, path, null, null);
            }
        }
    }

    private void addNew(String cashIn, String cashOut, String date, Path path, String group, String time) {
        scodOL.add(new Scod(scodIdCount, cashIn, cashOut, scodLineNumberCount, date, path, group, time));
        scodIdCount++;
    }
}
