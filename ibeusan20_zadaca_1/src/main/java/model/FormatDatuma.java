package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Centralna pomoćna klasa za formatiranje datuma i vremena
 * u hrvatskom standardnom formatu.
 */
public class FormatDatuma {

    private static final String FORMAT_DATUM = "dd.MM.yyyy.";
    private static final String FORMAT_VRIJEME = "HH:mm:ss";
    private static final String FORMAT_DATUM_VRIJEME = "dd.MM.yyyy. HH:mm:ss";

    private static final DateTimeFormatter DF_DATUM =
            DateTimeFormatter.ofPattern(FORMAT_DATUM);
    private static final DateTimeFormatter DF_VRIJEME =
            DateTimeFormatter.ofPattern(FORMAT_VRIJEME);
    private static final DateTimeFormatter DF_DATUM_VRIJEME =
            DateTimeFormatter.ofPattern(FORMAT_DATUM_VRIJEME);

    private FormatDatuma() {} // privatni konstruktor — ne može se instancirati

    public static String formatiraj(LocalDate datum) {
        if (datum == null) return "";
        return datum.format(DF_DATUM);
    }

    public static String formatiraj(LocalTime vrijeme) {
        if (vrijeme == null) return "";
        return vrijeme.format(DF_VRIJEME);
    }

    public static String formatiraj(LocalDateTime datumVrijeme) {
        if (datumVrijeme == null) return "";
        return datumVrijeme.format(DF_DATUM_VRIJEME);
    }
}
