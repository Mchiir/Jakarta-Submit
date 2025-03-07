package home.jakartasubmit.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Dates {
    private Dates() {}

    public static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
}
