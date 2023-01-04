package com.example.demofx;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Test {
    public static void main(String[] args) {
        File selectedDirectory = new File("D:\\TEMP\\PROBASE\\LOG");
        int count = 0;

        // проверяем на наличие выбранной директории
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
                            count++;
                            System.out.println(count);
                        } else if (currentLine.contains("LEN=0279")
                                || currentLine.contains("LEN=0250")) {
                            count++;
                            System.out.println(count);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Handle file I/O exception");
                }
            }
        }
    }

    private static boolean isXml(File file) {
        String ex = ".XML";
        int index = file.getName().length() - ex.length();
        String fileEx = file.getName().substring(index);

        return fileEx.toUpperCase().equals(ex);
    }
}
