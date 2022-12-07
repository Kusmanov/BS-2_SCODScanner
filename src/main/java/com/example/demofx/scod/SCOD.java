package com.example.demofx.scod;

import java.io.File;
import java.time.LocalDate;

public class SCOD {
    private final String line;
    private String number;
    private String group;
    private final File file;
    private final int lineNumber;
    private final LocalDate localDate;


    public SCOD(String line, File file, int lineNumber) {
        this.line = line;

        String[] numbers = {
                "SCOD=00", "SCOD=01", "SCOD=02", "SCOD=03", "SCOD=04",
                "SCOD=05", "SCOD=06", "SCOD=07", "SCOD=08", "SCOD=09",
                "SCOD=10", "SCOD=11", "SCOD=12", "SCOD=13", "SCOD=14",
                "SCOD=15", "SCOD=16", "SCOD=17", "SCOD=18", "SCOD=19",
                "SCOD=20", "SCOD=21", "SCOD=22", "SCOD=23", "SCOD=24",
                "SCOD=25", "SCOD=26", "SCOD=27", "SCOD=28", "SCOD=29",
                "SCOD=30", "SCOD=31", "SCOD=32", "SCOD=33", "SCOD=34",
                "SCOD=35", "SCOD=36", "SCOD=37", "SCOD=38", "SCOD=39",
                "SCOD=40", "SCOD=41", "SCOD=42", "SCOD=43", "SCOD=44",
                "SCOD=45", "SCOD=46", "SCOD=47", "SCOD=48", "SCOD=49",
                "SCOD=50", "SCOD=51", "SCOD=52", "SCOD=53", "SCOD=54",
                "SCOD=55", "SCOD=56", "SCOD=57", "SCOD=58", "SCOD=59",
                "SCOD=60", "SCOD=61", "SCOD=62", "SCOD=63", "SCOD=64",
                "SCOD=65", "SCOD=66", "SCOD=67", "SCOD=68", "SCOD=69",
                "SCOD=70", "SCOD=71", "SCOD=72", "SCOD=73", "SCOD=74",
                "SCOD=75", "SCOD=76", "SCOD=77", "SCOD=78", "SCOD=79",
                "SCOD=80", "SCOD=81", "SCOD=82", "SCOD=83", "SCOD=84",
                "SCOD=85", "SCOD=86", "SCOD=87", "SCOD=88", "SCOD=89",
                "SCOD=90", "SCOD=91", "SCOD=92", "SCOD=93", "SCOD=94",
                "SCOD=95", "SCOD=96", "SCOD=97", "SCOD=98", "SCOD=99",
        };
        String[] groups = {
                "GREEN", "YELLOW", "RED"
        };

        for (String number : numbers) {
            if (line.contains(number)) {
                this.number = number;
            }
        }

        if (number != null) {
            if (number.equals("SCOD=00")) {
                group = groups[0]; // группа green
            } else if (number.equals("SCOD=09") || number.equals("SCOD=12") || number.equals("SCOD=14")) {
                group = groups[1]; // группа yellow
            } else {
                group = groups[2]; // группа red
            }
        }

        String[] fileName = file.getName().split("\\.");
        String date = fileName[0];
        localDate = LocalDate.of(
                Integer.parseInt(date.substring(0, 4)),
                Integer.parseInt(date.substring(4, 6)),
                Integer.parseInt(date.substring(6))
        );

        this.file = file;
        this.lineNumber = lineNumber;

    }

    public String getLine() {
        return line;
    }

    public String getNumber() {
        return number;
    }

    public String getGroup() {
        return group;
    }

    public File getFile() {
        return file;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }
}
