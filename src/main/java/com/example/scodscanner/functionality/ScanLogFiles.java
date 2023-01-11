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

    private static void addToObservableListNewScod(String type, Path path, String currentLine, String previousLine) {
        String date;
        String scod = null;
        String ecod = null;
        String timeCashIn = null;
        String timeCashOut = null;

        String fileName = path.getFileName().toString();
        String yyyy = fileName.substring(0, 4);
        String mm = fileName.substring(4, 6);
        String dd = fileName.substring(6, 8);
        date = yyyy + "-" + mm + "-" + dd;

        Pattern scodPattern = Pattern.compile("SCOD=.{2}");

        Matcher scodMatcher = scodPattern.matcher(currentLine);
        if (scodMatcher.find()) {
            int start = scodMatcher.start();
            int end = scodMatcher.end();
            scod = currentLine.substring(start, end);
        }

        Pattern timePattern = Pattern.compile("time=\"\\d{2}:\\d{2}:\\d{2}\"");

        Matcher prevTimeMatcher = timePattern.matcher(previousLine);
        if (prevTimeMatcher.find()) {
            int start = prevTimeMatcher.start() + 6;
            int end = prevTimeMatcher.end() - 1;
            timeCashIn = previousLine.substring(start, end);
        }

        Matcher curTimeMatcher = timePattern.matcher(currentLine);
        if (curTimeMatcher.find()) {
            int start = curTimeMatcher.start() + 6;
            int end = curTimeMatcher.end() - 1;
            timeCashOut = currentLine.substring(start, end);
        }

        Pattern ecodPattern = Pattern.compile("ECOD=.{4}");

        Matcher ecodMatcher = ecodPattern.matcher(currentLine);
        if (ecodMatcher.find()) {
            int start = ecodMatcher.start();
            int end = ecodMatcher.end();
            ecod = currentLine.substring(start, end);
        }

        if (scod != null) {
            if (!scod.equals("SCOD=00")) {
                if (scod.equals("SCOD=09") || scod.equals("SCOD=12") || scod.equals("SCOD=14")) {
                    addNew(scod, date, path, "medium", timeCashIn, timeCashOut, ecod, type);
                } else {
                    addNew(scod, date, path, "high", timeCashIn, timeCashOut, ecod, type);
                }
            }
        }
    }

    private static void addNew(String scod, String date, Path path, String group, String timeCashIn, String timeCashOut, String ecod, String type) {
        if (type.equals("Cash-in")) {
            scodOL.add(new Scod(scodIdCount, scod, null, scodLineNumberCount, date, path, group, timeCashIn, ecod));
        } else if (type.equals("Cash-out")) {
            scodOL.add(new Scod(scodIdCount, null, scod, scodLineNumberCount, date, path, group, timeCashOut, null));
        }
        scodIdCount++;
    }
}
