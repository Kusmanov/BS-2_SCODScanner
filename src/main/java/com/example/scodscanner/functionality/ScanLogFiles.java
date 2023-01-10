package com.example.scodscanner.functionality;

import com.example.scodscanner.objects.Scod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

public class ScanLogFiles {
    private static final ObservableList<Scod> scodOL = FXCollections.observableArrayList();
    private static int scodIdCount;
    private static int scodLineNumberCount;

    public static void execute(TableColumn<Scod, Integer> idColumn,
                               TableColumn<Scod, Integer> cashInColumn,
                               TableColumn<Scod, Integer> cashOutColumn,
                               TableColumn<Scod, String> dateColumn,
                               TableColumn<Scod, String> groupColumn,
                               TableColumn<Scod, String> timeColumn,
                               TableView<Scod> scodTable) {
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
            // получаем каждый вложенный файл в каталоге
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                // проверяем расширение файла
                if (isXml(file)) {
                    Path path = file.toPath();
                    // читаем файл с потока данных
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
                            // сохраняем строку перед выходом, чтобы далее использовать как предыдущую
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
        cashInColumn.setCellValueFactory(new PropertyValueFactory<>("cashIn"));
        cashOutColumn.setCellValueFactory(new PropertyValueFactory<>("cashOut"));
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
                    if (Objects.equals(item, "medium")) {
                        setText("medium");
                        setStyle("""
                                -fx-background-color: #FFE1B3;
                                -fx-border-color: #FFFFFF;
                                """);
                    } else if (Objects.equals(item, "high")) {
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
    }

    private static boolean isXml(File file) {
        String ex = ".XML";
        int index = file.getName().length() - ex.length();
        String fileEx = file.getName().substring(index);

        return fileEx.toUpperCase().equals(ex);
    }

    private static void addToObservableListNewScod(String scodType,
                                                   Path path,
                                                   String currentLine,
                                                   String previousLine) {
        String date;
        String scod = null;
        String timeCashIn = null;
        String timeCashOut = null;

        String fileName = path.getFileName().toString();
        String yyyy = fileName.substring(0, 4);
        String mm = fileName.substring(4, 6);
        String dd = fileName.substring(6, 8);
        date = yyyy + "-" + mm + "-" + dd;

        Pattern scodPattern = Pattern.compile("SCOD=.{2}");

        Matcher matcher1 = scodPattern.matcher(currentLine);
        if (matcher1.find()) {
            int start = matcher1.start();
            int end = matcher1.end();
            scod = currentLine.substring(start, end);
        }

        Pattern timePattern = Pattern.compile("time=\"\\d{2}:\\d{2}:\\d{2}\"");

        Matcher matcher2 = timePattern.matcher(previousLine);
        if (matcher2.find()) {
            int start = matcher2.start() + 6;
            int end = matcher2.end() - 1;
            timeCashIn = previousLine.substring(start, end);
        }

        Matcher matcher3 = timePattern.matcher(currentLine);
        if (matcher3.find()) {
            int start = matcher3.start() + 6;
            int end = matcher3.end() - 1;
            timeCashOut = currentLine.substring(start, end);
        }

        if (scod != null) {
            if (!scod.equals("SCOD=00")) {
                if (scod.equals("SCOD=09") || scod.equals("SCOD=12") || scod.equals("SCOD=14")) {
                    if (scodType.equals("Cash-in")) {
                        addNew(scod, null, date, path, "medium", timeCashIn);
                    } else if (scodType.equals("Cash-out")) {
                        addNew(null, scod, date, path, "medium", timeCashOut);
                    }
                } else {
                    if (scodType.equals("Cash-in")) {
                        addNew(scod, null, date, path, "high", timeCashIn);
                    } else if (scodType.equals("Cash-out")) {
                        addNew(null, scod, date, path, "high", timeCashOut);
                    }
                }
            }
        }
    }

    private static void addNew(String cashIn, String cashOut, String date, Path path, String group, String time) {
        scodOL.add(new Scod(scodIdCount, cashIn, cashOut, scodLineNumberCount, date, path, group, time));
        scodIdCount++;
    }
}
