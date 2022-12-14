package com.example.demofx;

import java.nio.file.Path;

public class Scod {
    private final int id;
    private final String cashIn;
    private final String cashOut;
    private final int line;
    private final String date;
    private final Path file;
    private final String group;

    public Scod(int id, String cashIn, String cashOut, int line, String date, Path file, String group) {
        this.id = id;
        this.cashIn = cashIn;
        this.cashOut = cashOut;
        this.line = line;
        this.date = date;
        this.file = file;
        this.group = group;
    }

    public int getId() {
        return id;
    }

    public int getLine() {
        return line;
    }

    public String getDate() {
        return date;
    }

    public Path getFile() {
        return file;
    }

    public String getCashIn() {
        return cashIn;
    }

    public String getCashOut() {
        return cashOut;
    }

    public String getGroup() {
        return group;
    }
}
