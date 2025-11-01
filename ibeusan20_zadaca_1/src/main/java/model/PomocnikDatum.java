package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Klasa za parsiranje datuma i vremena u hrvatskom formatu.
 * Koristi se pri učitavanju podataka iz datoteka.
 *
 * Svaka metoda:
 *  - pokušava prepoznati više hrvatskih formata,
 *  - standardizira vrijednost,
 *  - baca iznimku ako je unos neispravan.
 */
public class PomocnikDatum {

    // Standardni hrvatski formati
    private static final String FORMAT_DATUM = "dd.MM.yyyy.";
    private static final String FORMAT_VRIJEME = "HH:mm";
    private static final String FORMAT_DATUM_VRIJEME = "dd.MM.yyyy. HH:mm:ss";

    // --------------------------------------------------------------
    // 1) SAMO DATUM
    // --------------------------------------------------------------
    public static LocalDate procitajDatum(String tekst) {
        if (tekst == null || tekst.isBlank()) return null;

        String[] formati = { "d.M.yyyy.", "d.M.yyyy" };

        for (String f : formati) {
            try {
                LocalDate datum = LocalDate.parse(
                        tekst.trim(),
                        DateTimeFormatter.ofPattern(f)
                );
                // standardiziraj u propisani oblik
                String standard = datum.format(DateTimeFormatter.ofPattern(FORMAT_DATUM));
                return LocalDate.parse(standard,
                        DateTimeFormatter.ofPattern(FORMAT_DATUM));
            } catch (DateTimeParseException e) {
                // pokušaj sljedeći
            }
        }

        throw new IllegalArgumentException("Neispravan format datuma: '" + tekst + "'");
    }

    // --------------------------------------------------------------
    // 2) SAMO VRIJEME
    // --------------------------------------------------------------
    public static LocalTime procitajVrijeme(String tekst) {
        if (tekst == null || tekst.isBlank()) return null;

        String[] formati = { "H:mm", "HH:mm" };

        for (String f : formati) {
            try {
                LocalTime vrijeme = LocalTime.parse(
                        tekst.trim(),
                        DateTimeFormatter.ofPattern(f)
                );
                String standard = vrijeme.format(DateTimeFormatter.ofPattern(FORMAT_VRIJEME));
                return LocalTime.parse(standard,
                        DateTimeFormatter.ofPattern(FORMAT_VRIJEME));
            } catch (DateTimeParseException e) {
                // pokušaj sljedeći
            }
        }

        throw new IllegalArgumentException("Neispravan format vremena: '" + tekst + "'");
    }

    // --------------------------------------------------------------
    // 3) DATUM + VRIJEME
    // --------------------------------------------------------------
    public static LocalDateTime procitajDatumIVrijeme(String tekst) {
        if (tekst == null || tekst.isBlank()) return null;

        String[] formati = {
            "d.M.yyyy. H:mm:ss",
            "d.M.yyyy H:mm:ss",
            "d.M.yyyy. H:mm",
            "d.M.yyyy H:mm"
        };

        for (String f : formati) {
            try {
                LocalDateTime dt = LocalDateTime.parse(
                        tekst.trim(),
                        DateTimeFormatter.ofPattern(f)
                );
                // standardiziraj u propisani oblik
                String standard = dt.format(DateTimeFormatter.ofPattern(FORMAT_DATUM_VRIJEME));
                return LocalDateTime.parse(standard,
                        DateTimeFormatter.ofPattern(FORMAT_DATUM_VRIJEME));
            } catch (DateTimeParseException e) {
                // pokušaj sljedeći format
            }
        }

        throw new IllegalArgumentException("Neispravan format datuma/vremena: '" + tekst + "'");
    }
}
