package outils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateHelper {
    public static String aujourdhui() {
        return LocalDate.now().toString();
    }

    public static String formater(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
