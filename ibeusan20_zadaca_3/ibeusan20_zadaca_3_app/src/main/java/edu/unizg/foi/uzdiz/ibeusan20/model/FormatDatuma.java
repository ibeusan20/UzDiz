package edu.unizg.foi.uzdiz.ibeusan20.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Pomoćna klasa za formatiranje datuma i vremena u standardizirani hrvatski format.
 * <p>
 * Koristi se u ispisima i izvještajima aplikacije.
 * </p>
 */
public class FormatDatuma {
  private static final String FORMAT_DATUM = "dd.MM.yyyy.";
  private static final String FORMAT_VRIJEME = "HH:mm:ss";
  private static final String FORMAT_DATUM_VRIJEME = "dd.MM.yyyy. HH:mm:ss";
  private static final DateTimeFormatter DF_DATUM = DateTimeFormatter.ofPattern(FORMAT_DATUM);
  private static final DateTimeFormatter DF_VRIJEME = DateTimeFormatter.ofPattern(FORMAT_VRIJEME);
  private static final DateTimeFormatter DF_DATUM_VRIJEME =
      DateTimeFormatter.ofPattern(FORMAT_DATUM_VRIJEME);

  private FormatDatuma() {}

  /**
   * Formatiraj.
   *
   * @param datum the datum
   * @return the string
   */
  public static String formatiraj(LocalDate datum) {
    if (datum == null)
      return "";
    return datum.format(DF_DATUM);
  }

  /**
   * Formatiraj.
   *
   * @param vrijeme the vrijeme
   * @return the string
   */
  public static String formatiraj(LocalTime vrijeme) {
    if (vrijeme == null)
      return "";
    return vrijeme.format(DF_VRIJEME);
  }

  /**
   * Formatiranje vremena.
   *
   * @param datumVrijeme the datum vrijeme
   * @return the string datumVrijeme.format(DF_DATUM_VRIJEME);
   */
  public static String formatiraj(LocalDateTime datumVrijeme) {
    if (datumVrijeme == null)
      return "";
    return datumVrijeme.format(DF_DATUM_VRIJEME);
  }
}
