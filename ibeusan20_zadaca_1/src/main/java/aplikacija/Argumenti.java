package aplikacija;

import java.util.HashMap;
import java.util.Map;

public class Argumenti {

  /** Mapa argumenata komandne linije (ključevi počinju s "--"). */
  private final Map<String, String> argumenti = new HashMap<>();

  /**
   * Konstruktor koji prima argumente komandne linije i
   * automatski ih parsira i validira.
   *
   * @param args polje argumenata dobivenih iz {@code main(String[] args)}
   * @throws IllegalArgumentException ako nedostaje obavezni argument
   *                                  ili je format pogrešan
   */
  public Argumenti(String[] args) {
    try {
      ucitajArgumente(args);
      provjeriObavezne();
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Neispravni argumenti: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new IllegalArgumentException("Greška pri parsiranju argumenata.", e);
    }
  }

  /**
   * Parsira argumente i pohranjuje ih u mapu {@link #argumenti}.
   * <p>
   * Očekuje se format: {@code --ključ vrijednost}.
   * Ako argument nije u tom formatu, jednostavno se preskače.
   * </p>
   *
   * @param args argumenti komandne linije
   */
  private void ucitajArgumente(String[] args) {
    if (args == null) {
      throw new IllegalArgumentException("Argumenti nisu zadani (null).");
    }
    for (int i = 0; i < args.length - 1; i++) {
      if (args[i].startsWith("--")) {
        argumenti.put(args[i], args[i + 1]);
      }
    }
  }

  /**
   * Provjerava prisutnost obaveznih argumenata:
   * <ul>
   *   <li>{@code --ta} – putanja do datoteke aranžmana</li>
   *   <li>{@code --rta} – putanja do datoteke rezervacija</li>
   * </ul>
   *
   * @throws IllegalArgumentException ako bilo koji od argumenata nedostaje
   */
  private void provjeriObavezne() {
    if (!argumenti.containsKey("--ta")) {
      throw new IllegalArgumentException("Nije zadana datoteka aranžmana (--ta)");
    }
    if (!argumenti.containsKey("--rta")) {
      throw new IllegalArgumentException("Nije zadana datoteka rezervacija (--rta)");
    }
  }

  /**
   * Dohvaća putanju do datoteke s aranžmanima.
   *
   * @return putanja do datoteke aranžmana
   */
  public String dohvatiPutanjuAranzmana() {
    return argumenti.get("--ta");
  }

  /**
   * Dohvaća putanju do datoteke s rezervacijama.
   *
   * @return putanja do datoteke rezervacija
   */
  public String dohvatiPutanjuRezervacija() {
    return argumenti.get("--rta");
  }
}
