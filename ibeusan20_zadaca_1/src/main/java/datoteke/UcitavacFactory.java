package datoteke;

import model.Aranzman;
import model.Rezervacija;

/**
 * <p>
 * Stvara konkretne instance čitača podataka ({@link CitacAranzmana}, {@link CitacRezervacija}) bez
 * izravnog pozivanja njihovih konstruktora.
 * </p>
 */
public final class UcitavacFactory {
  private UcitavacFactory() {}

  /**
   * Kreira čitač za turističke aranžmane.
   *
   * @return nova instanca {@link CitacAranzmana}
   */
  public static UcitavacPodataka<Aranzman> createAranzmanReader() {
    return new CitacAranzmana();
  }

  /**
   * Kreira čitač za rezervacije aranžmana.
   *
   * @return nova instanca {@link CitacRezervacija}
   */
  public static UcitavacPodataka<Rezervacija> createRezervacijaReader() {
    return new CitacRezervacija();
  }
}
