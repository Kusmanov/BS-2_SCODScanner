package com.example.demofx;

import com.example.demofx.scod.SCOD;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelloController {
    @FXML
    public Button selectFolder;

    @FXML
    protected void onDirectoryChooserButtonClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = new Stage();
        File selectedDirectory = directoryChooser.showDialog(stage);
        List<File> files = new ArrayList<>();
        List<SCOD> cashInSCODs = new ArrayList<>();
        List<SCOD> cashOutSCODs = new ArrayList<>();

        if (selectedDirectory == null) {
            System.out.println("No Directory selected");
        } else {
            File dir = new File(selectedDirectory.getAbsolutePath());
            // получаем все вложенные XML файлы в каталоге и помещаем их в коллекцию
            for (File item : Objects.requireNonNull(dir.listFiles())) {
                String fileExtension = "XML";
                int beginIndex = item.getName().length() - fileExtension.length();
                String desiredExtension = item.getName().substring(beginIndex);
                if (item.isFile() && desiredExtension.toUpperCase().equals(fileExtension)) {
                    files.add(item);
                }
            }
        }
        // открываем каждый файл, ищем все SCOD и сохроняем их в коллекции: cashInSCODs и cashOutSCODs
        for (File file : files) {
            Path path = file.toPath();
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                // читаем со стрима
                String currentLine;
                int lineNumber = 1;
                while ((currentLine = reader.readLine()) != null) {
                    if (currentLine.contains("LEN=0179")) {
                        cashInSCODs.add(new SCOD(currentLine, file, lineNumber));
                    } else if (currentLine.contains("LEN=0279")) {
                        cashOutSCODs.add(new SCOD(currentLine, file, lineNumber));
                    }
                    lineNumber++;
                }
            } catch (IOException e) {
                // Handle file I/O exception
            }
        }

        for (SCOD cashInSCOD : cashInSCODs) {
            if (!cashInSCOD.getGroup().equals("GREEN")) {
                System.out.print(cashInSCOD.getFile() + ";");
                System.out.print(cashInSCOD.getLocalDate()  + ";");
                System.out.print(cashInSCOD.getGroup()  + ";");
                System.out.print(cashInSCOD.getLineNumber()  + ";");
                System.out.println(cashInSCOD.getNumber());
            }
        }

        for (SCOD cashInSCOD : cashInSCODs) {
            if (!cashInSCOD.getGroup().equals("GREEN")) {
                System.out.print(cashInSCOD.getFile() + ";");
                System.out.print(cashInSCOD.getLocalDate()  + ";");
                System.out.print(cashInSCOD.getGroup()  + ";");
                System.out.print(cashInSCOD.getLineNumber()  + ";");
                System.out.println(cashInSCOD.getNumber());
            }
        }
    }
}