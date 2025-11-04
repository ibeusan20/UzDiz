package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Alatna klasa za parsiranje i formatiranje datuma i vremena u hrvatskom formatu.
 * <p>
 * Metode prepoznaju više inačica formata te standardiziraju rezultat. Ako unos nije valjan, baca se
 * {@link IllegalArgumentException}.
 * </p>
 */
public class PomocnikDatum {
  private static final String FORMAT_DATUM = "dd.MM.yyyy.";
  private static final String FORMAT_VRIJEME = "HH:mm";
  private static final String FORMAT_DATUM_VRIJEME = "dd.MM.yyyy. HH:mm:ss";

  /**
   * Procitaj datum.
   *
   * @param tekst the tekst
   * @return the local date
   */
  public static LocalDate procitajDatum(String tekst) {
    if (tekst == null || tekst.isBlank())
      return null;

    String[] formati = {"d.M.yyyy.", "d.M.yyyy"};

    for (String f : formati) {
      try {
        LocalDate datum = LocalDate.parse(tekst.trim(), DateTimeFormatter.ofPattern(f));
        String standard = datum.format(DateTimeFormatter.ofPattern(FORMAT_DATUM));
        return LocalDate.parse(standard, DateTimeFormatter.ofPattern(FORMAT_DATUM));
      } catch (DateTimeParseException e) {

      }
    }

    throw new IllegalArgumentException("Neispravan format datuma: '" + tekst + "'");
  }

  /**
   * Procitaj vrijeme.
   *
   * @param tekst the tekst
   * @return the local time
   */
  public static LocalTime procitajVrijeme(String tekst) {
    if (tekst == null || tekst.isBlank())
      return null;

    String[] formati = {"H:mm", "HH:mm"};

    for (String f : formati) {
      try {
        LocalTime vrijeme = LocalTime.parse(tekst.trim(), DateTimeFormatter.ofPattern(f));
        String standard = vrijeme.format(DateTimeFormatter.ofPattern(FORMAT_VRIJEME));
        return LocalTime.parse(standard, DateTimeFormatter.ofPattern(FORMAT_VRIJEME));
      } catch (DateTimeParseException e) {
        // pokušaj sljedeći
      }
    }

    throw new IllegalArgumentException("Neispravan format vremena: '" + tekst + "'");
  }

  /**
   * Procitaj datum I vrijeme.
   *
   * @param tekst the tekst
   * @return the local date time
   */
  public static LocalDateTime procitajDatumIVrijeme(String tekst) {
    if (tekst == null || tekst.isBlank())
      return null;

    String[] formati = {"d.M.yyyy. H:mm:ss", "d.M.yyyy H:mm:ss", "d.M.yyyy. H:mm", "d.M.yyyy H:mm"};

    for (String f : formati) {
      try {
        LocalDateTime dt = LocalDateTime.parse(tekst.trim(), DateTimeFormatter.ofPattern(f));
        String standard = dt.format(DateTimeFormatter.ofPattern(FORMAT_DATUM_VRIJEME));
        return LocalDateTime.parse(standard, DateTimeFormatter.ofPattern(FORMAT_DATUM_VRIJEME));
      } catch (DateTimeParseException e) {
      }
    }
    throw new IllegalArgumentException("Neispravan format datuma/vremena: '" + tekst + "'");
  }

  /**
   * Formatiraj datum - vrijeme.
   *
   * @param dt the dt
   * @return the string
   */
  public static String formatirajDatumVrijeme(LocalDateTime dt) {
    if (dt == null)
      return "";
    DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss");
    return dt.format(f);
  }
}
