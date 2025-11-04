package komande;

import logika.UpraviteljAranzmanima;
import logika.UpraviteljRezervacijama;

/**
 * Factory Method za stvaranje objekata tipa {@link Komanda}.
 * <p>
 * Na temelju unosa korisnika (naredbe) stvara odgovarajuću konkretnu komandu.
 * </p>
 */
public final class KomandaFactory {
  private KomandaFactory() {}

  /**
   * Kreira i vraća konkretnu komandu prema korisničkom unosu.
   *
   * @param unos korisnički unos s nazivom komande i argumentima
   * @param ua upravitelj aranžmanima
   * @param ur upravitelj rezervacijama
   * @return nova instanca odgovarajuće komande ili {@code null} ako naredba nije prepoznata
   */
  public static Komanda kreiraj(String unos, UpraviteljAranzmanima ua, UpraviteljRezervacijama ur) {

    String[] dijelovi = unos.trim().split("\\s+");
    String naredba = dijelovi[0].toUpperCase();

    String[] argumenti = new String[dijelovi.length - 1];
    if (dijelovi.length > 1) {
      System.arraycopy(dijelovi, 1, argumenti, 0, dijelovi.length - 1);
    }

    return switch (naredba) {
      case "ITAK" -> new KomandaItak(ua, argumenti);
      case "ITAP" -> new KomandaItap(ua, argumenti);
      case "IRTA" -> new KomandaIrta(ur, argumenti);
      case "IRO" -> new KomandaIro(ur, ua, argumenti);
      case "ORTA" -> new KomandaOrta(ur, ua, argumenti);
      case "DRTA" -> new KomandaDrta(ur, ua, argumenti);
      case "Q" -> new KomandaQ();
      default -> null;
    };
  }
}
