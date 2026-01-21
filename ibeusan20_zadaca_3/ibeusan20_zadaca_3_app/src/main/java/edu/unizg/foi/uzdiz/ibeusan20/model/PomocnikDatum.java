package edu.unizg.foi.uzdiz.ibeusan20.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Pomoćna klasa za parsiranje datuma i vremena iz tekstualnog zapisa.
 * <p>
 * Metode pokušavaju parsirati ulaz prema više podržanih formata. Ako parsiranje ne uspije ili je
 * ulaz prazan, vraća se null.
 * </p>
 */
public class PomocnikDatum {

  public static LocalDate procitajDatum(String tekst) {
    if (tekst == null || tekst.isBlank())
      return null;

    String[] formati = {"d.M.yyyy.", "d.M.yyyy"};

    for (String f : formati) {
      try {
        LocalDate datum = LocalDate.parse(tekst.trim(), DateTimeFormatter.ofPattern(f));
        return datum;
      } catch (DateTimeParseException e) {
      }
    }
    return null;
  }

  public static LocalTime procitajVrijeme(String tekst) {
    if (tekst == null || tekst.isBlank())
      return null;

    String[] formati = {"H:mm", "HH:mm"};

    for (String f : formati) {
      try {
        return LocalTime.parse(tekst.trim(), DateTimeFormatter.ofPattern(f));
      } catch (DateTimeParseException e) {
      }
    }
    return null;
  }

  public static LocalDateTime procitajDatumIVrijeme(String tekst) {
    if (tekst == null || tekst.isBlank())
      return null;

    String[] formati = {"d.M.yyyy. H:mm:ss", "d.M.yyyy H:mm:ss", "d.M.yyyy. H:mm", "d.M.yyyy H:mm"};

    for (String f : formati) {
      try {
        return LocalDateTime.parse(tekst.trim(), DateTimeFormatter.ofPattern(f));
      } catch (DateTimeParseException e) {
      }
    }
    return null;
  }
}
